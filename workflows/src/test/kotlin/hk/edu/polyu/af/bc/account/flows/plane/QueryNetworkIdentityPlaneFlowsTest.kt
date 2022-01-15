package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.flows.*
import net.corda.core.flows.FlowException
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QueryNetworkIdentityPlaneFlowsTest : UnitTestBase() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(QueryNetworkIdentityPlaneFlowsTest::class.java)
    }

    @Test
    fun `should query all the plan`() {
        partyA.startFlow(CreateNetworkIdentityPlane("plane-1", listOf())).getOrThrow(network)
        partyA.startFlow(CreateNetworkIdentityPlane("plane-2", listOf())).getOrThrow(network)
        val allPlanes = partyA.startFlow(GetAllNetworkIdentityPlanes()).getOrThrow(network)
        val allNames = allPlanes.map{it.name}
        assertTrue(allNames.contains("plane-1"))
        assertTrue(allNames.contains("plane-2"))
    }

    @Test
    fun `can query by name`() {
        partyA.startFlow(CreateNetworkIdentityPlane("plane-1", listOf())).getOrThrow(network)
        val plane = partyA.startFlow(GetNetworkIdentityPlaneByName("plane-1")).getOrThrow(network)
        assertEquals("plane-1",plane.name)
    }

    @Test(expected = FlowException::class)
    fun `no name can be found`() {
        partyA.startFlow(CreateNetworkIdentityPlane("plane-1", listOf())).getOrThrow(network)
        partyA.startFlow(GetNetworkIdentityPlaneByName("plane-2")).getOrThrow(network)
    }
}