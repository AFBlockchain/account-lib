package hk.edu.polyu.af.bc.account.flows.mapping

import co.paralleluniverse.fibers.Suspendable
import hk.edu.polyu.af.bc.account.flows.*
import hk.edu.polyu.af.bc.account.flows.plane.CreateNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.flows.plane.SetCurrentNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.flows.user.CreateUser
import hk.edu.polyu.af.bc.account.flows.user.GetUserStates
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import hk.edu.polyu.af.bc.message.flows.SendMessage
import hk.edu.polyu.af.bc.message.states.MessageState
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.testing.common.internal.log
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
import org.junit.jupiter.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SendMessageUser(private val sender: String,
                      private val receiver: String,
                      private val msg: String): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val senderId = toAnonymousParty(sender)
        val receiverId = toAnonymousParty(receiver)

        return subFlow(SendMessage(senderId, receiverId, msg))
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlowMappingTest{
    private lateinit var network: MockNetwork
    private lateinit var a: StartedMockNode
    private lateinit var b: StartedMockNode

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FlowMappingTest::class.java)
    }

    @BeforeAll
    fun setup() {
        network = MockNetwork(mockNetworkParameters)
        a = network.createPartyNode()
        b = network.createPartyNode()
        network.runNetwork()

        // create an identity plane and set it for a and b
        val plane = a.startFlow(CreateNetworkIdentityPlane("message-plane", listOf(b.party()))).getOrThrow(network).output(NetworkIdentityPlane::class.java)
        a.startFlow(SetCurrentNetworkIdentityPlane(plane)).getOrThrow(network)
        b.startFlow(SetCurrentNetworkIdentityPlane(plane)).getOrThrow(network)

        // create cordapp users
        a.startFlow(CreateUser("user1")).getOrThrow(network)
        b.startFlow(CreateUser("user2")).getOrThrow(network)
    }

    @AfterAll
    fun tearDown() {
        network.stopNodes()
    }

    @Test
    fun sendMessageWithCordappUsers() {
        val messageState = a.startFlow(SendMessageUser("user1", "user2", "Hello Cordapp"))
            .getOrThrow(network)
            .output(MessageState::class.java)

        assert(messageState.msg == "Hello Cordapp")
        a.assertHaveState(messageState, messageStateComparator)
        b.assertHaveState(messageState, messageStateComparator)
    }

    @Test
    fun canQueryVaultForUsers() {
        a.startFlow(SendMessageUser("user1", "user2", "Hello"))
            .getOrThrow(network)

        val queriedFromA = a.startFlow(GetUserStates("user1", MessageState::class.java)).getOrThrow(network)
        val queriedFromB = b.startFlow(GetUserStates("user2", MessageState::class.java)).getOrThrow(network)

        listOf(queriedFromA, queriedFromB).forEach {
            logger.info("States: {}", it)
            assert(it.map { it.state.data }.any { it.msg == "Hello" })
        }
    }

    private val messageStateComparator = {
        s1: MessageState, s2: MessageState -> s1.msg == s2.msg
    }
}