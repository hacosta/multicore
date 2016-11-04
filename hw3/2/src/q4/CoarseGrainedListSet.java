package q4;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedListSet implements ListSet {


    ReentrantLock lock;
    public Node head;
    public AtomicInteger size;

    public CoarseGrainedListSet() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE); /* TAIL */
        lock = new ReentrantLock();
        size = new AtomicInteger(0);
    }

    public boolean add(int value) {
        lock.lock();
        try {
            Node newNode = new Node(value);
            Node curr = head;
            Node prev = head.next;

            while (curr.value < value) {
                prev = curr;
                curr = curr.next;
            }

            if (curr.value == value)
                /* Reject duplicates */
                return false;

            newNode.next = curr;
            prev.next = newNode;

            size.incrementAndGet();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(int value) {
        lock.lock();
        try {
            if (head == null)
                return false;

            Node headPtr = head;
            Node prev = null;
            while (headPtr != null && headPtr.value < value) {
                prev = headPtr;
                headPtr = headPtr.next;
            }
            if (headPtr.value != value) {
                /* We didn't find the value */
                return false;
            }
            if (prev != null) {
                prev.next = headPtr.next;
            } else {
                head = head.next;
            }

            size.decrementAndGet();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(int value) {
        try {
            lock.lock();
            Node headPtr = head;
            while (headPtr != null) {
                if (headPtr.value == value)
                    return true;
                headPtr = headPtr.next;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    protected class Node {
        public Integer value;
        public Node next;

        public Node(Integer x) {
            value = x;
            next = null;
        }
    }
}
