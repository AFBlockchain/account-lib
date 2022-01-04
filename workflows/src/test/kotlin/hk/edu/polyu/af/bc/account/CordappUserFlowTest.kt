package hk.edu.polyu.af.bc.account

import hk.edu.polyu.af.bc.account.flows.client.CreateCordappUser
import hk.edu.polyu.af.bc.account.identity.CordappUser
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestCordapp
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.assertNotNull

class SimpleCordappUser(
    private val name: String,
    private var accountHostCordaX500Name: String? = null,
    private var accountUUID: UUID? = null
): CordappUser {
    override fun getAccountName(): String {
        return name
    }

    override fun getAccountHostCordaX500Name(): String? {
        return accountHostCordaX500Name
    }

    override fun setAccountHostCordaX500Name(name: String) {
        accountHostCordaX500Name = name
    }

    override fun getAccountUUID(): UUID? {
        return accountUUID
    }

    override fun setAccountUUID(uuid: UUID) {
        accountUUID = uuid
    }

    override fun toString(): String {
        return "{name: $name, host: $accountHostCordaX500Name, uid: $accountUUID}"
    }
}

class CordappUserFlowTest {
    private lateinit var network: MockNetwork
    private lateinit var a: StartedMockNode
    private lateinit var b: StartedMockNode

    companion object {
        val logger: Logger = LoggerFactory.getLogger(CordappUserFlowTest::class.java)
    }

    @Before
    fun setup() {
        network = MockNetwork(
            MockNetworkParameters(cordappsForAllNodes = listOf(
                TestCordapp.findCordapp("hk.edu.polyu.af.bc.message.contracts"),
                TestCordapp.findCordapp("hk.edu.polyu.af.bc.message.flows"),
                TestCordapp.findCordapp("hk.edu.polyu.af.bc.account.flows"),
                TestCordapp.findCordapp("com.r3.corda.lib.accounts.workflows.flows"),
                TestCordapp.findCordapp("com.r3.corda.lib.accounts.contracts")
            ))
        )

        a = network.createPartyNode()
        b = network.createPartyNode()
        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }

    @Test
    fun `create user at node a`() {
        val user1 = SimpleCordappUser("user1")
        a.startFlow(CreateCordappUser(user1)).getOrThrow(network)

        logger.info("user1: $user1")
        assertNotNull(user1.getAccountHostCordaX500Name())
        assertNotNull(user1.getAccountUUID())
    }
}