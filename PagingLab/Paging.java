import java.util.*;
import java.io.*;

public class Paging {
	
	static int quantum = 3;
	
	public static void main(String[] args) throws FileNotFoundException{
		
		int machineSize = Integer.parseInt(args[0]);
		int pageSize = Integer.parseInt(args[1]);
		int processSize = Integer.parseInt(args[2]);
		int jobMix = Integer.parseInt(args[3]);
		int numOfRef = Integer.parseInt(args[4]);
		String replacementAlgo = args[5];
		int debug = -1;
		
		if(args.length ==  7)
			debug = Integer.parseInt(args[6]);
		
		Page[] pageList = new Page[machineSize/pageSize];
		Arrays.fill(pageList,null);
		Scanner random = new Scanner(new File("random-numbers"));
		
		System.out.println("The machine size is " + machineSize);
		System.out.println("The page size is " + pageSize);
		System.out.println("The process size is " + processSize);
		System.out.println("The job mix number is " + jobMix);
		System.out.println("The number of references per process is " + numOfRef);
		System.out.println("The replacement algorithm is " + replacementAlgo);
		
		if(debug != -1)
			System.out.println("The level of debugging output is " + debug);
		System.out.println();
		
		Process[] procs;
		
		
		if(jobMix == 1) {
			procs = new Process[1];
			procs[0] = new Process(processSize, 1, numOfRef, 1.0, 0.0, 0.0);
		}
		else if(jobMix == 2){
			procs = new Process[4];
			for(int i = 0; i < 4; i++) {
				procs[i] = new Process(processSize, i+1, numOfRef, 1.0, 0.0, 0.0);
			}
		}
		else if(jobMix == 3) {
			procs = new Process[4];
			for(int i = 0; i < 4; i++) {
				procs[i] = new Process(processSize, i+1, numOfRef, 0.0, 0.0, 0.0);
			}
		}
		else if(jobMix == 4) {
			procs = new Process[4];
			procs[0] = new Process(processSize, 1, numOfRef, 0.75, 0.25, 0.0);
			procs[1] = new Process(processSize, 2, numOfRef, 0.75, 0.0, 0.25);
			procs[2] = new Process(processSize, 3, numOfRef, 0.75, 0.125, 0.125);
			procs[3] = new Process(processSize, 4, numOfRef, 0.50, 0.125, 0.125);
		}
		else {
			//System.out.println("Not a valid job mix number");
			random.close();
			throw new IllegalArgumentException("Not a valid job mix number");
		}
		
		int current = 1;
		int counter = 1;
		int procIndex = 0;
		int totalWalk = 0;
		
		//calculate the total number of 3-run cycles the processes have to do.
		if(numOfRef % 3 == 0 && procs.length != 1)
			totalWalk = (numOfRef / 3) * 4;
		else if(numOfRef % 3 != 0 && procs.length != 1)
			totalWalk = ( (numOfRef / 3) + 1) * 4;
		else if(numOfRef % 3 != 0 && procs.length != 1 && numOfRef < 3)
			totalWalk = 4;
		else if(procs.length == 1 && numOfRef % 3 != 0 && numOfRef > 3)
			totalWalk = (numOfRef / 3) + 1;
		else if(procs.length == 1 && numOfRef % 3 == 0 )
			totalWalk = numOfRef / 3;
		else if(procs.length == 1 && numOfRef % 3 != 0 && numOfRef < 3)
			totalWalk = 1;
		else
			totalWalk = -1;
		
		Queue<Page> lruQueue = new LinkedList<Page>();
		Stack<Page> lifoStack = new Stack<Page>();
		
		//walk through all the references
		for(; current <= totalWalk; current++) {
			//can only do a max of 3 references for a process or the total amount of references left if less than 3.
			for(int i = 0; i < quantum && procs[procIndex].currentNumOfRef > 0; i++) {
				int word = -1;
				
				if(!procs[procIndex].firstRef) {
					word = (procs[procIndex].nextWord + processSize) % processSize;
				}
				else {
					word = procs[procIndex].getWord();
					procs[procIndex].nextWord  = word;
					procs[procIndex].firstRef = false;
				}
				
				int pageNum = word / pageSize;
				Page pg = new Page(pageNum, procs[procIndex].num);
				int indexInP = pContains(pageList, pg);
				//System.out.print((procIndex+1) + " references word "+word+ " at time " + counter);
				
				//this page is already in pageList, but if replacementAlgo is "lru" the page must be removed from the Queue
				//in order to "reset" the position of this page
				if(indexInP != -1) {
					//System.out.print(": Hit! frame " + indexInP);
					if(replacementAlgo.equals("lru") ) {
						Iterator<Page> it = lruQueue.iterator();
						int c = 0;
						while(it.hasNext()){
							Page temp = it.next();
							if(temp.pageNum == pg.pageNum && temp.processNum == pg.processNum) {
								c = temp.loadingTime;
								it.remove();
							}
						}
						pg.loadingTime = c;
						lruQueue.add(pg);
					}
				}
				//page is not in pageList
				else {
					//System.out.print(": Fault");
					boolean pageFault = true;
					procs[procIndex].increaseFaultTot();
					
					//find an empty(null) position in pageList
					for(int k = pageList.length - 1; k >= 0 ; k--) {
						if(pageList[k] == null) {
							pg.loadingTime = counter;
							pageList[k] = pg;
							if(replacementAlgo.equals("lru") )
								lruQueue.add(pg);
							else if(replacementAlgo.equals("lifo"))
								lifoStack.push(pg);
							else {}
							pageFault = false;
							break;
						}
					}
					//need to use a replacement algorithm
					if(pageFault) {
						if(replacementAlgo.equals("lru")) {
							hasPageFaultLRU(pageList, lruQueue, pg, counter, procs);
						}
						else if(replacementAlgo.equals("lifo"))
							hasPageFaultLIFO(pageList, lifoStack, pg, counter, procs);
						else {
							int rand = random.nextInt();
							hasPageFaultRandom(pageList, rand, pg, counter, procs);
						}
					}
				}
				//System.out.println();
				int test = random.nextInt();
				//System.out.println("Process "+ (procIndex +  1) + " uses random number "+test);
				double ran = test/(Integer.MAX_VALUE + 1d);
				//System.out.println("RAN = "+ ran);
				
				//calculate the process's next word
				if(ran < procs[procIndex].a)
					procs[procIndex].nextWord = (procs[procIndex].nextWord + 1) % processSize;
				else if(ran < procs[procIndex].a + procs[procIndex].b)
					procs[procIndex].nextWord = (procs[procIndex].nextWord - 5 + processSize) % processSize;
				else if(ran < procs[procIndex].a + procs[procIndex].b + procs[procIndex].c)
					procs[procIndex].nextWord = (procs[procIndex].nextWord + 4) % processSize;
				else{
					test = random.nextInt();
//					System.out.println(procIndex+1 + " uses random number "+test);
					procs[procIndex].nextWord = test % processSize;
				}
				
				procs[procIndex].currentNumOfRef--;
				counter++;
			}
			if(procs.length == 1)
				;
			else {
				if(procIndex != 3)
					procIndex++;
				else
					procIndex = 0;
			}
		}
		
		random.close();
		int totalFaults = 0;
		int totalEvictions = 0;
		double overallAvg = 0;
		System.out.println("");
		
		//Print out results
		for(int i = 0; i < procs.length; i++) {
			procs[i].calcAvg();
			totalFaults += procs[i].numOfFaults;
			overallAvg += procs[i].resTime;
			totalEvictions += procs[i].numOfEvictions;
			
			if(procs[i].numOfEvictions == 0)
				System.out.println("Process "+procs[i].num+" had "+procs[i].numOfFaults+" fault(s) and an undefined average residency");
			else
				System.out.println("Process "+procs[i].num+" had "+procs[i].numOfFaults+" fault(s) and a "+procs[i].averageResidency+" average residency");
		}
		System.out.println("The total number of faults is " + totalFaults+ " and the overall average residency is "+ (overallAvg/totalEvictions) );
	}
	
