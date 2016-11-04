import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue implements MyQueue {

    public Node tmp = new Node(0, null);
    public AtomicReference<Node> head
            = new AtomicReference<Node>(tmp);
    public AtomicReference<Node> tail
            = new AtomicReference<Node>(tmp);

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
        String output = "<";
        //traverse the node till head is null
        int counter = 0;
        if (head != null) {
            //grab the next node
            Node current = head.get().next.get();
            //if next node is not null
            // should take care of exceptions
            while (current != null) {
                //print the output
                output += "[ " ;
                if (current.getValue() != null) {
                    output += current.getValue().toString();
                }
                output += " ]";
                //advance the current node for the next loop
                current = current.next.get();
                counter++;
            }
        }

        output += ">" + " size of queue: " + counter;
        return output;
    }


    public boolean enq(Integer value) {
        Node newNode = new Node(value, null);
        while (true) {

            Node currentTail = tail.get();
            Node tailNext = currentTail.next.get();
            // verify that current tail has not chagned
            if (currentTail == tail.get()) {
                if (tailNext != null) {
                    //  attempt CAS operation, if fail try again
                    tail.compareAndSet(currentTail, tailNext);
                } else { // inserting new node
                    if (currentTail.next.compareAndSet(null, newNode)) {
                        //  advancing tail
                        // if CAS fails, back to start of loop
                        tail.compareAndSet(currentTail, newNode); 
                        System.out.println("enq : " + printQueue().toString() );
                        return true;
                    }
                }
            }
        }
    }

    public Integer deq() {
        while (true) {
            //get the currrent head that will be deq
            Node chead = head.get();
            Node headNext = chead.next.get();
            
            Node ctail = tail.get();
            //check to make sure things have not changed
            if (chead == head.get()) {
                if (chead == ctail) {
                    if (headNext == null)
                        return null; 
                    // CAS attemp, if fail start loop again
                    tail.compareAndSet(ctail, headNext);
                } else { 
                    if (head.compareAndSet(chead, headNext))
                        System.out.println("deq : " + printQueue().toString() );
                    return headNext.vaule;
                }
            }
        }
    }
}
