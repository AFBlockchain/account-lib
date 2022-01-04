package hk.edu.polyu.af.bc.account

import net.corda.core.concurrent.CordaFuture
import net.corda.core.contracts.ContractState
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
import java.time.Duration


/**
 * Get the first output of type `clazz` from the tx.
 */
inline fun <reified T: ContractState> SignedTransaction.output(clazz: Class<T>): T {
    return coreTransaction.filterOutputs<T> { true }[0]
}

/**
 * Assert the node's vault contain the given state.
 */
fun <T: ContractState> StartedMockNode.assertHaveState(state: T, comparator: (s1: T, s2: T) -> Boolean) {
    val hasNone = services.vaultService.queryBy(state.javaClass).states.none { comparator(state, it.state.data) }
    if (hasNone) throw AssertionError("State not found in ${info.legalIdentities[0]}: $state")
}

/**
 * Wrap `getOrThrow(Duration)` by inserting a network run.
 */
fun <V> CordaFuture<V>.getOrThrow(network: MockNetwork, rounds: Int = -1, timeout: Duration? = null): V {
    network.runNetwork(rounds);
    return getOrThrow(timeout)
}