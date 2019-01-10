import java.util.Comparator;

public class TieBreakRule implements Comparator<Process>{

	public int compare(Process p1, Process p2) {
		if(p1.getA() == p2.getA()) {
			if(p1.index > p2.index)
				return 1;
			else
				return -1;
		}
		else {
			if(p1.getA() > p2.getA())
				return 1;
			else 
				return -1;
		}
	}
}
