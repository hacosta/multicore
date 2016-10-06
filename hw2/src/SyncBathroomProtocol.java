// TODO
// Use synchronized, wait(), notify(), and notifyAll() to implement this
// bathroom protocol

public class SyncBathroomProtocol implements BathroomProtocol {
    int maleCount;
    int femaleCount;
    int totalProcessed;


    public SyncBathroomProtocol() {
        maleCount = 0;
        femaleCount = 0;
    }

    public void printStatus() {
        System.out.println("There are now " + maleCount + " male(s) and " + femaleCount + " female(s) using the bathroom.");
        assert ((maleCount != 0 && femaleCount != 0));
    }

    public synchronized void enterMale() {
        totalProcessed++;
        while (femaleCount > 0) {
            System.out.println("Male waiting...");
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        maleCount++;
    }

    public synchronized void leaveMale() {
        maleCount--;
        System.out.println("Male left the bathroom");
        printStatus();
        this.notifyAll();
    }

    public synchronized void enterFemale() {
        totalProcessed++;
        while (maleCount > 0) {
            System.out.println("Female waiting...");
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        femaleCount++;
        printStatus();
    }

    public synchronized void leaveFemale() {
        femaleCount--;
        System.out.println("Female left the bathroom");
        printStatus();
        this.notifyAll();
    }


}
