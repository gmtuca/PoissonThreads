import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class PoissonThread extends Thread {

    private final double[] F;
    private final ArrayWrapper wrapperV, wrapperCopyV;
    private final int start, end; //start to end inclusive
    private final CyclicBarrier barrier;
    private final AtomicBoolean allConverged, stopSignal;

    public PoissonThread(ArrayWrapper wrapperV,
                         ArrayWrapper wrapperCopyV,
                         double[] F,
                         int start,
                         int end,
                         CyclicBarrier barrier,
                         AtomicBoolean allConverged,
                         AtomicBoolean stopSignal){

        this.wrapperV = wrapperV;
        this.wrapperCopyV = wrapperCopyV;
        this.F = F;
        this.start = start;
        this.end = end;
        this.barrier = barrier;
        this.allConverged = allConverged;
        this.stopSignal = stopSignal;
    }

    @Override
    public void run() {
        do {
            approximate(); //write approximation function to copyV

            // check if the function has converged in this thread's section
            boolean converged = hasConverged();
            if(!converged){
                // if this thread has not converged, signal others that we
                // are not ready to terminate
                allConverged.set(false);
            }

            // wait for all threads to agree to proceed
            try {
                barrier.await();
                // remember that this barrier uses runnable
                // (PoissonSolver.java @ line 59)
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }

            // if all threads have converged, terminate, otherwise
            // move to the next iteration
        } while(!stopSignal.get());
    }

    private void approximate(){
        double[] V = wrapperV.get();
        double[] copyV = wrapperCopyV.get();

        for(int i = start; i <= end; i++){
            copyV[i] = (V[i-1] + V[i+1] - F[i]) / 2.0D;
        }
    }

    private boolean hasConverged(){
        double[] V = wrapperV.get();
        double[] copyV = wrapperCopyV.get();

        for(int i = start; i <= end; i++){
            if(Math.abs(V[i] - copyV[i]) >= 0.001D * Math.abs(copyV[i])){
                return false;
            }
        }

        return true;
    }
}
