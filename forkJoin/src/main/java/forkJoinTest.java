import java.util.concurrent.*;

/**
 * Created by ScorpionOrange on 2016/11/20.
 * This program demonstrates the fork-join framework.
 */
public class forkJoinTest {
    public static void main(String[] args){
        final int SIZE = 10000000;
        double[] numbers = new double[SIZE];
        for(int i = 0; i < SIZE; i++){
            numbers[i] = Math.random();
        }
        Counter counter = new Counter(numbers, 0, numbers.length, (double t) -> (t > 0.5));
        /*
        * non-Lambda mode
        * new Filter() {
        *     @Override
        *     public boolean accept(double x) {
        *         return x > 0.5;
        *     }
        * }
        */
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(counter);
        System.out.println(counter.join());
    }
}

interface Filter{
    boolean accept(double x);
}

class Counter extends RecursiveTask<Integer>{
    private static final int THRESHOLD = 1000;
    private double[] values;
    private int from;
    private int to;
    private Filter filter;

    private Counter(double[] values, int from, int to, Filter filter){
        this.values = values;
        this.from = from;
        this.to = to;
        this.filter = filter;
    }

    protected Integer compute(){
        if(to - from < THRESHOLD){
            int count = 0;
            for(int i = from; i < to; i++){
                if(filter.accept(values[i])) {
                    count++;
                }
            }
            return count;
        }
        else {
            int mid = (from + to) / 2;
            Counter first = new Counter(values, from, mid, filter);
            Counter second = new Counter(values, mid, to, filter);
            invokeAll(first, second);
            return first.join() + second.join();
        }
    }
}
