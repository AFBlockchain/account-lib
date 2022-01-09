package hk.edu.polyu.af.bc.account.flows.user

import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByName
import hk.edu.polyu.af.bc.account.flows.plane.NetworkIdentityPlaneContext
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowLogic
import java.lang.IllegalStateException

/**
 * Deliminator to separate [NetworkIdentityPlane] name and username.
 */
const val DELIMINATOR = "/"

/**
 * A shortcut to fetch the current [NetworkIdentityPlane] for the node.
 */
fun context(): NetworkIdentityPlane = NetworkIdentityPlaneContext.currentPlane ?: throw IllegalStateException("NetworkIdentityPlane is not set")

/**
 * Map the application username to account name
 */
fun toAccountName(username: String): String = "${context().name}${DELIMINATOR}${username}"

/**
 * Check whether an account exists
 */
fun FlowLogic<*>.isAccountNonExist(accountName: String): Boolean {
    return subFlow(AccountInfoByName(accountName)).isEmpty()
}