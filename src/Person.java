
public class Person {
	private int startFloor;
	private int destFloor;
	private int step = 0;
	private int fullStep = 3;
	
	private	int inTime = 0;
	
	
	public Person(int startFloor,int destFloor,int step,int currentClock){
		this.startFloor = startFloor;
		this.destFloor = destFloor;
		fullStep = step;
		inTime = currentClock;
	}
	
	public int getInTime(){
		return inTime;
	}
	public boolean move(){
		step++;
		
		if(step >= fullStep)
		{
			step = 0;
			return true;
		}else
			return false;
	}
	
	
	
	public int getStartFloor(){
		return startFloor;
	}
	
	public int getDestFloor(){
		return destFloor;
	}
	public void setStep(int i){
		step = i;
	}

	
}
