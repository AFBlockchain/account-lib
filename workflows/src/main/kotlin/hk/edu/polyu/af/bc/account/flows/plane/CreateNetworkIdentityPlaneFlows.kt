package hk.edu.polyu.af.bc.account.flows.plane

import co.paralleluniverse.fibers.Suspendable
import hk.edu.polyu.af.bc.account.contracts.NetworkIdentityPlaneContract
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

/// The flow hierarchy follows https://github.com/corda/accounts/blob/master/workflows/src/main/kotlin/com/r3/corda/lib/accounts/workflows/flows/RequestKeyForAccountFlows.kt

/**
 * In-lined subflow to create a [NetworkIdentityPlane]. This flow and its counter part [CreateNetworkIdentityPlaneResponderFlow]
 * should check that there is no [NetworkIdentityPlane] with the same `name` in the node's vaults.
 */
class CreateNetworkIdentityPlaneFlow(
    private val name: String,
    private val otherParticipants: List<Party>
): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val allNetworkIdentityPlanes = subFlow(GetAllNetworkIdentityPlanes())
        for(netWorkIdentityPlane in allNetworkIdentityPlanes) {
            require(netWorkIdentityPlane.name != name) {
                throw FlowException("The $name has already existed")
            }
        }

        //Create transaction

        val participants = ArrayList(otherParticipants)
        participants.add(ourIdentity)
        val networkIdentityPlane = NetworkIdentityPlane(name,participants as List<Party>, UniqueIdentifier())
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val transactionBuilder = TransactionBuilder(notary = notary).apply {
            addOutputState(networkIdentityPlane)
            addCommand(NetworkIdentityPlaneContract.Commands.Create(),networkIdentityPlane.participants.map{it.owningKey})
        }

        transactionBuilder.verify(serviceHub)

        //Sign transaction
        val partSignedTx = serviceHub.signInitialTransaction(transactionBuilder)


        val otherPartySessions = otherParticipants.map{initiateFlow(it)}
        val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, otherPartySessions))
        return subFlow(FinalityFlow(fullySignedTx, otherPartySessions))


    }
}

class CreateNetworkIdentityPlaneResponderFlow(
    private val otherPartySession: FlowSession
):FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(otherPartySession) {
            override fun checkTransaction(stx: SignedTransaction) {
                val allNetworkIdentityPlanes = subFlow(GetAllNetworkIdentityPlanes())
                for(netWorkIdentityPlane in allNetworkIdentityPlanes) {
                    val output = stx.tx.outputStates[0] as NetworkIdentityPlane
                    check(netWorkIdentityPlane.name != output.name) {throw FlowException("The ${output.name} has already existed")}
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
class CreateNetworkIdentityPlane(
    private val name: String,
    private val otherParticipants: List<Party>
): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        return subFlow(CreateNetworkIdentityPlaneFlow(name,otherParticipants))
    }
}

@InitiatedBy(CreateNetworkIdentityPlane::class)
class CreateNetworkIdentityPlaneResponder(
    private val otherPartySession: FlowSession
): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        return subFlow(CreateNetworkIdentityPlaneResponderFlow(otherPartySession))
    }
}