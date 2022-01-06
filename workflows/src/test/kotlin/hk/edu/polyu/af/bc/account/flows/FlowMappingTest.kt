package hk.edu.polyu.af.bc.account.flows

import co.paralleluniverse.fibers.Suspendable
import hk.edu.polyu.af.bc.account.SimpleCordappUser
import hk.edu.polyu.af.bc.account.user.CordappUser
import hk.edu.polyu.af.bc.message.flows.SendMessage
import hk.edu.polyu.af.bc.message.states.MessageState
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SendMessageUser(private val sender: CordappUser,
                      private val receiver: CordappUser,
                      private val msg: String): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val senderId = toAnonymousParty(sender)
        val receiverId = toAnonymousParty(receiver)

        return subFlow(SendMessage(senderId, receiverId, msg))
    }
}

class FlowMappingTest{
    private lateinit var network: MockNetwork
    private lateinit var a: StartedMockNode
    private lateinit var b: StartedMockNode
    private var users: MutableMap<String, CordappUser> = mutableMapOf()

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FlowMappingTest::class.java)
    }

    @Before
    fun setup() {
        network = MockNetwork(mockNetworkParameters)
        a = network.createPartyNode()
        b = network.createPartyNode()
        network.runNetwork()

        // create cordapp users
        val user1 = SimpleCordappUser("user1")
        val user2 = SimpleCordappUser("user2")
        a.startFlow(CreateCordappUser(user1)).getOrThrow(network)
        logger.info("User created: $user1")
        b.startFlow(CreateCordappUser(user2)).getOrThrow(network)
        logger.info("User created: $user2")

        // share the account
        a.startFlow(ShareCordappUser(user1, listOf(b.party())))
        logger.info("User shared: $user1")
        b.startFlow(ShareCordappUser(user2, listOf(a.party())))
        logger.info("User shared: $user2")

        users["user1"] = user1
        users["user2"] = user2
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }

    @Test
    fun sendMessageWithCordappUsers() {
        val messageState = a.startFlow(SendMessageUser(users["user1"]!!, users["user2"]!!, "Hello Cordapp"))
            .getOrThrow(network)
            .output(MessageState::class.java)

        assert(messageState.msg == "Hello Cordapp")
        a.assertHaveState(messageState, messageStateComparator)
        b.assertHaveState(messageState, messageStateComparator)
    }

    private val messageStateComparator = {
        s1: MessageState, s2: MessageState -> s1.msg == s2.msg
    }
}