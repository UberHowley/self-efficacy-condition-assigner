
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.lang.NumberFormatException;

import struct.Pair;
import struct.Participant;

/**
 * The SEAnalyzer (i.e. "Self efficacy analyzer") is the main class
 * that coordinates a series of actions:
 * (1) reading in a csv of survey responses
 * (2) turning the survey responses into a list of participants
 * (3) assigns participants to pairs given a desirable composition
 * (4) assigns the pairs to conditions, spread evenly across
 * (5) outputs the teams, targeted/untargeted, rooms, etc. so tutor 
 * 	   software can read it
 * 
 * @author iris
 *
 */
public class SEAnalyzer {
	public final static String readFile = VarConstants.readFile;
	public final static String writeFile = VarConstants.writeFile;
	public final static boolean  unprocessed = VarConstants.unprocessed; 
	public final static int numSplit = VarConstants.NUM_SPLIT; 
	public final static double medianVal = VarConstants.MEDIAN_VAL;
	
	public static TutorWriter tw;
	
	public static void main(String[] argv) {
		tw = new TutorWriter();
		tw.readLines(writeFile);
		ParticipantCreator pCreat = new ParticipantCreator();
		
		if (unprocessed)
			pCreat.readUnprocessedFile(readFile);
		else
			pCreat.readProcessedFile(readFile);
		
		System.out.println("S: Median split will divide into: " + (numSplit+1) + " groups."); 
		
		Hashtable<String, Pair<Participant>> pList;
		if (unprocessed) {
			int numKnownParticipants = pCreat.getParticipantList().size()-pCreat.getUnknownParticipantList().size(); // ignoring unknown participants
			
			
			// Print breakdown of each team composition type in each condition
			if (numSplit == 1) {
				// Recommend Team Break Down here
				System.out.println(tw.toString());
			}
			
			// Printing diagnostic information
			if (numSplit == 1) {
				System.out.println("\tS: Median split value of " + medianVal);
				System.out.println("\tS: Calculated median split value of " + pCreat.calculateMedianSE());
			} else if (numSplit > 1) {
				System.err.println("E: Too many median split groups, not set up for more than 1.");
			}
			System.out.println("S: There are " + numKnownParticipants + " countable users.");
			System.out.println("\tS: You will need approximately " + numKnownParticipants/2 + " teams.");
		
		// Gather input
       Scanner scanIn = new Scanner(System.in);     
       System.out.print("Q: Team prefix? ");
       TeamAssigner.setTeamPrefix(scanIn.nextLine());
	
		int numAllLow = 0;
		int numAllMed = 0;
		int numAllHigh = 0;
		int numLowHigh = 0;
		int numLowMed = 0;
		int numMedHigh = 0;
		
		boolean valid = false;
		
		while (!valid) {
				try {
					switch (numSplit) {
					case 0:
						numAllMed = pCreat.getParticipantList().size()/2;
						break;
					case 2:
						System.out.print("Q: Number of Teams ALL-MED? ");
						numAllMed = Integer.parseInt(scanIn.nextLine());
						System.out.print("Q: Number of Teams LOW-MED? ");
						numLowMed = Integer.parseInt(scanIn.nextLine());
						System.out.print("Q: Number of Teams MED-HIGH? ");
						numMedHigh = Integer.parseInt(scanIn.nextLine());
					case 1:
						System.out.print("Q: Number of Teams ALL-LOW? ");
						numAllLow = Integer.parseInt(scanIn.nextLine());
						System.out.print("Q: Number of Teams ALL-HIGH? ");
						numAllHigh = Integer.parseInt(scanIn.nextLine());
						System.out.print("Q: Number of Teams LOW-HIGH? ");
						numLowHigh = Integer.parseInt(scanIn.nextLine());
						break;
					}
					
					int totNumTeams = numAllLow+numAllMed+numAllHigh+numLowHigh+numLowMed+numMedHigh;
					int totNumPs = totNumTeams * 2;
					if (totNumPs > (numKnownParticipants+1)) { // too many teams
						System.err.println("E: Too many teams ("+totNumTeams+" teams for " + numKnownParticipants+" participants). Try again.");
						valid = false;
					} else if (totNumPs < (numKnownParticipants-1)) { // not enough teams
						System.err.println("E: Not enough teams ("+totNumTeams+" teams for " + numKnownParticipants+" participants). Try again.");
						valid = false;
					} else {
						System.out.println("S: Appropriate number of teams.");
						valid = true;
					}
				} catch (NumberFormatException ne) {
					System.err.println("E: Invalid number. Try again.");
					valid = false;
				}
			} // end while(!valid)
			scanIn.close(); 

			pList = TeamAssigner.assignTeams(pCreat.getParticipantList(), numAllLow, numAllMed, numAllHigh, numLowHigh, numLowMed, numMedHigh);
		} else { // file is processed, let's look at old stuff!!			
			pList = pCreat.readProcessedFile(readFile);
			pCreat.assignMedianSplit(); // this is already done in readUnprocessedFile()
		}
		
		printSortedTeams(pList);
		tw.writeFile();
		
		System.out.println("Done.");
	}
	
