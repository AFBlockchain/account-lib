package hk.edu.polyu.af.bc.account

import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import hk.edu.polyu.af.bc.account.flows.CreateCordappUser
import hk.edu.polyu.af.bc.account.flows.ShareCordappUser
import hk.edu.polyu.af.bc.account.identity.CordappUser
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
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
        network = MockNetwork(mockNetworkParameters)
        a = network.createPartyNode()
        b = network.createPartyNode()
        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }

    @Test
    fun canCreateUser() {
        val user1 = SimpleCordappUser("user1")
        a.startFlow(CreateCordappUser(user1)).getOrThrow(network)

        logger.info("user1: $user1")
        assertNotNull(user1.getAccountHostCordaX500Name())
        assertNotNull(user1.getAccountUUID())
    }

    @Test
    fun canShareUser() {
        val user2 = SimpleCordappUser("user2")
        b.startFlow(CreateCordappUser(user2)).getOrThrow(network)  //create
        b.startFlow(ShareCordappUser(user2, listOf(a.party()))).getOrThrow(network)  // share with a

        b.assertHaveState(AccountInfo::class.java) { it.name == "user2" }
        a.assertHaveState(AccountInfo::class.java) { it.name == "user2" }
    }
}