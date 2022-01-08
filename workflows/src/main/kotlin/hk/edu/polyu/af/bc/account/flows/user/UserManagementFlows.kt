package hk.edu.polyu.af.bc.account.flows.user

import net.corda.core.flows.FlowLogic
import java.util.*

// Representation of a user is just a String. So there's no need for "reading" and "updating" a user
// The meaning of "deleting" a user is not clear yet.

/**
 * Create a user in the current identity plane.
 */
class CreateUser(val username: String): FlowLogic<Unit>() {
    override fun call() {
        TODO("Not yet implemented")
    }
}

/**
 * Check whether the user given exists in the current identity plane.
 */
class IsUserExists(val username: String): FlowLogic<Boolean>() {
    override fun call(): Boolean {
        TODO("Not yet implemented")
    }
}

/**
 * Return all users in the current identity plane
 */
class AllUsers(): FlowLogic<List<String>>() {
    override fun call(): List<String> {
        TODO("Not yet implemented")
    }
}

/**
 * Get the underlying [UUID] for this user. Primarily, this identifier is used for vault query.
 */
class GetUserUUID(val username: String): FlowLogic<UUID>() {
    override fun call(): UUID {
        TODO("Not yet implemented")
    }
}