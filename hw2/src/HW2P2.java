import java.util.Random;

public class HW2P2 {
    final Random random = new Random();
    final int NUM_THREADS = 20;

    public static void sleep(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static BathroomProtocol newProtocol(String type) {
        if(type.equals("LOCK"))
            return new LockBathroomProtocol();
        else
            return new SyncBathroomProtocol();
    }

    public void backgroundEnter(final BathroomProtocol bathroomProtocol) throws InterruptedException {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if(random.nextBoolean()) {
                    bathroomProtocol.enterMale();
                    sleep(random.nextInt(5));
                    bathroomProtocol.leaveMale();
                } else {
                    bathroomProtocol.enterFemale();
                    sleep(random.nextInt(10));
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
    }

    public void simplestTest(BathroomProtocol bathroomProtocol) {

        /* Simple test. */
        bathroomProtocol.enterFemale();
        System.out.println("Female in bathroom");
        bathroomProtocol.leaveFemale();
        bathroomProtocol.enterMale();
        System.out.println("Male in bathroom");
        bathroomProtocol.leaveMale();
    }

    public void parallelTest(final BathroomProtocol bathroomProtocol) throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                /* Female goes first, and stays ther for 5 seconds.
                 * Male should not enter the Bathroom until the lady
                 * leaves.
                 */
                bathroomProtocol.enterFemale();
                sleep(5);
                bathroomProtocol.leaveFemale();
            }
        });
        t.start();
        sleep(1);
        bathroomProtocol.enterMale();
        bathroomProtocol.leaveMale();
        t.join();
    }

    public void multipleFemales(final BathroomProtocol bathroomProtocol) throws InterruptedException {

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
        HW2P2 hw = new HW2P2();

        String[] protocolTypes = {"SYNC", "LOCK"};

        for(String type: protocolTypes) {
            System.out.println("Testing using protocol of type: " + type);
            try {
                System.out.println("======================");
                hw.simplestTest(newProtocol(type));
                System.out.println("======================");
                hw.parallelTest(newProtocol(type));
                System.out.println("======================");
                hw.multipleFemales(newProtocol(type));
                System.out.println("======================");
                hw.backgroundEnter(newProtocol(type));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
