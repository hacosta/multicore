
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockQueue implements MyQueue {
    // you are free to add members

    //init value of 0
    AtomicInteger count = new AtomicInteger();
    private Node head;

    // locks for Enq and Deq operations
    final Lock lockEnq = new ReentrantLock();
    final Lock lockDeq = new ReentrantLock();
    final Condition notEmpty = lockDeq.newCondition();

    public LockQueue() {
        // implement your constructor here
    }

    public boolean enq(Integer value) {
        // implement your enq method here
        try{
            lockEnq.lock();

            // if head empty, set node value
            if (head == null) {
                head = new Node(value);
            }

            Node tmp = new Node(value);
            Node current = head;

            //go to tail
            if (current != null) {

                while (current.getNext() != null) {
                    current = current.getNext();
                }

                // set tail to new node
                current.setNext(tmp);

            }

        } finally {
            //increment index, and signal deg if its waiting
            count.incrementAndGet();
            //notEmpty.signalAll();
            lockEnq.unlock();
            System.out.println("enq : " + printQueue().toString() + " size of queue: " + count.toString());
        }
        return false;
    }

    public Integer deq() {
        // implement your deq method here
        try {

            lockDeq.lock();
            //wait till count is at least 1
            while (count.intValue() < 1) {
                //System.out.println("Queue is empty, awaiting value for deq...");
                // notEmpty.await();
                Thread.sleep(5);
            }

            //deque a value
            Node current = head;
            int oldHeadNextValue = current.getNext().value;
            //if (head != null) {
            //   current.setNext(current.getNext().getNext());
            //}
            current.setNext(current.getNext().getNext());

            count.decrementAndGet();
            System.out.println("deq : " + printQueue().toString() + " size of queue: " + count.toString());

            lockDeq.unlock();

            return oldHeadNextValue;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int size(){
        return count.intValue();
    }

    public void printSize(){
        System.out.println(count.toString());
    }

    public String printQueue() {
        String output = "";

        if (head != null) {
            Node current = head.getNext();
            while (current != null) {
                output += "[" + current.getValue().toString() + "]";
                current = current.getNext();
            }

        }
        return output;
    }


    protected class Node {

        public Integer value;
        public Node next;

        public Node(Integer x) {
            value = x;
            next = null;
        }

        public Integer getValue(){
            return value;
        }

        public void setValue(Integer x){
            value = x;
        }

        public void setNext (Node nextValue){
            next = nextValue;
        }

        public Node getNext (){
            return next;
        }

    }
}
