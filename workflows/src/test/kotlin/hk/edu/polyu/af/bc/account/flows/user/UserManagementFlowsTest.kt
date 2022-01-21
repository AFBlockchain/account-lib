package hk.edu.polyu.af.bc.account.flows.user

import hk.edu.polyu.af.bc.account.flows.UnitTestBase
import hk.edu.polyu.af.bc.account.flows.getOrThrow
import hk.edu.polyu.af.bc.account.flows.output
import hk.edu.polyu.af.bc.account.flows.party
import hk.edu.polyu.af.bc.account.flows.plane.CreateNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.flows.plane.GetCurrentNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.flows.plane.SetCurrentNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.flows.plane.SetCurrentNetworkIdentityPlaneByName
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowException
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserManagementFlowsTest : UnitTestBase() {
    @BeforeAll
    override fun setup() {
        super.setup()

        // create a test plane and set it for the entire test
        val plane = partyA.startFlow(CreateNetworkIdentityPlane("test-plane", listOf(partyB.party(), partyC.party())))
            .getOrThrow(network)
            .output(NetworkIdentityPlane::class.java)

        partyA.startFlow(SetCurrentNetworkIdentityPlane(plane)).getOrThrow(network)
        partyB.startFlow(SetCurrentNetworkIdentityPlane(plane)).getOrThrow(network)
        partyC.startFlow(SetCurrentNetworkIdentityPlane(plane)).getOrThrow(network)
    }

    @Test
    fun `can create users at three parties`() {
        partyA.startFlow(CreateUser("alice")).getOrThrow(network)
        partyB.startFlow(CreateUser("bob")).getOrThrow(network)
        partyC.startFlow(CreateUser("charlie")).getOrThrow(network)

        assert(partyA.startFlow(IsUserExists("alice")).getOrThrow(network))
        assert(partyB.startFlow(IsUserExists("bob")).getOrThrow(network))
        assert(partyC.startFlow(IsUserExists("charlie")).getOrThrow(network))
    }

    @Test
    fun `parties in the same plane should be aware of the created users`() {
        partyA.startFlow(CreateUser("dilan")).getOrThrow(network)

        assert(partyB.startFlow(IsUserExists("dilan")).getOrThrow(network))
        assert(partyC.startFlow(IsUserExists("dilan")).getOrThrow(network))
    }

    @Test
    fun `cannot create users with the same name at the same node`() {
        partyA.startFlow(CreateUser("eva")).getOrThrow(network)

        assertThrows<FlowException> {
            partyA.startFlow(CreateUser("eva")).getOrThrow(network)
        }
    }

    @Test
    fun `cannot create users with the same name at different nodes`() {
        partyA.startFlow(CreateUser("fiona")).getOrThrow(network)
        assertThrows<FlowException> {
            partyB.startFlow(CreateUser("fiona")).getOrThrow(network)
        }
    }

    @Test
    fun `users with the same name at different planes should not know each other`() {
        partyA.startFlow(CreateUser("gina")).getOrThrow(network)

        // use another plane
        val plane = partyA.startFlow(CreateNetworkIdentityPlane("another-plane", listOf(partyB.party(), partyC.party())))
            .getOrThrow(network)
            .output(NetworkIdentityPlane::class.java)
        partyA.startFlow(SetCurrentNetworkIdentityPlane(plane))

        assertFalse(partyA.startFlow(IsUserExists("gina")).getOrThrow(network))

        partyA.startFlow(SetCurrentNetworkIdentityPlaneByName("test-plane")) // restore
    }

    @Test
    @Disabled(
        "This test fails because the mock network uses the same address space for all mock nodes. Thus all" +
            "nodes are in the same plane all the time"
    )
    fun `different parties can be in different planes`() {
        partyA.startFlow(CreateNetworkIdentityPlane("for-a", listOf())).getOrThrow(network)
        partyA.startFlow(SetCurrentNetworkIdentityPlaneByName("for-a"))

        partyB.startFlow(CreateNetworkIdentityPlane("for-b", listOf())).getOrThrow(network)
        partyB.startFlow(SetCurrentNetworkIdentityPlaneByName("for-b"))

        partyC.startFlow(CreateNetworkIdentityPlane("for-c", listOf())).getOrThrow(network)
        partyC.startFlow(SetCurrentNetworkIdentityPlaneByName("for-c"))

        assert(partyA.startFlow(GetCurrentNetworkIdentityPlane()).getOrThrow(network)!!.name == "for-a")
        assert(partyB.startFlow(GetCurrentNetworkIdentityPlane()).getOrThrow(network)!!.name == "for-b")
        assert(partyC.startFlow(GetCurrentNetworkIdentityPlane()).getOrThrow(network)!!.name == "for-c")

        partyA.startFlow(SetCurrentNetworkIdentityPlaneByName("test-plane"))
        partyB.startFlow(SetCurrentNetworkIdentityPlaneByName("test-plane"))
        partyC.startFlow(SetCurrentNetworkIdentityPlaneByName("test-plane"))
    }
}
