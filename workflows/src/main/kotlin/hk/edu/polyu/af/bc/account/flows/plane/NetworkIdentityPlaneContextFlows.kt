package hk.edu.polyu. af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.flows.plane.NetworkIdentityPlaneContext.Companion.currentPlane
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic

/// Flows to manage the current NetworkIdentityPlane at the corda node

/**
 * Return the current [NetworkIdentityPlane] in the [NetworkIdentityPlaneContext].
 */
class GetCurrentNetworkIdentityPlane(): FlowLogic<NetworkIdentityPlane?>() {
    override fun call(): NetworkIdentityPlane? {
        if(currentPlane==null){
            throw FlowException("currentPlane is null")
        }

        return currentPlane
    }
}

/**
 * Set the current [NetworkIdentityPlaneContext] to hold the given plane
 */
class SetCurrentNetworkIdentityPlane(val plane: NetworkIdentityPlane): FlowLogic<Unit>() {
    override fun call(): Unit {
        if(currentPlane==null){
            throw FlowException("currentPlane is null")
        }
        NetworkIdentityPlaneContext.currentPlane=plane
    }
}
