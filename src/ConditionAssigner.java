import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import struct.Pair;
import struct.Participant;

public class ConditionAssigner {

	/**
	 * Randomly assigns given participant list into *three* conditions.
	 * @param pList the list of Participants (in teams) to assign conditions to 
	 * (all teams must me of the same team type, as the one specified)
	 * @param teamType the type of team that the ArrayList is
	 * @return the list of Participants (in teams) with conditions assigned
	 */
	public static ArrayList<Pair<Participant>> assignConditions(TutorWriter tw, String teamType, ArrayList<Pair<Participant>> pList) {
		if (pList == null || pList.size() < 1) {
			return pList;
		}
		
		//ArrayList<Pair<Participant>> pList = new ArrayList<Pair<Participant>>(prList.size());
		//Collections.copy(pList, prList);
		
		Collections.shuffle(pList); // shuffle for randomness
		ArrayList<Pair<Participant>> neutral = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> targetLow = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> targetHigh = new ArrayList<Pair<Participant>>();
		
		// If not evenly divisible by 3, we're going to need to randomly assign
		Random randomGenerator = new Random();
		int numReassign = pList.size()%3;
		for (int i = 0; i < numReassign; i++) {
			int randomInt = randomGenerator.nextInt(3);
			Pair<Participant> toAssign = pList.remove(pList.size()-1);
			switch (randomInt) {
			  case 0:
				  neutral.add(toAssign);
				  toAssign.getFirst().setCondition(VarConstants.CONDITION_NEUTRAL);
				  toAssign.getSecond().setCondition(VarConstants.CONDITION_NEUTRAL);
					tw.addParticipant(toAssign.getFirst(), toAssign.getSecond(), teamType, VarConstants.CONDITION_NEUTRAL, false);
					break;
			  case 1: 
					targetHigh.add(toAssign);
					toAssign.getFirst().setCondition(VarConstants.CONDITION_TARGETHIGH);
					toAssign.getSecond().setCondition(VarConstants.CONDITION_TARGETHIGH);
					
					// TODO: I hope there's not some ordering bias here...
					if (toAssign.getFirst().getSEmedian().equals(VarConstants.MEDIAN_HIGH)) {
						tw.addParticipant(toAssign.getFirst(), toAssign.getSecond(), teamType, VarConstants.CONDITION_TARGET, true);
					} else if (toAssign.getSecond().getSEmedian().equals(VarConstants.MEDIAN_HIGH)) {
						tw.addParticipant(toAssign.getSecond(), toAssign.getFirst(), teamType, VarConstants.CONDITION_TARGET, true);
					} else { // They're both low. Pick one!
						if (toAssign.getFirst().getSE() > toAssign.getSecond().getSE()) { // Pick the one with the higher SE score
							tw.addParticipant(toAssign.getFirst(), toAssign.getSecond(), teamType, VarConstants.CONDITION_TARGET, true);
						} else {
							tw.addParticipant(toAssign.getSecond(), toAssign.getFirst(), teamType, VarConstants.CONDITION_TARGET, true);
						}
					}
					break;
			  case 2:
					targetLow.add(pList.get(i));
					pList.get(i).getFirst().setCondition(VarConstants.CONDITION_TARGETLOW);
					pList.get(i).getSecond().setCondition(VarConstants.CONDITION_TARGETLOW);
					
					// TODO: I hope there's not some ordering bias here...
					if (toAssign.getFirst().getSEmedian().equals(VarConstants.MEDIAN_LOW)) {
						tw.addParticipant(toAssign.getFirst(), toAssign.getSecond(), teamType, VarConstants.CONDITION_TARGET, true);
					} else if (toAssign.getSecond().getSEmedian().equals(VarConstants.MEDIAN_LOW)) {
						tw.addParticipant(toAssign.getSecond(), toAssign.getFirst(), teamType, VarConstants.CONDITION_TARGET, true);
					} else { // They're both high. Pick one!
						if (toAssign.getFirst().getSE() < toAssign.getSecond().getSE()) { // Pick the one with the lower SE score
							tw.addParticipant(toAssign.getFirst(), toAssign.getSecond(), teamType, VarConstants.CONDITION_TARGET, true);
						} else {
							tw.addParticipant(toAssign.getSecond(), toAssign.getFirst(), teamType, VarConstants.CONDITION_TARGET, true);
						}
					}
			      break;
			}
			if (toAssign.getFirst().getTeam() == null) {
				System.err.println("ConditionAssigner1: Warning, p1's team == null");
			}
		}
		
	
		for (int i = 0; i < pList.size(); i++) {
			if (pList.get(i).getSecond() == null) { // One person team, should not be in here!
				System.err.println("E: One person team, shouldn't be in default condition assigning method.");				
			} else if (i < pList.size()/3) { // bottom third, neutral
				neutral.add(pList.get(i));
				pList.get(i).getFirst().setCondition(VarConstants.CONDITION_NEUTRAL);
				pList.get(i).getSecond().setCondition(VarConstants.CONDITION_NEUTRAL);
				tw.addParticipant(pList.get(i).getFirst(), pList.get(i).getSecond(), teamType, VarConstants.CONDITION_NEUTRAL, false);
			} else if (i >= pList.size()*2/3) { // top third, target high
				targetHigh.add(pList.get(i));
				pList.get(i).getFirst().setCondition(VarConstants.CONDITION_TARGETHIGH);
				pList.get(i).getSecond().setCondition(VarConstants.CONDITION_TARGETHIGH);
				
				// TODO: I hope there's not some ordering bias here...
				if (pList.get(i).getFirst().getSEmedian().equals(VarConstants.MEDIAN_HIGH)) {
					tw.addParticipant(pList.get(i).getFirst(), pList.get(i).getSecond(), teamType, VarConstants.CONDITION_TARGET, true);
				} else if (pList.get(i).getSecond().getSEmedian().equals(VarConstants.MEDIAN_HIGH)) {
					tw.addParticipant(pList.get(i).getSecond(), pList.get(i).getFirst(), teamType, VarConstants.CONDITION_TARGET, true);
				} else { // They're both low. Pick one!
					if (pList.get(i).getFirst().getSE() > pList.get(i).getSecond().getSE()) { // Pick the one with the higher SE score
						tw.addParticipant(pList.get(i).getFirst(), pList.get(i).getSecond(), teamType, VarConstants.CONDITION_TARGET, true);
					} else {
						tw.addParticipant(pList.get(i).getSecond(), pList.get(i).getFirst(), teamType, VarConstants.CONDITION_TARGET, true);
					}
				}
			} else { // others, target low
				targetLow.add(pList.get(i));
				pList.get(i).getFirst().setCondition(VarConstants.CONDITION_TARGETLOW);
				pList.get(i).getSecond().setCondition(VarConstants.CONDITION_TARGETLOW);
				
				// TODO: I hope there's not some ordering bias here...
				if (pList.get(i).getFirst().getSEmedian().equals(VarConstants.MEDIAN_LOW)) {
					tw.addParticipant(pList.get(i).getFirst(), pList.get(i).getSecond(), teamType, VarConstants.CONDITION_TARGET, true);
				} else if (pList.get(i).getSecond().getSEmedian().equals(VarConstants.MEDIAN_LOW)) {
					tw.addParticipant(pList.get(i).getSecond(), pList.get(i).getFirst(), teamType, VarConstants.CONDITION_TARGET, true);
				} else { // They're both high. Pick one!
					if (pList.get(i).getFirst().getSE() < pList.get(i).getSecond().getSE()) { // Pick the one with the lower SE score
						tw.addParticipant(pList.get(i).getFirst(), pList.get(i).getSecond(), teamType, VarConstants.CONDITION_TARGET, true);
					} else {
						tw.addParticipant(pList.get(i).getSecond(), pList.get(i).getFirst(), teamType, VarConstants.CONDITION_TARGET, true);
					}
				}
			}
			if (pList.get(i).getFirst().getTeam() == null) {
				System.err.println("ConditionAssigner2: Warning, p1's team == null");
			}
		}
		
		ArrayList<Pair<Participant>> allAssigned = new ArrayList<Pair<Participant>>();
		allAssigned.addAll(targetLow); 
		allAssigned.addAll(targetHigh);
		allAssigned.addAll(neutral);
				
		return allAssigned;
	}
	
