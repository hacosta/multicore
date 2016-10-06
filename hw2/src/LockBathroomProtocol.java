import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class LockBathroomProtocol implements BathroomProtocol {


    int maleCount;
    int femaleCount;
    int totalProcessed;

    final Random random = new Random();
    final Lock lock = new ReentrantLock();
    final Condition females = lock.newCondition();
    final Condition males = lock.newCondition();

    public boolean isBathroomEmpty() {
        return maleCount == 0 && femaleCount == 0;
    }

    public LockBathroomProtocol() {
        totalProcessed = maleCount = femaleCount = 0;

    }

    public void reset() {
        maleCount = femaleCount = 0; // Useful for testing.
    }

    public void printStatus() {
        System.out.println("There are now " + maleCount + " male(s) and " + femaleCount + " female(s) using the bathroom.");
        assert ((maleCount != 0 && femaleCount != 0));
    }

    public void enterMale() {
        try {
            lock.lock();
            totalProcessed++;
            while (femaleCount > 0) {
                System.out.println("Male waiting...");
                males.await();
            }
            maleCount++;
            printStatus();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void leaveMale() {
        try {
            lock.lock();
            maleCount--;
            System.out.println("Male left the bathroom");
            printStatus();
            if (maleCount == 0)
                females.signalAll();
            males.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void enterFemale() {
        try {
            lock.lock();
            totalProcessed++;
            while (maleCount > 0) {
                System.out.println("Female waiting...");
                females.await();
            }
            femaleCount++;
            printStatus();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void leaveFemale() {
        try {
            lock.lock();
            femaleCount--;
            System.out.println("Female left the bathroom");
            printStatus();
            if (femaleCount == 0)
                males.signalAll();
            females.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
