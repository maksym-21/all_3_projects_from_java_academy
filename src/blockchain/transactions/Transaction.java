package blockchain.transactions;

public class Transaction {
    private final String from;
    private final String to;
    private final int sum;

    public Transaction(String from, String to, int sum) {
        this.from = from;
        this.to = to;
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "miner" + from +
                " sent " + sum + " VC to " +
                "miner" + to;
    }
}
