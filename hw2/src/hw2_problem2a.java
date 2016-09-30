/**
 * Created by  on 9/29/2016.
 */
import java.util.Random;

class Male extends Thread {
    private int sleeptime;
    private bathroomLocks barrier;


    public Male(int time, bathroomLocks barrier, String name) {
        super(name);
        this.sleeptime = time;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleeptime);
            System.out.println(Thread.currentThread().getName() + " is trying to get in the bathroom");
            barrier.enterMale();
            Random rand = new Random();
            int n = rand.nextInt(1000) + 500;
            System.out.println(Thread.currentThread().getName() + " is doing something in the bathroom for " + n);
            Thread.sleep(n);
            barrier.leaveMale();
            System.out.println(Thread.currentThread().getName() + "has left  the bathroom");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class Female extends Thread {
    private int sleeptime;
    private bathroomLocks barrier;

    public Female(int time, bathroomLocks barrier, String name) {
        super(name);
        this.sleeptime = time;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleeptime);
            System.out.println(Thread.currentThread().getName() + " is trying to get in the bathroom");
            barrier.enterFemale();
            Random rand = new Random();
            int n = rand.nextInt(1000) + 500;
            System.out.println(Thread.currentThread().getName() + " is busy in the bathroom for " + n);
            Thread.sleep(n);
            barrier.leaveFemale();
            System.out.println(Thread.currentThread().getName() + "has left  the bathroom");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

public class hw2_problem2a {

    public static void main(String[] args) throws InterruptedException {
        // Prints "Hello, World" to the terminal window.
        //System.out.println("Hello, World");

        bathroomLocks barrier = new bathroomLocks();

        Male male1 = new Male(1455, barrier, "male-1");
        Male male2 = new Male(1999, barrier, "male-2");
        Female female1 = new Female(1279, barrier, "female-1");
        Female female2 = new Female(2016, barrier, "female-2");
        Male male3 = new Male(2017, barrier, "male-3");

        male1.start();
        male2.start();
        female1.start();
        female2.start();
        male3.start();
        // do stuff
        //in thread, wait to  stop

    }

}
