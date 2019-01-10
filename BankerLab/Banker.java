import java.util.*;
import java.io.*;

public class Banker {

	public static void main(String[] args) throws FileNotFoundException {
		//System.out.println(args[0]);
		Scanner file = new Scanner(new File(args[0]) );
		
		int totalNumOfTasks = file.nextInt();
		int totalNumOfResources = file.nextInt();
		int resourceIDCounter = 1;
		int taskIDCounter = 1;
		ArrayList<Task> allTasks = new ArrayList<Task>();
		ArrayList<Resource> allResources = new ArrayList<Resource>();
		ArrayList<Task> allTasksO = new ArrayList<Task>();
		ArrayList<Resource> allResourcesO = new ArrayList<Resource>();
		
		//so the IDs can match up with the indexes we have an empty placeholder at index 0.
		Task unused = new Task();
		Resource unused2 = new Resource();
		allTasks.add(unused);
		allResources.add(unused2);
		allTasksO.add(unused);
		allResourcesO.add(unused2);
		
		for(int i = 0; i < totalNumOfTasks; i++) {
			Task temp = new Task();
			Task temp2 = new Task();
			temp.id = taskIDCounter;
			temp2.id = taskIDCounter;
			allTasks.add(temp);
			allTasksO.add(temp2);
			taskIDCounter++;
		}
		
		for(int i = 0; i < totalNumOfResources;i++) {
			int tempUnits = file.nextInt();
			Resource temp = new Resource(resourceIDCounter,tempUnits);
			Resource temp2 = new Resource(resourceIDCounter, tempUnits);
			allResources.add(temp);
			allResourcesO.add(temp2);
			resourceIDCounter++;
		}
		
		while(file.hasNext()) {
			String walk = file.next();
			
			if(walk.equals("initiate")) {
				int id = file.nextInt();
				int claimFrom = file.nextInt();
				int claim = file.nextInt();
				allTasks.get(id).instructions.add("initiate" + " " + id+ " "+claimFrom + " " + claim);
				allTasksO.get(id).instructions.add("initiate" + " " + id+ " "+claimFrom + " " + claim);
			}
			else if(walk.equals("request")) {
				int taskID = file.nextInt();
				int resourceID = file.nextInt();
				int amount = file.nextInt();
				allTasks.get(taskID).instructions.add("request" + " "+ taskID+" "+resourceID + " "+amount);
				allTasksO.get(taskID).instructions.add("request" + " "+ taskID+" "+resourceID + " "+amount);
			}
			else if(walk.equals("release")) {
				int taskID = file.nextInt();
				int resourceID = file.nextInt();
				int amount = file.nextInt();
				allTasks.get(taskID).instructions.add("release" + " "+ taskID+" "+resourceID + " "+amount);
				allTasksO.get(taskID).instructions.add("release" + " "+ taskID+" "+resourceID + " "+amount);
			}
			else if(walk.equals("compute")) {
				int taskID = file.nextInt();
				int numOfCycles = file.nextInt();
				int unusedSkip = file.nextInt();
				allTasks.get(taskID).instructions.add("compute" + " "+ numOfCycles);
				allTasksO.get(taskID).instructions.add("compute" + " "+ numOfCycles);
			}
			else { //walk would then equal terminate
				int taskID = file.nextInt();
				int unusedSkip = file.nextInt();
				int unusedSkip2 = file.nextInt();
				allTasks.get(taskID).instructions.add("terminate" + " ");
				allTasksO.get(taskID).instructions.add("terminate" + " ");
			}
			//System.out.println(walk);
		}
		
		/*check to make sure that the Tasks have the right instructions and resources have right units.
		for(int i = 1; i < allTasks.size(); i++) {
			System.out.println(allTasks.get(i).toString() );
		}
		
		for(int i = 1; i < allResources.size(); i++) {
			System.out.println(allResources.get(i).toString() );
		}*/
		
		optimisticManager(allTasksO, allResourcesO, totalNumOfTasks);
		System.out.println();
		dijsktraBanker(allTasks, allResources, totalNumOfTasks);
	}
	
