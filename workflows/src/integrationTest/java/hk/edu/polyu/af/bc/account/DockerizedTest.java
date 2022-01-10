package hk.edu.polyu.af.bc.account;

import hk.edu.polyu.af.bc.account.flows.plane.CreateNetworkIdentityPlane;
import hk.edu.polyu.af.bc.account.states.NetworkIdentityPlane;
import net.corda.core.transactions.SignedTransaction;
import org.junit.Ignore;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.ExecutionException;
import java.util.function.BiPredicate;

import static com.google.common.collect.Lists.newArrayList;
import static hk.edu.polyu.af.bc.account.Utils.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Ignore("Bug: container cannot start")
public class DockerizedTest extends DockerizedTestBase {
    @Test
    @Order(1)
    public void createPlaneForAllParties() throws ExecutionException, InterruptedException {
        SignedTransaction tx = partyA.startFlowDynamic(CreateNetworkIdentityPlane.class, "test-plane", newArrayList(party(partyB), party(partyC)))
                .getReturnValue().get();
        NetworkIdentityPlane plane = firstOutput(tx);

        assertHaveState(partyA, plane, planeComparator);
        assertHaveState(partyB, plane, planeComparator);
        assertHaveState(partyC, plane, planeComparator);
    }

    private final BiPredicate<NetworkIdentityPlane, NetworkIdentityPlane> planeComparator = ((networkIdentityPlane, networkIdentityPlane2) ->
            networkIdentityPlane.getLinearId().equals(networkIdentityPlane2.getLinearId()));
}
