package hk.edu.polyu.af.bc.account

import com.github.manosbatsis.corda.testacles.nodedriver.NodeHandles
import com.github.manosbatsis.corda.testacles.nodedriver.config.NodeDriverNodesConfig
import com.github.manosbatsis.corda.testacles.nodedriver.jupiter.NodeDriverExtensionConfig
import com.github.manosbatsis.corda.testacles.nodedriver.jupiter.NodeDriverNetworkExtension
import hk.edu.polyu.af.bc.account.flows.output
import hk.edu.polyu.af.bc.account.flows.plane.CreateNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.flows.plane.GetAllNetworkIdentityPlanes
import hk.edu.polyu.af.bc.account.flows.plane.GetCurrentNetworkIdentityPlane
import hk.edu.polyu.af.bc.account.flows.plane.SetCurrentNetworkIdentityPlaneByName
import hk.edu.polyu.af.bc.account.flows.planeComparator
import hk.edu.polyu.af.bc.account.flows.user.CreateUser
import hk.edu.polyu.af.bc.account.flows.user.IsUserExists
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowException
import net.corda.core.messaging.CordaRPCOps
import net.corda.testing.driver.NodeHandle
import net.corda.testing.node.StartedMockNode
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(NodeDriverNetworkExtension::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DriverBasedTest {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(DriverBasedTest::class.java)

        @NodeDriverExtensionConfig
        @JvmStatic
        val nodeDriverConfig: NodeDriverNodesConfig = customNodeDriverConfig
    }

    lateinit var proxyA: CordaRPCOps
    lateinit var proxyB: CordaRPCOps

    @Test
    @Order(1)
    fun setUp(nodeHandles: NodeHandles) {
        proxyA = nodeHandles.getNode("partyA").rpc
        proxyB = nodeHandles.getNode("partyB").rpc
    }

    @Test
    @Order(2)
    fun createPlane(nodeHandles: NodeHandles) {
        val tx = proxyA.startFlowDynamic(CreateNetworkIdentityPlane::class.java, "plane1", listOf(proxyB.party()))
            .returnValue.get()
        logger.info("Transaction: {}", tx)
        val plane = tx.output(NetworkIdentityPlane::class.java)
        logger.info("Plane: {}", plane)

        proxyA.assertHaveState(plane, planeComparator)
        proxyB.assertHaveState(plane, planeComparator)
    }

    @Test
    @Order(3)
    fun setPlane(nodeHandles: NodeHandles) {
        proxyA.startFlowDynamic(SetCurrentNetworkIdentityPlaneByName::class.java, "plane1").returnValue.get()
        proxyB.startFlowDynamic(SetCurrentNetworkIdentityPlaneByName::class.java, "plane1").returnValue.get()

        assert(proxyA.startFlowDynamic(GetCurrentNetworkIdentityPlane::class.java).returnValue.get()!!.name == "plane1")
        assert(proxyB.startFlowDynamic(GetCurrentNetworkIdentityPlane::class.java).returnValue.get()!!.name == "plane1")
    }

    @Test
    @Order(4)
    fun createUsers(nodeHandles: NodeHandles) {
        proxyA.startFlowDynamic(CreateUser::class.java, "alice").returnValue.get()
        proxyB.startFlowDynamic(CreateUser::class.java, "bob").returnValue.get()

        assert(proxyA.startFlowDynamic(IsUserExists::class.java, "alice").returnValue.get())
        assert(proxyA.startFlowDynamic(IsUserExists::class.java, "bob").returnValue.get())
        assert(proxyB.startFlowDynamic(IsUserExists::class.java, "alice").returnValue.get())
        assert(proxyB.startFlowDynamic(IsUserExists::class.java, "bob").returnValue.get())
    }

    @Test
    @Order(5)
    fun cannotCreateSameUser(nodeHandles: NodeHandles) {
        assertThrows<Exception> {
            proxyA.startFlowDynamic(CreateUser::class.java, "bob").returnValue.get()
        }
    }

    @Test
    @Order(6)
    fun canCreateSameUserInAnotherPlane(nodeHandles: NodeHandles) {
        proxyA.startFlowDynamic(CreateNetworkIdentityPlane::class.java, "plane2", listOf(proxyB.party()))
            .returnValue.get()
        proxyA.startFlowDynamic(SetCurrentNetworkIdentityPlaneByName::class.java, "plane2").returnValue.get()
        proxyB.startFlowDynamic(SetCurrentNetworkIdentityPlaneByName::class.java, "plane2").returnValue.get()

        assertFalse(proxyA.startFlowDynamic(IsUserExists::class.java, "bob").returnValue.get()) // no bob here
        assertFalse(proxyB.startFlowDynamic(IsUserExists::class.java, "bob").returnValue.get()) // no bob here

        proxyA.startFlowDynamic(CreateUser::class.java, "alice").returnValue.get()
        assert(proxyA.startFlowDynamic(IsUserExists::class.java, "alice").returnValue.get())
        assert(proxyB.startFlowDynamic(IsUserExists::class.java, "alice").returnValue.get())
    }

    @Test
    @Order(7)
    fun canQueryPlanes(nodeHandles: NodeHandles) {
        listOf(proxyA, proxyB).forEach {
            val planes = it.startFlowDynamic(GetAllNetworkIdentityPlanes::class.java).returnValue.get().map { it.name }
            assertTrue(planes.containsAll(listOf("plane1", "plane2")))
        }
    }
}