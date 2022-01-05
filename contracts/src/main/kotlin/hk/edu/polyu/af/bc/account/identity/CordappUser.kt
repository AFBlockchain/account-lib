package hk.edu.polyu.af.bc.account.identity

import java.util.*

/**
 * Username of a [CordappUser] is unique at host-level, while uuid is unique at network level. When creating a user, the
 * calling node should make sure it has no users with the same `accountName`, whether hosted by itself or other nodes. It
 * might happen that multiple users have the same name, when more information is required to determine which one is the
 * intended user. This can be solved if there is a *global user store* with which every node must check against when
 * creating a user.
 */
interface CordappUser {
    fun getAccountName(): String
    fun getAccountHostCordaX500Name(): String?
    fun setAccountHostCordaX500Name(name: String)
    fun getAccountUUID(): UUID?
    fun setAccountUUID(uuid: UUID)
}