package blockchain.block;

import blockchain.transactions.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Block implements Serializable {
    private final int id;
    private final long timestamp;
    private final String previousHash;
    private final String ownHash;
    private final List<Transaction> data;

    private long spendTimeForGeneratingNs;
    private int magicNumber;
    private int zeroNumber;
    private String minerId, zeroNumberInfo;
    private int rewardForMinerForBlock;

    public Block(int id, Date timestamp, String previousHash, int zeroNumber, String ownHash) {
        this.id = id;
        this.timestamp = timestamp.getTime();
        this.previousHash = previousHash;
        this.zeroNumber = zeroNumber;
        this.ownHash = ownHash;

        this.data = Collections.synchronizedList(new ArrayList<>());
    }


    public synchronized void addTransaction(Transaction transaction) {
        if (transaction != null) this.data.add(transaction);
    }

    public synchronized List<Transaction> getData() {
        return data;
    }

    public synchronized void setMinerId(String minerId) {
        this.minerId = minerId;
    }

    public synchronized void setZeroNumberInfo(String zeroNumberInfo) {
        this.zeroNumberInfo = zeroNumberInfo;
    }

    public synchronized void setZeroNumber(int zeroNumber) {
        this.zeroNumber = zeroNumber;
    }

    public synchronized void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public synchronized void setSpendTimeForGeneratingNs(long spendTimeForGeneratingNs) {
        this.spendTimeForGeneratingNs = spendTimeForGeneratingNs;
    }

    public synchronized String getMinerId() {
        return minerId;
    }

    public synchronized String getZeroNumberInfo() {
        return zeroNumberInfo;
    }

    public synchronized long getSpendTimeForGenerating() {
        return spendTimeForGeneratingNs;
    }

    public synchronized int getMagicNumber() {
        return magicNumber;
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized long getTimestamp() {
        return timestamp;
    }

    public synchronized String getPreviousHash() {
        return previousHash;
    }

    public synchronized String getOwnHash() {
        return ownHash;
    }

    public synchronized int getZeroNumber() {
        return zeroNumber;
    }

    public synchronized void setRewardForMinerForBlock(int sum) {
        if (sum > 0) this.rewardForMinerForBlock = sum;
    }

    public synchronized int getRewardForMinerForBlock() {
        return rewardForMinerForBlock;
    }
}
