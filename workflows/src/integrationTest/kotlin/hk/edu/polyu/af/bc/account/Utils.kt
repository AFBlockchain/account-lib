package hk.edu.polyu.af.bc.account

import com.github.manosbatsis.corda.testacles.nodedriver.NodeParamsHelper
import com.github.manosbatsis.corda.testacles.nodedriver.config.NodeDriverNodesConfig
import com.github.manosbatsis.corda.testacles.nodedriver.config.SimpleNodeDriverNodesConfig
import net.corda.core.contracts.ContractState
import net.corda.core.messaging.CordaRPCOps
import net.corda.testing.core.ALICE_NAME
import net.corda.testing.core.BOB_NAME

val nodeParamsHelper = NodeParamsHelper()

val customNodeDriverConfig: NodeDriverNodesConfig =
    SimpleNodeDriverNodesConfig(
        cordappPackages = listOf(
            "hk.edu.polyu.af.bc.account.contracts",
            "hk.edu.polyu.af.bc.account.flows",
            "com.r3.corda.lib.accounts.workflows.flows",
            "com.r3.corda.lib.accounts.contracts"
        ),
        nodes = mapOf("partyA" to nodeParamsHelper.toNodeParams(ALICE_NAME), "partyB" to nodeParamsHelper.toNodeParams(BOB_NAME)),
        minimumPlatformVersion = 4,
        debug = false
    )

fun CordaRPCOps.party() = nodeInfo().legalIdentities[0]

fun <T : ContractState> CordaRPCOps.assertHaveState(state: T, comparator: (s1: T, s2: T) -> Boolean) {
    val hasNone = this.vaultQuery(state.javaClass).states.none { comparator(state, it.state.data) }
    if (hasNone) throw AssertionError("State not found in ${party()}: $state")
}
