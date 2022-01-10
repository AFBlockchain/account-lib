package hk.edu.polyu.af.bc.account;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiPredicate;

public class Utils {
    public static Party party(CordaRPCOps proxy) {
        return proxy.nodeInfo().getLegalIdentities().get(0);
    }

    public static CordaRPCConnection getConnection(int port) {
        NetworkHostAndPort networkHostAndPort = new NetworkHostAndPort("localhost", port);
        CordaRPCClient client = new CordaRPCClient(networkHostAndPort);

        return client.start("user1", "test");
    }

    public static String getCordappPath() {
        Path projectBase = Paths.get(System.getProperty("user.dir")).getParent();
        Path appRel = Paths.get("cordapps");
        Path appAbs = projectBase.resolve(appRel);

        assert appAbs.toFile().exists();

        return appAbs.toString();
    }

    public static <T extends ContractState> T firstOutput(SignedTransaction tx) {
        return (T) tx.getCoreTransaction().getOutput(0);
    }

    public static <T extends ContractState> void assertHaveState(CordaRPCOps proxy, T state, BiPredicate<T, T> comparator) {
        boolean found = proxy.vaultQuery(state.getClass()).getStates().stream().anyMatch(stateAndRef -> comparator.test(state, (T) stateAndRef.getState().getData()));
        if (!found) throw new AssertionError(state + " not found in " + party(proxy));
    }
}
