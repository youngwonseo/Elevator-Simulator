import java.util.Random;


public class PersonFactory {
	
	private int outProbability=50,inProbability=50;
	private Random random = new Random();
	private int fullStep;
	public PersonFactory(){}
	
	
	
	public void setOutProbability(int i){
		outProbability = i;
	}
	public void setInProbability(int i){
		inProbability = i;
	}
	public void setFullStep(int i){
		fullStep = i;
	}
	
	
	
	//일정한 확률료 사람을 생성해서 각층에 배치시킨다. 
	//getOff가 true이면 1층에 도착하는 사람들
	//		  false이면 그 외층
	public Person makePerson(boolean getOff,int currentClock){
		if(getOff){
			if(random.nextInt(100) <= outProbability)
				return new Person(0,random.nextInt(9)+1,fullStep,currentClock);			
		}else{
			if(random.nextInt(100) <= inProbability)
				return new Person(random.nextInt(9)+1,0,fullStep,currentClock);		
		}		
		return null;
	}
	
	
}
