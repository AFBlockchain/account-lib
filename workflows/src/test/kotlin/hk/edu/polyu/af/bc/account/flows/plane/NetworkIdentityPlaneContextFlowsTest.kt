package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.flows.UnitTestBase
import hk.edu.polyu.af.bc.account.flows.getOrThrow
import hk.edu.polyu.af.bc.account.flows.output
import hk.edu.polyu.af.bc.account.flows.party
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import org.junit.Test

class NetworkIdentityPlaneContextFlowsTest: UnitTestBase() {
    @Test
    fun `can set and get IdentityPlane at one nodes`() {
        val plane = partyA.startFlow(CreateNetworkIdentityPlane("plane-a", listOf()))
            .getOrThrow(network)
            .output(NetworkIdentityPlane::class.java)

        partyA.startFlow(SetCurrentNetworkIdentityPlane(plane)).getOrThrow(network)
        val currentPlane = partyA.startFlow(GetCurrentNetworkIdentityPlane()).getOrThrow(network)!!

        assert(currentPlane.name == plane.name)
    }

    @Test
    fun `can set the plane when the plane is not created at current node`() {
        val plane = partyA.startFlow(CreateNetworkIdentityPlane("plane-abc", listOf(partyB.party(), partyC.party())))
            .getOrThrow(network)
            .output(NetworkIdentityPlane::class.java)

        listOf(partyB, partyC).forEach {
            it.startFlow(SetCurrentNetworkIdentityPlane(plane))

            val currentPlane = it.startFlow(GetCurrentNetworkIdentityPlane()).getOrThrow(network)!!
            assert(currentPlane.name == plane.name)
        }
    }
}