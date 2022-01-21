package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService

/**
 * Return all [NetworkIdentityPlane]s for the calling node.
 */
@StartableByRPC
@StartableByService
class GetAllNetworkIdentityPlanes : FlowLogic<List<NetworkIdentityPlane>>() {
    override fun call(): List<NetworkIdentityPlane> {
        val planeRef = serviceHub.vaultService.queryBy(NetworkIdentityPlane::class.java).states
        return planeRef.map { it.state.data }
    }
}

/**
 * Return the [NetworkIdentityPlane]s by the name. Null if the plane by the given name is not found.
 */
@StartableByRPC
@StartableByService
class GetNetworkIdentityPlaneByName(val name: String) : FlowLogic<NetworkIdentityPlane?>() {
    override fun call(): NetworkIdentityPlane? {
        // TODO: make more efficient queries (not in memory)

        val allPlanes = subFlow(GetAllNetworkIdentityPlanes())
        return allPlanes.firstOrNull { it.name == name }
    }
}
