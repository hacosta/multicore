/**
 * Created by hacosta on 11/3/16.
 */
package q4;

public class Main {

    protected static class ParallelTester implements Runnable {
        int min;
        int max;
        ListSet l;

        public ParallelTester(int min, int max, ListSet l) {
            this.min = min;
            this.max = max;
            this.l = l;
        }

        @Override
        public void run() {
            for (int i = min; i < max; i++) {
                l.add(i);
            }
        }
    }

    public static void testList(CoarseGrainedListSet l) throws InterruptedException {
        /* We create 10 threads
            t0 inserts range (0, 9)
            t1 inserts range (10, 19)
            ...
            t9 inserts range (90, 99)

            The end result should be a list that contains elements 0..99
         */
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            ParallelTester tester = new ParallelTester(i *10, i * 10 + 10, l);
            Thread t = new Thread(tester);
            t.start();
            threads[i] = t;
        }

        for (Thread t : threads)
            t.join();

        assert(l.size.get() == 100);

        /* Now let's remove all even elements from the list */
        for (int i = 0; i < 100; i+=2)
            l.remove(i);

        for (int i = 0; i < l.size.get(); i++) {
            System.out.println(l.head.next.value);
            l.head = l.head.next;
        }

    }

    public static void testList(FineGrainedListSet l) throws InterruptedException {
        /* We create 10 threads
            t0 inserts range (0, 9)
            t1 inserts range (10, 19)
            ...
            t9 inserts range (90, 99)

            The end result should be a list that contains elements 0..99
         */
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            ParallelTester tester = new ParallelTester(i *10, i * 10 + 10, l);
            Thread t = new Thread(tester);
            t.start();
            threads[i] = t;
        }

        for (Thread t : threads)
            t.join();

        assert(l.size.get() == 100);

        /* Now let's remove all even elements from the list */
        for (int i = 0; i < 100; i+=2)
            l.remove(i);

        for (int i = 0; i < l.size.get(); i++) {
            System.out.println(l.head.next.value);
            l.head = l.head.next;
        }

    }

    public static void main(String args[]) throws InterruptedException {
        CoarseGrainedListSet set = new CoarseGrainedListSet();
        testList(set);
        FineGrainedListSet set2 = new FineGrainedListSet();
        testList(set2);

    }
}
