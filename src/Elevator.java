import java.io.BufferedWriter;
import java.io.IOException;

import Queue.ArrayQueue;

public class Elevator {

	private int currentFloor;
	private int fullStep = 3;
	private int movePerClock;
	private int direction = 0;
	private int dest;
	private int numOfPerson = 0;
	private int capacity = 5;
	private int waitingTime = 0;
	private int usedPersonNum = 0;
	private int movedDistance = 0;
	private boolean doorOpen = false;

	private BufferedWriter bw;

	private ArrayQueue<Person>[] innerButton = new ArrayQueue[10];
	private boolean[] outerButton = new boolean[10];

	// ������
	public Elevator(BufferedWriter bw) {
		
		
		currentFloor = 0;

		this.bw = bw;

		for (int i = 0; i < 10; i++) {
			innerButton[i] = new ArrayQueue<Person>();
			outerButton[i] = false;
		}

	}


	public double getAvgWaitingTime() {
		return (double) waitingTime / usedPersonNum;
	}

	public void setFullStep(int s) {
		fullStep = s;
	}

	public void setCapacity(int c) {
		capacity = c;
	}
	public void callCancle(int floor){
		outerButton[floor] = false;
	}
	public boolean move(int currentClock) {
		try {

			//���� ������ ���
			if (doorOpen) {

				if (!innerButton[currentFloor].isEmpty()) {
					if (innerButton[currentFloor].peek().move()) {
						
						bw.write(currentFloor + " ������ ����� ���Ƚ��ϴ�. \r\n");

						Person returnP = innerButton[currentFloor].poll();

						// ���ð�
						waitingTime += (currentClock - returnP.getInTime())
								- ((returnP.getStartFloor() - returnP
										.getDestFloor()) * fullStep);
						
						usedPersonNum++;
						numOfPerson--;
					}
				} else if (outerButton[currentFloor]
						&& numOfPerson < capacity
						&& (currentFloor == 0 || (currentFloor != 0 && direction != 1))) {
					// ���������� �ȵ�� ����� Ż�� �ֵ��� true
					return true;
				} else {
					closeDoor(false);
				}
				
			//�����̰� ���� ���
			} else if (direction != 0) {

				movePerClock += 1;
				movedDistance++;
				if (movePerClock >= fullStep) {
					movePerClock = 0;
					currentFloor += direction;

					bw.write("���������� ���� �� : " + currentFloor + " ��ǥ�� : " + dest
							+ " ���� : " + direction + "\r\n");

					/*
					 * ������������ ������ �ٲ�� �� ��� ���������� ������, 0���̰ų� ����� ���϶�
					 */
					if ((dest == currentFloor)	|| (currentFloor <= 0 || currentFloor >= 9)) {

						dest = evalDest();
						if (dest != -1)
							direction *= -1;
						else
							direction = 0;

					}
					
	
					if (!innerButton[currentFloor].isEmpty() || (outerButton[currentFloor] && direction != 1 && capacity > numOfPerson))  {
						openDoor();
					}

				}
			}
		} catch (IOException e) {}
		
		return false;
	}

	public int getMoveDistance() {
		return movedDistance;
	}

	
	/*
	 * �������� �������� ��� �ܺι�ư�̳� ���������� ���� �� �Ÿ��� �ش��ϴ� ��ư�� �����Ͽ� ������ ����
	 */
	private int evalDest() {
		if (currentFloor != 0) {

			for (int i = 0; i <= 9; i++) {
				if (!innerButton[i].isEmpty() || outerButton[i])
					return i;
			}
			return -1;
		} else {

			for (int i = currentFloor - 1; i >= 0; i--) {
				if (!innerButton[i].isEmpty() || outerButton[i])
					return i;
			}
			return -1;
		}
	}

	/*
	 * �ܺο��� ȣ�� �� if�������̶� ������� else �ٸ��� �������� ����
	 */

	public void call(int floor) {
		outerButton[floor] = true;
		if (floor == currentFloor && movePerClock == 0 && !doorOpen && direction != 1) {
			openDoor();
		} else {
			setDest(floor);
		}
	}

	/*
	 * ������������� �ԷµǴ� ���Ǽ��� �� ���ų� ������ �������� �����ȴ�.
	 */
	
	private void setDest(int floor) {

		// �ΰ��� if�� �Ϲ����ΰ��(�����̴� ���⿡ �־ �� �ָ� ���������)
		if (direction == 1) {
			if (floor > dest)
				dest = floor;

			return;
		}

		if (direction == -1) {
			if (floor < dest)
				
				dest = floor;
			return;
		}

		// �������� ������ �������� �����ϰ� �� �������� direction ����
		dest = floor;

		if (currentFloor != floor)
			direction = floor > currentFloor ? 1 : -1;

	}

	public void openDoor() {
		try {
			bw.write("���� �����ϴ�. \r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doorOpen = true;
	}

	/*
	 * ���ݱ�
	 */
	public void closeDoor(boolean remaining) {
		try {
			bw.write("���� �����ϴ�. \r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doorOpen = false;
		outerButton[currentFloor] = remaining;
	}

	/*
	 * ��� ž�� ������ ��ư�� ����� �ִ½����� ǥ�� �� ����� �������� �̿��Ͽ� Dest���
	 */
	public void into(Person p) {
		try {
			bw.write(currentFloor + " ������ ����� �����ϴ�. \r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		numOfPerson++;

		innerButton[p.getDestFloor()].offer(p);
		setDest(p.getDestFloor());
	}

	/*
	 * �Ÿ���� �������� �� ��� ���������Ͱ�+ �����Ǿ� ������� �μ� ���� ���밪 ��ȯ ���� �̵����� ��� �������� �� �������, ��
	 * ������� �Ʒ��� �̵����� ��� �������� �� �������, �� �������
	 */
	public int getDistance(int floor) {
		if (direction == 0) {
			return Math.abs(floor - currentFloor);
		} else if (direction == 1) {
			return currentFloor < floor ? (floor - currentFloor)
					: (dest - currentFloor) + (dest - floor);
		} else {
			return currentFloor > floor ? (currentFloor - floor) : currentFloor
					+ floor;
		}
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public int getDirection() {
		return direction;
	}

	public int getSize() {
		return numOfPerson;
	}

	public int getDest() {
		return dest;
	}

	public int getCurrentStateForDraw() {

		return currentFloor * 100 + movePerClock;
	}

}
