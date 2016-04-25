import java.util.Arrays;

public class PoissonThreads {

    private static final int size = 4096;
    private static final double[] F = new double[size];
    static {
        F[1024] = 1.0D;
        F[2048] = 5.0D;
    }

    public static void main(String[] args){

        printArr(F);

        double[] V = new double[size];

        double[] approx = approximate(V);

        int i = 0;
        while(!changeAtEveryPointIsLt(V, approx, 0.00001)){
        //for(int i = 0; i < 5000; i++){
            V = approx;
            approx = approximate(V);

            //if(i % 10000 == 0)
            //    printArr(approx);
            i++;
        }

        System.out.println(i);
    }

    private static double[] approximate(double[] V){
        double[] newV = Arrays.copyOf(V, V.length);

        for(int i = 1; i < V.length-1; i++){
            newV[i] =  (V[i-1] + V[i+1] - F[i]) / 2;
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
}
