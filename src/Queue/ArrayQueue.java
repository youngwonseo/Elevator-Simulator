package Queue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class ArrayQueue<E> implements QueueInt<E>{

	private int front;
	private int rear;
	private int size;
	private int capacity;
	private static final int DEFAULT_CAPACITY = 10;
	private E[] theData;
	
	public ArrayQueue(){
		this(DEFAULT_CAPACITY);
	}
	
	public ArrayQueue(int initCapacity){
		capacity = initCapacity;
		theData = (E[])new Object[capacity];
		front = 0;
		rear = capacity - 1;
		size = 0;		
	}

	@Override
	public boolean offer(E item) {
		// TODO Auto-generated method stub
		if(size == capacity){
			reallocate();
		}
		size++;
		rear = (rear+1) % capacity;
		theData[rear] = item;
		return true;
	}
	
	public int size(){
		return size;
	}
	private void reallocate(){
		int newCapacity = capacity * 2;
		E[] newData = (E[])new Object[newCapacity];
		int j = front;
		for(int i=0;i<size;i++){
			newData[i] = theData[j];
			j = (j+1)%capacity;
		}
		front = 0;
		rear = size - 1;
		capacity = newCapacity;
		theData = newData;
		theData = newData;
	}
	
	@Override
	public E remove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		if(size == 0)
			return null;
		E result = theData[front];
		front = (front+1)%capacity;
		size--;
		return result;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		if(size==0)
			return null;
		else
			return theData[front];
	}

	@Override
	public E element() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public boolean isEmpty(){
		return (size==0);
	}
	
	
}
