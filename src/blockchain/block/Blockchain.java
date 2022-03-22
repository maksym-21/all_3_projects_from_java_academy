package blockchain.block;

import blockchain.miner.Miner;
import blockchain.transactions.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Blockchain {
    private static final Blockchain instance = new Blockchain();

    private static volatile List<Block> blockchain = Collections.synchronizedList(new LinkedList<>());
    private final List<Miner> miners = Collections.synchronizedList(new ArrayList<>());

    private final BlockingQueue<Transaction> transactions_storage = new ArrayBlockingQueue<>(10_000);
    private final AtomicInteger zeroNumber = new AtomicInteger(0);


    private Blockchain() {
        blockchain = BlockTools.checkExistingOnHardDrive();
    }

    public static synchronized Blockchain getInstance(){
        return instance;
    }


    public synchronized boolean proposeNewBlock(Block block, Miner miner){
        // 1 - check last hash with previous of new block
        // 2 - check if n first zeros are in hash of block
        if (blockchain.isEmpty()) {
            if (!block.getPreviousHash().equals("0") || block.getZeroNumber() != zeroNumber.get()) return false;
        } else {
            if (!block.getPreviousHash().equals(getLastBlock().getOwnHash()) || block.getZeroNumber() != zeroNumber.get()) {
                return false;
            }
        }

        // update info about number of zeros
        getActualNumberOfZeros(block);


        block.setRewardForMinerForBlock(100);
        miner.payMoney(100);


        // get all messages which are not in block yet and add 'em to new block
        while (!transactions_storage.isEmpty()) {
            try {
                block.addTransaction(transactions_storage.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        blockchain.add(block);

        return true;
    }

    public synchronized boolean acceptTransaction(Transaction transaction) {
        boolean flag = true;

        try {
            transactions_storage.add(transaction);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());

            flag = false;
        }

        return flag;
    }

    public void addMiner(Miner miner) {
        if (miner != null) miners.add(miner);
    }

    private synchronized void getActualNumberOfZeros(Block block){
        // if time of creation was short -  we can increase a number of zeros:
        //
        // if previous_time < 3 * this_time - decrease a time (in ms)
        // else - it remains the same
        int tmp = zeroNumber.get();
        if (blockchain.size() == 1 || getLastBlock() == null){
            zeroNumber.compareAndSet(tmp, tmp + 1);

            block.setZeroNumberInfo("N was increased to " + (tmp + 1));
        }
        else {
            long previous = blockchain.get(blockchain.size() - 2).getSpendTimeForGenerating();
            long last = getLastBlock().getSpendTimeForGenerating();

            if (last == 0 || previous == 0){
                //zeroNumber.compareAndSet(tmp, tmp + 1);

                block.setZeroNumberInfo("N was increased to " + (tmp + 1));
            }
            else {
                if (previous * 3 <= last){
                    block.setZeroNumberInfo("N was decreased by 1");

                    zeroNumber.compareAndSet(tmp, tmp - 1);
                }
                else {
                    block.setZeroNumberInfo("N stays the same");
                }
            }
        }
    }

    public int getActualZeroNumber(){
        return zeroNumber.get();
    }

    public synchronized Block getLastBlock(){
        if (blockchain.isEmpty()) return null;
        else return blockchain.get(blockchain.size() - 1 );
    }

    public List<Miner> getMiners() {
        return miners;
    }

    // info method about state of blockchain
    public final void showBlockchainInfo() {
        for (Block block : blockchain) {
            System.out.println("Block:");
            System.out.println("Created by miner # " + block.getMinerId());
            System.out.println("miner" + block.getMinerId() +"gets " + block.getRewardForMinerForBlock() + " VC");
            System.out.println("Id: " + block.getId());
            System.out.println("Timestamp: " + block.getTimestamp());
            System.out.println("Magic number: " + block.getMagicNumber());
            System.out.println("Hash of the previous block:\n" + block.getPreviousHash());
            System.out.println("Hash of the block:\n" + block.getOwnHash());
            System.out.println("Block data:");

            if (block.getData().isEmpty()) System.out.println("No transactions");
            else block.getData().forEach(System.out::println);

            System.out.println("Block was being generated for " + block.getSpendTimeForGenerating() + " nanoseconds");
            System.out.println(block.getZeroNumberInfo());
            System.out.println();
        }
    }
}

