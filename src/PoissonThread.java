import java.util.Arrays;

public class PoissonThread extends Thread {

    private double[] V, F;
    private int start, end;

    public PoissonThread(double[] V, double[] F, int start, int end){
        this.V = V;
        this.F = F;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        System.out.println(this);

        double[] approx = approximate(V);

        int a = 0;
        //while(!changeAtEveryPointIsLt(V, approx, 0.00001)){
        for(int i = 0; i < 5000; i++){
            V = approx;
            approx = approximate(V);

            a++;
            if(a % 1000 == 0)
                printArr(V);
        }

        //System.out.println(a);
    }

    private double[] approximate(double[] V){
        double[] newV = Arrays.copyOf(V, V.length);

        for(int i = start; start < end; i++){
            newV[i] =  (V[i-1] + V[i+1] - F[i]) / 2.0D;
        }

        return newV;
    }

    private static boolean changeAtEveryPointIsLt(double[] arrOld,
                                                  double[] arrNew,
                                                  double change){

        for(int i = 0; i < arrOld.length; i++){
            if(Math.abs(arrOld[i] - arrNew[i]) / arrOld[i] > change){
                return false;
            }
        }

        return true;
    }


    private static void printArr(double[] A){
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
