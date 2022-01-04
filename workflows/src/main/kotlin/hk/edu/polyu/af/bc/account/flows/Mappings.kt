package hk.edu.polyu.af.bc.account.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import hk.edu.polyu.af.bc.account.identity.CordappUser
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.AnonymousParty
import net.corda.core.identity.CordaX500Name

fun FlowLogic<*>.toAccountInfo(cordappUser: CordappUser): AccountInfo {
    val x500Name = CordaX500Name.parse(cordappUser.getAccountHostCordaX500Name())
    val host = serviceHub.identityService.wellKnownPartyFromX500Name(x500Name) ?:
            throw FlowException("Cannot identify party given name: ${cordappUser.getAccountHostCordaX500Name()}")
    val uid = UniqueIdentifier(id=cordappUser.getAccountUUID())

    return AccountInfo(cordappUser.getAccountName(), host, uid)
}

@Suspendable
fun FlowLogic<*>.toAnonymousParty(accountInfo: AccountInfo): AnonymousParty {
    return subFlow(RequestKeyForAccount(accountInfo))
}

@Suspendable
fun FlowLogic<*>.toAnonymousParty(cordappUser: CordappUser): AnonymousParty {
    return toAnonymousParty(toAccountInfo(cordappUser))
}