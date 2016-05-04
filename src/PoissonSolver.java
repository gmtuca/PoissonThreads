import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class PoissonSolver {

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

        //printArr(F);

        final double[] V = new double[VECTOR_LENGTH];

        final Thread[] threads = new Thread[nThreads];
        final CyclicBarrier barrier = new CyclicBarrier(nThreads);
        final AtomicBoolean atomicBoolean = new AtomicBoolean(true);

        final int threadLoad = (VECTOR_LENGTH-2) / nThreads;
        int leftOvers = (VECTOR_LENGTH-2) - (threadLoad * nThreads);

        //Spread load equally to the different threads such that they
        //process the same amount of elements (or one more)
        int i = 1;
        int j = threadLoad;
        for(int t = 0; t < nThreads; t++){
            if(leftOvers > 0){
                leftOvers--;
                j++;
            }

            threads[t] = new PoissonThread(V, F, i, j, barrier, atomicBoolean);

            i = j+1;
            j += threadLoad;
        }

        //long startTime = System.nanoTime();
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

        //long endTime = System.nanoTime();

        //Calculate performance as 1 / execution_time_in_seconds
        //System.out.println(1000000000.0/(endTime - startTime));

    }

}
