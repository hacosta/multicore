import java.util.concurrent.Semaphore;


public class CyclicBarrier {
    int parties;
    Semaphore sem;

    public CyclicBarrier(int parties) {
        this.parties = parties;
        this.sem = new Semaphore(parties, true);
        try {
            this.sem.acquire(parties);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int await() {
        this.sem.release();
        return parties - sem.availablePermits();
    }
}
