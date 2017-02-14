package Queue;

public interface QueueInt<E> {
	boolean offer(E item);
	E remove();
	E poll();
	E peek();
	E element();
}
