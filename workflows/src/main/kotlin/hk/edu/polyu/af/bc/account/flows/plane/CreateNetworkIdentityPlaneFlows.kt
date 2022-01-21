package hk.edu.polyu.af.bc.account.flows.plane

import co.paralleluniverse.fibers.Suspendable
import hk.edu.polyu.af.bc.account.contracts.NetworkIdentityPlaneContract
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.contracts.UniqueIdentifier
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
 * In-lined subflow to create a [NetworkIdentityPlane]. This flow and its counter part [CreateNetworkIdentityPlaneResponderFlow]
 * should check that there is no [NetworkIdentityPlane] with the same `name` in the node's vaults. If a plane with the same name
 * is found (checked against all proposed participants within the plane), a [FlowException] will be thrown and the creation fails.
 *
 * @property name a unique name for the [NetworkIdentityPlane]
 * @property otherParticipants other parties in the network to participate in this plane
 */
@InitiatingFlow
@StartableByRPC
@StartableByService
class CreateNetworkIdentityPlane(
    private val name: String,
    private val otherParticipants: List<Party>
) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        checkPlaneExistence(name)

        val participants = otherParticipants.toMutableList()
        if (!participants.contains(ourIdentity)) participants.add(ourIdentity) // add our identity if it is not already contained

        val networkIdentityPlane = NetworkIdentityPlane(name, participants, UniqueIdentifier())
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val transactionBuilder = TransactionBuilder(notary = notary).apply {
            addOutputState(networkIdentityPlane)
            addCommand(NetworkIdentityPlaneContract.Commands.Create(), networkIdentityPlane.participants.map { it.owningKey })
        }

        transactionBuilder.verify(serviceHub)
        val partSignedTx = serviceHub.signInitialTransaction(transactionBuilder)

        val otherPartySessions = otherParticipants.map { initiateFlow(it) }
        val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, otherPartySessions))
        return subFlow(FinalityFlow(fullySignedTx, otherPartySessions))
    }
}

@InitiatedBy(CreateNetworkIdentityPlane::class)
class CreateNetworkIdentityPlaneResponder(
    private val otherPartySession: FlowSession
) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(otherPartySession) {
            override fun checkTransaction(stx: SignedTransaction) {
                val output = stx.tx.outputStates[0] as NetworkIdentityPlane
                checkPlaneExistence(output.name)
            }
        }

        val txId = subFlow(signTransactionFlow).id
        return subFlow(ReceiveFinalityFlow(otherPartySession, expectedTxId = txId))
    }
}

private fun FlowLogic<*>.checkPlaneExistence(name: String) {
    val allNetworkIdentityPlanes = subFlow(GetAllNetworkIdentityPlanes())
    val planeOrNull = allNetworkIdentityPlanes.firstOrNull { it.name == name }

    if (planeOrNull != null) throw FlowException("NetworkIdentityPlane $name already exists: $planeOrNull")
}
