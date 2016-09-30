/**
 * Created by hacosta on 9/29/16.
 */
public class HW2P2a {

    public static void sleep(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void simplestTest() {
        LockBathroomProtocol bathroomProtocol = new LockBathroomProtocol();

        /* Simple test. */
        bathroomProtocol.enterFemale();
        System.out.println("Female in bathroom");
        bathroomProtocol.leaveFemale();
        bathroomProtocol.enterMale();
        System.out.println("Male in bathroom");
        bathroomProtocol.leaveMale();
    }

    public static void parallelTest() throws InterruptedException {
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

    public static void multipleFemales() throws InterruptedException {
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
        try {
            System.out.println("======================");
            simplestTest();
            System.out.println("======================");
            parallelTest();
            System.out.println("======================");
            multipleFemales();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
