import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class PoissonSolver {

    //Initialize force function
    private static final int VECTOR_LENGTH = 4097;
    private static final double[] F = new double[VECTOR_LENGTH];
    static {
        F[1024] = 1.0D;
        F[2048] = 5.0D;
    }

    public static void main(String[] args){

        if(args.length != 1){
            System.err.println("Expected single argument: number of threads");
            System.exit(1);
        }

        int nThreads = 0;

        try {
            nThreads = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e){
            System.err.println("Number of threads must be an integer");
            System.exit(1);
        }

        if(nThreads <= 0){
            System.err.println("Number of threads must be a positive");
            System.exit(1);
        }

        /*
           V is a wrapper to a double[] (ie. equivalent to C double**),
           representing the approximation to the Poisson's equation
           applied to the Force Function. copyV represents the newV,
           ie. the V at next iteration, which will be swapped with
           V at the end of the iteration.
           Wrappers are used to avoid using System.arraycopy
           and simply use this swapping technique from within
           the thread, as seen on the barrier runnable on line 59
         */
        final ArrayWrapper V = new ArrayWrapper(new double[VECTOR_LENGTH]);
        final ArrayWrapper copyV = new ArrayWrapper(new double[VECTOR_LENGTH]);

        /* allConverged is a boolean wrapper, visible to all Poisson Threads
           which tells if 'at least one thread has not converged'.
           If allConverged is set, it means we have reached the
           final convergence criteria and then stopSignal is set
           to tell all threads to stop. */
        final AtomicBoolean allConverged = new AtomicBoolean(true);
        final AtomicBoolean stopSignal = new AtomicBoolean(false);

        final Thread[] threads = new Thread[nThreads];

        final CyclicBarrier barrier = new CyclicBarrier(nThreads,
                new Runnable() {
                    @Override
                    public void run() {
                        //code run by the last thread to reach barrier

                        /*
                           if all threads have converged, ask them
                           to not do any more iterations and stop.
                           Much better performance than having
                           this one thread reading the whole array
                           to check for convergence.
                           Performance! :)
                           */
                        if(allConverged.get()){
                            stopSignal.set(true);
                            return;
                        }
                        else{
                            /* else, reset allConverged for the next
                               iteration, which will later be set to
                               false if at least one thread
                               has not converged */
                            allConverged.set(true);
                        }

                        /*
                           Swap: the new approximation function is now
                           placed on V (instead of arraycopy at every
                           iteration). Much better performance! :D
                         */
                        double[] tmp = V.get();
                        V.set(copyV.get());
                        copyV.set(tmp);
                    }
                });

        final int threadLoad = (VECTOR_LENGTH-2) / nThreads;
        int leftOvers = (VECTOR_LENGTH-2) - (threadLoad * nThreads);

        /* Spread load equally to the different threads such that they
           process the same amount of elements (or one more) */
        int i = 1;
        int j = threadLoad;
        for(int t = 0; t < nThreads; t++){
            if(leftOvers > 0){
                leftOvers--;
                j++;
            }

            /* so many parameters! only other way would be having static
               variables, but that's dirty! */
            threads[t] = new PoissonThread(V, copyV, F, i, j,
                                           barrier, allConverged, stopSignal);

            i = j+1;
            j += threadLoad;
        }

        long startTime = System.nanoTime();
        for(Thread t : threads){
            t.start();
        }

        for(Thread t : threads) {
            try {
                t.join();
            }
            catch (InterruptedException e){
                System.err.println("Thread " + t + " was interrupted!");
            }
        }

        long endTime = System.nanoTime();

        //Print performance as 1 / execution_time_in_seconds
        System.out.println(1000000000.0/(endTime - startTime));
    }
}