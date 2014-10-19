/*
 * QueueInterface.java 
 *
 * provides an interface for all of the funtions
 * a Queue should implement
 *
 * If a class implements this interface, it can
 * then be used as a Queue...
 */

interface QueueInterface
{
  public boolean isEmpty();
  public Object peek();
  public Object dequeue();
  public void enqueue(Object data);
  public String toString();
}

