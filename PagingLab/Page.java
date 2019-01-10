
public class Page {

	int pageNum;
	int processNum;
	int loadingTime = 0;

	public Page() {}
	
	public Page (int page, int proc) {
		this.pageNum = page;
		this.processNum = proc;
	}
}
