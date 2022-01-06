package hk.edu.polyu.af.bc.account.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByUUID
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import hk.edu.polyu.af.bc.account.user.CordappUser
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.AnonymousParty
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party

/// This package contains utilities for defining application-level workflows using CordappUser for the user identity


/**
 * Map the given [CordappUser] to the underlying [AccountInfo]. This needs to be carried out in the context of a flow
 * since a [Party] needs to be resolved from its [CordaX500Name] using the flow's serviceHub (IdentityService)
 */
fun FlowLogic<*>.toAccountInfo(cordappUser: CordappUser): AccountInfo {
    //TODO: Null check
    val x500Name = CordaX500Name.parse(cordappUser.getAccountHostCordaX500Name()!!)
    val host = serviceHub.identityService.wellKnownPartyFromX500Name(x500Name) ?:
            throw FlowException("Cannot identify party given name: ${cordappUser.getAccountHostCordaX500Name()}")
    val uid = UniqueIdentifier(id=cordappUser.getAccountUUID()!!)

    return AccountInfo(cordappUser.getAccountName(), host, uid)
}

/**
 * Map the given [AccountInfo] to an [AnonymousParty]. This is done by requesting a new key for the account.
 */
@Suspendable
fun FlowLogic<*>.toAnonymousParty(accountInfo: AccountInfo): AnonymousParty {
    return subFlow(RequestKeyForAccount(accountInfo))
}

/**
 * Directly map the given [CordappUser] to an [AnonymousParty] usable in smart contracts. A new identity (i.e., key pair)
 * is created everytime this method is called.
 */
@Suspendable
fun FlowLogic<*>.toAnonymousParty(cordappUser: CordappUser): AnonymousParty {
    return toAnonymousParty(toAccountInfo(cordappUser))
}

/**
 * Map the given [CordappUser] to [StateAndRef] of the underlying [AccountInfo] to gain more information about the account.
 */
fun FlowLogic<*>.toAccountInfoStateAndRef(cordappUser: CordappUser): StateAndRef<AccountInfo> {
    return subFlow(AccountInfoByUUID(cordappUser.getAccountUUID()!!))!!
}