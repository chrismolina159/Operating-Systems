import java.util.*;
import java.io.*;

public class Scheduler {

	static int randomNumberTracker = 1; //so we can walk through the list of random numbers
	static int totalNumOfProc = -1;
	static boolean verbose = false;
	static ArrayList<Process> procList = new ArrayList<Process>();//list to hold all of the processes
	
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {

		int cmdLength = args.length;
//		System.out.println(args[0]+"|"+args[1]);
		String fileName = "";
		ArrayList<Process> procListSorted = new ArrayList<Process>();
		if(cmdLength == 2) {
			if(args[0].equals("--verbose"))
				verbose = true;
			fileName = args[1];
		}
		else
			fileName = args[0];
		
		Scanner file;
		int count = 0; // resets once we have added M for the process
		int index = 0;
		
		//Getting all the processes with an input that has parenthesis
		try {
			file = new Scanner(new File(fileName));
			
			while(file.hasNext()) {
				String walk = file.next();
				boolean isWalkInt = false;
				Process p = new Process();
				
				while(count < 4) {
					
					if(walk.charAt(0) == '(') {
						isWalkInt = isInteger(walk.substring(1));
						if(isWalkInt) {
							p.setA(Integer.parseInt(walk.substring(1)));
							count++;
							walk = file.next();
						}
					}
					else if(walk.charAt(walk.length()-1) == ')') {
						isWalkInt = isInteger(walk.substring(0,walk.length()-1));
						if(isWalkInt) {
							p.setIO(Integer.parseInt(walk.substring(0,walk.length()-1)));
							count++;
						}
					}
					else {
						isWalkInt = isInteger(walk);
						if(isWalkInt) {
							if(totalNumOfProc == -1) {
								totalNumOfProc = Integer.parseInt(walk);
								walk = file.next();
							}
							else {
								if(count == 1) {
									p.setB(Integer.parseInt(walk));
									count++;
									walk = file.next();
								}
								else if(count == 2) {
									p.setC(Integer.parseInt(walk));
									count++;
									walk = file.next();
								}
							}
						}
					}
					
					if(count == 4) {
						p.index = index;
						procList.add(p);
						break;
					}
				} //end of count < 4 loop
				index++;
				count = 0;
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		printProcesses(procListSorted);
		if(verbose)
			System.out.println("This detailed printout gives the state and remaining burst for each process");

		ArrayList<Process> arrFCFS = new ArrayList<Process>();
		ArrayList<Process> arrLCFS = new ArrayList<Process>();
		ArrayList<Process> arrRR = new ArrayList<Process>();
		ArrayList<Process> arrHPRN = new ArrayList<Process>();
		
		for(int i = 0; i < procListSorted.size(); i++) {
			Process a = new Process(procListSorted.get(i).getA(),procListSorted.get(i).getB(),procListSorted.get(i).getC(),procListSorted.get(i).getIO(), procListSorted.get(i).index);
			Process b = new Process(procListSorted.get(i).getA(),procListSorted.get(i).getB(),procListSorted.get(i).getC(),procListSorted.get(i).getIO(), procListSorted.get(i).index);
			Process c = new Process(procListSorted.get(i).getA(),procListSorted.get(i).getB(),procListSorted.get(i).getC(),procListSorted.get(i).getIO(), procListSorted.get(i).index);
			Process d = new Process(procListSorted.get(i).getA(),procListSorted.get(i).getB(),procListSorted.get(i).getC(),procListSorted.get(i).getIO(), procListSorted.get(i).index);
			arrFCFS.add(a);
			arrLCFS.add(b);
			arrRR.add(c);
			arrHPRN.add(d);
		}
		
		FCFS(arrFCFS);
		RR(arrRR);
		LCFS(arrLCFS);
		HPRN(arrHPRN);
	}
	
	public static boolean isInteger(String str) {
	    if (str == null) {
	    	System.out.println("This doesn't count as an int cause null " + str);
	        return false;
	    }
	    if (str.isEmpty()) {
	    	System.out.println("This doesn't count as an int cause empty " + str);
	        return false;
	    }
	    for (int i = 0; i < str.length(); i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	        	System.out.println("This doesn't count as an int cause parameters " + str);
	            return false;
	        }
	    }
	    return true;
	}
	
	public static void printProcesses(ArrayList<Process> procListSorted) {
		System.out.print("The original input was: "+totalNumOfProc);
		
		for(int i = 0; i < totalNumOfProc; i++) {
			System.out.print("("+procList.get(i).getA()+" "+procList.get(i).getB()+" "+procList.get(i).getC()+" "+procList.get(i).getIO()+") ");
		}
		System.out.println();
		
		for(int i = 0; i < procList.size();i++) {
			procListSorted.add(procList.get(i));
		}
		
		Collections.sort(procListSorted);
		
		System.out.print("The (sorted) input by arrival time is: "+totalNumOfProc);
		for(int i = 0; i < totalNumOfProc; i++) {
			System.out.print("("+procListSorted.get(i).getA()+" "+procListSorted.get(i).getB()+" "+procListSorted.get(i).getC()+" "+procListSorted.get(i).getIO()+") ");
		}
		
		System.out.println();
	}
	
	public static int randomOS(int b) throws FileNotFoundException{
		Scanner randomNum = new Scanner(new File("random-numbers"));
		int rtrn = 0;
		for(int i = 0; i < randomNumberTracker; i++) {
			rtrn = randomNum.nextInt();
		}
		randomNumberTracker++;
		
		return 1 + (rtrn % b);
	}
	
	public static void FCFS(ArrayList<Process>  procListSorted) throws FileNotFoundException {
		List<Process> procsNotReady = new ArrayList<Process>();
		List<Process> procsBlocked = new ArrayList<Process>();
		Queue<Process> readyProcs = new LinkedList<Process>();
		
		Process runningP = null;
		int ioTime = 0;
		int cpuTime = 0;
		int cycle = 0;
		
		for(int i = 0; i < totalNumOfProc;i++) {
			if(procListSorted.get(i).getA() == 0) {
				procListSorted.get(i).ready = true;
				readyProcs.add(procListSorted.get(i));
			}
			else {
				procsNotReady.add(procListSorted.get(i));
			}
		}
		
		int loopTerminate = 0;
		
		//Main loop for FCFS
		while(loopTerminate < totalNumOfProc) {
			for(int i = 0; i < procsNotReady.size();i++) {
				if(procsNotReady.get(i).getA() == cycle) {
					procsNotReady.get(i).ready = true;
					readyProcs.add(procsNotReady.get(i));
				}
			}
			
			cycle++;
			
			//There is no current running process
			if(runningP == null && !readyProcs.isEmpty()) {
				runningP = readyProcs.poll();
				if( runningP != null) {
					runningP.ready = false;
					runningP.running = true;
					runningP.burstTime = randomOS(runningP.getB());
					if(runningP.burstTime > runningP.getTimeLeft())
						runningP.burstTime = runningP.getTimeLeft();
					runningP.ioBurstTime = runningP.burstTime * runningP.io;
				}
			}
			
			if( runningP != null) {
				runningP.burstTime--;
				runningP.setTimeLeft();
				cpuTime++;
			}
			
			if(verbose) {
				System.out.print("Current cycle "+cycle+":  ");
				for( Process e: procListSorted) {
					System.out.print(e.getStatus()+"  ");
				}
				System.out.println();
			}
			
			if(!readyProcs.isEmpty()) {
				for(Process e: readyProcs)
					e.increaseWait();
			}
			
			if(!procsBlocked.isEmpty()) {
				ioTime++;
				int add = 0;
				List<Process> addToReady = new ArrayList<Process>();
				Process[] arr = procsBlocked.toArray(new Process[0]);
				for(int i = 0; i < arr.length;i++) {
					arr[i].ioBurstTime--;
					arr[i].ioTime++;
					if(arr[i].ioBurstTime == 0) {
						add++;
						arr[i].blocked = false;
						arr[i].ready = true;
						addToReady.add(arr[i]);
						procsBlocked.remove(arr[i]);
					}
				}
				//If only one process is no longer blocked then do not need to sort by priority
				if(add == 1)
					readyProcs.add(addToReady.get(0));
				else {
					Collections.sort(addToReady, new TieBreakRule());
					
					readyProcs.addAll(addToReady);
				}
			}
			
			if( runningP != null) {
				//If the process has terminated (CPU time = 0)
				if(runningP.getTimeLeft() == 0) {
					runningP.blocked = false;
					runningP.running = false;
					runningP.ready = false;
					runningP.done = true;
					runningP.setFinTime(cycle);
					runningP.setTaTime(runningP.getFinTime()-runningP.getA());
					runningP = null;
					loopTerminate++;
				}
				//The burst time = 0 but the process is not terminated.  Block this process
				else {
					if(runningP.burstTime <= 0 && runningP.getTimeLeft() != 0) {
						runningP.blocked = true;
						runningP.ready = false;
						runningP.running = false;
						procsBlocked.add(runningP);
						runningP = null;
					}
				}
			}
		} //end of while loop
		
		int totalTaTime = 0;
		int totalWaitingTime = 0;
		System.out.println("The scheduling algorithim used was First Come First Serve\n");
		
		for(int i = 0; i < procListSorted.size(); i++) {
			System.out.println("Process "+i+":");
			System.out.println(procListSorted.get(i).toString());
			System.out.println("Finishing time: "+procListSorted.get(i).getFinTime());
			System.out.println("Turnaround time: "+procListSorted.get(i).getTaTime());
			System.out.println("I/O time: "+procListSorted.get(i).ioTime);
			System.out.println("Waiting time: "+procListSorted.get(i).getWait());
			totalTaTime += procListSorted.get(i).getTaTime();
			totalWaitingTime += procListSorted.get(i).getWait();
			System.out.println();
		}
		
		System.out.println("Summary data:");
		System.out.println("Finishing time: "+cycle);
		System.out.println("CPU Utilization: "+(double)cpuTime/cycle);
		System.out.println("I/O Utilization: "+(double)ioTime/cycle);
		System.out.println("Throughput: "+(double)procListSorted.size()*100/cycle+" processes per hundred cycles");
		System.out.println("Average turnaround time: "+(double)totalTaTime/procListSorted.size());
		System.out.println("Average waiting time: "+(double)totalWaitingTime/procListSorted.size());
		System.out.println("___________________________");
	}
	
	//Round Robin implementation
	public static void RR(ArrayList<Process> procListSorted) throws FileNotFoundException{
		System.out.println("Next scheduling algorithm");
		List<Process> procsNotReady = new ArrayList<Process>();
		List<Process> procsBlocked = new ArrayList<Process>();
		List<Process> multReadyProcs = new ArrayList<Process>();
		Queue<Process> readyProcs = new LinkedList<Process>();
		
		Process runningP = null;
		int ioTime = 0;
		int cpuTime = 0;
		int cycle = 0;
		int quantum = 2;
		
		for(int i = 0; i < totalNumOfProc;i++) {
			if(procListSorted.get(i).getA() == 0) {
				procListSorted.get(i).ready = true;
				readyProcs.add(procListSorted.get(i));
			}
			else {
				procsNotReady.add(procListSorted.get(i));
			}
		}
		
		int loopTerminate = 0;
		
		//Main loop for RR
		while(loopTerminate < totalNumOfProc) {
			for(int i = 0; i < procsNotReady.size();i++) {
				if(procsNotReady.get(i).getA() == cycle) {
					procsNotReady.get(i).ready = true;
					readyProcs.add(procsNotReady.get(i));
				}
			}
			
			cycle++;
			
			//There is no current running process
			if(runningP == null && !readyProcs.isEmpty()) {
				if(quantum != 2)
					quantum = 2;
				runningP = readyProcs.poll();
				runningP.ready = false;
				runningP.running = true;
				if( runningP != null && runningP.burstTime<=0) {
					runningP.burstTime = randomOS(runningP.getB());
					if(runningP.burstTime > runningP.getTimeLeft())
						runningP.burstTime = runningP.getTimeLeft();
					runningP.ioBurstTime = runningP.burstTime * runningP.io;
				}
			}
			
			if( runningP != null) {
				runningP.burstTime--;
				runningP.setTimeLeft();
				cpuTime++;
			}
			
			if(verbose) {
				System.out.print("Current cycle "+cycle+":  ");
				for( Process e: procListSorted) {
					int stat = 0;
					switch(e.getStatus()) {
					case "ready":
						stat = 0;
						break;
					case "running":
						stat = quantum;
						break;
					case "blocked":
						stat = e.ioBurstTime;
						break;
					default:
						stat = 0;
					}
					System.out.print(e.getStatus()+" "+stat+"  ");
				}
				System.out.println();
			}
			
			quantum--;
			
			if(!readyProcs.isEmpty()) {
				for(Process e: readyProcs)
					e.increaseWait();
			}
			
			if(!procsBlocked.isEmpty()) {
				ioTime++;
				int add = 0;
				List<Process> addToReady = new ArrayList<Process>();
				Process[] arr = procsBlocked.toArray(new Process[0]);
				for(int i = 0; i < arr.length;i++) {
					arr[i].ioBurstTime--;
					arr[i].ioTime++;
					if(arr[i].ioBurstTime == 0) {
						add++;
						arr[i].blocked = false;
						arr[i].ready = true;
						addToReady.add(arr[i]);
						procsBlocked.remove(arr[i]);
					}
				}
				//If only one process is no longer blocked then do not need to sort by priority
				if(add == 1)
					multReadyProcs.add(addToReady.get(0));
				else if(add  > 1){
					Collections.sort(addToReady, new TieBreakRule());
					
					for(int k = 0; k < addToReady.size();k++) {
						multReadyProcs.add(addToReady.get(k));
					}
				}
			} //end of block if statement
			
				if( runningP != null) {
					//If the process has terminated (CPU time = 0)
					if(runningP.getTimeLeft() == 0) {
						runningP.blocked = false;
						runningP.running = false;
						runningP.ready = false;
						runningP.done = true;
						runningP.setFinTime(cycle);
						runningP.setTaTime(runningP.getFinTime()-runningP.getA());
						runningP = null;
						loopTerminate++;
						quantum = 2;
					}
					//The burst time = 0 but the process is not terminated.  Block this process
					else if(runningP.burstTime <= 0 && runningP.getTimeLeft() != 0) {
							runningP.blocked = true;
							runningP.ready = false;
							runningP.running = false;
							procsBlocked.add(runningP);
							runningP = null;
							quantum = 2;
						}
					else {
						if(quantum <= 0) {
							runningP.ready = true;
							runningP.running = false;
							multReadyProcs.add(runningP);
							runningP = null;
							quantum = 2;
						}
					}
				}

				//Multiple processes became ready at once.  Use the tie break rule
				if(multReadyProcs.size() > 1) {
					Collections.sort(multReadyProcs, new TieBreakRule());
				}
				
				readyProcs.addAll(multReadyProcs);
				multReadyProcs.clear();
		} //end of while loop
		
		int totalTaTime = 0;
		int totalWaitingTime = 0;
		
		System.out.println("The scheduling algorithm used was Round Robin\n");
		for(int i = 0; i < procListSorted.size(); i++) {
			System.out.println("Process "+i+":");
			System.out.println(procListSorted.get(i).toString());
			System.out.println("Finishing time: "+procListSorted.get(i).getFinTime());
			System.out.println("Turnaround time: "+procListSorted.get(i).getTaTime());
			System.out.println("I/O time: "+procListSorted.get(i).ioTime);
			System.out.println("Waiting time: "+procListSorted.get(i).getWait());
			totalTaTime += procListSorted.get(i).getTaTime();
			totalWaitingTime += procListSorted.get(i).getWait();
			System.out.println();
		}
		
		System.out.println("Summary data:");
		System.out.println("Finishing time: "+cycle);
		System.out.println("CPU Utilization: "+(double)cpuTime/cycle);
		System.out.println("I/O Utilization: "+(double)ioTime/cycle);
		System.out.println("Throughput: "+(double)procListSorted.size()*100/cycle+" processes per hundred cycles");
		System.out.println("Average turnaround time: "+(double)totalTaTime/procListSorted.size());
		System.out.println("Average waiting time: "+(double)totalWaitingTime/procListSorted.size());
		System.out.println("___________________________");

	}
	
	//Last Come First Serve Implementation
	public static void LCFS(ArrayList<Process> procListSorted) throws FileNotFoundException{
		System.out.println("Next scheduling algorithm");
		List<Process> procsNotReady = new ArrayList<Process>();
		List<Process> procsBlocked = new ArrayList<Process>();
		LinkedList<Process> readyProcs = new LinkedList<Process>();
		Process runningP = null;
		int ioTime = 0;
		int cpuTime = 0;
		int cycle = 0;
		
		for(int i = 0; i < totalNumOfProc; i++) {
			if(procListSorted.get(i).getA() == 0) {
				procListSorted.get(i).ready = true;
				readyProcs.addFirst(procListSorted.get(i));
			}
			else {
				procsNotReady.add(procListSorted.get(i));
			}
		}
		
		int loopTerminate = 0;
		
		//Main loop for LCFS
		while(loopTerminate < totalNumOfProc) {
			for(int i = 0; i < procsNotReady.size();i++) {
				if(procsNotReady.get(i).getA() == cycle) {
					procsNotReady.get(i).ready = true;
					readyProcs.addFirst(procsNotReady.get(i));
				}
			}
			
			cycle++;
			
			//There is no current running process
			if(runningP == null && !readyProcs.isEmpty()) {
				//runningP = readyProcs.remove();
				runningP = readyProcs.removeLast();
				if( runningP != null) {
					runningP.ready = false;
					runningP.running = true;
					runningP.burstTime = randomOS(runningP.getB());
					if(runningP.burstTime > runningP.getTimeLeft())
						runningP.burstTime = runningP.getTimeLeft();
					runningP.ioBurstTime = runningP.burstTime * runningP.io;
				}
			}
			
			if(verbose) {
				System.out.print("Current cycle "+cycle+":  ");
				for( Process e: procListSorted) {
					int stat = 0;
					switch(e.getStatus()) {
					case "ready":
						stat = 0;
						break;
					case "running":
						stat = e.burstTime;
						break;
					case "blocked":
						stat = e.ioBurstTime;
						break;
					default:
						stat = 0;
					}
					System.out.print(e.getStatus()+" "+stat+"  ");
				}
				System.out.println();
			}
			
			if( runningP != null) {
				runningP.burstTime--;
				runningP.setTimeLeft();
				cpuTime++;
			}
			
			if(!readyProcs.isEmpty()) {
				for(Process e: readyProcs)
					e.increaseWait();
			}
			
			//If there are blocked processes
			if(!procsBlocked.isEmpty()) {
				ioTime++;
				int add = 0;
				List<Process> addToReady = new ArrayList<Process>();
				Process[] arr = procsBlocked.toArray(new Process[0]);
				for(int i = 0; i < arr.length;i++) {
					arr[i].ioBurstTime--;
					arr[i].ioTime++;
					if(arr[i].ioBurstTime == 0) {
						add++;
						arr[i].blocked = false;
						arr[i].ready = true;
						addToReady.add(arr[i]);
						procsBlocked.remove(arr[i]);
					}
				}
				//If only one process is no longer blocked then do not need to sort by priority
				//else use tie break rule
				if(add == 1)
					readyProcs.addLast(addToReady.get(0));
				else if(add > 1) {
						Collections.sort(addToReady, new TieBreakRule());
						
						for(int z = addToReady.size()-1; z >= 0; z--) {
							readyProcs.addLast(addToReady.get(z));
						}
				}
			}
			
			if( runningP != null) {
				//If the process has terminated (CPU time = 0)
				if(runningP.getTimeLeft() == 0) {
					runningP.blocked = false;
					runningP.running = false;
					runningP.ready = false;
					runningP.done = true;
					runningP.setFinTime(cycle);
					runningP.setTaTime(runningP.getFinTime()-runningP.getA());
					runningP = null;
					loopTerminate++;
				}
				//The burst time = 0 but the process is not terminated.  Block this process
				else {
					if(runningP.burstTime <= 0 && runningP.getTimeLeft() != 0) {
						runningP.blocked = true;
						runningP.ready = false;
						runningP.running = false;
						procsBlocked.add(runningP);
						runningP = null;
					}
				}
			}
		} //end of while loop
		
		int totalTaTime = 0;
		int totalWaitingTime = 0;
		System.out.println("The scheduling algorithm used was Last Come First Serve\n");
		
		for(int i = 0; i < procListSorted.size(); i++) {
			System.out.println("Process "+i+":");
			System.out.println(procListSorted.get(i).toString());
			System.out.println("Finishing time: "+procListSorted.get(i).getFinTime());
			System.out.println("Turnaround time: "+procListSorted.get(i).getTaTime());
			System.out.println("I/O time: "+procListSorted.get(i).ioTime);
			System.out.println("Waiting time: "+procListSorted.get(i).getWait());
			totalTaTime += procListSorted.get(i).getTaTime();
			totalWaitingTime += procListSorted.get(i).getWait();
			System.out.println();
		}
		
		System.out.println("Summary data:");
		System.out.println("Finishing time: "+cycle);
		System.out.println("CPU Utilization: "+(double)cpuTime/cycle);
		System.out.println("I/O Utilization: "+(double)ioTime/cycle);
		System.out.println("Throughput: "+(double)procListSorted.size()*100/cycle+" processes per hundred cycles");
		System.out.println("Average turnaround time: "+(double)totalTaTime/procListSorted.size());
		System.out.println("Average waiting time: "+(double)totalWaitingTime/procListSorted.size());
		System.out.println("___________________________");
	}
	
	public static void HPRN(ArrayList<Process> procListSorted) throws FileNotFoundException{
		System.out.println("Final scheduling algorithm");
		List<Process> procsNotReady = new ArrayList<Process>();
		List<Process> procsBlocked = new ArrayList<Process>();
		List<Process> readyProcs = new ArrayList<Process>();
		
		Process runningP = null;
		int ioTime = 0;
		int cpuTime = 0;
		int cycle = 0;
		
		for(int i = 0; i < totalNumOfProc;i++) {
			if(procListSorted.get(i).getA() == 0) {
				procListSorted.get(i).ready = true;
				readyProcs.add(procListSorted.get(i));
			}
			else {
				procsNotReady.add(procListSorted.get(i));
			}
		}
		
		int loopTerminate = 0;
		
		//Main loop for HPRN
		while(loopTerminate < totalNumOfProc) {
			for(int i = 0; i < procsNotReady.size();i++) {
				if(procsNotReady.get(i).getA() == cycle) {
					procsNotReady.get(i).ready = true;
					readyProcs.add(procsNotReady.get(i));
				}
			}
			
			cycle++;
			
			//There is no current running process
			if(runningP == null && !readyProcs.isEmpty()) {
				runningP = readyProcs.remove(0);
				if( runningP != null) {
					runningP.ready = false;
					runningP.running = true;
					runningP.burstTime = randomOS(runningP.getB());
					if(runningP.burstTime > runningP.getTimeLeft())
						runningP.burstTime = runningP.getTimeLeft();
					runningP.ioBurstTime = runningP.burstTime * runningP.io;
				}
			}
			
			if(verbose) {
				System.out.print("Current cycle "+cycle+":  ");
				for( Process e: procListSorted) {
					int stat = 0;
					switch(e.getStatus()) {
					case "ready":
						stat = 0;
						break;
					case "running":
						stat = e.burstTime;
						break;
					case "blocked":
						stat = e.ioBurstTime;
						break;
					default:
						stat = 0;
					}
					System.out.print(e.getStatus()+" "+stat+"  ");
				}
				System.out.println();
			}
			
			if( runningP != null) {
				runningP.burstTime--;
				runningP.setTimeLeft();
				cpuTime++;
				runningP.incrementCurrWait();
			}
			
			if(!readyProcs.isEmpty()) {
				for(Process e: readyProcs) {
					e.increaseWait();
					e.setCurrTime(cycle);
				}
			}
			
			//if there are blocked processes
			if(!procsBlocked.isEmpty()) {
				ioTime++;
				int add = 0;
				List<Process> addToReady = new ArrayList<Process>();
				Process[] arr = procsBlocked.toArray(new Process[0]);
				for(int i = 0; i < arr.length;i++) {
					arr[i].ioBurstTime--;
					arr[i].ioTime++;
					if(arr[i].ioBurstTime == 0) {
						add++;
						arr[i].blocked = false;
						arr[i].ready = true;
						addToReady.add(arr[i]);
						procsBlocked.remove(arr[i]);
					}
				}
				//If only one process is no longer blocked then do not need to sort by priority
				if(add == 1)
					readyProcs.add(addToReady.get(0));
				else {
					Collections.sort(addToReady, new TieBreakRule());
					
					for(int z = 0; z < addToReady.size(); z++)
						readyProcs.add(addToReady.get(z));
				}
			}
			
			if( runningP != null) {
				//If the process has terminated (CPU time = 0)
				if(runningP.getTimeLeft() == 0) {
					runningP.blocked = false;
					runningP.running = false;
					runningP.ready = false;
					runningP.done = true;
					runningP.setFinTime(cycle);
					runningP.setTaTime(runningP.getFinTime()-runningP.getA());
					runningP = null;
					loopTerminate++;
				}
				//The burst time = 0 but the process is not terminated.  Block this process
				else {
					if(runningP.burstTime <= 0 && runningP.getTimeLeft() != 0) {
						runningP.blocked = true;
						runningP.ready = false;
						runningP.running = false;
						procsBlocked.add(runningP);
						runningP = null;
					}
				}
			}
			
			for(Process e: readyProcs)
				e.currentCycle = cycle;
			Collections.sort(readyProcs, new HPRNComparator());
		} //end of while loop
		
		int totalTaTime = 0;
		int totalWaitingTime = 0;
		System.out.println("The scheduling algorithm used was HPRN\n");
		for(int i = 0; i < procListSorted.size(); i++) {
			System.out.println("Process "+i+":");
			System.out.println(procListSorted.get(i).toString());
			System.out.println("Finishing time: "+procListSorted.get(i).getFinTime());
			System.out.println("Turnaround time: "+procListSorted.get(i).getTaTime());
			System.out.println("I/O time: "+procListSorted.get(i).ioTime);
			System.out.println("Waiting time: "+procListSorted.get(i).getWait());
			totalTaTime += procListSorted.get(i).getTaTime();
			totalWaitingTime += procListSorted.get(i).getWait();
			System.out.println();
		}
		
		System.out.println("Summary data:");
		System.out.println("Finishing time: "+cycle);
		System.out.println("CPU Utilization: "+(double)cpuTime/cycle);
		System.out.println("I/O Utilization: "+(double)ioTime/cycle);
		System.out.println("Throughput: "+(double)procListSorted.size()*100/cycle+" processes per hundred cycles");
		System.out.println("Average turnaround time: "+(double)totalTaTime/procListSorted.size());
		System.out.println("Average waiting time: "+(double)totalWaitingTime/procListSorted.size());
	}
	
	//pseudocode for helper method for HPRN to find the index of the process with the highest Penalty ratio
//		public static int findProcWithHighestPenaltyRatio(List<Process> temp, int currCycle){
//			int index = 0;
//			
//			for(int i = 1; i < temp.size(); i++) {
//				if(temp.get(i).getRatio(currCycle) > temp.get(i-1).getRatio(currCycle))
//					index = i;
//			}
//			
//			return index;
//		}
	
}
