import java.util.*;

public class Linker {

	private static final int MAX_MACHINE_SIZE = 300;
	public static void main(String[] args) {
		
		Scanner input = new Scanner(System.in);
		ArrayList<String> inputHolder = new ArrayList<String>(); //list to hold each line of input
		System.out.println("Please enter input:");
		String addToHolder = input.nextLine(); //input
		
		//Loop to receive input
		while(!addToHolder.equals("end")){
			if(addToHolder.length() < 1){
				addToHolder = input.nextLine();
			}
			else{
			 inputHolder.add(addToHolder);
			 addToHolder = input.nextLine();
			}
		}
		
		input.close();
		int sizeOfHolder = inputHolder.size();
		int endOfAlgoCounter;  //the first number in the input
		int endChecker = 0;
		int numOfAddressesSoFar = 0;
		String rewrittenInput = "";
		Hashtable<String, Integer> symbolTable = new Hashtable<String, Integer>();
		ArrayList<String> addressPositions = new ArrayList<String>();
		
		//Rewriting the input so I can easily traverse through entire input
		//without having to guess what I am reading
		for(int i = 0; i < sizeOfHolder; i++){
			String test = inputHolder.get(i);
			String delims = "[ ]+";
			String[] split = test.split(delims);
			
			if(i == 0){
				for(int j = 0; j<split.length; j++){
					if(!split[j].equals("\t") && !split[j].equals(""))
						rewrittenInput += split[j] + " ";
				}
			}
			else if( i == sizeOfHolder - 1){
				for(int j = 0; j < split.length; j++){
					if(j == split.length - 1 && !split[j].equals("\t") && !split[j].equals("") )
						rewrittenInput += split[j];
					else{
						if(!split[j].equals("\t") && !split[j].equals(""))
							rewrittenInput += split[j] + " ";
					}
				}
			}
			else{
				for(int j = 0; j < split.length; j++){
					if(!split[j].equals("\t") && !split[j].equals(""))
						rewrittenInput += split[j] + " ";
				}
			}
		}
		
		int totalVarDef = 0;
		int totalVarWithAddrLocs = 0;
		int totalNumOfAddr = 0;
		
		ArrayList<Integer> iPosOfAddr = new ArrayList<Integer>();
		
		//System.out.println("The rewrittenInput is:"+rewrittenInput);
		rewrittenInput = rewrittenInput.substring(rewrittenInput.indexOf(" ")+1,rewrittenInput.length());
		int rewrittenLength = rewrittenInput.length();

		//first pass through the input
		for( int i = 0; i < rewrittenLength;){
			//if statement to go through the first module
			//code is nearly identical to else statement but with small changes
			if ( i == 0){
				totalVarDef = Integer.parseInt(rewrittenInput.substring(0, rewrittenInput.indexOf(" ")));
				i = rewrittenInput.indexOf(" ") + 1;
				
				for(int j = 0; j < totalVarDef; j++){
					String var = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
					i = rewrittenInput.indexOf(" ", i) + 1;
					int varDef = Integer.parseInt(rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) ) );
					
					if(symbolTable.containsKey(var)){
						System.err.println("Error: "+var+" is multiply defined.  Last value is used");
						symbolTable.replace(var, varDef);
					}
					else
						symbolTable.put( var, varDef);
					//System.out.println("Putting var " + var + " and varDef " + varDef);
					i = rewrittenInput.indexOf(" ", i) + 1;
				}
				
				totalVarDef = 0;
				totalVarWithAddrLocs = Integer.parseInt(rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) ) );
				i = rewrittenInput.indexOf(" ", i) + 1;
				String var = "";
				
				for(int j = 0; j < totalVarWithAddrLocs; j++){
					if(j == 0)
						var = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
					else{
						;
					}
					i = rewrittenInput.indexOf(" ", i) + 1;
					String addrLocs = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
					
					while(!addrLocs.equals("-1")){
						if( Integer.parseInt(addrLocs) >= addressPositions.size()){
							for (int k = 0; k < Integer.parseInt(addrLocs) - addressPositions.size()+1;){
								addressPositions.add(null);
								//System.out.println("Size has been increased");
							}
						}
						try{
							if(addressPositions.get(Integer.parseInt(addrLocs)).equals(null)){
								addressPositions.set((Integer.parseInt(addrLocs)), var);
							}
							else{
								System.err.println("Error multiple symbols used in instruction; all but last ignored");
								addressPositions.set(Integer.parseInt(addrLocs), var);
							}
						} catch (NullPointerException e){
							addressPositions.set((Integer.parseInt(addrLocs)), var);
						}
						i = rewrittenInput.indexOf(" ", i) + 1;
						addrLocs = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
					}
					
					i = rewrittenInput.indexOf(" ", i) + 1;
					var = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
				}
				
				totalVarWithAddrLocs = 0;
				totalNumOfAddr = Integer.parseInt(rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) ) );
				iPosOfAddr.add(i);
				i = rewrittenInput.indexOf(" ", i) + 1;
				i = 5 * totalNumOfAddr + i + totalNumOfAddr;
				//System.out.println("Succesfully passed through the first group of numbers");
			}
			
			else{
				//System.out.println("Initiating the else statement");
				totalVarDef = Integer.parseInt(rewrittenInput.substring(i, rewrittenInput.indexOf(" ",i) ) );
				i = rewrittenInput.indexOf(" ",i) + 1;
				ArrayList<String> newDefVars = new ArrayList<String>();
				
				for(int j = 0; j < totalVarDef; j++){
					String var = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
					i = rewrittenInput.indexOf(" ", i) + 1;
					int varDef = Integer.parseInt(rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) ) );
					newDefVars.add(var);
					
					if(symbolTable.containsKey(var)){
						System.err.println("Error: "+var+" is multiply defined.  Last value is used");
						symbolTable.remove(var);
					}
					symbolTable.put( var, varDef+totalNumOfAddr);
					//System.out.println("Putting var " + var + "and varDef " + varDef);
					i = rewrittenInput.indexOf(" ", i) + 1;
				}
				
				totalVarDef = 0;
				totalVarWithAddrLocs = Integer.parseInt(rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) ) );
				i = rewrittenInput.indexOf(" ", i) + 1;
				String var = "";
				
				for(int j = 0; j < totalVarWithAddrLocs; j++){
					if(j == 0)
						var = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
					else{
						;
					}
					i = rewrittenInput.indexOf(" ", i) + 1;
					String addrLocs = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
					
					while(!addrLocs.equals("-1")){
						if( Integer.parseInt(addrLocs)+ totalNumOfAddr >= addressPositions.size()){
							for (int k = 0; k < (Integer.parseInt(addrLocs)+totalNumOfAddr) - addressPositions.size()+1;){
								addressPositions.add(null);
								//System.out.println("Size has been increased");
							}
						}
						try{
							if(addressPositions.get(Integer.parseInt(addrLocs)+totalNumOfAddr).equals(null)){
								addressPositions.set((Integer.parseInt(addrLocs)+totalNumOfAddr), var);
							}
							else{
								System.err.println("Error multiple symbols used in instruction; all but last ignored");
								addressPositions.set(Integer.parseInt(addrLocs)+totalNumOfAddr, var);
							}
						} catch (NullPointerException e){
							addressPositions.set((Integer.parseInt(addrLocs)+totalNumOfAddr), var);
						}
						i = rewrittenInput.indexOf(" ", i) + 1;
						addrLocs = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
					}
					
					i = rewrittenInput.indexOf(" ", i) + 1;
					var = rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) );
				}
				
				totalVarWithAddrLocs = 0;
				int currentNumOfAddr = Integer.parseInt(rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) ) );
				totalNumOfAddr += Integer.parseInt(rewrittenInput.substring(i,rewrittenInput.indexOf(" ", i) ) );
				
				for(int z = 0; z < newDefVars.size(); z++){
					if(symbolTable.get(newDefVars.get(z)) >= totalNumOfAddr){
						symbolTable.replace(newDefVars.get(z), totalNumOfAddr-1);
						System.err.println("Error address exceeds module size.  Last word in module used");
					}
				}
				iPosOfAddr.add(i);
				i = rewrittenInput.indexOf(" ", i) + 1;
				i = 5 * currentNumOfAddr + i + currentNumOfAddr;
			}
		}
		
		System.out.println("Contents of the symbol table");
		System.out.println(symbolTable.toString());
		System.out.println();
		System.out.println("Memory Map");
		
		//Second pass
		int currentNumOfAddr = 0;
		int totalAddrSecondPass = 0;
		for(int i = 0; i < iPosOfAddr.size();i++){
			int walk = 0;
			int pos = iPosOfAddr.get(i);
			currentNumOfAddr = Integer.parseInt(rewrittenInput.substring(pos,rewrittenInput.indexOf(" ", pos) ) );
			pos = rewrittenInput.indexOf(" ", pos) + 1;
			
			for(int j = 0; j < currentNumOfAddr;j++){
				int firstFour;
				int lastDigit;
				
				if(i == iPosOfAddr.size() - 1 && j == currentNumOfAddr -1){
					firstFour = Integer.parseInt(rewrittenInput.substring(pos,pos+4) );
					lastDigit = Integer.parseInt(rewrittenInput.substring(pos+4, pos + 5 ) );
				}
				else{
					firstFour = Integer.parseInt(rewrittenInput.substring(pos,rewrittenInput.indexOf(" ", pos)-1 ) );
					lastDigit = Integer.parseInt(rewrittenInput.substring(pos+4,rewrittenInput.indexOf(" ", pos) ) );
				}
				
				if(lastDigit == 1){
					System.out.println(firstFour);
					if(i == iPosOfAddr.size() - 1 && j == currentNumOfAddr -1)
						pos = rewrittenInput.length();
					else
						pos = rewrittenInput.indexOf(" ", pos) + 1;
					walk++;
				}
				else if(lastDigit == 2){
					int testMax = Integer.parseInt((firstFour+"").substring(1));
					
					if(testMax > MAX_MACHINE_SIZE){
						System.err.println("Error: Absolute address exceeds machine size; largest legal value used");
						System.out.println((firstFour+"").charAt(0)+""+(MAX_MACHINE_SIZE-1) );
					}
					else
						System.out.println(firstFour);
					if(i == iPosOfAddr.size() - 1 && j == currentNumOfAddr -1)
						pos = rewrittenInput.length();
					else
						pos = rewrittenInput.indexOf(" ", pos) + 1;
					walk++;
				}
				else if(lastDigit == 3){
					int relative = firstFour + totalAddrSecondPass;
					System.out.println(relative);
					if(i == iPosOfAddr.size() - 1 && j == currentNumOfAddr -1)
						pos = rewrittenInput.length();
					else
						pos = rewrittenInput.indexOf(" ", pos) + 1;
					walk++;
				}
				else if(lastDigit == 4){
					String lookup = addressPositions.get(totalAddrSecondPass+walk);
					int ext;
					try{
						ext = symbolTable.get(lookup);
					}
					catch(NullPointerException e){
						System.err.println("Error: "+lookup+" is used but not defined. 111 is used ");
						ext = 111;
					}
					
					int lengthOfExt = (ext+"").length();
					String extPrint = (firstFour+"").substring(0, 1);
						
					for(int diff = 0; diff < 3 - lengthOfExt; diff++){
						extPrint += 0;
					}
						
					extPrint += ext;
					System.out.println(extPrint);
						
					if(i == iPosOfAddr.size() - 1 && j == currentNumOfAddr -1)
						pos = rewrittenInput.length();
					else
						pos = rewrittenInput.indexOf(" ", pos) + 1;
					walk++;
				}
			}
			totalAddrSecondPass += currentNumOfAddr;
		}
		//System.out.println("Finished second pass");
		Object[] testForDefButNotUsed = symbolTable.keySet().toArray();
		
		for(int i = 0; i < testForDefButNotUsed.length; i++){
			if(addressPositions.contains(testForDefButNotUsed[i]))
				;
			else
				System.out.println("Warning "+testForDefButNotUsed[i]+" was defined but never used");
		}
	}
}
