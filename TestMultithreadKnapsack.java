package aleksirasio.advancedalgorithms.lab06.multithread;

import aleksirasio.advancedalgorithms.lab06.bruteforce.Item;
import aleksirasio.advancedalgorithms.lab06.bruteforce.Solution;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestMultithreadKnapsack {

    private static final double LOG2 = Math.log(2.0);
    private static Item[] items;
    private static int c;

    private final static String FILE = "resources/hard33.txt";

    /**
     * @param args command line arguments
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("Knapsack file: " + FILE);
        // populate knapsack from file
        try {
            readKnapsackFile(new File(FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // get amount of threads
        int nThreads = Runtime.getRuntime().availableProcessors();
        // initialize thread array
        MultithreadKnapsack[] knapsackThreads = new MultithreadKnapsack[nThreads];

        // create threads
        for(int i = 0; i < nThreads; i++) {
            knapsackThreads[i] = new MultithreadKnapsack(items, c, i);
            // start executing new thread
            knapsackThreads[i].start();
        }

        // print knapsack file
        System.out.println(knapsackThreads[0].toString());
        System.out.println();

        // progress processing starts here
        BigInteger current = MultithreadKnapsack.current;

        boolean combinationsCalculated = false;

        while(!combinationsCalculated) {
            Boolean[] booleans = new Boolean[knapsackThreads.length];
            int i = 0;
            for(MultithreadKnapsack k : knapsackThreads) {
                booleans[i++] = k.isCombinationsCalculated();
            }
            if(!Arrays.asList(booleans).contains(false)) {
                combinationsCalculated = true;
            }
        }

        System.out.println("Brute forcing using " + nThreads + " threads:\n");

        while(current.compareTo(MultithreadKnapsack.total) < 0) {
            // check & update progress every 1 000 000 iterations
            if(current.mod(BigInteger.valueOf(1000000)).compareTo(BigInteger.ZERO) == 0) {
                printProgress(startTime, MultithreadKnapsack.total, current.add(BigInteger.ONE));
            }
            current = MultithreadKnapsack.current;
        }

        // force 100% progress output
        printProgress(startTime, MultithreadKnapsack.total, current);

        // ensure all threads have finished execution
        for (MultithreadKnapsack k : knapsackThreads) {
            try {
                k.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n\nSubset processing finished.\n");

        // instantiate new zero-value solution
        Solution best = new Solution();

        // find best solution
        for(MultithreadKnapsack k : knapsackThreads) {
            if(k.getSolution().getValue() > best.getValue()) {
                best = k.getSolution();
            }
        }

        long endTime = System.currentTimeMillis();
        long executionTime = (endTime - startTime);
        String executionTimeString = String.format("%d minutes, %d seconds",
                TimeUnit.MILLISECONDS.toMinutes(executionTime),
                TimeUnit.MILLISECONDS.toSeconds(executionTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(executionTime)));

        // finally print the solution and total execution time
        System.out.println(best.toString());
        System.out.println("\nApproximate execution time: " + executionTimeString);
    }

    // private methods
    private static void readKnapsackFile(File file) throws FileNotFoundException {
        Scanner s;
        s = new Scanner(file);
        // read line count
        int lineCount = s.nextInt();
        // initialize item array
        items = new Item[lineCount];
        // start parsing the file
        for(int i = 0; i < lineCount; i++) {
            // skip index number, we won't need it
            s.nextInt();
            // read data & create new item
            items[i] = new Item(i + 1, s.nextInt(), s.nextInt());
        }
        // read carrying capacity value from EOF
        c = s.nextInt();
        s.close();
    }

    private static void printProgress(long startTime, BigInteger total, BigInteger current) {
        // this was converted from long to BigInteger
        // ETA accuracy isn't guaranteed on arbitrarily big integers!
        BigInteger eta = current.equals(BigInteger.ZERO) ? BigInteger.ZERO :
                (total.subtract(current)).multiply(BigInteger.valueOf(System.currentTimeMillis() - startTime)).divide(current);

        String etaHms = current.equals(BigInteger.ZERO) ? "N/A" :
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta.longValue()),
                        TimeUnit.MILLISECONDS.toMinutes(eta.longValue()) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta.longValue()) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = current.multiply(BigInteger.valueOf(100)).divide(total).intValue();
        string
                .append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.join("", Collections.nCopies((int) (logBigInteger(total) - logBigInteger(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }

    private static double logBigInteger(BigInteger val) {
        int blex = val.bitLength() - 1022; // any value in 60..1023 is ok
        if (blex > 0)
            val = val.shiftRight(blex);
        double res = Math.log(val.doubleValue());
        return blex > 0 ? res + blex * LOG2 : res;
    }
}
