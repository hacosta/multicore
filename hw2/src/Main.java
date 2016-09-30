import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // Prints "Hello, World" to the terminal window.
        //System.out.println("Hello, World");

        CyclicBarrier barrier = new CyclicBarrier(4);
        Party first = new Party(1000, barrier, "PARTY-1");
        Party second = new Party(2000, barrier, "PARTY-2");
        Party third = new Party(3000, barrier, "PARTY-3");
        Party fourth = new Party(4000, barrier, "PARTY-4");

        first.start();
        second.start();
        third.start();
        fourth.start();
        // do stuff

        //in thread, wait to  stop

    }

}

class Party extends Thread {
    private int sleeptime;
    private CyclicBarrier barrier;

    public Party(int time, CyclicBarrier barrier, String name) {
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