	public static void optimisticManager(ArrayList<Task> taskList, ArrayList<Resource> resourceList, int totalTasks) {
		System.out.println("FIFO results:");
		
		ArrayList<Task> blockedTasks = new ArrayList<Task>();
		ArrayList<Task> completedTasks = new ArrayList<Task>();
		ArrayList<Task> waitingTasks = new ArrayList<Task>();
		int[] releasedResources = new int[resourceList.size()]; 
		int numOfCompletedTasks = 0;
		int numOfCurrentTasks = totalTasks;
		int cycle = 0;
		
		while(numOfCompletedTasks != totalTasks) {
			//System.out.println("During cycle "+cycle +"-"+(cycle+1));
			ArrayList<Task> readyTasks = new ArrayList<Task>(); 
			int deadlocked = 0;
			
			//check blocked tasks first
			if(!blockedTasks.isEmpty()) {
				for(int k = 0; k < blockedTasks.size();) {
					String fullInstruction = blockedTasks.get(k).instructions.get(0);
					int firstSpace = fullInstruction.indexOf(" ");
					String specificInstr = fullInstruction.substring(0, firstSpace);
					int secondSpace = fullInstruction.indexOf(" ", firstSpace + 1);
					int thirdSpace = fullInstruction.indexOf(" ", secondSpace + 1);
					
					int taskID = Integer.parseInt(fullInstruction.substring(firstSpace + 1, secondSpace) );
					int resourceID = Integer.parseInt(fullInstruction.substring(secondSpace + 1, thirdSpace) );
					int amount = Integer.parseInt(fullInstruction.substring(thirdSpace + 1) );
					
					if(amount > resourceList.get(resourceID).unitsAvailable) {
						blockedTasks.get(k).incrementTimeWaiting();
						k++;
						deadlocked++;
					}
					else {
						blockedTasks.get(k).currentUnits.set(resourceID, blockedTasks.get(k).currentUnits.get(resourceID) + amount);
						resourceList.get(resourceID).subtractUnitsAvailable(amount);
						blockedTasks.get(k).instructions.remove(0);
						readyTasks.add(blockedTasks.remove(k));
					}
				}
			}
			//check tasks that are doing the computing instruction
			if(!waitingTasks.isEmpty()) {
				for(int k = 0; k < waitingTasks.size();) {
					waitingTasks.get(k).computationTime--;
					
					//computation is finished
					if(waitingTasks.get(k).computationTime <= 0) {
						waitingTasks.get(k).setComputeTime(0);
						readyTasks.add(waitingTasks.remove(k));
					}
					else
						k++;
				}
			}
			//read and process the next instruction for each task
			for(int i = 1; i < taskList.size();) {
				String fullInstruction = taskList.get(i).instructions.get(0);
				int firstSpace = fullInstruction.indexOf(" ");
				String specificInstr = fullInstruction.substring(0, firstSpace);
				
				if(specificInstr.equals("terminate")) {
					taskList.get(i).setTimeTaken(cycle);
					completedTasks.add(taskList.get(i));
					taskList.remove(i);
					numOfCompletedTasks++;
					numOfCurrentTasks--;
				}
				else {
					
					if(specificInstr.equals("initiate")) {
						int secondSpace = fullInstruction.indexOf(" ", firstSpace + 1);
						int thirdSpace = fullInstruction.indexOf(" ", secondSpace + 1);
						int taskID = Integer.parseInt(fullInstruction.substring(firstSpace + 1, secondSpace) );
						int resourceID = Integer.parseInt(fullInstruction.substring(secondSpace + 1, thirdSpace) );
						int claim = Integer.parseInt(fullInstruction.substring(thirdSpace + 1) );
						
						taskList.get(i).claims.put(resourceList.get(resourceID), claim);
						taskList.get(i).currentUnits.add(0);
						taskList.get(i).instructions.remove(0);
						i++;
					}
					else if(specificInstr.equals("request")) {
						int secondSpace = fullInstruction.indexOf(" ", firstSpace + 1);
						int thirdSpace = fullInstruction.indexOf(" ", secondSpace + 1);
						int taskID = Integer.parseInt(fullInstruction.substring(firstSpace + 1, secondSpace) );
						int resourceID = Integer.parseInt(fullInstruction.substring(secondSpace + 1, thirdSpace) );
						int amount = Integer.parseInt(fullInstruction.substring(thirdSpace + 1) );
						
						//check if the resource has enough current units
						if(amount > resourceList.get(resourceID).unitsAvailable) {
							taskList.get(i).incrementTimeWaiting();
							blockedTasks.add(taskList.remove(i));
							deadlocked++;
						}
						else {
							taskList.get(i).currentUnits.set(resourceID, taskList.get(i).currentUnits.get(resourceID) + amount);
							resourceList.get(resourceID).subtractUnitsAvailable(amount);
							taskList.get(i).instructions.remove(0);
							i++;
						}
					}
					else if(specificInstr.equals("release")) {
						int secondSpace = fullInstruction.indexOf(" ", firstSpace + 1);
						int thirdSpace = fullInstruction.indexOf(" ", secondSpace + 1);
						int taskID = Integer.parseInt(fullInstruction.substring(firstSpace + 1, secondSpace) );
						int resourceID = Integer.parseInt(fullInstruction.substring(secondSpace + 1, thirdSpace) );
						int amount = Integer.parseInt(fullInstruction.substring(thirdSpace + 1) );
						releasedResources[resourceID] += amount;
						taskList.get(i).currentUnits.set(resourceID, taskList.get(i).currentUnits.get(resourceID) - amount);
						taskList.get(i).instructions.remove(0);
						i++;
					}
					else if(specificInstr.equals("compute")) {
						int numOfCycles = Integer.parseInt(fullInstruction.substring(firstSpace + 1) );
						taskList.get(i).setComputeTime(numOfCycles-1);
						if(taskList.get(i).computationTime == 0) {
							taskList.get(i).instructions.remove(0);
							readyTasks.add(taskList.remove(i));
						}
						else {
							taskList.get(i).instructions.remove(0);
							waitingTasks.add(taskList.remove(i));
						}
						//i++;
					}
				} //end of checking instructions that were not "terminate"
				
			}//end of loop checking all tasks' instructions in taskList also represents the end of a cycle
			
			//check to see if any tasks were able to complete their next instruction
			//and how many tasks must be aborted
			if(deadlocked == numOfCurrentTasks) {
				Collections.sort(blockedTasks, new TaskSortId());
				while(0 < blockedTasks.size()) {
					blockedTasks.get(0).aborted = true;
					
					for(int z = 1; z < blockedTasks.get(0).currentUnits.size(); z++) {
						releasedResources[z] += blockedTasks.get(0).currentUnits.get(z);
					}
					System.out.println("Aborted task "+blockedTasks.get(0).id+".  Task must be aborted to break the deadlock");
					completedTasks.add(blockedTasks.remove(0));
					numOfCompletedTasks++;
					numOfCurrentTasks--;
					
					for(int y = 1; y < releasedResources.length; y++) {
						if(releasedResources[y] == 0)
							continue;
						else {
							resourceList.get(y).addUnitsAvailable(releasedResources[y]);
							releasedResources[y] = 0;
						}
					}
					//check to see if the tasks are still deadlocked after removing the lowest numbered Task.
					boolean deadlockBroken = false;
					for(int y = 0; y < blockedTasks.size();y++) {
						String fullInstruction = blockedTasks.get(y).instructions.get(0);
						int firstSpace = fullInstruction.indexOf(" ");
						String specificInstr = fullInstruction.substring(0, firstSpace);
						int secondSpace = fullInstruction.indexOf(" ", firstSpace + 1);
						int thirdSpace = fullInstruction.indexOf(" ", secondSpace + 1);
						
						int taskID = Integer.parseInt(fullInstruction.substring(firstSpace + 1, secondSpace) );
						int resourceID = Integer.parseInt(fullInstruction.substring(secondSpace + 1, thirdSpace) );
						int amount = Integer.parseInt(fullInstruction.substring(thirdSpace + 1) );
						
						if(amount > resourceList.get(resourceID).unitsAvailable) {
							continue;
						}
						else {
							readyTasks.add(blockedTasks.remove(y));
							deadlockBroken = true;
							break;
						}
					}
					
					if(deadlockBroken)
						break;
					else
						continue;
				}
			}
			
			while(!readyTasks.isEmpty()) {
				taskList.add(readyTasks.remove(0));
			}
			
			for(int k = 1; k < releasedResources.length; k++) {
				if(releasedResources[k] == 0)
					continue;
				else {
					resourceList.get(k).addUnitsAvailable(releasedResources[k]);
					releasedResources[k] = 0;
				}
			}
			cycle++;
		}// end of while loop so all the tasks' instructions are completed
		
		int totalTime = 0;
		int totalWaitingTime = 0;
		double overallPercentage = 0.0;
		Collections.sort(completedTasks, new TaskSortId());
		
		//print out the results
		for(int i = 0; i < completedTasks.size(); i++) {
			if(completedTasks.get(i).aborted)
				System.out.println("Task " + completedTasks.get(i).id + ": aborted");
			else {
				double percentage = ( ( (double)completedTasks.get(i).timeWaiting)/completedTasks.get(i).timeTaken) * 100;
				System.out.println("Task " + completedTasks.get(i).id + ": time taken-" + completedTasks.get(i).timeTaken + ", time spent waiting-" + completedTasks.get(i).timeWaiting + ", percentage waiting-"+ percentage + "%");
				totalTime += completedTasks.get(i).timeTaken;
				totalWaitingTime +=  completedTasks.get(i).timeWaiting;
			}
		}
		overallPercentage = ( ( (double)(totalWaitingTime) )/totalTime) *  100;
		System.out.println("Total time taken:" + totalTime + ". Total time spent waiting:" + totalWaitingTime + ". Percentage waiting:"+ overallPercentage + "%");
	}
	
