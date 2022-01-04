package hk.edu.polyu.af.bc.account.identity

import java.util.*

interface CordappUser {
    fun getAccountName(): String
    fun getAccountHostCordaX500Name(): String?
    fun setAccountHostCordaX500Name(name: String)
    fun getAccountUUID(): UUID?
    fun setAccountUUID(uuid: UUID)
}