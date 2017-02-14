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
			bw.write("���� Ŭ�� : " + currentClock + "\r\n");
			
			
			
			
			
			
			//1�� �׸��� ���������� ������ Ȯ���� ��� ����
			for (int i = 0; i < 2; i++) {
				Person p = pf.makePerson(i == 0 ? true : false,currentClock);
				if (p != null) {
					floor[p.getStartFloor()].offer(p);
					bw.write(p.getStartFloor()	+ "�� �� ����� �����Ͽ����ϴ�. ��������  " + p.getDestFloor()	+ "�� �Դϴ�. \r\n");	
					
					pressOuterButton(p.getStartFloor());
				}
			}
			

			
			//�ΰ��� ���������� MOVE 
			//MOVE�� TRUE�� ��ȯ�� ����� Ż���ִ� ����
			for (int i = 0; i < 2; i++) {
				if (elevator[i].move(currentClock)) {
					//����� Ż�� �ִ� �����ϰ�� �ش����� ť �˻�
					if (!floor[elevator[i].getCurrentFloor()].isEmpty()) {
						if (floor[elevator[i].getCurrentFloor()].peek().move()) {
							elevator[i].into(floor[elevator[i].getCurrentFloor()].poll());
						}
					//Ż����� ������ �� �ݱ�
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
		
		bw.write("���������� 1 ��� ���ð�: "+elevator[0].getAvgWaitingTime() +"\r\n");
		bw.write("���������� 1 �̵��Ÿ� : "+elevator[0].getMoveDistance()+"\r\n");
		
		bw.write("���������� 2 ��� ���ð�: "+elevator[1].getAvgWaitingTime() +"\r\n");
		bw.write("���������� 2 �̵��Ÿ� : "+elevator[1].getMoveDistance()+"\r\n");
		bw.flush();
		bw.close();
	}

	

	
	
	
	
	private void pressOuterButton(int floor) {
		
		//���������Ͱ� 2���� �۵� , �ƴҽ� ����
		//�� ���������Ͱ� ���� ���������͸� ȣ���ϴ� ���� �����ϴ� �Ÿ��� ����Ͽ� ª�� ���� ȣ��
		int callEvel = elevator[0].getDistance(floor) <= elevator[1].getDistance(floor) ? 0 : 1;
		try {
			bw.write(callEvel + "���������Ͱ� "+ floor+"������ ȣ��Ǿ����ϴ�. \r\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		elevator[callEvel].call(floor);
		

	}

}
