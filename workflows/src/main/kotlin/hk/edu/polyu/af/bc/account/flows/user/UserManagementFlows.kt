package hk.edu.polyu.af.bc.account.flows.user

import net.corda.core.flows.FlowLogic

// Representation of a user is just a String. So there's no need for "reading" and "updating" a user
// The meaning of "deleting" a user is not clear yet.

/**
 * Create a user in the current identity plane.
 */
class CreateUser(val name: String): FlowLogic<Unit>() {
    override fun call() {
        TODO("Not yet implemented")
    }
}

/**
 * Check whether the user given exists in the current identity plane.
 */
class IsUserExists(val name: String): FlowLogic<Boolean>() {
    override fun call(): Boolean {
        TODO("Not yet implemented")
    }
}