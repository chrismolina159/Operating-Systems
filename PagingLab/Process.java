
public class Process {

	int pSize;
	int num;
	int word;
	int nextWord = 0;
	int currentNumOfRef;
	int numOfEvictions = 0;
	int numOfFaults = 0;
	double a;
	double b;
	double c;
	double resTime = 0;
	double averageResidency = -1;
	boolean firstRef = true;
	
	
	public Process() {}
	
	public Process(int pS, int n, int current, double a, double b, double c) {
		this.pSize = pS;
		this.num = n;
		this.currentNumOfRef = current;
		this.a = a;
		this.b = b;
		this.c = c;
		word = (111 *  num) % this.pSize;
	}
	
	public int getWord() {
		return this.word;
	}
	
	public void increaseEvictTot() {
		this.numOfEvictions++;
	}
	
	public void increaseFaultTot() {
		this.numOfFaults++;
	}
	
	public void calcAvg() {
		this.averageResidency = resTime/numOfEvictions;
	}
}