	/**
	 * Randomly assigns given participant list into *two* conditions.
	 * This is for teams with only one member in them, or 'unknown' self-efficacies
	 * 
	 * @param pList the list of Participants (in teams) to assign conditions to 
	 * (all teams must be of the same team type, as the one specified)
	 * @param teamType the type of team that the ArrayList is
	 * @return the list of Participants (in teams) with conditions assigned
	 */
	public static ArrayList<Pair<Participant>> assignSimpleConditions(TutorWriter tw, ArrayList<Pair<Participant>> pList) {
		if (pList == null || pList.size() < 1) {
			return pList;
		}
		
		Collections.shuffle(pList); // shuffle for randomness
		ArrayList<Pair<Participant>> neutral = new ArrayList<Pair<Participant>>();
		ArrayList<Pair<Participant>> target = new ArrayList<Pair<Participant>>();	
	
		// Randomly assign the first one, just so that
		// single user teams are not always assigned to 'target'
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(2);
		switch (randomInt) {
			case 0:
				neutral.add(pList.get(0));
				pList.get(0).getFirst().setCondition(VarConstants.CONDITION_NEUTRAL);
				if (pList.get(0).getSecond() != null)
					pList.get(0).getSecond().setCondition(VarConstants.CONDITION_NEUTRAL);
				tw.addSimpleTeam(pList.get(0).getFirst(), pList.get(0).getSecond(), VarConstants.CONDITION_NEUTRAL);
				break;
			case 1:
				target.add(pList.get(0));
				pList.get(0).getFirst().setCondition(VarConstants.CONDITION_TARGET);
				if (pList.get(0).getSecond() != null)
					pList.get(0).getSecond().setCondition(VarConstants.CONDITION_TARGET);
				tw.addSimpleTeam(pList.get(0).getFirst(), pList.get(0).getSecond(), VarConstants.CONDITION_TARGET);
				break;
		}	
		
		// continue as usual with the rest (for unknown teams)
		for (int i = 1; i < pList.size(); i++) {
			if (i < pList.size()/2) { // bottom half, neutral
				neutral.add(pList.get(i));
				pList.get(i).getFirst().setCondition(VarConstants.CONDITION_NEUTRAL);
				if (pList.get(i).getSecond() != null)
					pList.get(i).getSecond().setCondition(VarConstants.CONDITION_NEUTRAL);
				tw.addSimpleTeam(pList.get(i).getFirst(), pList.get(i).getSecond(), VarConstants.CONDITION_NEUTRAL);
			} else { // top half, target
				target.add(pList.get(i));
				pList.get(i).getFirst().setCondition(VarConstants.CONDITION_TARGET);
				if (pList.get(i).getSecond() != null)
					pList.get(i).getSecond().setCondition(VarConstants.CONDITION_TARGET);
				tw.addSimpleTeam(pList.get(i).getFirst(), pList.get(i).getSecond(), VarConstants.CONDITION_TARGET);
			} 
		}
		
		ArrayList<Pair<Participant>> allAssigned = new ArrayList<Pair<Participant>>();
		allAssigned.addAll(target); 
		allAssigned.addAll(neutral);
				
		return allAssigned;
	}
}
