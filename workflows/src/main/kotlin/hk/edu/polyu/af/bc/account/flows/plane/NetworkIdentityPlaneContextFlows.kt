package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService

// / Flows to manage the current NetworkIdentityPlane at the corda node

/**
 * Return the current [NetworkIdentityPlane] in the [NetworkIdentityPlaneContext]. Return `null` if the context hasn't
 * been set
 */
@StartableByRPC
@StartableByService
class GetCurrentNetworkIdentityPlane : FlowLogic<NetworkIdentityPlane?>() {
    override fun call(): NetworkIdentityPlane? {
        return NetworkIdentityPlaneContext.currentPlane
    }
}

/**
 * Set the current [NetworkIdentityPlaneContext] to hold the given plane.
 */
@StartableByRPC
@StartableByService
class SetCurrentNetworkIdentityPlane(val plane: NetworkIdentityPlane) : FlowLogic<Unit>() {
    override fun call() {
        // no name matches
        val queried = subFlow(GetNetworkIdentityPlaneByName(plane.name)) ?: throw IllegalArgumentException("Plane not found: $plane")
        // are two planes really the same, despite having the same name?
        require(queried.linearId == plane.linearId) {
            "Cannot find plane: $plane. Although a plane of the same name was found: $queried"
        }

        NetworkIdentityPlaneContext.currentPlane = plane
        logger.info("Network identity plane is set to: $plane")

        // TODO: if we are not a participant in the given plane, the call should fail
    }
}

@StartableByRPC
@StartableByService
class SetCurrentNetworkIdentityPlaneByName(private val planeName: String) : FlowLogic<Unit>() {
    override fun call() {
        val plane = subFlow(GetNetworkIdentityPlaneByName(planeName)) ?: throw IllegalArgumentException("Plane not found: $planeName")
        subFlow(SetCurrentNetworkIdentityPlane(plane))
    }
}