	public static void printTeams(Hashtable<String, Pair<Participant>> p) {
		Iterator<String> itr = p.keySet().iterator(); 
		while(itr.hasNext()) {
		    String team = itr.next(); 
		    
			System.out.println("Team: " + team);
			System.out.println(p.get(team).toString());

		} 
	}
	
	public static void printSortedTeams(Hashtable<String, Pair<Participant>> p) {		
		Iterator<String> itr = p.keySet().iterator(); 
		
		ArrayList<Pair<Participant>> allHigh = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> allLow = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> allMed = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> lowHigh = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> lowMed = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> medHigh = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> unknown = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> onePerson = new ArrayList<Pair<Participant>>();
		
		// Organize teams by their composition
		while(itr.hasNext()) {
		    String team = itr.next(); 
		    
		    if (p.get(team).getSecond() == null) {
		    	// One Person pair
		    	onePerson.add(p.get(team));
			} else if ((p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_UNKNOWN) || p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_UNKNOWN))) {
				// Unknown pair
				unknown.add(p.get(team));
		    } else if (p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_LOW) 		    		
		    		&& p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_LOW)) {
		    	allLow.add(p.get(team));		    	
		    } else if (p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_HIGH)
		    		&& p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_HIGH)) {
		    	allHigh.add(p.get(team));
		    } else if (p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_MED) 
		    		&& p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_MED)) {
		    	allMed.add(p.get(team));
		    } else if ((p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_LOW) && p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_HIGH))
					|| (p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_HIGH) && p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_LOW))) {
				// Low/High pair
				lowHigh.add(p.get(team));
			} else if ((p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_LOW) && p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_MED))
					|| (p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_MED) && p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_LOW))) {
				// Low/Med pair
				lowMed.add(p.get(team));
			} else if ((p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_MED) && p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_HIGH))
					|| (p.get(team).getFirst().getSEmedian().equals(VarConstants.MEDIAN_HIGH) && p.get(team).getSecond().getSEmedian().equals(VarConstants.MEDIAN_MED))) {
				// Med/High pair
				medHigh.add(p.get(team));
			} else {
				System.err.println("Team is not a correct mix: " + p.get(team).getFirst().getSEmedian() + "/" + p.get(team).getSecond().getSEmedian());
				System.err.println("Team: " + p.get(team).getFirst().getTeam() + ": ["+p.get(team).getFirst().getUid() + " , "+ p.get(team).getFirst().getUid() + "]");
			} 
		    
		} 
		
		// Now, assign to conditions!
		// It will split into three conditions, evenly (target high, target low, and neutral)
		allHigh = ConditionAssigner.assignConditions(tw, "HighHigh", allHigh);
		allLow = ConditionAssigner.assignConditions(tw, "LowLow", allLow);
		lowHigh = ConditionAssigner.assignConditions(tw, "LowHigh", lowHigh);
		unknown = ConditionAssigner.assignSimpleConditions(tw, unknown);
		onePerson = ConditionAssigner.assignSimpleConditions(tw, onePerson);		
		
		// Print counts
		System.out.println("Low-Low Teams: " + allLow.size());
		System.out.println("High-High Teams: " + allHigh.size());
		System.out.println("Low-High Teams: " + lowHigh.size());
		if (numSplit > 1) {
			System.out.println("Med-Med Teams: " + allMed.size());
			System.out.println("Low-Med Teams: " + lowMed.size());
			System.out.println("Med-High Teams: " + medHigh.size());
		}
		System.out.println("Unknown Teams: " + unknown.size());
		System.out.println("Single User Teams: " + onePerson.size());
		
		// Printing		
		if (unknown.size() > 0) {
			System.out.println("\n--- UNKNOWN ---");
			for(Pair<Participant> pair : unknown) {
				System.out.println("Team: " + pair.getFirst().getTeam());
				System.out.println(pair.toString());
				System.out.println("\tCond:"+pair.getFirst().getCondition());
			}
		}
		if (onePerson.size() > 0) {
			System.out.println("\n--- SINGLE USER ---");
			for(Pair<Participant> pair : onePerson) {
				System.out.println("Team: " + pair.getFirst().getTeam());
				System.out.println(pair.toString());
				System.out.println("\tCond:"+pair.getFirst().getCondition());
			}
		}
		
		Iterator<Pair<Participant>> pItr = lowHigh.iterator();
		if (lowHigh.size() > 0) {
			System.out.println("\n--- LOW-HIGH MIXED ---");
			while (pItr.hasNext()) {
				Pair<Participant> pair = pItr.next();
				System.out.println("Team: " + pair.getFirst().getTeam());
				System.out.println(pair.toString());
				System.out.println("\tCond:"+pair.getFirst().getCondition());
			}
		}
		
		if (lowMed.size() > 0) {
			pItr = lowMed.iterator();
			System.out.println("\n--- LOW-MED MIXED ---");
			while (pItr.hasNext()) {
				Pair<Participant> pair = pItr.next();
				System.out.println("Team: " + pair.getFirst().getTeam());
				System.out.println(pair.toString());
				System.out.println("\tCond:"+pair.getFirst().getCondition());
			}
		}
		
		if (medHigh.size() > 0) {
			pItr = medHigh.iterator();
			System.out.println("\n--- MED-HIGH MIXED ---");
			while (pItr.hasNext()) {
				Pair<Participant> pair = pItr.next();
				System.out.println("Team: " + pair.getFirst().getTeam());
				System.out.println(pair.toString());
				System.out.println("\tCond:"+pair.getFirst().getCondition());
			}
		}

		if (allHigh.size() > 0) {
			pItr = allHigh.iterator();
			System.out.println("\n--- HIGH HIGH ---");
			while (pItr.hasNext()) {
				Pair<Participant> pair = pItr.next();
				System.out.println("Team: " + pair.getFirst().getTeam());
				System.out.println(pair.toString());
				System.out.println("\tCond:"+pair.getFirst().getCondition());
			}
		}
		
		if (allMed.size() > 0) { // Three-way median split, so we have a 'medium'
			pItr = allMed.iterator();
			System.out.println("\n--- MED MED ---");
			while (pItr.hasNext()) {
				Pair<Participant> pair = pItr.next();
				System.out.println("Team: " + pair.getFirst().getTeam());
				System.out.println(pair.toString());
				System.out.println("\tCond:"+pair.getFirst().getCondition());
			}
		}
		
		if (allLow.size() > 0) {
			pItr = allLow.iterator();
			System.out.println("\n--- LOW LOW ---");
			while (pItr.hasNext()) {
				Pair<Participant> pair = pItr.next();
				System.out.println("Team: " + pair.getFirst().getTeam());
				System.out.println(pair.toString());
				System.out.println("\tCond:"+pair.getFirst().getCondition());
			}
		}
	}

}
