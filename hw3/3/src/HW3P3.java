import java.util.Random;

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
                    MyQueue.enq(random.nextInt(64));
                    sleep(random.nextInt(64));
                } else {
                    //MyQueue.deq();
                    System.out.println("return value : " + MyQueue.deq().toString() );
                    sleep(random.nextInt(64));
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
    public void simplestTest(MyQueue MyQueue) {

        /* Simple test. */
        MyQueue.enq(random.nextInt(64));
        //myQueue.deq();
        System.out.println("return value : " + MyQueue.deq().toString() );
        MyQueue.enq(random.nextInt(64));
        System.out.println("return value : " + MyQueue.deq().toString() );
        MyQueue.enq(random.nextInt(64));
        MyQueue.enq(random.nextInt(64));
        MyQueue.enq(random.nextInt(64));
        System.out.println("return value : " + MyQueue.deq().toString() );
        MyQueue.enq(random.nextInt(64));
        System.out.println("return value : " + MyQueue.deq().toString() );
        System.out.println("return value : " + MyQueue.deq().toString() );
        System.out.println("return value : " + MyQueue.deq().toString() );

    }

    public static void main(String args[]) {
        HW3P3 hw = new HW3P3();

        String[] protocolTypes = {"LOCK"};
        //String[] protocolTypes = {"unLOCK"};

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
