package tech.pegasys.support.pan3027;

import tech.pegasys.pantheon.Pantheon;

import java.io.File;
import java.util.Arrays;

import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.simplestorage.SimpleStorage;
import org.web3j.simplestorage2.SimpleStorage2;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

public class Main {
  private static final Logger LOG = LogManager.getLogger();

  private static String PRIVATE_KEY_1 =
      "8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63";

  public static void main(final String[] args) throws Exception {
    final File dataPath = Files.createTempDir();

    final int jsonRpcPort = 18545;

    startPantheon(dataPath, jsonRpcPort);

    try {
      Thread.sleep(5000); // startup

      final Web3j web3j = Web3j.build(new HttpService("http://127.0.0.1:" + jsonRpcPort));
      LOG.info(
          "Connected to Ethereum client version: "
              + web3j.web3ClientVersion().send().getWeb3ClientVersion());

      final Credentials credentials = Credentials.create(PRIVATE_KEY_1);

      final ContractGasProvider contractGasProvider = new DefaultGasProvider();
      final SimpleStorage simpleStorage =
          SimpleStorage.deploy(web3j, credentials, contractGasProvider).send();

      final String contractAddress = simpleStorage.getContractAddress();
      LOG.info("SimpleStorage address " + contractAddress);

      final TransactionReceipt setContractTx = simpleStorage.setContract().send();
      LOG.info("SimpleStorage.setContract result {}", setContractTx.getStatus());

      final String address = simpleStorage.getAddress().send();
      LOG.info("Created contract address {}", address);

      final SimpleStorage2 ss2 =
          SimpleStorage2.load(address, web3j, credentials, contractGasProvider);

      final byte[] first = new byte[32];
      final byte[] second = new byte[32];
      Arrays.fill(first, (byte) 1);
      Arrays.fill(second, (byte) 2);
      final TransactionReceipt setTx = ss2.set(first, second).send();
      LOG.info("SimpleStorage2.set result {}", setTx.getStatus());

      final var result = ss2.get().send();
      LOG.info(
          "SimpleStorage2.get result {}, {}",
          Numeric.toHexStringNoPrefix(result.getValue1()),
          Numeric.toHexStringNoPrefix(result.getValue2()));
    } finally {
      System.exit(0);
    }
  }

  private static void startPantheon(final File dataPath, final int jsonRpcPort) {
    final Thread pantheon =
        new Thread(
            () ->
                Pantheon.main(
                    "--network=dev",
                    "--p2p-enabled=false",
                    "--miner-enabled",
                    "--logging=trace",
                    "--miner-coinbase=f17f52151EbEF6C7334FAD080c5704D77216b732",
                    "--data-path=" + dataPath.getAbsolutePath(),
                    "--rpc-http-enabled",
                    "--rpc-http-port=" + jsonRpcPort));
    pantheon.start();
  }
}
