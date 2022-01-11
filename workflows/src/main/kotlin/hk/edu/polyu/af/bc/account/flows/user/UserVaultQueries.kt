package hk.edu.polyu.af.bc.account.flows.user

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import java.util.*

@StartableByRPC
@StartableByService
class GetUserUUID(private val username: String): FlowLogic<UUID>() {
    override fun call(): UUID {
        return toUUID(username)
    }
}

@StartableByService
@StartableByRPC
class GetUserStates<out T: ContractState>(private val username: String, private val stateClass: Class<T>):
    FlowLogic<List<StateAndRef<T>>>() {
    override fun call(): List<StateAndRef<T>> {
        return getUserStates(username, stateClass)
    }
}