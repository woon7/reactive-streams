import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ob {

    /**
     * final Iterable<Integer> iter = () -> new Iterator<Integer>() { int i = 0;
     * final static int MAX = 10;
     * <p>
     * public boolean hasNext() { return i < MAX; }
     * <p>
     * public Integer next() { return ++i; } };
     * <p>
     * for(Integer i : iter) { System.out.println(i); }
     * <p>
     * for(Iterator<Integer> it = iter.iterator(); it.hasNext();) {
     * System.out.println(it.next()); }
     */

    static class IntObservable extends Observable implements Runnable {

        @Override
        public void run() {
            for (int i = 1; i <= 10; ++i) {
                setChanged();
                notifyObservers(i);     // push
                // int i = it.next();   // pull
            }
        }
    }

    // DATA method(void) <-> void method(DATA)
    public static void main(String[] args) {
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob);

        ExecutorService es = Executors.newSingleThreadExecutor();

        es.execute(io);
        System.out.println(Thread.currentThread().getName() + " EXIT");
        es.shutdown();
    }
}