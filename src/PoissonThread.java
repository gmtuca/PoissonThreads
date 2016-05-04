import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class PoissonThread extends Thread {

    private final double[] V, F;
    private final int start, end; //start to end inclusive
    private final CyclicBarrier barrier;
    private final AtomicBoolean allConverged;

    public PoissonThread(double[] V, double[] F, int start, int end, CyclicBarrier barrier, AtomicBoolean allConverged){
        this.V = V;
        this.F = F;
        this.start = start;
        this.end = end;
        this.barrier = barrier;
        this.allConverged = allConverged;
    }

    @Override
    public void run() {
        System.out.println(this);

        double[] approx;
        boolean converged;

        int a = 0;
        do {
            a++;

            approx = approximate(V);

            try {
                barrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }

            converged = hasConverged(approx);
            if(!converged){
                allConverged.set(false);
            }

            System.arraycopy(approx, 0, V, start, approx.length);

            try {
                barrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }

            if(allConverged.get()){//if they all converged
                break;
            }
            else{
                allConverged.set(true);
            }

            //if(a % 10000 == 0)
            //    printArr(V);

        } while (true);

        System.out.println(Thread.currentThread().getName() + " converged after " + a + " iterations");
    }

    private double[] approximate(double[] V){
        double[] approx = Arrays.copyOfRange(V, start, end+1);

        for(int i = start; i <= end; i++){
            approx[i-start] = (V[i-1] + V[i+1] - F[i]) / 2.0D;
        }

        return approx;
    }

    private boolean hasConverged(double[] approx){
        //65251

        for(int i = start; i <= end; i++){ //todo
            if(Math.abs(V[i] - approx[i-start]) >= 0.001D * Math.abs(approx[i-start])){
                return false;
            }
        }

        return true;
    }

    private static void printArr(double[] A){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < A.length-1; i++){
            if(A[i] != 0) //TODO
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
