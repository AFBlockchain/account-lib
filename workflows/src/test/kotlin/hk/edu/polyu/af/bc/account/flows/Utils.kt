package hk.edu.polyu.af.bc.account.flows

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.concurrent.CordaFuture
import net.corda.core.contracts.ContractState
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestCordapp
import java.time.Duration

val mockNetworkParameters = MockNetworkParameters(cordappsForAllNodes = listOf(
    TestCordapp.findCordapp("hk.edu.polyu.af.bc.message.contracts"),
    TestCordapp.findCordapp("hk.edu.polyu.af.bc.message.flows"),
    TestCordapp.findCordapp("hk.edu.polyu.af.bc.account.flows"),
    TestCordapp.findCordapp("com.r3.corda.lib.accounts.workflows.flows"),
    TestCordapp.findCordapp("com.r3.corda.lib.accounts.contracts")
))


fun StartedMockNode.party(): Party {
    return this.info.legalIdentities[0]
}

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

fun <T: ContractState> StartedMockNode.assertHaveState(stateClass: Class<T>, matcher: (s: T) -> Boolean) {
    val matched =
        services.vaultService.queryBy(stateClass).states.map { it.state.data }.any { matcher(it) }

    if (!matched) throw AssertionError("State not found in ${info.legalIdentities[0]} for the given matcher of type $stateClass")
}

/**
 * Wrap `getOrThrow(Duration)` by inserting a network run.
 */
fun <V> CordaFuture<V>.getOrThrow(network: MockNetwork, rounds: Int = -1, timeout: Duration? = null): V {
    network.runNetwork(rounds);
    return getOrThrow(timeout)
}

val planeComparator = {
        p1: NetworkIdentityPlane, p2: NetworkIdentityPlane -> p1.linearId == p2.linearId
}