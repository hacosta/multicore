package q4;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedListSet implements ListSet {
    public Node head;
    public Node tail;
    public AtomicInteger size;

    public FineGrainedListSet() {
        /* Our fine grained list set starts at head.next */
        head = new Node(null);
        tail = new Node(null);
        head.next = tail;
        size = new AtomicInteger(0);
    }

    public boolean add(int value) {
        Node prev = head;
        head.lock();
        try {
            Node curr = prev.next;
            curr.lock();
            try {
                while (curr.value != null && curr.value < value) {
                    prev.unlock();
                    prev = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.value != null && curr.value == value)
                    return false;
                else {
                    Node node = new Node(value);
                    node.next = curr;
                    prev.next = node;
                    size.incrementAndGet();
                    return true;
                }
            } finally {
                curr.unlock();
            }
        } finally {
            prev.unlock();
        }
    }

    public boolean remove(int value) {
        Node prev = null;
        Node curr;
        head.lock();
        try {
            prev = head;
            curr = prev.next;
            curr.lock();
            try {
                while (curr.value < value) {
                    prev.unlock();
                    prev = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (value == curr.value) {
                    prev.next = curr.next;
                    size.decrementAndGet();
                    return true;
                }
                return false;
            } finally {
                curr.unlock();
            }
        } finally {
            prev.unlock();
        }
    }

    public boolean contains(int value) {
        Node prev = null;
        Node curr;
        head.lock();
        try {
            prev = head;
            curr = prev.next;
            curr.lock();
            try {
                while (curr.value < value) {
                    prev.unlock();
                    prev = curr;
                    curr = curr.next;
                    curr.lock();
                }
                return value == curr.value;
            } finally {
                curr.unlock();
            }
        } finally {
            prev.unlock();
        }
    }

    protected class Node {
        public Integer value;
        public Node next;
        private Lock lock;

        public Node(Integer x) {
            value = x;
            next = null;
            lock = new ReentrantLock();
        }

        public void lock() {
            lock.lock();
        }

        public void unlock() {
            lock.unlock();
        }
    }
}
