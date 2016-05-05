public class ArrayWrapper{
    private double[] arr;

    public ArrayWrapper(double[] arr){
        this.arr = arr;
    }

    public double[] get() {
        return arr;
    }

    public void set(double[] arr) {
        this.arr = arr;
    }
}