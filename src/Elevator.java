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

	// 생성자
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

			//문이 열였을 경우
			if (doorOpen) {

				if (!innerButton[currentFloor].isEmpty()) {
					if (innerButton[currentFloor].peek().move()) {
						
						bw.write(currentFloor + " 층에서 사람이 내렸습니다. \r\n");

						Person returnP = innerButton[currentFloor].poll();

						// 대기시간
						waitingTime += (currentClock - returnP.getInTime())
								- ((returnP.getStartFloor() - returnP
										.getDestFloor()) * fullStep);
						
						usedPersonNum++;
						numOfPerson--;
					}
				} else if (outerButton[currentFloor]
						&& numOfPerson < capacity
						&& (currentFloor == 0 || (currentFloor != 0 && direction != 1))) {
					// 엘리베이터 안드로 사람이 탈수 있도록 true
					return true;
				} else {
					closeDoor(false);
				}
				
			//움직이고 있을 경우
			} else if (direction != 0) {

				movePerClock += 1;
				movedDistance++;
				if (movePerClock >= fullStep) {
					movePerClock = 0;
					currentFloor += direction;

					bw.write("엘리베이터 현재 층 : " + currentFloor + " 목표층 : " + dest
							+ " 방향 : " + direction + "\r\n");

					/*
					 * 엘리베이터의 방향을 바꿔야 할 경우 목적지까지 왔을때, 0층이거나 꼭대기 층일때
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
	 * 목적지로 도착했을 경우 외부버튼이나 내부터픈중 가장 먼 거리에 해당하는 버튼을 선택하여 목적지 설정
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
	 * 외부에서 호출 시 if현재층이랑 같은경우 else 다를시 목적지로 설정
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
	 * 현재목적지보다 입력되는 층의수가 더 높거나 낮으면 목적지로 설정된다.
	 */
	
	private void setDest(int floor) {

		// 두개의 if는 일반적인경우(움직이는 방향에 있어서 더 멀리 떨어진경우)
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

		// 움직이지 않을때 목적지를 설정하고 그 방향으로 direction 설정
		dest = floor;

		if (currentFloor != floor)
			direction = floor > currentFloor ? 1 : -1;

	}

	public void openDoor() {
		try {
			bw.write("문이 열립니다. \r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doorOpen = true;
	}

	/*
	 * 뭄닫기
	 */
	public void closeDoor(boolean remaining) {
		try {
			bw.write("문이 닫힙니다. \r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doorOpen = false;
		outerButton[currentFloor] = remaining;
	}

	/*
	 * 사람 탑승 목적지 버튼에 사람을 넣는식으로 표현 그 사람의 목적지를 이용하여 Dest계산
	 */
	public void into(Person p) {
		try {
			bw.write(currentFloor + " 층에서 사람이 탔습니다. \r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		numOfPerson++;

		innerButton[p.getDestFloor()].offer(p);
		setDest(p.getDestFloor());
	}

	/*
	 * 거리계산 움직여야 할 계산 엘리베이터가+ 정지되어 있을경우 두수 차의 절대값 반환 위로 이동중일 경우 현재층이 더 낮은경우, 더
	 * 높은경우 아래로 이동중일 경우 현재증이 더 높은경우, 더 낮은경우
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
