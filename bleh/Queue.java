/*
 * An implementation of the Queue class that will be used in the Maze
 * class. 
 *
 */


class Queue implements QueueInterface {
  
  /*******************************************************************
   * an inner class, QCell, for the cells containing the Queue's data
   */  
  class QCell {
    private Object data;
    private Queue.QCell next;

    private QCell(Object data) {
      this.data = data;
      this.next = null;
    }
  }
  /*
   * end of QCell class
   ********************************************************************/
    
  // We store only the first and last QCell
  private Queue.QCell front;  // the front of the queue (for dequeueing)
  private Queue.QCell back;   // the back of the queue (for enqueueing)

  /*
   * a constructor for an empty Queue
   */ 
  public Queue() {
    front = null;
    back = null;
  }

  /*
   * isEmpty returns true if the Queue is empty; false otherwise
   */
  public boolean isEmpty() {
    return (front == null && back == null);
  }

  /*
   * enqueue adds an element (a QCell containing data) onto the back of this Queue
   */
  public void enqueue(Object data) {
    Queue.QCell new_back = new Queue.QCell(data);

    if (this.isEmpty()) {
      front = back = new_back;
    } else {
      // set the current last to point to the new last
      // before setting back to new_back
      back.next = new_back;
      back = new_back;
    }

    return;
  }

  /*
   * dequeue removes an element from the front of this Queue and returns its data
   */
  public Object dequeue() {
    if (isEmpty()) {
      System.out.println("You can't dequeue from an empty queue.");
      return null;
    }

    Object dqData = front.data;

    if (front == back) {  // only one element in queue
      front = back = null;
    } else {
      front = front.next; // garbage collection??
    }

    return dqData;
  }

  /*
   * peek returns the data at the front of this Queue
   * does not modify the queue
   */
  public Object peek() {
    if (isEmpty()) {
      System.out.println("You can't peek an empty Queue!");
      return null;
    }

    Object data = front.data;

    return data;
  }
  
  /*
   * peekNext returns the data in the second Qcell of this Queue 
   * if it exists
   */
  public Object peekNext() {
    if (isEmpty() || front.next == null) {
      System.out.println("You can't peekNext in a Queue with < 2 elements!");
      return null;
    }

    Object nextData = front.next.data;

    return nextData;
  }

  /*
   * toString returns the String representation of the Queue
   */
  public String toString() {
    String result = "<FRONT> ";
    Queue.QCell current = this.front;

    while (current != null) {
      result += "" + current.data + " ";
      current = current.next;
    } 

    result += "<BACK>";

    return result;
  }

  /*
   * main tests our Queue class
   */
  public static void main(String[] args) {
    Queue ball = new Queue();
    System.out.println("ball is " + ball);
    ball.enqueue("Will");
    System.out.println("ball is " + ball);
    ball.enqueue("this");
    System.out.println("ball is " + ball);
    ball.enqueue("work?");
    System.out.println("ball is " + ball);
    ball.dequeue();
    System.out.println("ball is " + ball);
    ball.dequeue();
    System.out.println("ball is " + ball);
    ball.dequeue();
    System.out.println("ball is " + ball);
    ball.dequeue();
    System.out.println("ball is " + ball);
    ball.enqueue("Will");
    System.out.println("ball is " + ball);
    ball.enqueue("this");
    System.out.println("ball is " + ball);
    ball.enqueue("work?");
    System.out.println("ball is " + ball);
  }
}




