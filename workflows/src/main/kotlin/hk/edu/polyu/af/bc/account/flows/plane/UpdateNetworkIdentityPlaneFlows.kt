package hk.edu.polyu.af.bc.account.flows.plane

import co.paralleluniverse.fibers.Suspendable
import hk.edu.polyu.af.bc.account.contracts.NetworkIdentityPlaneContract
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.ReceiveFinalityFlow
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

// / The flow hierarchy follows https://github.com/corda/accounts/blob/master/workflows/src/main/kotlin/com/r3/corda/lib/accounts/workflows/flows/RequestKeyForAccountFlows.kt

/**
 * In-lined subflow to update a [NetworkIdentityPlane]. This flow and its counter part [UpdateNetworkIdentityPlaneResponder]
 * should check that there is no [NetworkIdentityPlane] with the same `name` in the node's vaults. Only the name can be changed
 */
class UpdateNetworkIdentityPlaneFlow(
    private val newName: String,
    private val networkIdentityPlaneRef: StateAndRef<NetworkIdentityPlane>
) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val allNetworkIdentityPlanes = subFlow(GetAllNetworkIdentityPlanes())
        for (netWorkIdentityPlane in allNetworkIdentityPlanes) {
            require(netWorkIdentityPlane.name != newName) {
                throw FlowException("The $newName has already existed")
            }
        }

        // Create transaction
        val networkIdentityPlane = networkIdentityPlaneRef.state.data
        val newNetworkIdentityPlane = NetworkIdentityPlane(newName, networkIdentityPlane.participants as List<Party>, networkIdentityPlane.linearId)
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val transactionBuilder = TransactionBuilder(notary = notary).apply {
            addInputState(networkIdentityPlaneRef)
            addOutputState(newNetworkIdentityPlane)
            addCommand(NetworkIdentityPlaneContract.Commands.Update(), newNetworkIdentityPlane.participants.map { it.owningKey })
        }

        transactionBuilder.verify(serviceHub)

        // Sign transaction
        val partSignedTx = serviceHub.signInitialTransaction(transactionBuilder)

        val otherParticipants = ArrayList(newNetworkIdentityPlane.participants)
        otherParticipants.remove(ourIdentity)
        val otherPartySessions = otherParticipants.map { initiateFlow(it) }

        val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, otherPartySessions))
        return subFlow(FinalityFlow(fullySignedTx, otherPartySessions))
    }
}

class UpdateNetworkIdentityPlaneResponderFlow(
    private val otherPartySession: FlowSession
) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(otherPartySession) {
            override fun checkTransaction(stx: SignedTransaction) {
                val allNetworkIdentityPlanes = subFlow(GetAllNetworkIdentityPlanes())
                for (netWorkIdentityPlane in allNetworkIdentityPlanes) {
                    val output = stx.tx.outputStates[0] as NetworkIdentityPlane
                    check(netWorkIdentityPlane.name != output.name) { throw FlowException("The ${output.name} has already existed") }
                }
            }
        }
        val txId = subFlow(signTransactionFlow).id

        return subFlow(ReceiveFinalityFlow(otherPartySession, expectedTxId = txId))
    }
}

@InitiatingFlow
@StartableByRPC
@StartableByService
class UpdateNetworkIdentityPlane(
    private val newName: String,
    private val networkIdentityPlane: StateAndRef<NetworkIdentityPlane> // it needs to be changed after the implement query by name
) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        return subFlow(UpdateNetworkIdentityPlaneFlow(newName, networkIdentityPlane))
    }
}

@InitiatedBy(UpdateNetworkIdentityPlane::class)
class UpdateNetworkIdentityPlaneResponder(
    private val otherPartySession: FlowSession
) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        return subFlow(UpdateNetworkIdentityPlaneResponderFlow(otherPartySession))
    }
}
