
public class Process implements Comparable<Process>{
	
	private int arrivalTime;
	
	private int b;
	
	private int cpuTime;
	
	private int cpuTimeLeft;
	
	public int io;
	
	public int index;
	
	public int ioBurstTime = 0;
	
	private int finishedTime = 0;
	
	private int turnaroundTime = 0;
	
	public int ioTime = 0;
	
	private int wait = 0;
	
	public int currentCycle;
	
	private int currWaitingTime = 0;
	
	public int burstTime = 0;
	
	public boolean ready = false;
	
	public boolean running = false;
	
	public boolean blocked = false;
	
	public boolean done = false;
	
	public Process(int a, int b, int c, int m){
		this.arrivalTime = a;
		this.b = b;
		this.cpuTime = c;
		this.cpuTimeLeft = c;
		this.io = m;
	}
	
	public Process(int a, int b, int c, int m, int index){
		this.arrivalTime = a;
		this.b = b;
		this.cpuTime = c;
		this.cpuTimeLeft = c;
		this.io = m;
		this.index = index;
	}
	
	public Process() {
		this(0,0,0,0);
	}

	public int getA() {
		return arrivalTime;
	}
	
	public int getB() {
		return b;
	}
	
	public int getC() {
		return cpuTime;
	}
	
	public int getIO() {
		return io;
	}
	
	public int getFinTime() {
		return finishedTime;
	}
	
	//Add formula to get the penalty ratio once back home
	public double getRatio(int currentCycle) {
		double max = Math.max(1, this.getC()-this.getTimeLeft());
		return (currentCycle - this.getA())/max;
		//return ((this.getCurrWait() + this.getC())/max);
	}
	
	public int getTaTime() {
		return this.turnaroundTime;
	}
	
	public int getTimeLeft() {
		return cpuTimeLeft;
	}
	
	public int getWait() {
		return this.wait;
	}
	
	public int getCurrWait() {
		return this.currWaitingTime;
	}
	
	public void incrementCurrWait() {
		this.currWaitingTime++;
	}
	
	public void increaseWait() {
		wait++;
	}
	
	public void resetCurrWait() {
		this.currWaitingTime = 1;
	}
	
	public void setA(int a) {
		this.arrivalTime = a;
	}
	
	public void setB(int b) {
		this.b = b;
	}
	
	public void setC(int c) {
		this.cpuTime = c;
		this.cpuTimeLeft = c;
	}
	
	public void setIO(int i) {
		this.io = i;
	}
	
	public void setCurrTime(int curr) {
		this.currWaitingTime = curr;
	}
	
	public void setTimeLeft() {
		this.cpuTimeLeft--;
	}
	
	public void setTaTime(int tt) {
		this.turnaroundTime = tt;
	}
	
	public void setFinTime(int f) {
		this.finishedTime = f;
	}
	
	public String getStatus() {
		if(ready)
			return "ready";
		else if(running)
			return "running";
		else if(blocked)
			return "blocked";
		else if(done)
			return "done";
		else
			return "not ready";
	}
	
	public String toString() {
		return "A = "+this.arrivalTime+", B = "+this.b+", C = "+this.cpuTime+", M = "+this.io;
	}
	
	//Same as the tie break rule only used in the HPRNComparator class
	public int compareTo(Process p2) {
		if(this.getA() == p2.getA()) {
			if(this.index > p2.index)
				return 1;
			else
				return -1;
		}
		else {
			if(this.getA() > p2.getA())
				return 1;
			else 
				return -1;
		}
	}
}
