package hk.edu.polyu.af.bc.account.flows.user

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.flows.AllAccounts
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.identity.Party

// Representation of a user is just a String. So there's no need for "reading" and "updating" a user
// The meaning of "deleting" a user is not clear yet.

/**
 * Create a user in the current identity plane.
 *
 * TODO: introduce naming constraint. Assume simple name (alphabet + digits) for now.
 */
@StartableByService
@StartableByRPC
class CreateUser(private val username: String) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val accountName = toAccountName(username)
        if (!isAccountNonExist(accountName)) throw FlowException("User already exists at ${context()}: $username")

        // share the account within identity plane
        val accountRef = subFlow(CreateAccount(accountName))
        subFlow(ShareAccountInfo(accountRef, context().participants as List<Party>))

        logger.info("User successfully created at ${context()}: $username")
    }
}

/**
 * Check whether the user given exists in the current identity plane.
 */
@StartableByService
@StartableByRPC
class IsUserExists(private val username: String) : FlowLogic<Boolean>() {
    override fun call(): Boolean {
        return !isAccountNonExist(toAccountName(username))
    }
}

/**
 * Return all users in the current identity plane
 */
@StartableByRPC
@StartableByService
class AllUsers() : FlowLogic<List<String>>() {
    override fun call(): List<String> {
        return subFlow(AllAccounts()).map {
            it.state.data.name
        }.filter {
            it.split(DELIMINATOR)[0] == context().name
        }.map {
            it.split(DELIMINATOR)[1]
        }
    }
}
