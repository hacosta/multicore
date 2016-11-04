import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue implements MyQueue {

    protected class Node {

        public Integer vaule;
        public AtomicReference<Node> next;

        public Node(int value, Node next) {
            this.vaule = value;
            this.next = new AtomicReference<Node>(next);
        }

        public AtomicReference<Node> getNext (){ return next; }
        public Integer getValue(){
            return vaule;
        }
    }


    public String printQueue() {
        String output = "";

        if (head != null) {
            Node current = head.get().next.get();
            while (current != null) {
                output += "[" + current.getValue().toString() + "]";
                current = current.next.get();
            }

        }
        return output;
    }

    public Node dummy = new Node(0, null);
    public AtomicReference<Node> head
            = new AtomicReference<Node>(dummy);
    public AtomicReference<Node> tail
            = new AtomicReference<Node>(dummy);

    public boolean enq(Integer value) {
        Node newNode = new Node(value, null);
        while (true) {
            Node curTail = tail.get();
            Node tailNext = curTail.next.get();
            if (curTail == tail.get()) { // check tail
                if (tailNext != null) { //  advance tail
                    tail.compareAndSet(curTail, tailNext);
                } else { // inserting new node
                    if (curTail.next.compareAndSet(null, newNode)) {
                        //  advancing tail
                        tail.compareAndSet(curTail, newNode); // if CAS fails, back to start of loop
                        System.out.println("enq : " + printQueue().toString() );
                        return true;
                    }
                }
            }
        }
    }

    public Integer deq() {
        for (; ; ) {
            Node oldHead = head.get(); // get current head
            Node oldTail = tail.get(); // get current tail
            Node oldHeadNext = oldHead.next.get(); // get current head.next
            if (oldHead == head.get()) { // check old head and tail
                if (oldHead == oldTail) {
                    if (oldHeadNext == null)
                        return null; // there is no blocking , try again
                    tail.compareAndSet(oldTail, oldHeadNext); // CAS attemp, if fail start loop again
                } else { // No need to deal with tail
                    if (head.compareAndSet(oldHead, oldHeadNext))
                        System.out.println("deq : " + printQueue().toString() );
                        return oldHeadNext.vaule;
                        //return null;
                }
            }
        }
    }
}
