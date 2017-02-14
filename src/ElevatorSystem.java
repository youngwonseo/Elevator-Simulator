import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Queue.ArrayQueue;

public class ElevatorSystem {
	private final int numOfElevator = 2;
	private final int numOfFloor = 10;
	private int doneTime = 300;
	private int currentClock=0;
	
	private Elevator[] elevator = new Elevator[numOfElevator];
	private ArrayQueue<Person>[] floor = new ArrayQueue[numOfFloor];
	private PersonFactory pf = new PersonFactory();

	private BufferedWriter bw;
	private File f = new File("log.txt");
	
	
	
	public ElevatorSystem() {
		try {
			bw= new BufferedWriter(new FileWriter(f));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < numOfElevator; i++)
			elevator[i] = new Elevator(bw);

		for (int i = 0; i < numOfFloor; i++)
			floor[i] = new ArrayQueue<Person>();
	}
	
	public void initialization(int workOutPro,int workInPro,int capacity,int elvStep,int perStep,int doneTime){
		
		
		for(int i=0;i<numOfElevator;i++){
			
			
			elevator[i].setCapacity(capacity);
			elevator[i].setFullStep(elvStep);
		}
		pf.setFullStep(perStep);
		pf.setOutProbability(workOutPro);
		pf.setInProbability(workInPro);
		this.doneTime = doneTime;
	}
	
	public Elevator[] getElevator(){
		return elevator;
	}
	
	
	public int getSize(int i){
		return floor[i].size();
	}
	
	

	public int getCurrentClock(){
		return currentClock;
	}
	
	public int getDoneTime(){
		return doneTime;
	}
	
	public void run() throws IOException {
		
		currentClock = 0;

		while (currentClock < doneTime) {
			bw.write("현재 클럭 : " + currentClock + "\r\n");
			
			
			
			
			
			
			//1층 그리고 나머지층에 일정한 확률로 사람 생성
			for (int i = 0; i < 2; i++) {
				Person p = pf.makePerson(i == 0 ? true : false,currentClock);
				if (p != null) {
					floor[p.getStartFloor()].offer(p);
					bw.write(p.getStartFloor()	+ "층 에 사람이 도착하였습니다. 목적지는  " + p.getDestFloor()	+ "층 입니다. \r\n");	
					
					pressOuterButton(p.getStartFloor());
				}
			}
			

			
			//두개의 엘리베이터 MOVE 
			//MOVE가 TRUE를 반환시 사람이 탈수있는 상태
			for (int i = 0; i < 2; i++) {
				if (elevator[i].move(currentClock)) {
					//사람이 탈수 있는 상태일경우 해당층의 큐 검사
					if (!floor[elevator[i].getCurrentFloor()].isEmpty()) {
						if (floor[elevator[i].getCurrentFloor()].peek().move()) {
							elevator[i].into(floor[elevator[i].getCurrentFloor()].poll());
						}
					//탈사람이 업스면 문 닫기
					}else{
						elevator[i].callCancle(elevator[i].getCurrentFloor());
						elevator[i].closeDoor(!floor[elevator[i].getCurrentFloor()].isEmpty());
					}
				}								
			}			
			currentClock++;
			
			
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bw.flush();
		}
		
		bw.write("엘리베이터 1 평균 대기시간: "+elevator[0].getAvgWaitingTime() +"\r\n");
		bw.write("엘리베이터 1 이동거리 : "+elevator[0].getMoveDistance()+"\r\n");
		
		bw.write("엘리베이터 2 평균 대기시간: "+elevator[1].getAvgWaitingTime() +"\r\n");
		bw.write("엘리베이터 2 이동거리 : "+elevator[1].getMoveDistance()+"\r\n");
		bw.flush();
		bw.close();
	}

	

	
	
	
	
	private void pressOuterButton(int floor) {
		
		//엘리베이터가 2개시 작동 , 아닐시 수정
		//각 엘리베이터가 현재 엘리베이터를 호출하는 층에 도달하는 거리를 계산하여 짧은 쪽을 호출
		int callEvel = elevator[0].getDistance(floor) <= elevator[1].getDistance(floor) ? 0 : 1;
		try {
			bw.write(callEvel + "엘리베이터가 "+ floor+"층에서 호출되었습니다. \r\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		elevator[callEvel].call(floor);
		

	}

}
