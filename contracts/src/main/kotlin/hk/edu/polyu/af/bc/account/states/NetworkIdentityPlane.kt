package hk.edu.polyu.af.bc.account.states

import hk.edu.polyu.af.bc.account.contracts.NetworkIdentityPlaneContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

/**
 * A [NetworkIdentityPlane] is an application-level identity cluster. Within the same plane (i.e., across all participating
 * nodes in the plane), the identities of users are shared and each of them enjoys a virtual vault. Usernames are unique
 * in a given plane.
 *
 * A node can participate in multiple [NetworkIdentityPlane], as along as there is no naming conflict for the planes. Different
 * planes *can* define users with the same username and their vaults will be separate and agnostic of each other.
 *
 * [NetworkIdentityPlane] is defined as a [LinearState]. The expectation is that this state can incorporates more metadata
 * in the future and they should be updatable.
 */
@BelongsToContract(NetworkIdentityPlaneContract::class)
class NetworkIdentityPlane(
    var name: String,
    private val partiesInPlane: List<Party>,
    override val linearId: UniqueIdentifier
): ContractState, LinearState {
    override val participants: List<AbstractParty>
        get() = partiesInPlane

    override fun toString(): String {
        return "NetworkIdentityPlane(name='$name', partiesInPlane=$partiesInPlane, linearId=$linearId)"
    }
}