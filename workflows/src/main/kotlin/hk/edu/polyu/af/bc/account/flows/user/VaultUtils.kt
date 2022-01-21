package hk.edu.polyu.af.bc.account.flows.user

import hk.edu.polyu.af.bc.account.flows.mapping.toAccountInfo
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.node.services.vault.QueryCriteria
import java.lang.IllegalArgumentException
import java.util.UUID

/**
 * Map the user to the [UUID] of the underlying account. Used to form more flexible vault queries.
 */
fun FlowLogic<*>.toUUID(username: String): UUID {
    val accountInfo = toAccountInfo(username) ?: throw IllegalArgumentException("User not found: $username")
    return accountInfo.identifier.id
}

/**
 * Get the [QueryCriteria.VaultQueryCriteria] for the given user. THe obtained criteria can be used in conjunction with
 * other queries
 */
fun FlowLogic<*>.getUserQueryCriteria(username: String): QueryCriteria {
    val uuid = subFlow(GetUserUUID(username))
    return QueryCriteria.VaultQueryCriteria(externalIds = listOf(uuid))
}

/**
 * Get a list of [ContractState] of the given type for the given user.
 */
fun <T : ContractState> FlowLogic<*>.getUserStates(username: String, clazz: Class<T>): List<StateAndRef<T>> {
    return serviceHub.vaultService.queryBy(clazz, getUserQueryCriteria(username)).states
}
