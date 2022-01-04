package hk.edu.polyu.af.bc.account.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo
import hk.edu.polyu.af.bc.account.identity.CordappUser
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party

/// This package contains workflows for node-layer CordappUser management

/**
 * Create an [AccountInfo], and based on which populate the given [CordappUser]. The account created can be proactively
 * shared with other nodes on the network. This can be done via a configuration file.
 */
class CreateCordappUser(private val cordappUser: CordappUser): FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // create account
        val account = subFlow(CreateAccount(cordappUser.getAccountName())).state.data

        // TODO: account sharing according to configuration

        cordappUser.setAccountHostCordaX500Name(account.host.name.toString())
        cordappUser.setAccountUUID(account.identifier.id)

        logger.info("Account created for ${cordappUser.getAccountName()}: $cordappUser")
    }
}

/**
 * Share the underlying [AccountInfo] of the given [CordappUser] with `recipients`. This flow only ensures user identities
 * at the account level are shared and the `cordappUser` is not shared *per se*. For example, at the rpc side, the [CordappUser]
 * object needs to be shared across different hosts' `UserStore` for it to be discoverable.
 */
class ShareCordappUser(private val cordappUser: CordappUser,
                       private val recipients: List<Party>): FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val accountInfoStateAndRef = toAccountInfoStateAndRef(cordappUser)
        subFlow(ShareAccountInfo(accountInfoStateAndRef, recipients))

        logger.info("CordappUser shared with $recipients: $cordappUser")
    }
}