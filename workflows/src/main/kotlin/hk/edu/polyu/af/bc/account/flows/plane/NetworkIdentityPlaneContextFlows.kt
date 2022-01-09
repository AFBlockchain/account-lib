package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowLogic

/// Flows to manage the current NetworkIdentityPlane at the corda node

/**
 * Return the current [NetworkIdentityPlane] in the [NetworkIdentityPlaneContext].
 */
class GetCurrentNetworkIdentityPlane: FlowLogic<NetworkIdentityPlane?>() {
    override fun call(): NetworkIdentityPlane? {
        return NetworkIdentityPlaneContext.currentPlane
    }
}

/**
 * Set the current [NetworkIdentityPlaneContext] to hold the given plane.
 */
class SetCurrentNetworkIdentityPlane(val plane: NetworkIdentityPlane): FlowLogic<Unit>() {
    override fun call() {
        //TODO: check that the plane has been registered on this node
        subFlow(GetNetworkIdentityPlaneByName(plane.name)) ?: throw IllegalArgumentException("Plane not found: $plane")
        logger.info("Setting network identity plane to: $plane")
        NetworkIdentityPlaneContext.currentPlane = plane
    }
}


class SetCurrentNetworkIdentityPlaneByName(private val planeName: String): FlowLogic<Unit>() {
    override fun call() {
        val plane = subFlow(GetNetworkIdentityPlaneByName(planeName)) ?: throw IllegalArgumentException("Plane not found: $planeName")
        subFlow(SetCurrentNetworkIdentityPlane(plane))
    }
}
