import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

import struct.Pair;
import struct.Participant;

/**
 * Given an ArrayList of participants (user id, SE median score, etc), 
 * and a breakdown of team compositions from the user (i.e. 5 low-low, 6 high-high, 3 low-high)
 * assign participants to pairs. 
 * 
 * @author iris
 *
 */
public class TeamAssigner {
	private static final String MEDIAN_HIGH = VarConstants.MEDIAN_HIGH;
	private static final String MEDIAN_MED = VarConstants.MEDIAN_MED;
	private static final String MEDIAN_LOW = VarConstants.MEDIAN_LOW;
	private static final String MEDIAN_UNKNOWN = VarConstants.MEDIAN_UNKNOWN;	
	private static final int NUM_SPLIT = VarConstants.NUM_SPLIT; // How many groups to median-split the students into
	
	private static String teamPrefix ="x"; // the prefix to put before each team (section?)
	private static Hashtable<String, Pair<Participant>> byTeam; // A Hashtable TeamID --> Participants
	
	public static void setTeamPrefix(String t) {
		teamPrefix = t;
	}
	
	public static Hashtable<String, Pair<Participant>> assignTeams(ArrayList<Participant> participants, int numAllLow, int numAllMed, int numAllHigh, int numLowHigh, int numLowMed, int numMedHigh) {
		if (participants.size()%2 != 0) {
			System.err.println("S: Due to the odd number of participants, you will have one team with a lonely user.");
		}
		
		// Evenly distribute any remaining across all team types
		int sumTeams = numAllLow + numAllMed + numAllHigh + numLowHigh + numLowMed + numMedHigh;
		int numLeftovers = participants.size() - (sumTeams*2);
		int numTeamTypes = 1;
		if (NUM_SPLIT == 1) {
			numTeamTypes = NUM_SPLIT + 2;
		} else if (NUM_SPLIT == 2) {
			numTeamTypes = NUM_SPLIT + 4;
		}		

		int[] toAdd = new int[numTeamTypes]; // each index corresponds to a different type of team
		int teamTypeIndex = 0;
		for (int i = 0; i < (numLeftovers/2); i++) {
			if (teamTypeIndex > toAdd.length) {
				teamTypeIndex = 0;
			} 
			toAdd[teamTypeIndex]++;
			teamTypeIndex++;
		}
		numAllLow+= toAdd[0];
		numAllHigh+= toAdd[1];
		numLowHigh+= toAdd[2];
		if (numTeamTypes > 3) {
			numAllMed+= toAdd[3];
			numLowMed+= toAdd[4];
			numMedHigh+= toAdd[5];
		} 
		//System.out.println("Ideal Team Numbers: \tl-l/" + numAllLow + " m-m/" + numAllMed + " h-h/" + numAllHigh);
		//System.out.println("\t\tl-m/" + numLowMed + " l-h/" + numLowHigh + " m-h/" + numMedHigh);
		
		byTeam = new Hashtable<String, Pair<Participant>>();	
		
		ArrayList<Participant> lowSE = new ArrayList<Participant>();
		ArrayList<Participant> medSE = new ArrayList<Participant>();
		ArrayList<Participant> highSE = new ArrayList<Participant>();
		ArrayList<Participant> unkSE = new ArrayList<Participant>();
		
		// Arrange Participants by SE level
		Iterator<Participant> pItr = participants.iterator();	
		while (pItr.hasNext()) {
			Participant currP = pItr.next();
			
			if (currP.getSEmedian().equals(MEDIAN_LOW)) {
				lowSE.add(currP);				
			} else if (currP.getSEmedian().equals(MEDIAN_MED)) {
				medSE.add(currP);	
			} else if (currP.getSEmedian().equals(MEDIAN_HIGH)) {
				highSE.add(currP);	
			} else if (currP.getSEmedian().equals(MEDIAN_UNKNOWN)) {
				unkSE.add(currP);
			} else {
				System.err.println("E: Uninterpretable SE median value: " + currP.getSEmedian());
			}
		}		
		System.out.println("S: Number of Ps in each group: low/" + lowSE.size() + " med/" + medSE.size() + " high/" + highSE.size() + " unk/" + unkSE.size());
		
		// Randomly shuffle the arrays
		Collections.shuffle(lowSE);
		Collections.shuffle(medSE);
		Collections.shuffle(highSE);
		Collections.shuffle(unkSE);
		
		// Keeping track
		int teamNum = 0;
		int countAllHigh = 0;
		int countAllLow = 0;
		int countLowHigh = 0;
		int countAllMed = 0; // TODO: Haven't implemented functionality for 'medium' median
		int countMedHigh = 0;
		int countLowMed = 0;
		int countUnknown = 0;
		
		// Fill Up All-Low 
		for (int i = 0; i < numAllLow; i++) {
			if (lowSE.size() > 1) {
				Participant p1 = lowSE.remove(0);
				Participant p2 = lowSE.remove(0);
				p1.setTeam("LowLow_"+teamPrefix+teamNum);
				p1.setTeam("LowLow_"+teamPrefix+teamNum);
				teamNum++;
				countAllLow++;
				
				Pair<Participant> newPair = new Pair<Participant>(p1, p2);				
				byTeam.put(newPair.getFirst().getTeam(), newPair);
			} else {
				System.err.println("Low SE students have run out while building Low-Low Teams.");
			}
		}
		
		// Fill Up All-High 
		for (int i = 0; i < numAllHigh; i++) {
			if (highSE.size() > 1) {
				Participant p1 = highSE.remove(0);
				Participant p2 = highSE.remove(0);
				p1.setTeam("HighHigh_"+teamPrefix+teamNum);
				p1.setTeam("HighHigh_"+teamPrefix+teamNum);
				teamNum++;
				countAllHigh++;
				
				Pair<Participant> newPair = new Pair<Participant>(p1, p2);				
				byTeam.put(newPair.getFirst().getTeam(), newPair);
			} else {
				System.err.println("High SE students have run out while building High-High Teams.");
			}			
		}
		
		// Fill Up All-Med 
		for (int i = 0; i < numAllMed; i++) {
			if (medSE.size() > 1) {
				Participant p1 = medSE.remove(0);
				Participant p2 = medSE.remove(0);
				p1.setTeam("MedMed_"+teamPrefix+teamNum);
				p1.setTeam("MedMed_"+teamPrefix+teamNum);
				teamNum++;
				countAllMed++;
				
				Pair<Participant> newPair = new Pair<Participant>(p1, p2);				
				byTeam.put(newPair.getFirst().getTeam(), newPair);
			} else {
				System.err.println("Med SE students have run out while building Med-Med Teams.");
			}			
		}
		
		// Fill Up Low-High 
		for (int i = 0; i < numLowHigh; i++) {
			if (lowSE.size() > 0 && highSE.size() > 0) {
				Participant p1 = lowSE.remove(0);
				Participant p2 = highSE.remove(0);
				p1.setTeam("LowHigh_"+teamPrefix+teamNum);
				p1.setTeam("LowHigh_"+teamPrefix+teamNum);
				teamNum++;
				countLowHigh++;
				
				Pair<Participant> newPair = new Pair<Participant>(p1, p2);				
				byTeam.put(newPair.getFirst().getTeam(), newPair);
			} else {
				System.err.println("Low or High SE students have run out while building Low-High Teams.");
			}				
		}
		
		// Fill Up Low-Med 
		for (int i = 0; i < numLowMed; i++) {
			if (lowSE.size() > 0 && medSE.size() > 0) {
				Participant p1 = lowSE.remove(0);
				Participant p2 = medSE.remove(0);
				p1.setTeam("LowMed_"+teamPrefix+teamNum);
				p1.setTeam("LowMed_"+teamPrefix+teamNum);
				teamNum++;
				countLowMed++;
				
				Pair<Participant> newPair = new Pair<Participant>(p1, p2);				
				byTeam.put(newPair.getFirst().getTeam(), newPair);
			} else {
				System.err.println("Low or Med SE students have run out while building Low-Med Teams.");
			}				
		}
		
		// Fill Up Med-High 
		for (int i = 0; i < numMedHigh; i++) {
			if (medSE.size() > 0 && highSE.size() > 0) {
				Participant p1 = medSE.remove(0);
				Participant p2 = highSE.remove(0);
				p1.setTeam("MedHigh_"+teamPrefix+teamNum);
				p1.setTeam("MedHigh_"+teamPrefix+teamNum);
				teamNum++;
				countMedHigh++;
				
				Pair<Participant> newPair = new Pair<Participant>(p1, p2);				
				byTeam.put(newPair.getFirst().getTeam(), newPair);
			} else {
				System.err.println("Med or High SE students have run out while building Med-High Teams.");
			}				
		}
		
		// Fill Up Unknown
		int unknownTeamCount = 900;
		
		for (int i = unkSE.size()-1; i >= 0; i--) {
			Participant p1 = unkSE.get(i);
			
			if (i > 0) { // still have at least one more left
				unkSE.remove(i);
				i--;
				Participant p2 = unkSE.get(i);
				unkSE.remove(i);
				p1.setTeam("Unknown_"+teamPrefix+unknownTeamCount);
				p2.setTeam("Unknown_"+teamPrefix+unknownTeamCount);
				byTeam.put(("Unknown_"+teamPrefix+unknownTeamCount), new Pair<Participant>(p1,p2));
				unknownTeamCount++;
				countUnknown++;
			} 			
		}
		
		if (unkSE.size() > 1) {
			System.err.println("E: More than 1 Unknown SE leftover. How?"); // debug
		}
		
		// Debugging Output
		System.out.print("S: Sub-Final Team Numbers: ");
		System.out.println("\tl-l/" + countAllLow + " h-h/" + countAllHigh + " l-h/" + countLowHigh + " unk/" + countUnknown);
		if (NUM_SPLIT == 2)
			System.out.println("\t\t\t\tm-m/" + countAllMed + " l-m/" + countLowMed + " m-h/" + countMedHigh);		
		System.out.println("S: Participants leftover after assigning:\tlow SE/"+ lowSE.size() + " med SE/" + medSE.size() + " high SE/" + highSE.size() + " unk SE/" + unkSE.size());		
		
		// Assigning the leftovers
		ArrayList<Participant> leftovers = new ArrayList<Participant>();
		leftovers.addAll(lowSE);
		leftovers.addAll(medSE);
		leftovers.addAll(highSE);
		// Do not add 'unknown SE' here, we don't want them mixed in
		// Collections.shuffle(leftovers); // Do we want them randomly assigned?
		pItr = leftovers.iterator();		
		while (pItr.hasNext()) {
			Participant p1 = pItr.next();
			
			if (pItr.hasNext()) {
				Participant p2 = pItr.next();
				if (p1.getSEmedian().equals(p2.getSEmedian())) { // they're equal
					// Adjusting team-type counts
					if (p1.getSEmedian().equals(MEDIAN_HIGH)) { // h-h
						countAllHigh++;
						p1.setTeam("HighHigh_"+teamPrefix+teamNum);
						p2.setTeam("HighHigh_"+teamPrefix+teamNum);
					} else if (p1.getSEmedian().equals(MEDIAN_LOW)) { // l-l
						countAllLow++;
						p1.setTeam("LowLow_"+teamPrefix+teamNum);
						p2.setTeam("LowLow_"+teamPrefix+teamNum);
					} else { // m-m
						countAllMed++;
						p1.setTeam("MedMed_"+teamPrefix+teamNum);
						p2.setTeam("MedMed_"+teamPrefix+teamNum);
					}
				} else if (p1.getSEmedian().equals(MEDIAN_HIGH)) { // Need p2's median to come first in name
					// Adjusting team-type counts
					if (p2.getSEmedian().equals(MEDIAN_LOW)) { // l-h
						countLowHigh++;
						p1.setTeam("LowHigh_"+teamPrefix+teamNum);
						p2.setTeam("LowHigh_"+teamPrefix+teamNum);
					} else { // m-h
						countMedHigh++;
						p1.setTeam("MedHigh_"+teamPrefix+teamNum);
						p2.setTeam("MedHigh_"+teamPrefix+teamNum);
					} 
				} else if (p2.getSEmedian().equals(MEDIAN_HIGH)) { // Need p1's median to come first
					// Adjusting team-type counts
					if (p1.getSEmedian().equals(MEDIAN_LOW)) { // l-h
						countLowHigh++;
						p1.setTeam("LowHigh_"+teamPrefix+teamNum);
						p2.setTeam("LowHigh_"+teamPrefix+teamNum);
					} else { // m-h
						countMedHigh++;
						p1.setTeam("MedHigh_"+teamPrefix+teamNum);
						p2.setTeam("MedHigh_"+teamPrefix+teamNum);
					} 
				} else if (p1.getSEmedian().equals(MEDIAN_LOW)) { // Need p1's median to come first
					// Must be l-m
					countLowMed++;
					p1.setTeam("LowMed_"+teamPrefix+teamNum);
					p2.setTeam("LowMed_"+teamPrefix+teamNum);
				} else if (p2.getSEmedian().equals(MEDIAN_LOW)) { // Need p2's median to come first
					//Must be l-m
					countLowMed++;
					p1.setTeam("LowMed_"+teamPrefix+teamNum);
					p2.setTeam("LowMed_"+teamPrefix+teamNum);
				}
				Pair<Participant> newPair = new Pair<Participant>(p1, p2);				
				byTeam.put(newPair.getFirst().getTeam(), newPair);
			} else if (unkSE.size() > 0) { 
				Participant p2 = unkSE.get(0);
				unkSE.remove(p2);
				p1.setTeam("Unknown_"+teamPrefix+teamNum);				
				p2.setTeam("Unknown_"+teamPrefix+teamNum);
				Pair<Participant> newPair = new Pair<Participant>(p1, p2);				
				byTeam.put(newPair.getFirst().getTeam(), newPair);
			} else { // this is the absolute last one
				p1.setTeam("One" +p1.getSEmedian()+"_"+teamPrefix+teamNum);
				Pair<Participant> newPair = new Pair<Participant>(p1, null);				
				byTeam.put(newPair.getFirst().getTeam(), newPair);
			}
			teamNum++;
		} // end while pItr.hasNext()
		
		if (unkSE.size() == 1) { // A one-person team, unknown
			byTeam.put(("OneUnknown_"+teamPrefix+unknownTeamCount), new Pair<Participant>(unkSE.get(0),null));
		} else if (unkSE.size()>1) {
			System.err.println("E: More than one 'unknown' SE student leftover.");
		}
		
		// Debugging Output
		System.out.println("S: Final Team Numbers: \tl-l/" + countAllLow + " h-h/" + countAllHigh+ " l-h/" + countLowHigh + " unk/" + countUnknown);
		if (NUM_SPLIT ==2)
			System.out.println("\t\t\t m-m/" + countAllMed +  " l-m/" + countLowMed + " m-h/" + countMedHigh);
		return byTeam;
	}
}
