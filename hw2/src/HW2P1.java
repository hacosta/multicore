import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.Random;

public class HW2P1 {

    public static void startAll(Player[] players) {
        for (Player p : players) {
            p.start();
        }
    }

    public static void joinAll(Player[] players) throws InterruptedException {
        for (Player p : players) {
            p.join();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int numThreads = 4;
        CyclicBarrier barrier = new CyclicBarrier(4);
        Player players[] = new Player[numThreads];

        for (int i = 0; i < numThreads; i++) {
            players[i] = new Player(i * 1000, barrier, "Player-" + (i + 1));
        }
        startAll(players);
        joinAll(players);

        /* Notice that we can re-use the barrier,
         * We reinitialize all our threads and
         * then startAll(); joinAll() them.
         */
        System.out.println("Reusing our existing barrier");
        System.out.println("============================");
        System.out.println("This time wait times are random spikes in wait time");
        for (int i = 0; i < numThreads; i++) {
            Random rand = new Random();
            int n = rand.nextInt(100);
            if (n > 75){
                n = n * n;
            }
            if (n < 25){
                n = -450*i;
            }
            players[i] = new Player((n + 500*i), barrier, "Player-" + (i + 1));
        }

        startAll(players);
        joinAll(players);
    }

}

class Player extends Thread {
    private int sleeptime;
    private CyclicBarrier barrier;

    public Player(int time, CyclicBarrier barrier, String name) {
        super(name);
        this.sleeptime = time;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleeptime);
            System.out.println(Thread.currentThread().getName() + " is calling await() " + sleeptime);
            int turn = barrier.await();
            System.out.println(Thread.currentThread().getName() + " got " + turn);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}
