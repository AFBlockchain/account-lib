package hk.edu.polyu.af.bc.account.flows.client

import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import hk.edu.polyu.af.bc.account.identity.CordappUser
import net.corda.core.flows.FlowLogic

/**
 * Create an [AccountInfo], and based on which populate the given [CordappUser]. The account created can be proactively
 * shared with other nodes on the network. This can be done via a configuration file.
 */
class CreateCordappUser(val cordappUser: CordappUser): FlowLogic<Unit>() {
    override fun call() {
        TODO("Not yet implemented")
    }
}