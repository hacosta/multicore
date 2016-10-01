import java.util.Random;

public class HW2P2b {
    final Random random = new Random();
    final int NUM_THREADS = 20;

    public static void sleep(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void backgroundEnter() throws InterruptedException {
        final LockBathroomProtocol bathroomProtocol = new LockBathroomProtocol();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if(random.nextBoolean()) {
                    bathroomProtocol.enterMale();
                    sleep(random.nextInt(5));
                    bathroomProtocol.leaveMale();
                } else {
                    bathroomProtocol.enterFemale();
                    sleep(random.nextInt(5));
                    bathroomProtocol.leaveFemale();
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
        assert(bathroomProtocol.totalProcessed == NUM_THREADS); // Everyone eventually had its turn
    }

    public void simplestTest() {
        LockBathroomProtocol bathroomProtocol = new LockBathroomProtocol();

        /* Simple test. */
        bathroomProtocol.enterFemale();
        System.out.println("Female in bathroom");
        bathroomProtocol.leaveFemale();
        bathroomProtocol.enterMale();
        System.out.println("Male in bathroom");
        bathroomProtocol.leaveMale();
    }

    public void parallelTest() throws InterruptedException {
        final LockBathroomProtocol protocol = new LockBathroomProtocol();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                /* Female goes first, and stays ther for 5 seconds.
                 * Male should not enter the Bathroom until the lady
                 * leaves.
                 */
                protocol.enterFemale();
                sleep(5);
                protocol.leaveFemale();
            }
        });
        t.start();
        sleep(1);
        protocol.enterMale();
        protocol.leaveMale();
        t.join();
    }

    public void multipleFemales() throws InterruptedException {
        final LockBathroomProtocol bathroomProtocol = new LockBathroomProtocol();

        bathroomProtocol.enterFemale();
        bathroomProtocol.enterFemale();
        bathroomProtocol.enterFemale();
        System.out.println("Hello ladies!");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Male will wait in line til last lady is done");
                bathroomProtocol.enterMale();
            }
        });
        t.start();

        bathroomProtocol.leaveFemale();
        bathroomProtocol.leaveFemale();
        sleep(5); /* Last lady is kind of slow */
        bathroomProtocol.leaveFemale();

        t.join();
    }


    public static void main(String args[]) {
        HW2P2b hw = new HW2P2b();

        try {
            System.out.println("======================");
            hw.simplestTest();
            System.out.println("======================");
            hw.parallelTest();
            System.out.println("======================");
            hw.multipleFemales();
            System.out.println("======================");
            hw.backgroundEnter();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
