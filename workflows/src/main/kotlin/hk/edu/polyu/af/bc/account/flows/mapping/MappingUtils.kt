package hk.edu.polyu.af.bc.account.flows.mapping

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByName
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import hk.edu.polyu.af.bc.account.flows.user.toAccountName
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.AnonymousParty
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/// This package contains utilities for defining application-level workflows using CordappUser for the user identity


/**
 * Query the underlying [AccountInfo] from vault.
 */
fun FlowLogic<*>.toAccountInfo(username: String): AccountInfo? {
    return toAccountInfoRef(username)?.state?.data
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
    val accountInfo = toAccountInfo(username) ?: throw IllegalArgumentException("User not found: $username")
    return toAnonymousParty(accountInfo)
}

/**
 * Map the given user to [StateAndRef] of the underlying [AccountInfo] to gain more information about the account.
 */
fun FlowLogic<*>.toAccountInfoRef(username: String): StateAndRef<AccountInfo>? {
    val accountName = toAccountName(username)
    val ret = subFlow(AccountInfoByName(accountName))

    if (ret.size > 1) throw IllegalStateException("Found ${ret.size} accounts with name $username; Expected 0 or 1")
    return ret.firstOrNull()
}