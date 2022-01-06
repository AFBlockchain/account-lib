package hk.edu.polyu.af.bc.account.flows

import net.corda.core.flows.FlowException
import net.corda.core.identity.Party

class PlaneExistsException(
    name: String,
    party: Party
): FlowException(message = "NetworkIdentityPlane already exists at ${party.name}: $name")