package hk.edu.polyu.af.bc.account.contracts

import hk.edu.polyu.af.bc.account.contracts.NetworkIdentityPlaneContract.Commands.Create
import hk.edu.polyu.af.bc.account.contracts.NetworkIdentityPlaneContract.Commands.Update
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.contracts.UniqueIdentifier
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test

class NetworkIdentityPlaneContractTest {
    @Test
    fun `creating a plane needs signatures from all parties`() {
        val plane = NetworkIdentityPlane("test-plane", listOf(identityA.party, identityB.party, identityC.party), UniqueIdentifier())

        ledgerService.ledger {
            transaction {
                output(NetworkIdentityPlaneContract.ID, plane)

                tweak { // with only A's signature
                    command(listOf(identityA.publicKey), Create())
                    fails()
                }
                tweak { // with A,B's signatures
                    command(listOf(identityA.publicKey, identityB.publicKey), Create())
                    fails()
                }

                command(listOf(identityA.publicKey, identityB.publicKey, identityC.publicKey), Create())
                verifies()
            }
        }
    }

    @Test
    fun `updating a plane needs signatures from all parties`() {
        ledgerService.ledger {
            val plane = transaction {
                output(NetworkIdentityPlaneContract.ID, "input", NetworkIdentityPlane("test-plane", listOf(identityA.party, identityB.party, identityC.party), UniqueIdentifier()))
                command(listOf(identityA.publicKey, identityB.publicKey, identityC.publicKey), Create())
                verifies()
            }.outputsOfType(NetworkIdentityPlane::class.java)[0].apply { name = "test-plane-2" }

            transaction {
                input("input")
                output(NetworkIdentityPlaneContract.ID, plane)

                tweak { // with only A's signature
                    command(listOf(identityA.publicKey), Update())
                    fails()
                }
                tweak { // with A,B's signatures
                    command(listOf(identityA.publicKey, identityB.publicKey), Update())
                    fails()
                }

                command(listOf(identityA.publicKey, identityB.publicKey, identityC.publicKey), Update())
                verifies()
            }
        }
    }
}
