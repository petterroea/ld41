package com.petterroea.ld41.romance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.petterroea.ld41.GamePuppetMaster;

public class AcquaintanceManager {
	public static AcquaintanceManager SINGLETON = null;
	
	private HashMap<String, Integer> acquaintances = new HashMap<String, Integer>();
	private HashMap<String, Integer> newRelationPoints = new HashMap<String, Integer>();
	private GamePuppetMaster master;
	
	public AcquaintanceManager(GamePuppetMaster master) {
		this.master = master;
	}
	
	public void addAcquaintance(String actorName) {
		if(!acquaintances.containsKey(actorName.toLowerCase())) {
			acquaintances.put(actorName.toLowerCase(), 20);
			master.showAcquaintanceDialog(actorName, "You now know " + actorName);
		}
	}
	
	public int getAcquaintanceValue(String actorName) {
		if(!acquaintances.containsKey(actorName))
			return 0;
		return acquaintances.get(actorName);
	}
	
	public boolean hasAcquaintance(String actorName) {
		return acquaintances.containsKey(actorName);
	}
	
	public void addNewAcquaintancePoint(String actorName, int amount) {
		if(!newRelationPoints.containsKey(actorName)) {
			newRelationPoints.put(actorName, amount);
		} else {
			int oldAmt = newRelationPoints.get(actorName);
			newRelationPoints.replace(actorName, oldAmt, oldAmt+amount);
		}
	}
	
	public int getNewAcquaintancePoints(String actorName) {
		if(!newRelationPoints.containsKey(actorName))
			return 0;
		return newRelationPoints.get(actorName);
	}
	
	public void commitRelations() {
		Iterator it = newRelationPoints.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        int oldVal = acquaintances.get((String)pair.getKey());
	        acquaintances.replace((String)pair.getKey(), (Integer)oldVal, (Integer)(oldVal + (int)pair.getValue() ) );
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	public Iterator<String> getAcquaintances() {
		return acquaintances.keySet().iterator();
	}
}