	//helper method to see if the pageList contains the current page
	public static int pContains(Page[] pL, Page pg) {
		int index = -1;
		
		for(int i = 0; i < pL.length; i++) {
			if(pL[i] == null)
				continue;
			else if(pL[i].pageNum == pg.pageNum && pL[i].processNum == pg.processNum) {
				index = i;
				break;
			}
			else {}
		}
		
		return index;
	}
	
	//LIFO replacement algorithm
	public static void hasPageFaultLIFO(Page[] p, Stack<Page> lifoStack, Page current, int counter,
											Process[] procs) {
		Page removed = lifoStack.pop();
		current.loadingTime = counter;
		
		//locate page we are removing in pageList
		for(int i = 0; i < p.length; i++) {
			if(p[i].pageNum == removed.pageNum && p[i].processNum == removed.processNum) {
				procs[removed.processNum-1].increaseEvictTot();
				procs[removed.processNum-1].resTime += counter - removed.loadingTime;
				p[i] = current;
				lifoStack.push(current);
			}
		}
	}
	
	//Random replacement algorithm
	public static void hasPageFaultRandom(Page[] p, int random,Page current, int counter,
												Process[] procs) {
		int randomIndex = random % p.length;
		Page removed = p[randomIndex];
		current.loadingTime = counter;
		procs[removed.processNum-1].increaseEvictTot();
		procs[removed.processNum-1].resTime += counter - removed.loadingTime;
		p[randomIndex] = current;
	}
	
	//LRU replacement algorithm
	public static void hasPageFaultLRU(Page[] p, Queue<Page> lruQueue, Page current, int counter,
											Process[] procs) {
		Page removed = lruQueue.poll();
		current.loadingTime = counter;
		
		//locate page we are removing in pageList
		for(int i = 0; i < p.length; i++) {
			if(p[i].pageNum == removed.pageNum && p[i].processNum == removed.processNum) {
				procs[removed.processNum-1].increaseEvictTot();
				procs[removed.processNum-1].resTime += counter - removed.loadingTime;
				p[i] = current;
				lruQueue.add(current);
			}
		}
	}
}
