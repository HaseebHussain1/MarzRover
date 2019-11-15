package Mars;

import java.util.ArrayList;

import java.util.Collections;



class Vehicle extends Entity{
	public boolean carryingSample;
	ArrayList<Location>positionsWent;
	
	
	public Vehicle(Location l){
		
		super(l);	
		positionsWent=new ArrayList<Location>();
		this.carryingSample = false;
	}

	public void act(Field f, Mothership m, ArrayList<Rock> rocksCollected)
	{
		//actCollaborativeFix(f, m, rocksCollected);
		//actSimpleFix(f, m, rocksCollected);
		actSimple(f,m,rocksCollected);
		//actCollaborative(f,m,rocksCollected);
	}
	
	
    /**Collaborative act method which:
     * 1 checks if adjacent to mother ship and carrying rock and drop rock
     * 5if carrying rock then get adjacent location which has highest signal value and
     * changes location on field and drops crumbs
     * 3if there's a rock it is picked up
     * 6 if there are crumbs available in adjacent locations down the gradient then
     * change location to a location down gradient with highest value in crumbs and
     * pickup crumb
     * 4 if true move randomly
     * 
     * @param f the field that is used to reduce crumbs and change locations
     * @param m the mother ship
     * @param m the rocksCollected
     */
	public void actCollaborative(Field f, Mothership m, ArrayList<Rock> rocksCollected){
		//complete this method
		if (carryingSample==true&& f.isNeighbourTo(this.getLocation(), Mothership.class)) {//1
			this.carryingSample = false;
			//f.dropCrumbs(this.location, 2);
			
		}
		else if (carryingSample==true) {// 5 if carrying a sample and not at the base then drop two crumbs and travel up gradient
			ArrayList<Location> freeLocations=f.getAllfreeAdjacentLocations(this.getLocation());
			Location bestLocation=this.getLocation();
			
			if (freeLocations!=null&& freeLocations.isEmpty()==false) {
				
				bestLocation=freeLocations.get(0);
				
				for(Location l:freeLocations) {
				
					if(f.getSignalStrength(l)>f.getSignalStrength(bestLocation)) {
						bestLocation=l;
				}
				
			}
		}
			
				if (this.getLocation().equals(bestLocation)==false) {
					f.dropCrumbs(this.location, 2);
					changeLocationOnField(f,bestLocation);
					
					
			}
				
				
			
		}else if ( f.isNeighbourTo(this.getLocation(), Rock.class)) {//3
			
			
			Location rockLocation=	f.getNeighbour(this.location,Rock.class);
			rocksCollected.add((Rock)f.getObjectAt(rockLocation));
			this.carryingSample=true;
			f.clearLocation(rockLocation);
		}else if(crumsAvailableDownGradient(f)){//6
			changeLocationOnField(f, freeAdjacentLocationWithCrumbs(f));
			f.pickUpACrumb(this.location);
		}
		else {//4
			
			ArrayList<Location>freeLocations=f.getAllfreeAdjacentLocations(this.getLocation());
			if (freeLocations!=null&&freeLocations.isEmpty()==false) {
				
				Collections.shuffle(freeLocations);
				changeLocationOnField(f, freeLocations.get(0));
				
				
			}
		}
		
	}

