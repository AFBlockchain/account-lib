package hk.edu.polyu.af.bc.account.flows

import net.corda.core.identity.CordaX500Name
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestCordapp
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

/**
 * This class provides default setup and configuration for unit-testing this module.
 *
 * Test classes need to be annotated with `@TestInstance(PER_CLASS)`.
 */
abstract class UnitTestBase {
    // network
    protected lateinit var network: MockNetwork

    // nodes
    protected lateinit var partyA: StartedMockNode
    protected lateinit var partyB: StartedMockNode
    protected lateinit var partyC: StartedMockNode

    @BeforeAll
    open fun setup() {
        network = MockNetwork(
            MockNetworkParameters(cordappsForAllNodes = listOf(
                TestCordapp.findCordapp("hk.edu.polyu.af.bc.account.contracts"),
                TestCordapp.findCordapp("com.r3.corda.lib.accounts.workflows.flows"),
                TestCordapp.findCordapp("com.r3.corda.lib.accounts.contracts"),
                TestCordapp.findCordapp("hk.edu.polyu.af.bc.account.flows")
            ), networkParameters = testNetworkParameters(minimumPlatformVersion = 4)))

        partyA = network.createPartyNode(CordaX500Name.parse("O=PartyA, L=Athens, C=GR"))
        partyB = network.createPartyNode(CordaX500Name.parse("O=PartyB, L=Athens, C=GR"))
        partyC = network.createPartyNode(CordaX500Name.parse("O=PartyC, L=Athens, C=GR"))
        network.runNetwork()
    }

    @AfterAll
    fun tearDown() {
        network.stopNodes()
    }
}