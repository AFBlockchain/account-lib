package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.flows.*
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateNetworkIdentityPlaneFlowsTest: UnitTestBase() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(CreateNetworkIdentityPlaneFlowsTest::class.java)
    }

    @Test
    fun `should create a NetworkIdentityPlane with three parties`() {
        val tx = partyA.startFlow(CreateNetworkIdentityPlane("test-plane", listOf(partyB.party(), partyC.party()))).getOrThrow(network)
        logger.info("Transaction: $tx")

        val plane = tx.output(NetworkIdentityPlane::class.java)
        logger.info("Plane: $plane")

        listOf(partyA, partyB, partyC).forEach {
            it.assertHaveState(plane, planeComparator)
        }
    }


    @Test
    fun `should rejects creation when there is name conflict at other parties`() {
        logger.info("Creating plane-1 for PartyB")
        partyB.startFlow(CreateNetworkIdentityPlane("plane-1", listOf())).getOrThrow(network)

        logger.info("Creating plane-1 for PartyA, PartyB, PartyC")
        assertThrows<FlowException> {
            partyA.startFlow(CreateNetworkIdentityPlane("plane-1", listOf(partyB.party(), partyC.party()))).getOrThrow(network)
        }
    }
}