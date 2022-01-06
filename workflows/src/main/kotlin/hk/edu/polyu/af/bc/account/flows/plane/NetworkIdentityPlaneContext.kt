package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane

/**
 * A holder of the current [NetworkIdentityPlane]. Application flows will reference this global object to resolve identities.
 * This is similar with `SecurityContextHolder` from spring security.
 *
 * TODO: There will be conflicts when more than one applications is using [NetworkIdentityPlane].
 */
class NetworkIdentityPlaneContext {
    companion object {
        var currentPlane: NetworkIdentityPlane? = null
    }
}
