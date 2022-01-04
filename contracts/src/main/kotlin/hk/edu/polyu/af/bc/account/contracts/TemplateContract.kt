package hk.edu.polyu.af.bc.account.contracts

import hk.edu.polyu.af.bc.account.states.TemplateState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.contracts.requireThat

class TemplateContract : Contract {
    companion object {
        val ID: String = TemplateContract::class.java.canonicalName
    }

    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
        val command = tx.commands.requireSingleCommand<Commands.Create>()
        val output = tx.outputsOfType<TemplateState>().first()
        when (command.value) {
            is Commands.Create -> requireThat {
                "No inputs should be consumed when sending the Hello-World message.".using(tx.inputStates.isEmpty())
                "The message must be Hello-World".using(output.msg == "Hello-World")
            }
        }
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Create : Commands
    }
}