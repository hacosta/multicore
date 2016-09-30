import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

// this is a test
public class hw2_problem1 {

    public static void main(String[] args) throws InterruptedException {
        // Prints "Hello, World" to the terminal window.
        //System.out.println("Hello, World");

        CyclicBarrier barrier = new CyclicBarrier(4);
        Players first = new Players(1000, barrier, "Player-1");
        Players second = new Players(2000, barrier, "Player-2");
        Players third = new Players(3000, barrier, "Player-3");
        Players fourth = new Players(4000, barrier, "Player-4");

        first.start();
        second.start();
        third.start();
        fourth.start();
        // do stuff

        //in thread, wait to  stop

    }

}

class Players extends Thread {
    private int sleeptime;
    private CyclicBarrier barrier;

    public Players(int time, CyclicBarrier barrier, String name) {
        super(name);
        this.sleeptime = time;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleeptime);
            System.out.println(Thread.currentThread().getName() + " is calling await()");
            int turn = barrier.await();
            System.out.println(Thread.currentThread().getName() + " got " + turn);
            System.out.println(Thread.currentThread().getName() + " has started running again");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}
