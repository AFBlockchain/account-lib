package hk.edu.polyu.af.bc.account.contracts

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
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
        TODO("Not yet implemented")
    }
}