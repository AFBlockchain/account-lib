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
class GetAllNetworkIdentityPlanes(): FlowLogic<List<NetworkIdentityPlane>>() {
    override fun call(): List<NetworkIdentityPlane> {
        val planeRef = serviceHub.vaultService.queryBy(NetworkIdentityPlane::class.java).states
        return planeRef.map{it.state.data}
    }
}

/**
 * Return the [NetworkIdentityPlane]s by the name.
 * only one plan can be found
 */
@StartableByRPC
@StartableByService
class GetNetworkIdentityPlaneByName(val name: String): FlowLogic<NetworkIdentityPlane>() {
    override fun call(): NetworkIdentityPlane {
        val allPlanes = subFlow(GetAllNetworkIdentityPlanes())
        return allPlanes.filter{it.name == name}[0]
    }
}