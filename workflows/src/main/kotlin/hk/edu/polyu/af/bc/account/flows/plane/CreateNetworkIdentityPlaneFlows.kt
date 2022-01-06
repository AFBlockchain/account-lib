package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction

/// The flow hierarchy follows https://github.com/corda/accounts/blob/master/workflows/src/main/kotlin/com/r3/corda/lib/accounts/workflows/flows/RequestKeyForAccountFlows.kt

/**
 * In-lined subflow to create a [NetworkIdentityPlane]. This flow and its counter part [CreateNetworkIdentityPlaneResponderFlow]
 * should check that there is no [NetworkIdentityPlane] with the same `name` in the node's vaults.
 */
class CreateNetworkIdentityPlaneFlow(
    private val name: String,
    private val otherParticipants: List<Party>,
    private val flowSession: FlowSession
): FlowLogic<SignedTransaction>() {
    override fun call(): SignedTransaction {
        TODO("Not yet implemented")
    }
}

class CreateNetworkIdentityPlaneResponderFlow(
    private val otherPartySession: FlowSession
):FlowLogic<SignedTransaction>() {
    override fun call(): SignedTransaction {
        TODO("Not yet implemented")
    }
}

@InitiatingFlow
@StartableByRPC
@StartableByService
class CreateNetworkIdentityPlane(
    private val name: String,
    private val otherParticipants: List<Party>
): FlowLogic<SignedTransaction>() {
    override fun call(): SignedTransaction {
        TODO("Not yet implemented")
    }
}

@InitiatedBy(CreateNetworkIdentityPlane::class)
class CreateNetworkIdentityPlaneResponder(
    private val otherPartySession: FlowSession
): FlowLogic<SignedTransaction>() {
    override fun call(): SignedTransaction {
        TODO("Not yet implemented")
    }
}