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
	
	
	
	//������ Ȯ���� ����� �����ؼ� ������ ��ġ��Ų��. 
	//getOff�� true�̸� 1���� �����ϴ� �����
	//		  false�̸� �� ����
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
