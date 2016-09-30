import java.util.Random;

public class HW2P2a {
    final Random random = new Random();

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
                    sleep(random.nextInt(3));
                    bathroomProtocol.leaveMale();
                } else {
                    bathroomProtocol.enterFemale();
                    sleep(random.nextInt(3));
                    bathroomProtocol.leaveFemale();
                }
            }
        };
        Thread threads[] = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(r);
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
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
        HW2P2a hw = new HW2P2a();

        try {

            System.out.println("======================");
            hw.simplestTest();
            System.out.println("======================");
            hw.parallelTest();
            System.out.println("======================");
            hw.multipleFemales();
            System.out.println("======================");
            /* This proves that it's broken */
            hw.backgroundEnter();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
