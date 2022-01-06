package hk.edu.polyu.af.bc.account.flows.user

import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane
import net.corda.core.flows.FlowException

class UserExistsException(val name: String, val plane: NetworkIdentityPlane):
                    FlowException("User already exists at plane $plane: name")