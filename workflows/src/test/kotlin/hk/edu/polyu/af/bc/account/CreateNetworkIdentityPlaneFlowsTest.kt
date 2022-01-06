package hk.edu.polyu.af.bc.account

import hk.edu.polyu.af.bc.account.flows.CreateNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.flows.PlaneExistsException
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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


    @Test(expected = PlaneExistsException::class)
    fun `should rejects creation when there is name conflict at other parties`() {
        logger.info("Creating plane-1 for PartyB")
        partyB.startFlow(CreateNetworkIdentityPlane("plane-1", listOf())).getOrThrow(network)

        logger.info("Creating plane-1 for PartyA, PartyB, PartyC")
        partyA.startFlow(CreateNetworkIdentityPlane("plane-1", listOf(partyB.party(), partyC.party()))).getOrThrow(network)
    }
}