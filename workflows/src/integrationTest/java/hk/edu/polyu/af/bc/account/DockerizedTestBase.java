package hk.edu.polyu.af.bc.account;

import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.messaging.CordaRPCOps;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static hk.edu.polyu.af.bc.account.Utils.getConnection;
import static hk.edu.polyu.af.bc.account.Utils.getCordappPath;


@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DockerizedTestBase {
    public static final String IMAGE_NAME="corda-four-nodes:0.1.1"; // TODO: change reference to docker hub instead of locally-built image
    protected final static Logger logger = LoggerFactory.getLogger(DockerizedTest.class);

    private CordaRPCConnection notaryConnection;
    private CordaRPCConnection partyAConnection;
    private CordaRPCConnection partyBConnection;
    private CordaRPCConnection partyCConnection;

    protected CordaRPCOps notary;
    protected CordaRPCOps partyA;
    protected CordaRPCOps partyB;
    protected CordaRPCOps partyC;

    @Container
    @SuppressWarnings("rawtypes")
    public static GenericContainer network = new GenericContainer(DockerImageName.parse(IMAGE_NAME))
            .withFileSystemBind(getCordappPath(), "/nodes/Notary/cordapps", BindMode.READ_WRITE)
            .withFileSystemBind(getCordappPath(), "/nodes/PartyA/cordapps", BindMode.READ_WRITE)
            .withFileSystemBind(getCordappPath(), "/nodes/PartyB/cordapps", BindMode.READ_WRITE)
            .withFileSystemBind(getCordappPath(), "/nodes/PartyC/cordapps", BindMode.READ_WRITE)
            .withExposedPorts(10010, 10011, 10012, 10013)
            .withStartupTimeout(Duration.ofSeconds(180));

    @BeforeAll
    public void setUp() {
        notaryConnection = getConnection(network.getMappedPort(10010));
        partyAConnection = getConnection(network.getMappedPort(10011));
        partyBConnection = getConnection(network.getMappedPort(10012));
        partyCConnection = getConnection(network.getMappedPort(10013));

        notary = notaryConnection.getProxy();
        partyA = partyAConnection.getProxy();
        partyB = partyBConnection.getProxy();
        partyC = partyCConnection.getProxy();
    }

    @AfterAll
    public void shutDown() {
        notaryConnection.close();
        partyAConnection.close();
        partyBConnection.close();
        partyCConnection.close();
    }
}
