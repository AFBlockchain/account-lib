package hk.edu.polyu.af.bc.account.flows.plane

import hk.edu.polyu.af.bc.account.flows.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QueryNetworkIdentityPlaneFlowsTest : UnitTestBase() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(QueryNetworkIdentityPlaneFlowsTest::class.java)
    }

    @Test
    fun `should query all the planes`() {
        partyA.startFlow(CreateNetworkIdentityPlane("plane-1", listOf())).getOrThrow(network)
        partyA.startFlow(CreateNetworkIdentityPlane("plane-2", listOf())).getOrThrow(network)
        val allPlanes = partyA.startFlow(GetAllNetworkIdentityPlanes()).getOrThrow(network)
        val allNames = allPlanes.map{it.name}

        assertTrue(allNames.contains("plane-1"))
        assertTrue(allNames.contains("plane-2"))
    }

    @Test
    fun `can query by name`() {
        //TODO: abnormality check (e.g., no-such-plane)
        partyA.startFlow(CreateNetworkIdentityPlane("plane-3", listOf())).getOrThrow(network)
        val plane = partyA.startFlow(GetNetworkIdentityPlaneByName("plane-3")).getOrThrow(network)
        assertEquals("plane-3",plane!!.name)
    }
}