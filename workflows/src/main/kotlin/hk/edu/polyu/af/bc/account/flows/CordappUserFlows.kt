package hk.edu.polyu.af.bc.account.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo
import hk.edu.polyu.af.bc.account.user.CordappUser
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party

/// This package contains workflows for node-layer CordappUser management
/// parallel with [UserDetailsManager] from Spring Security (https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/provisioning/UserDetailsManager.html)
/// No need to manage credentials since this is only carried out at the rpc side

/**
 * Create an [AccountInfo], and based on which populate the given [CordappUser]. The account created can be proactively
 * shared with other nodes on the network. This can be done via a configuration file.
 *
 * The created [CordappUser] *should* be visible for the entire business network.
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

/**
 * Fetch information about a [CordappUser] at the node level. The returned instance is of an anonymous class, which severs
 * as a container for node-level user information.
 */
class GetCordappUserByName(private val accountName: String): FlowLogic<CordappUser?>() {
    override fun call(): CordappUser? {
        TODO("Not yet implemented")
    }
}

/**
 * The behaviour of this check is consistent with whether [CreateCordappUser] can successfully create the user. If this
 * flow returns `true`, then a [CordappUser] can be created.
 *
 * The recommended practice is to assume an *Application Network* within which the account name is unique. The network
 * can be defined either hard-coded or with a configuration file.
 */
class CheckCordappUserExistence(private val accountName: String): FlowLogic<Boolean>() {
    override fun call(): Boolean {
        TODO("Not yet implemented")
    }
}