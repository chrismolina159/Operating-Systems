import java.util.ArrayList;
import java.util.HashMap;

public class Task {

	int id;
	int computationTime;
	ArrayList<Integer> currentUnits = new ArrayList<Integer>();
	int timeTaken;
	int timeWaiting;
	ArrayList<String> instructions;
	HashMap<Resource,Integer> claims;
	boolean aborted = false;
	
	public Task() {
		instructions = new ArrayList<String>();
		claims = new HashMap<Resource,Integer>();
		currentUnits.add(-1);
	}
	
	public Task(int id) {
		this.id = id;
		instructions = new ArrayList<String>();
		claims = new HashMap<Resource,Integer>();
		currentUnits.add(-1);
	}
	
	public void setComputeTime(int cTime) {
		this.computationTime = cTime;
	}
	
	public void setTimeTaken(int t) {
		timeTaken = t;
	}
	
	public void incrementTimeWaiting() {
		timeWaiting++;
	}
	
	public String toString() {
		return "Task " + this.id + "'s instructions are " + instructions.toString();
	}
}
