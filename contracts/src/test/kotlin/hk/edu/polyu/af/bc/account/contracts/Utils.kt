package hk.edu.polyu.af.bc.account.contracts

import net.corda.core.identity.CordaX500Name
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices

/**
 * Identities
 */
val identityA = TestIdentity(CordaX500Name("PartyA", "London", "GB"))
val identityB = TestIdentity(CordaX500Name("PartyB", "London", "GB"))
val identityC = TestIdentity(CordaX500Name("PartyC", "London", "GB"))

/**
 * Package level mock service
 */
val ledgerService = MockServices(
    listOf("hk.edu.polyu.af.bc.account"),
    identityA,
    testNetworkParameters(minimumPlatformVersion = 4),
    identityB, identityC
)