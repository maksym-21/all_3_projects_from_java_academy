package blockchain.miner;

import blockchain.block.Block;
import blockchain.block.Blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Miner extends Thread{
    private final AtomicInteger account = new AtomicInteger(100);
    private final Blockchain blockchain;

    public Miner(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public synchronized void payMoney(int amount) {
        if (amount > 0) {
            int tmp = account.get();

            account.compareAndSet(tmp, tmp + amount);
        }
    }

    @Override
    public void run() {
        while (true) {
            long t1 = System.nanoTime();
            int magic = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);

            Block lastOne = blockchain.getLastBlock();

            int newId;
            String previousHash;
            if (lastOne != null) {
                newId = lastOne.getId() + 1;
                previousHash = lastOne.getOwnHash();
            } else {
                newId = 1;
                previousHash = "0";
            }

            String wholeDataOfBlock = newId + previousHash + magic;

            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                /* Applies sha256 to our input */
                byte[] hash = digest.digest(wholeDataOfBlock.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte elem : hash) {
                    String hex = Integer.toHexString(0xff & elem);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                long t2 = System.nanoTime();
                synchronized (this) {
                    int zeroNumber = blockchain.getActualZeroNumber();
                    if (hexString.toString().startsWith("0".repeat(zeroNumber))) {
                        Block candidate = new Block(newId, new Date(), previousHash, zeroNumber, hexString.toString());
                        candidate.setSpendTimeForGeneratingNs(t2 - t1);
                        candidate.setMagicNumber(magic);
                        candidate.setZeroNumber(zeroNumber);
                        candidate.setMinerId(String.valueOf(Thread.currentThread().getId()));

                        boolean flag = blockchain.proposeNewBlock(candidate, this);
                        if (flag) break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


