package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.flows.UnitTestBase
import hk.edu.polyu.af.bc.account.flows.getOrThrow
import hk.edu.polyu.af.bc.account.flows.output
import hk.edu.polyu.af.bc.account.flows.party
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    fun `can set a plane by name`() {
        val plane = partyA.startFlow(CreateNetworkIdentityPlane("named-plane", listOf()))
            .getOrThrow(network)
            .output(NetworkIdentityPlane::class.java)

        partyA.startFlow(SetCurrentNetworkIdentityPlaneByName(plane.name))
        val currentPlane = partyA.startFlow(GetCurrentNetworkIdentityPlane()).getOrThrow(network)!!

        assert(currentPlane.name == plane.name)
    }

    @Test
    fun `should throw exception when the plane is not known`() {
        assertThrows<IllegalArgumentException> {
            partyA.startFlow(SetCurrentNetworkIdentityPlaneByName("no-such-plane")).getOrThrow(network)
        }
    }

    @Test
    fun `can set the plane when the plane is not created by current node`() {
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