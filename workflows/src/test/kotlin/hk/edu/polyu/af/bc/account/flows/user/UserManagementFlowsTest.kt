package hk.edu.polyu.af.bc.account.flows.user

import hk.edu.polyu.af.bc.account.flows.*
import hk.edu.polyu.af.bc.account.flows.plane.CreateNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.flows.plane.SetCurrentNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse

class UserManagementFlowsTest: UnitTestBase() {
    @Before
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

    @Test(expected = UserExistsException::class)
    fun `cannot create users with the same name at the same node`() {
        partyA.startFlow(CreateUser("eva")).getOrThrow(network)
        partyA.startFlow(CreateUser("eva")).getOrThrow(network)
    }

    @Test(expected = UserExistsException::class)
    fun `cannot create users with the same name at different nodes`() {
        partyA.startFlow(CreateUser("fiona")).getOrThrow(network)
        partyB.startFlow(CreateUser("fiona")).getOrThrow(network)
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
    }
}