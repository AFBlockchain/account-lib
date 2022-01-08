package hk.edu.polyu.af.bc.account.flows.user

import com.r3.corda.lib.accounts.workflows.flows.AllAccounts
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount
import net.corda.core.flows.FlowLogic
import java.util.*

// Representation of a user is just a String. So there's no need for "reading" and "updating" a user
// The meaning of "deleting" a user is not clear yet.

/**
 * Create a user in the current identity plane.
 *
 * TODO: introduce naming constraint. Assume simple name (alphabet + digits)
 */
class CreateUser(private val username: String): FlowLogic<Unit>() {
    override fun call() {
        val accountName = toAccountName(username)
        if (!isAccountNonExist(accountName)) throw UserExistsException(username, context)

        subFlow(CreateAccount(accountName))
    }
}

/**
 * Check whether the user given exists in the current identity plane.
 */
class IsUserExists(private val username: String): FlowLogic<Boolean>() {
    override fun call(): Boolean {
        return !isAccountNonExist(toAccountName(username))
    }
}

/**
 * Return all users in the current identity plane
 */
class AllUsers(): FlowLogic<List<String>>() {
    override fun call(): List<String> {
        return subFlow(AllAccounts()).map {
            it.state.data.name
        }.filter {
            it.split(DELIMINATOR)[0] == context.name
        }.map {
            it.split(DELIMINATOR)[1]
        }
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