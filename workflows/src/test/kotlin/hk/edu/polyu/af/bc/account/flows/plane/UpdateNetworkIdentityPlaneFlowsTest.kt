package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.flows.*
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.contracts.LinearPointer
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowException
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals


class UpdateNetworkIdentityPlaneFlowsTest: UnitTestBase() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(UpdateNetworkIdentityPlaneFlowsTest::class.java)
    }

    @Test
    fun `should update a NetworkIdentityPlane with newName`() {
        val tx1 = partyA.startFlow(CreateNetworkIdentityPlane("plane-1", listOf())).getOrThrow(network)
        logger.info("Transaction: $tx1")

        val plane = tx1.output(NetworkIdentityPlane::class.java)
        logger.info("Plane: $plane")

        //query the NetworkIdentityPlane by uuid
        val networkIdentityPlaneRef: StateAndRef<NetworkIdentityPlane> = LinearPointer<NetworkIdentityPlane>(
            plane.linearId,
            NetworkIdentityPlane::class.java,
            false).resolve(partyA.services)


        logger.info("Update the name to plane 2")
        val tx2 = partyA.startFlow(UpdateNetworkIdentityPlane("plane-2",networkIdentityPlaneRef)).getOrThrow(network)

        val plane2 = tx2.output(NetworkIdentityPlane::class.java)
        logger.info("Plane: $plane2")

        partyA.assertHaveState(plane2, planeComparator)

        assertEquals("plane-2",plane2.name)


    }


    @Test(expected = FlowException::class)
    fun `should rejects update when there is name conflict at other parties`() {
        logger.info("Creating plane-1 for PartyA")
        val tx1 = partyA.startFlow(CreateNetworkIdentityPlane("plane-1", listOf())).getOrThrow(network)

        logger.info("Creating plane-2 for PartyA, PartyB")
        partyB.startFlow(CreateNetworkIdentityPlane("plane-2", listOf(partyA.party()))).getOrThrow(network)

        logger.info("Update the name")
        //query the NetworkIdentityPlane by uuid
        val plane = tx1.output(NetworkIdentityPlane::class.java)
        val networkIdentityPlaneRef: StateAndRef<NetworkIdentityPlane> = LinearPointer<NetworkIdentityPlane>(
            plane.linearId,
            NetworkIdentityPlane::class.java,
            false).resolve(partyA.services)

        logger.info("Update the name to plane-2")
        partyA.startFlow(UpdateNetworkIdentityPlane("plane-2",networkIdentityPlaneRef)).getOrThrow(network)
    }
}