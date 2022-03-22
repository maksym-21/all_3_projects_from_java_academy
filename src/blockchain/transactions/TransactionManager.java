package blockchain.transactions;

import blockchain.block.Blockchain;
import blockchain.miner.Miner;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TransactionManager implements Runnable {
    private final List<Miner> miners;
    private final Blockchain blockchain;

    public TransactionManager(List<Miner> miners, Blockchain blockchain) {
        this.miners = miners;
        this.blockchain = blockchain;
    }

    private int generateNumber(int boundary) {
        return ThreadLocalRandom.current().nextInt(0, boundary);
    }

    @Override
    public void run() {
        while (true) {
            int from, to, sum;

            do {
                from = generateNumber(miners.size());
                to = generateNumber(miners.size());
            } while (from == to);

            Miner m1 = miners.get(from);
            Miner m2 = miners.get(to);

            sum = generateNumber(35);


            try {
                Thread.sleep(55);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            boolean flag = blockchain.acceptTransaction(new Transaction(String.valueOf(m1.getId()), String.valueOf(m2.getId()), sum));

            if (flag) break;
        }
    }
}
