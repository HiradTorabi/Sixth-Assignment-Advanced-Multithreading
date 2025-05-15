package MonteCarloPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

public class MonteCarloPi {

    static final long NUM_POINTS = 50_000_000L;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        // Without Threads
        System.out.println("Single threaded calculation started: ");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single threads): " + (endTime - startTime) / 1_000_000 + " ms");

        // With Threads
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n",NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (Multi-threaded): " + piWithThreads);
        System.out.println("Time taken (Multi-threaded): " + (endTime - startTime) / 1_000_000 + " ms");

        // TODO: After completing the implementation, reflect on the questions in the description of this task in the README file
        //       and include your answers in your report file.
    }

    // Monte Carlo Pi Approximation without threads
    public static double estimatePiWithoutThreads(long numPoints)
    {
        Random rnd = new Random();
        long inside = 0;
        for (long i = 0; i < numPoints; i++)
        {
            double x = rnd.nextDouble() * 2 - 1;
            double y = rnd.nextDouble() * 2 - 1;
            if (x * x + y * y <= 1.0)
            {
                inside++;
            }
        }
        return 4.0 * inside / numPoints;
    }

    // Monte Carlo Pi Approximation with threads
    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException, ExecutionException
    {
        numThreads = (int) Math.min(numThreads, numPoints);
        long pointsPerThread = numPoints / numThreads;
        long remainder       = numPoints % numThreads;
        Thread[] threads = new Thread[numThreads];
        long[]   inside  = new long[numThreads];
        for (int t = 0; t < numThreads; t++)
        {
            long quota = (t == numThreads - 1) ? pointsPerThread + remainder
                    : pointsPerThread;
            final int idx = t;
            threads[t] = new Thread(() ->
            {
                Random rnd = new Random();
                long localInside = 0;

                for (long i = 0; i < quota; i++)
                {
                    double x = rnd.nextDouble() * 2 - 1;
                    double y = rnd.nextDouble() * 2 - 1;
                    if (x * x + y * y <= 1.0) localInside++;
                }
                inside[idx] = localInside;
            });
            threads[t].start();
        }
        for (Thread th : threads) th.join();
        long totalInside = 0;
        for (long n : inside) totalInside += n;
        return 4.0 * totalInside / numPoints;
    }
    }
