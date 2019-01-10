import java.util.Comparator;

public class HPRNComparator implements Comparator<Process> {

	public int compare(Process p1, Process p2) {
		if(p1.getRatio(p1.currentCycle) < p2.getRatio(p2.currentCycle) ) {
			return 1;
		}
		else if(p1.getRatio(p1.currentCycle) > p2.getRatio(p2.currentCycle) ) {
			return -1;
		}
		else {
			return p1.compareTo(p2);
		}
	}
}
