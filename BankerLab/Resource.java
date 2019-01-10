 
public class Resource {

	int id;
	int totalUnits;
	int unitsAvailable;
	
	public Resource() {}
	
	public Resource(int id, int units) {
		this.id = id;
		totalUnits =  units;
		this.unitsAvailable = units;
	}
	
	public void subtractUnitsAvailable(int u) {
		unitsAvailable -= u;
	}
	
	public void addUnitsAvailable(int u) {
		unitsAvailable += u;
	}
	
	public String toString() {
		return "Resource " + this.id +" has "+this.totalUnits + " total units";
	}
}
