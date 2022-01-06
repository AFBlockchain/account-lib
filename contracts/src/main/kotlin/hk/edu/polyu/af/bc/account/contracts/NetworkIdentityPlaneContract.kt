package hk.edu.polyu.af.bc.account.contracts

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

/**
 * A [NetworkIdentityPlane] can be `Created` and `Updated`.
 *
 * For creation, all parties need to sign. The precondition for a party to sign is that he does not know of any other
 * [NetworkIdentityPlane] with the same name. For update, currently only the name of the plane can be changed. All parties
 * need to sign and the precondition is the same.
 *
 * The check for uniqueness cannot be enforced by the contract as it cannot access the node's vault. Flow level check instead
 * should be carried out.
 */
class NetworkIdentityPlaneContract: Contract {
    companion object {
        val ID: String = NetworkIdentityPlaneContract::class.java.canonicalName
    }

    interface Commands: CommandData {
        class Create(): Commands
        class Update(): Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()

        when(command.value) {
            is Commands.Create -> requireThat {
                val output = tx.outputsOfType<NetworkIdentityPlane>().single()
                "All parties must be signers." using (command.signers.containsAll(output.participants.map {it.owningKey}))
            }

            is Commands.Update -> requireThat {
                val output = tx.outputsOfType<NetworkIdentityPlane>().single()
                val input = tx.inputsOfType<NetworkIdentityPlane>().single()
                "All parties must be signers." using (command.signers.containsAll(output.participants.map {it.owningKey}))
                "Name must be changed." using (input.name != output.name)
                "Parties can not be changed." using (input.participants.size == output.participants.size && input.participants.containsAll(output.participants))
                "LinerId can not be changed." using (input.linearId == output.linearId)
            }
        }

    }
}