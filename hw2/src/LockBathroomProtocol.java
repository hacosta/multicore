import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hacosta on 9/29/16.
 */
public class LockBathroomProtocol implements BathroomProtocol {


    int maleCount;
    int femaleCount;

    final Random random = new Random();
    final Lock lock = new ReentrantLock();
    final Condition females = lock.newCondition();
    final Condition males = lock.newCondition();

    public boolean isBathroomEmpty() {
        return maleCount == 0 && femaleCount == 0;
    }

    public LockBathroomProtocol() {
        maleCount = femaleCount = 0;
    }

    public void printStatus() {
        System.out.println("There is now " + maleCount + " male(s) and " + femaleCount + " female(s) using the bathroom.");
        assert((maleCount != 0 && femaleCount != 0));
    }

    public void enterMale() {
        try {
            lock.lock();
            while (femaleCount > 0)
                males.await();
            maleCount++;
            printStatus();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void leaveMale() {
        lock.lock();
        maleCount--;
        System.out.println("Male left the bathroom");
        if(maleCount == 0)
            females.signal();
        lock.unlock();
    }

    public void enterFemale() {
        try {
            lock.lock();
            while (maleCount > 0)
                females.await();
            femaleCount++;
            printStatus();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void leaveFemale() {
        lock.lock();
        femaleCount--;
        System.out.println("Female left the bathroom");
        if(femaleCount == 0)
            males.signal();
        lock.unlock();
    }
}
