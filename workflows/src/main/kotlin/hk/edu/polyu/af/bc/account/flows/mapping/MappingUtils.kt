package hk.edu.polyu.af.bc.account.flows.mapping

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.AnonymousParty

/// This package contains utilities for defining application-level workflows using CordappUser for the user identity


/**
 * Query the underlying [AccountInfo] from vault.
 */
fun FlowLogic<*>.toAccountInfo(username: String): AccountInfo {
    TODO()
}

/**
 * Map the given [AccountInfo] to an [AnonymousParty]. This is done by requesting a new key for the account.
 */
@Suspendable
fun FlowLogic<*>.toAnonymousParty(accountInfo: AccountInfo): AnonymousParty {
    return subFlow(RequestKeyForAccount(accountInfo))
}

/**
 * Directly map the given user to an [AnonymousParty] usable in smart contracts. A new identity (i.e., key pair)
 * is created everytime this method is called.
 */
@Suspendable
fun FlowLogic<*>.toAnonymousParty(username: String): AnonymousParty {
    TODO()
}

/**
 * Map the given user to [StateAndRef] of the underlying [AccountInfo] to gain more information about the account.
 */
fun FlowLogic<*>.toAccountInfoStateAndRef(username: String): StateAndRef<AccountInfo> {
    TODO()
}