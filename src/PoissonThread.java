import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class PoissonThread extends Thread {

    private final double[] F;
    private final ArrayWrapper wrapperV, wrapperCopyV;
    private final int start, end; //start to end inclusive
    private final CyclicBarrier barrier;
    private final AtomicBoolean allConverged, stopSignal;

    public PoissonThread(ArrayWrapper wrapperV, ArrayWrapper wrapperCopyV,
                         double[] F, int start, int end,
                         CyclicBarrier barrier,
                         AtomicBoolean allConverged, AtomicBoolean stopSignal){

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

        //int a = 0;
        do {
            //a++;
            //System.out.println(a);

            approximate();
            boolean converged = hasConverged();
            if(!converged){
                allConverged.set(false);
            }

            try {
                barrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }

            //if(a % 10000 == 0)
            //    printArr(wrapperV.get());

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
        //65251

        double[] V = wrapperV.get();
        double[] copyV = wrapperCopyV.get();

        for(int i = start; i <= end; i++){
            if(Math.abs(V[i] - copyV[i]) >= 0.001D * Math.abs(copyV[i])){
                return false;
            }
        }

        return true;
    }

    public static void printArr(double[] A){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < A.length-1; i++){
            sb.append(A[i])
                    .append(',');
        }

        sb.append(A[A.length-1]);

        System.out.println(sb.toString());
    }

    @Override
    public String toString() {
        return "(" + start + ", " + end + ")";
    }
}
