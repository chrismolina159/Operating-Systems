import java.util.Comparator;

public class TaskSortId implements Comparator<Task>{
	
	public int compare(Task t1, Task t2) {
		if(t1.id < t2.id)
			return -1;
		else
			return 1;
	}
}
