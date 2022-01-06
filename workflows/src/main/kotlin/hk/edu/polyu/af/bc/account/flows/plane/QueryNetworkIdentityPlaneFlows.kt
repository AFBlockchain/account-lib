package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowLogic

/**
 * Return all [NetworkIdentityPlane]s for the calling node.
 */
class GetAllNetworkIdentityPlanes(): FlowLogic<List<NetworkIdentityPlane>>() {
    override fun call(): List<NetworkIdentityPlane> {
        TODO("Not yet implemented")
    }
}

/// Define more for calling by name, id, etc.