	  /**Collaborative act method which uses past history of locations to
	 * avoid getting stuck coming back to mother ship:
     * 1 checks if adjacent to mother ship and carrying rock and drop rock
     * 5if carrying rock then get adjacent location which has highest signal value
     *  and that is not in positionsWent to prevent getting stuck
     * changes location on field and drops crumbs and add current location to positionsWent
     * 3if there's a rock it is picked up and clear positionsWent and add rocks current position
     * 6 if there are crumbs available in adjacent locations down the gradient then
     * change location to a location down gradient with highest value in crumbs and
     * pickup crumb
     * 4 if true move randomly
     * 
     * @param f the field that is used to reduce crumbs and change locations
     * @param m the mothership
     * @param m the rocksCollected
     */
	public void actCollaborativeFix(Field f, Mothership m, ArrayList<Rock> rocksCollected){
		//complete this method
		if (carryingSample==true&& f.isNeighbourTo(this.getLocation(), Mothership.class)) {//1 check if your at mother ship and drop rock
			this.carryingSample = false;
			
		}
		else if (carryingSample==true) {// 5 if carrying a sample and not at the base then drop two crumbs and travel up gradient
			ArrayList<Location> freeLocations=checkIfNodeHasFreeNode( f,f.getAllfreeAdjacentLocations(this.getLocation()));
			Location bestLocation=this.getLocation();
			freeLocations.removeAll(positionsWent);// removing all locations previosly visited
			if (freeLocations!=null&& freeLocations.isEmpty()==false) {
				
				bestLocation=freeLocations.get(0);
					
				for(Location l:freeLocations) {// checking signal strength to get loc with best signal
				
				
					if(f.getSignalStrength(l)>f.getSignalStrength(bestLocation)) {
						
						bestLocation=l;
						
					
				}
				
			}
		}else if(freeLocations.isEmpty()) {
			// the free locations is empty because postionWent has all adjacent locations
			positionsWent.clear();
			
		}
				// if the bestLocation is not current location them add currentlocation to 
				//locationWent and change location on field and drop crumbs
				if (this.getLocation().equals(bestLocation)==false) {
					f.dropCrumbs(this.location, 2);
					addPreviousLoc(this.location);
					changeLocationOnField(f,bestLocation);
					
					
			}
				
				
			
		}else if ( f.isNeighbourTo(this.getLocation(), Rock.class)) {//3 rock at adjacent locations then pickup
			positionsWent.clear();//no need to remember locations from last time sample collected
			
			//geting location of an adjacient rock and removing it from the field 
			Location rockLocation=	f.getNeighbour(this.location,Rock.class);
			rocksCollected.add((Rock)f.getObjectAt(rockLocation));
			addPreviousLoc(rockLocation);
			this.carryingSample=true;
			f.clearLocation(rockLocation);
		}else if(crumsAvailableDownGradient(f)){
			//6 if there are crumbs avialable down gradient from current loc then
			
			changeLocationOnField(f, freeAdjacentLocationWithCrumbs(f));
			f.pickUpACrumb(this.location);
		}
		else {//4 move randomly
			
			ArrayList<Location>freeLocations=f.getAllfreeAdjacentLocations(this.getLocation());
			if (freeLocations!=null&&freeLocations.isEmpty()==false) {
				
				Collections.shuffle(freeLocations);
				
				changeLocationOnField(f, freeLocations.get(0));
				
				
			}
		}
		
	}
	  /**simple act method which:
     * 1 checks if adjacent to mother ship and carrying rock and drop rock
     * 2if carrying rock then get adjacent location which has highest signal value and
     * changes location on field 
     * 3if there's a rock it is picked up
     * 4 if true move randomly
     * 
     * @param f the field that is used to reduce crumbs and change locations
     * @param m the mothership
     * @param m the rocksCollected
     */
	public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected){

		
		if (carryingSample==true&& f.isNeighbourTo(this.getLocation(), Mothership.class)) {//1 check if your at mother ship and drop rock
			this.carryingSample = false;
			
		}else if (carryingSample==true) {//2 if carrying a sample and not at the base then travel up gradient
			ArrayList<Location> freeLocations= f.getAllfreeAdjacentLocations(this.getLocation());
			Location bestLocation=this.getLocation();
	
			if (freeLocations!=null&& freeLocations.isEmpty()==false) {
				
				 
				bestLocation=freeLocations.get(0);
				
				for(Location l:freeLocations) {

				
					if(f.getSignalStrength(l)>f.getSignalStrength(bestLocation)) {

						bestLocation=l;
						
					
				}
				
			}
		}
			
			if (this.getLocation().equals(bestLocation)==false) {
				

				changeLocationOnField(f,bestLocation);
				
				
			}
			
		}else if ( f.isNeighbourTo(this.getLocation(), Rock.class)) {//3 rock at adjacent locations then pickup
			
			Location rocklocation=	f.getNeighbour(this.location,Rock.class);
			
		
			this.carryingSample=true;
			f.clearLocation(rocklocation);
		}else {//4 move randomly
			
			ArrayList<Location>freeLocations=f.getAllfreeAdjacentLocations(this.getLocation());
			if (freeLocations!=null &&freeLocations.isEmpty()==false) {
				
				Collections.shuffle(freeLocations);
				changeLocationOnField(f,freeLocations.get(0));
				
				
			}
		}
		
	}
	
	  /**simple act fix method which:
     * 1 checks if adjacent to mother ship and carrying rock and drop rock
     * 2if carrying rock then get adjacent location which has highest signal and has not been visited before value and
     * changes location on field 
     * 3if there's a rock it is picked up and clear locationsWent
     * 4 if true move randomly
     * 
     * @param f the field that is used to reduce crumbs and change locations
     * @param m the mothership
     * @param m the rocksCollected
	**/
