package aleksirasio.advancedalgorithms.lab06.multithread;

import aleksirasio.advancedalgorithms.lab06.bruteforce.Item;
import aleksirasio.advancedalgorithms.lab06.bruteforce.Solution;

import java.math.BigInteger;
import java.util.TreeSet;

public class MultithreadKnapsack extends Thread {
    // lock object for synchronization across threads
    // private static final is best, as the object is not exposed outside class
    private static final Object countLock = new Object();

    // static counters
    static BigInteger current = BigInteger.ZERO, total = BigInteger.ZERO;

    private Item[] items;
    private TreeSet<Solution> solutions;
    private final int c, threadId;
    private boolean combinationsCalculated;

    MultithreadKnapsack(Item[] items, int c, int threadId) {
        super("bruteForceThread" + threadId);
        this.items = items;
        this.c = c;
        this.threadId = threadId;
        combinationsCalculated = false;
    }

    private void bruteforce() {
        solutions = new TreeSet<>();

        // create leading binary string for this thread
        int threadBits = Integer.SIZE - Integer.numberOfLeadingZeros(Runtime.getRuntime().availableProcessors() - 1);
        String threadBinary = String.format("%" + threadBits + "s", Integer.toBinaryString(threadId)).replace(' ', '0');

        // count binary string length to be solved by this thread
        int solvableBinaryLength = items.length - threadBits;

        // count number of combinations for this thread to solve
        BigInteger combinations = BigInteger.valueOf(2).pow(solvableBinaryLength);

        addTotal(combinations);

        combinationsCalculated = true;

        // start brute forcing all possible combinations
        for (BigInteger i = BigInteger.ZERO; i.compareTo(combinations) < 0; i = i.add(BigInteger.ONE)) {
            // instantiate new solution object
            Solution s = new Solution();

            int z = items.length - 1;

            // insert items for this thread based on the binary signature
            for(char c : threadBinary.toCharArray()) {
                if(c == 48) {
                    s.addItem(items[z]);
                }
                z--;
            }

            // parse current combination to full zero-padded binary format
            String combinationBinary = String.format("%" + solvableBinaryLength + "s", i.toString(2)).replace(' ', '0');

            for(char c : combinationBinary.toCharArray()) {
                // parse through each character
                if(c == 48) {
                    s.addItem(items[z]);
                }
                // decrement index
                z--;
            }

            if(s.getWeight() <= c && s.getWeight() > 0) {
                // reject overweight & possible zero-weight solutions
                solutions.add(s);
            }

            addCurrent(BigInteger.ONE);
        }
    }

    private void addTotal(BigInteger val) {
        synchronized (countLock) {
            total = total.add(val);
        }
    }

    private void addCurrent(BigInteger val) {
        synchronized (countLock) {
            current = current.add(val);
        }
    }

    Solution getSolution() {
        // return best solution of this thread
        return solutions.first();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(items.length);
        sb.append('\n');
        for(Item i : items) {
            sb.append(i.getId());
            sb.append("\t\t");
            sb.append(i.getValue());
            sb.append("\t\t");
            sb.append(i.getWeight());
            sb.append('\n');
        }
        sb.append(c);
        return sb.toString();
    }

    boolean isCombinationsCalculated() {
        return combinationsCalculated;
    }

    @Override
    public void run() {
        bruteforce();
    }
}
