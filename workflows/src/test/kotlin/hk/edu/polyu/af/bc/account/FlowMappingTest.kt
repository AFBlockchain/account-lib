package hk.edu.polyu.af.bc.account

import co.paralleluniverse.fibers.Suspendable
import hk.edu.polyu.af.bc.account.flows.toAnonymousParty
import hk.edu.polyu.af.bc.account.identity.CordappUser
import hk.edu.polyu.af.bc.message.flows.SendMessage
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction

class SendMessageUser(private val sender: CordappUser,
                      private val receiver: CordappUser,
                      private val msg: String): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val senderId = toAnonymousParty(sender)
        val receiverId = toAnonymousParty(receiver)

        return subFlow(SendMessage(senderId, receiverId, msg))
    }
}