	public static void dijsktraBanker(ArrayList<Task> taskList, ArrayList<Resource> resourceList, int totalTasks) {
		System.out.println("Dijsktra's Banker Algorithm results:");
		
		ArrayList<Task> blockedTasks = new ArrayList<Task>();
		ArrayList<Task> completedTasks = new ArrayList<Task>();
		ArrayList<Task> waitingTasks = new ArrayList<Task>();
		int[] releasedResources = new int[resourceList.size()]; 
		int numOfCompletedTasks = 0;
		int cycle = 0;
		
		while(numOfCompletedTasks != totalTasks) {
			//System.out.println("During cycle "+cycle +"-"+(cycle+1));
			ArrayList<Task> readyTasks = new ArrayList<Task>(); 
			
			//check blocked tasks first
			if(!blockedTasks.isEmpty()) {
				for(int k = 0; k < blockedTasks.size();) {
					String fullInstruction = blockedTasks.get(k).instructions.get(0);
					int firstSpace = fullInstruction.indexOf(" ");
					String specificInstr = fullInstruction.substring(0, firstSpace);
					int secondSpace = fullInstruction.indexOf(" ", firstSpace + 1);
					int thirdSpace = fullInstruction.indexOf(" ", secondSpace + 1);
					
					int taskID = Integer.parseInt(fullInstruction.substring(firstSpace + 1, secondSpace) );
					int resourceID = Integer.parseInt(fullInstruction.substring(secondSpace + 1, thirdSpace) );
					int amount = Integer.parseInt(fullInstruction.substring(thirdSpace + 1) );
					
					if(amount > resourceList.get(resourceID).unitsAvailable || blockedTasks.get(k).claims.get(resourceList.get(resourceID)) - (amount + blockedTasks.get(k).currentUnits.get(resourceID)) > resourceList.get(resourceID).unitsAvailable - amount) {
						blockedTasks.get(k).incrementTimeWaiting();
						k++;
					}
					else {
						//have to make sure that all claims can be done if this request goes through
						boolean unblocked = true;
						for(Map.Entry<Resource, Integer> entry : blockedTasks.get(k).claims.entrySet()) {
							Resource r = entry.getKey();
							int claim = entry.getValue();
							if(claim > r.unitsAvailable)
								unblocked = false;
						}
						if(unblocked) {
							blockedTasks.get(k).currentUnits.set(resourceID, blockedTasks.get(k).currentUnits.get(resourceID) + amount);
							resourceList.get(resourceID).subtractUnitsAvailable(amount);
							blockedTasks.get(k).instructions.remove(0);
							readyTasks.add(blockedTasks.remove(k));
						}
						else {
							blockedTasks.get(k).incrementTimeWaiting();
							k++;
						}
					}
				}
			}
			//check tasks that are doing the computing instruction
			if(!waitingTasks.isEmpty()) {
				for(int k = 0; k < waitingTasks.size();) {
					waitingTasks.get(k).computationTime--;
					
					if(waitingTasks.get(k).computationTime <= 0) {
						waitingTasks.get(k).setComputeTime(0);
						readyTasks.add(waitingTasks.remove(k));
					}
					else
						k++;
				}
			}
			
			//loop to get the next instruction for each Task.
			for(int i = 1; i < taskList.size();) {
				String fullInstruction = taskList.get(i).instructions.get(0);
				int firstSpace = fullInstruction.indexOf(" ");
				String specificInstr = fullInstruction.substring(0, firstSpace);
				
				if(specificInstr.equals("terminate")) {
					taskList.get(i).setTimeTaken(cycle);
					completedTasks.add(taskList.get(i));
					taskList.remove(i);
					numOfCompletedTasks++;
				}
				else {
	
					if(specificInstr.equals("initiate")) {
						int secondSpace = fullInstruction.indexOf(" ", firstSpace + 1);
						int thirdSpace = fullInstruction.indexOf(" ", secondSpace + 1);
						int taskID = Integer.parseInt(fullInstruction.substring(firstSpace + 1, secondSpace) );
						int resourceID = Integer.parseInt(fullInstruction.substring(secondSpace + 1, thirdSpace) );
						int claim = Integer.parseInt(fullInstruction.substring(thirdSpace + 1) );
						
						//have to abort this, because the initial claim exceeds total resources available
						if(claim > resourceList.get(resourceID).totalUnits) {
							taskList.get(i).aborted = true;
							System.out.println("Aborted task "+taskList.get(i).id+"'s initial claim greater than the amount of units resource has");
							completedTasks.add(taskList.remove(i));
							numOfCompletedTasks++;
						}
						else {
							taskList.get(i).claims.put(resourceList.get(resourceID), claim);
							taskList.get(i).currentUnits.add(0);
							taskList.get(i).instructions.remove(0);
							i++;
						}
					}
					else if(specificInstr.equals("request")) {
						int secondSpace = fullInstruction.indexOf(" ", firstSpace + 1);
						int thirdSpace = fullInstruction.indexOf(" ", secondSpace + 1);
						int taskID = Integer.parseInt(fullInstruction.substring(firstSpace + 1, secondSpace) );
						int resourceID = Integer.parseInt(fullInstruction.substring(secondSpace + 1, thirdSpace) );
						int amount = Integer.parseInt(fullInstruction.substring(thirdSpace + 1) );
						
						//check if the resource has enough current units and if the max claim would be able to be satisfied after receiving these units
						if( (amount + taskList.get(i).currentUnits.get(resourceID)) > taskList.get(i).claims.get(resourceList.get(resourceID) ) ) {
							taskList.get(i).aborted = true;
							releasedResources[resourceID] += taskList.get(i).currentUnits.get(resourceID);
							System.out.println("Aborted task "+taskList.get(i).id+"'s initial claim greater than the amount of units resource has");
							completedTasks.add(taskList.remove(i));
							numOfCompletedTasks++;
						}
						else if(amount > resourceList.get(resourceID).unitsAvailable || taskList.get(i).claims.get(resourceList.get(resourceID)) - (amount + taskList.get(i).currentUnits.get(resourceID)) > resourceList.get(resourceID).unitsAvailable - amount) {
							taskList.get(i).incrementTimeWaiting();
							blockedTasks.add(taskList.remove(i));
						}
						else {
							//everything is okay for this task to receive units that the task has requested
							taskList.get(i).currentUnits.set(resourceID, taskList.get(i).currentUnits.get(resourceID) + amount);
							resourceList.get(resourceID).subtractUnitsAvailable(amount);
							taskList.get(i).instructions.remove(0);
							i++;
						}
					}
					//add released units to array to be added to the Resources at the end of current cycle.
					else if(specificInstr.equals("release")) {
						int secondSpace = fullInstruction.indexOf(" ", firstSpace + 1);
						int thirdSpace = fullInstruction.indexOf(" ", secondSpace + 1);
						int taskID = Integer.parseInt(fullInstruction.substring(firstSpace + 1, secondSpace) );
						int resourceID = Integer.parseInt(fullInstruction.substring(secondSpace + 1, thirdSpace) );
						int amount = Integer.parseInt(fullInstruction.substring(thirdSpace + 1) );
						releasedResources[resourceID] += amount;
						taskList.get(i).currentUnits.set(resourceID, taskList.get(i).currentUnits.get(resourceID) - amount);
						taskList.get(i).instructions.remove(0);
						i++;
					}
					else if(specificInstr.equals("compute")) {
						int numOfCycles = Integer.parseInt(fullInstruction.substring(firstSpace + 1) );
						taskList.get(i).setComputeTime(numOfCycles-1);
						if(taskList.get(i).computationTime == 0) {
							taskList.get(i).instructions.remove(0);
							readyTasks.add(taskList.remove(i));
						}
						else {
							taskList.get(i).instructions.remove(0);
							waitingTasks.add(taskList.remove(i));
						}
					}
				} //end of checking instructions that were not "terminate"
				
			}//end of loop checking all tasks' instructions in taskList also represents the end of a cycle
			
			while(!readyTasks.isEmpty()) {
				taskList.add(readyTasks.remove(0));
			}
			
			for(int k = 1; k < releasedResources.length; k++) {
				if(releasedResources[k] == 0)
					continue;
				else {
					resourceList.get(k).addUnitsAvailable(releasedResources[k]);
					releasedResources[k] = 0;
				}
			}
			cycle++;
		}// end of while loop so all the tasks' instructions are completed
		
		int totalTime = 0;
		int totalWaitingTime = 0;
		double overallPercentage = 0.0;
		Collections.sort(completedTasks, new TaskSortId());
		
		//print the results
		for(int i = 0; i < completedTasks.size(); i++) {
			if(completedTasks.get(i).aborted)
				System.out.println("Task " + completedTasks.get(i).id + ": aborted");
			else {
				double percentage = ( ( (double)completedTasks.get(i).timeWaiting)/completedTasks.get(i).timeTaken) * 100;
				System.out.println("Task " + completedTasks.get(i).id + ": time taken-" + completedTasks.get(i).timeTaken + ", time spent waiting-" + completedTasks.get(i).timeWaiting + ", percentage waiting-"+ percentage + "%");
				totalTime += completedTasks.get(i).timeTaken;
				totalWaitingTime +=  completedTasks.get(i).timeWaiting;
			}
		}
		overallPercentage = ( ( (double)(totalWaitingTime) )/totalTime) *  100;
		System.out.println("Total time taken:" + totalTime + ". Total time spent waiting:" + totalWaitingTime + ". Percentage waiting:"+ overallPercentage + "%");
	}
}
