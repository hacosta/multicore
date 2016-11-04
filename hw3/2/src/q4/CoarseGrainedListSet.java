package q4;

import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrainedListSet implements ListSet {


    ReentrantLock lock;
    public Node head;
    public int size;

    public CoarseGrainedListSet() {
        head = null;
        lock = new ReentrantLock();
    }

    public boolean add(int value) {
        lock.lock();
        try {
            Node newNode = new Node(value);

            if (head == null) {
                head = newNode;
                size++;
                return true;
            }

            Node headPtr = head;
            Node prev = null;

            while (headPtr != null && headPtr.value < value) {
                prev = headPtr;
                headPtr = headPtr.next;
            }

            if (headPtr != null && headPtr.value == value)
                /* Reject duplicates */
                return false;

            if (prev == null) {
                /* We need a new head */
                newNode.next = head;
                head = newNode;
            } else {
                prev.next = newNode;
                newNode.next = headPtr;
            }

            size++;
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

            size--;
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
