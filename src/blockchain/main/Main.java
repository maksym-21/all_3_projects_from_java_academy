package blockchain.main;

import blockchain.block.Blockchain;
import blockchain.miner.Miner;
import blockchain.transactions.TransactionManager;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Blockchain blockchain = Blockchain.getInstance();

        ThreadPoolExecutor minerExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
        ThreadPoolExecutor transactionClientExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        for (int i = 0; i < 15; i++) blockchain.addMiner(new Miner(blockchain));

        List<Miner> blockchainMiners = blockchain.getMiners();

        for (int i = 0; i < 15; i++) {
            minerExecutor.execute(blockchainMiners.get(i));

            transactionClientExecutor.execute(new TransactionManager(blockchainMiners, blockchain));
        }

        minerExecutor.shutdown();
        transactionClientExecutor.shutdown();

        try {
            if (!minerExecutor.awaitTermination(14, TimeUnit.SECONDS))
                minerExecutor.shutdownNow();

            if (!transactionClientExecutor.awaitTermination(12,TimeUnit.SECONDS))
                transactionClientExecutor.shutdownNow();

        } catch (InterruptedException ex) {
            minerExecutor.shutdownNow();
            transactionClientExecutor.shutdownNow();

            Thread.currentThread().interrupt();
        }

        blockchain.showBlockchainInfo();
    }
}