public void actSimpleFix(Field f, Mothership m, ArrayList<Rock> rocksCollected){

		
		if (carryingSample==true&& f.isNeighbourTo(this.getLocation(), Mothership.class)) {//1 check if your at mother ship and drop rock
			this.carryingSample = false;
			
		}else if (carryingSample==true) {//2 if carrying a sample and not at the base then travel up gradient 
			ArrayList<Location> freeLocations= checkIfNodeHasFreeNode( f,f.getAllfreeAdjacentLocations(this.getLocation()));
			Location bestLocation=this.getLocation();
			addPreviousLoc(this.location);
			
			
			freeLocations.removeAll(positionsWent);// removing previose locations from adjacent locations
			if (freeLocations!=null&& freeLocations.isEmpty()==false) {
				
				 
				bestLocation=freeLocations.get(0);
				
				for(Location l:freeLocations) {

				
					if(f.getSignalStrength(l)>f.getSignalStrength(bestLocation)) {

						bestLocation=l;
						
					
				}
				
			}
		}else if(freeLocations.isEmpty()) {// if the freeLocations is empty clear positionsWent to allow movment
			positionsWent.clear();
			
		}
			
			if (this.getLocation().equals(bestLocation)==false) {
				

				changeLocationOnField(f,bestLocation);
				
				
			}
			
		}else if ( f.isNeighbourTo(this.getLocation(), Rock.class)) {//3 rock at adjacent locations then pickup
			
			Location rocklocation=	f.getNeighbour(this.location,Rock.class);
			positionsWent.clear();
			addPreviousLoc(this.location);
			addPreviousLoc(rocklocation);
			this.carryingSample=true;
			f.clearLocation(rocklocation);
		}else {//4 move randomly
			
			ArrayList<Location>freeLocations=f.getAllfreeAdjacentLocations(this.getLocation());
			if (freeLocations!=null &&freeLocations.isEmpty()==false) {
				
				Collections.shuffle(freeLocations);
				changeLocationOnField(f,freeLocations.get(0));
				
				
			}
		}
		
	}
	

/**crumsAvailableDownGradient method which:
 * gets adjacent locations and loops through them to check if there is one which has a crumb and is down the gradient
 * 
 * @param f the field that is used to reduce crumbs and change locations
**/
	public boolean crumsAvailableDownGradient(Field f) {
		int currentGradientValue=f.getSignalStrength(this.location);
		ArrayList<Location>freeLocations=f.getAllfreeAdjacentLocations(this.location);
		for(Location l:freeLocations) {
			
			if (f.getCrumbQuantityAt(l)>0&&f.getSignalStrength(l)<currentGradientValue) {
				return true;
				
				
			}
		}
		return false;
		
		
		
	}
	
	

	/**freeAdjacentLocationWithCrumbs method which:
	 * gets adjacent locations and loops through them to check if the position has crumbs and is down gradient
	 * then loop through new collection to find best location
	 * 
	 * @param f the field that is used to reduce crumbs and change locations
	**/
	public Location freeAdjacentLocationWithCrumbs(Field f) {
		int currentGradientValue=f.getSignalStrength(this.location);
		ArrayList<Location>freeLocations=f.getAllfreeAdjacentLocations(this.location);
		ArrayList<Location>freeLocationsWithCrumbs=new ArrayList<Location>();
		for(Location l:freeLocations) {// find all locations with crumbs and down gradient 
			
			if (f.getCrumbQuantityAt(l)>0&&f.getSignalStrength(l)<currentGradientValue) {
				freeLocationsWithCrumbs.add(l);
				
			}
		}
		Location highestNumberOfCrumbs=freeLocationsWithCrumbs.get(0);// check for null
		for(Location l:freeLocationsWithCrumbs) {// find the location with the highest amount of crumbs
			
			if (f.getCrumbQuantityAt(l)>f.getCrumbQuantityAt(highestNumberOfCrumbs)) {
				 highestNumberOfCrumbs=l;
			}
		}
		
		return highestNumberOfCrumbs;
		
		
		
		
		
	}
	/**changeLocationOnField method which:
	 * Clear current location from field and set current location to newLoc
	 *  and place rover at new current location
	 * 
	 * @param f the field that is used to reduce crumbs and change locations
	 * @param newLocation the Location that is the rover is changed to
	 * */
	public void changeLocationOnField(Field f, Location newLocation) {// changing current location to newloc
		
		f.clearLocation(this.getLocation());//removing location from field
		this.setLocation(newLocation);
		f.place(this, this.getLocation());
		
	}
	/**addPreviousLoc method which:
	 * the previous location is added to the positionWent ArrayList and check if 
	 * positionWent is bigger than 20 and if it is remove the first element
	 * @param f the field that is used to reduce crumbs and change locations
	 * @param newLocation the Location that is the rover is changed to
	 * */
	private void addPreviousLoc(Location l) {
		
		positionsWent.add(l);
		if (positionsWent.size() > 20) {
			
			positionsWent.remove(0); 
			
			}
	}
	
	/**addPreviousLoc method which:
	 * used to get the locations with a more than 1 free adjacent locations to insure the
	 * new location is not a dead end
	 * @param f the field that is used to reduce crumbs and change locations
	 * @param freeLocations the Locations that are free
	 * */
	private ArrayList<Location> checkIfNodeHasFreeNode(Field f,ArrayList<Location> freeLocations) {
		ArrayList<Location> validfreeLocations=new ArrayList<Location>();
		for(Location l:freeLocations) {
			
			ArrayList<Location> temporaryLocs=f.getAllfreeAdjacentLocations(l);
			if (temporaryLocs.size()>1) {// checking if temporaryLocs is not dead end eg not <1
				validfreeLocations.add(l);
			}
			
		}
		return validfreeLocations;
		
	}
}
