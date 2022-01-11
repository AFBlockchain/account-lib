package hk.edu.polyu.af.bc.account.flows.user

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.node.services.vault.QueryCriteria

fun FlowLogic<*>.getUserQueryCriteria(username: String): QueryCriteria {
    val uuid = subFlow(GetUserUUID(username))
    return QueryCriteria.VaultQueryCriteria(externalIds = listOf(uuid))
}

fun <T: ContractState> FlowLogic<*>.getUserStates(username: String, clazz: Class<T>): List<StateAndRef<T>> {
    return serviceHub.vaultService.queryBy(clazz, getUserQueryCriteria(username)).states
}