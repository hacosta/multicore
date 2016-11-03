import java.util.Random;

/**

 */

public class HW3P3 {

    final Random random = new Random();
    final int NUM_THREADS = 20;

    public static void sleep(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static MyQueue newProtocol(String type) {
        if(type.equals("LOCK"))
            return new LockQueue();
        else
            return new LockFreeQueue();
    }


    public void parallelTest(final MyQueue MyQueue) throws InterruptedException {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if(random.nextBoolean()) {
                    MyQueue.enq(random.nextInt(5));
                    sleep(random.nextInt(5));
                } else {
                    MyQueue.deq();
                    sleep(random.nextInt(10));
                }
            }
        };

        Thread threads[] = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(r);
            threads[i].start();
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].join();
        }
    }
    public void simplestTest(MyQueue myQueue) {

        /* Simple test. */
        myQueue.enq(random.nextInt(5));
        myQueue.deq();
        myQueue.enq(random.nextInt(5));
        myQueue.deq();
        myQueue.enq(random.nextInt(5));
        myQueue.enq(random.nextInt(5));
        myQueue.enq(random.nextInt(5));
        myQueue.deq();
        myQueue.enq(random.nextInt(5));
        myQueue.deq();
        myQueue.deq();
        myQueue.deq();

    }

    public static void main(String args[]) {
        HW3P3 hw = new HW3P3();

        String[] protocolTypes = {"LOCK"};
        //String[] protocolTypes = {"LOCK", "noLOCK"};

        for(String type: protocolTypes) {
            System.out.println("Testing using protocol of type: " + type);
            try {
                System.out.println("======================");
                hw.simplestTest(newProtocol(type));
                System.out.println("======================");
                hw.parallelTest(newProtocol(type));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
