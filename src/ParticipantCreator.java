

import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

import struct.Pair;
import struct.Participant;

/**
 * Reads a SurveyMonkey file full of survey responses
 * and creates a list of Participants from those responses.
 * Also, it calculates self-efficacy from a 4-question scale
 * and then assigns each Participant a SE median split score.
 * 
 * @author iris
 *
 */
public class ParticipantCreator {	
	// Column headers
	private final String[] SE_COL_HEADERS = VarConstants.SE_COL_HEADERS;
	private final String STUDENT_ID = VarConstants.STUDENT_ID;
	private final String TEAM_ID = VarConstants.TEAM_ID;
	
	private final String MEDIAN_HIGH = VarConstants.MEDIAN_HIGH;
	private final String MEDIAN_MED = VarConstants.MEDIAN_MED;
	private final String MEDIAN_LOW = VarConstants.MEDIAN_LOW;
	private final String MEDIAN_UNKNOWN = VarConstants.MEDIAN_UNKNOWN;
	private final int NUM_SE_COLS = VarConstants.NUM_SE_COLS;
	
	// Global variables
	private ArrayList<Participant> participants; // An array of all students/participants
	private Hashtable<String, Pair<Participant>> byTeam; 	// A Hashtable TeamID --> Participants 
															// Only used for a processed file!
	private ArrayList<Double> selfEfficacyScores; // a sorted list of all student SE scores
	private ArrayList<Participant> unknownSE;
	private int split = VarConstants.NUM_SPLIT; // How many groups to median-split the students into
	private double medianSEvalue = VarConstants.MEDIAN_VAL; // the predetermined median split value

		
	public ArrayList<Participant> getParticipantList() {
		return participants;
	}
	public ArrayList<Participant> getUnknownParticipantList() {
		return unknownSE;
	}
	
	public ArrayList<Double> getAllSE() {
		return selfEfficacyScores;
	}
	
	public void clear() {
		participants = null;
		selfEfficacyScores = null;
		split = -1;
		medianSEvalue = 0;
		unknownSE = null;
	}
	
	public double calculateMedianSE() {
		Collections.sort(selfEfficacyScores);
		return selfEfficacyScores.get(selfEfficacyScores.size()/2).doubleValue();
	}
	
	/**
	 * Assigns a 'median split' value to each participant, based upon
	 * where there self-efficacy score lies within the median split groups
	 * @param s the number of groups to median split into (0, 1, 2)
	 */
	public void assignMedianSplit() {
		if (split < 0 || split > 2) {
			System.err.println("Split value must be in range [0,2]");
			return;
		}
				
		if (split == 0) { // no split
			Iterator<Participant> pItr = participants.iterator();
			
			Participant p;
			while (pItr.hasNext()) {
				p = pItr.next();
				p.setSEmedian(MEDIAN_MED);
			}
			
		} else if (split == 1) { // True Median split
			// If invalid median split value, get a valid one
			if (this.medianSEvalue < 1) {
				medianSEvalue = this.calculateMedianSE();
			}
			
			Iterator<Participant> pItr = participants.iterator();
			
			Participant p;
			while (pItr.hasNext()) {
				p = pItr.next();
				if (p.getSE() > this.medianSEvalue) {
					p.setSEmedian(MEDIAN_HIGH);
				} else if (p.getSE() <= this.medianSEvalue) {
					p.setSEmedian(MEDIAN_LOW);
				} else { // unknown!
					p.setSEmedian(MEDIAN_UNKNOWN);
				}
			}
		} else if (split == 2) { // Three-way split
			System.err.println("E: This program is currently not set up for three-way median splits. You will encounter errors.");
			double lowSplit = selfEfficacyScores.get(selfEfficacyScores.size()/3).doubleValue();
			double highSplit = selfEfficacyScores.get((selfEfficacyScores.size()*2)/3).doubleValue();
			
			Iterator<Participant> pItr = participants.iterator();
			Participant p;
			while (pItr.hasNext()) {
				p = pItr.next();
				if (p.getSE() >= highSplit) {
					p.setSEmedian(MEDIAN_HIGH);
				} else if (p.getSE() <= lowSplit){
					p.setSEmedian(MEDIAN_LOW);
				} else if (p.getSE() < highSplit && p.getSE() > lowSplit){
					p.setSEmedian(MEDIAN_MED);
				} else {
					p.setSEmedian(MEDIAN_UNKNOWN);
				}
			}
		}
	}
	
	public ArrayList<Participant> readUnprocessedFile(String fname) {
		int uidCol = 0;
		int[] seCols = new int[NUM_SE_COLS];
		unknownSE = new ArrayList<Participant>();
		
		int rowCount = 0;
		
		selfEfficacyScores = new ArrayList<Double>();		
		participants = new ArrayList<Participant>();
		
		try {
			FileInputStream fstream = new FileInputStream(fname);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			if ((strLine = br.readLine()) != null) {
				String[] firstRow = strLine.split(",");
				rowCount++;
				
				// find where the User ID and the Self-Efficacy Scores are located
				for (int i = 0; i < firstRow.length; i++) {
					if (firstRow[i].equals(STUDENT_ID)) {
						uidCol = i;
					} else { // looking for one of ~4 header titles
						for (int j = 0; j < SE_COL_HEADERS.length; j++) {
							if (firstRow[i].equals(SE_COL_HEADERS[j])) {
								seCols[j] = i;
							}
						}
					}
				}
			}
			
			while ((strLine = br.readLine()) != null)   {
				rowCount++;
				
				// Print the content on the console
				//System.out.println (strLine);
				
				// Create a Participant with UID, self-efficacy scores, etc.
				String[] row = strLine.split(",");
				String uidLine = row[uidCol];
				Double seValue = null;
				
				// Computing Self-efficacy score from ~4 user responses
				double sumSE = 0;
				boolean hasAllValues = true;
				// Determine self-efficacy/mastery score
				for (int i = 0; i < seCols.length; i++) {
					if (row[seCols[i]] != null && row[seCols[i]].length() > 0) { // if we have an SE Value here
						sumSE += Double.parseDouble(row[seCols[i]]);
					} else { // otherwise, we're missing a self-efficacy score
						hasAllValues = false;
					}
				}
				
				if (hasAllValues) { // if we aren't missing any data, then make it work
					seValue = new Double(sumSE/seCols.length);
				} else {
					//System.err.println("Missing user mastery or self-efficacy scores: " + uidLine); // debug
				}
				
				// If we have all data, create a new Participant
				if (uidLine != null && seValue != null && uidLine.length() > 0 ) {
					Participant newP = new Participant(uidLine, seValue.doubleValue());
					// Adding to list of all participants
					participants.add(newP);
					
					// Adding self-efficacy score
					selfEfficacyScores.add(new Double(newP.getSE()));					
				} else {
					System.err.println("Warning in unprocessed row " + rowCount + " (null user param): u/" + uidLine + " se/" + seValue);
					Participant uP = new Participant(uidLine, -1);
					uP.setSEmedian(MEDIAN_UNKNOWN);
					unknownSE.add(uP);
				}
			}
			in.close();
		} catch (Exception e) {	//Catch exception if any
			System.err.println("Error in row " + rowCount + ": " + e.getMessage() + "\n" + e);
		}
		
		Collections.sort(selfEfficacyScores);
		// Median Split will be different for each class, need to base it on past years
		this.assignMedianSplit(); 	// give each Participant a Median split value
									// according to where they are in the class
		participants.addAll(unknownSE); // adding all the unknown students so the Team Assigner can deal with it
		System.out.println("S: Number of rows: " + rowCount);
		return participants;
    }
	
	
	public Hashtable<String, Pair<Participant>> readProcessedFile(String fname) {
		int uidCol = 0;
		int teamCol = 0;
		int[] seCols = new int[NUM_SE_COLS];
		
		int rowCount = 0;
		
		selfEfficacyScores = new ArrayList<Double>();
		
		participants = new ArrayList<Participant>();
		byTeam = new Hashtable<String, Pair<Participant>>();
		
		try {
			FileInputStream fstream = new FileInputStream(fname);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			if ((strLine = br.readLine()) != null) {
				String[] firstRow = strLine.split(",");
				rowCount++;
				
				// find where the User ID and the Self-Efficacy Scores are located
				for (int i = 0; i < firstRow.length; i++) {
					if (firstRow[i].equals(STUDENT_ID)) {
						uidCol = i;
					} else if (firstRow[i].equals(TEAM_ID)) {
						teamCol = i;
					} else { // looking for one of ~4 header titles
						for (int j = 0; j < SE_COL_HEADERS.length; j++) {
							if (firstRow[i].equals(SE_COL_HEADERS[j])) {
								seCols[j] = i;
							}
						}
					}
				}
			}
			
			while ((strLine = br.readLine()) != null)   {
				rowCount++;
				
				// Print the content on the console
				//System.out.println (strLine);
				
				// Create a Participant with UID, team, self-efficacy, etc.
				String[] row = strLine.split(",");
				String uidLine = row[uidCol];
				String teamLine = row[teamCol];
				Double seValue = null;
				
				// Computing Self-efficacy score from ~4 user responses
				double sumSE = 0;
				boolean hasAllValues = true;
				// Determine self-efficacy/mastery score
				for (int i = 0; i < seCols.length; i++) {
					if (row[seCols[i]] != null && row[seCols[i]].length() > 0) { // if we have an SE Value here
						sumSE += Double.parseDouble(row[seCols[i]]);
					} else { // otherwise, we're missing a self-efficacy score
						hasAllValues = false;
					}
				}
				
				if (hasAllValues) { // if we aren't missing any data, then make it work
					seValue = new Double(sumSE/seCols.length);
				} else {
					//System.err.println("Missing user mastery or self-efficacy scores: " + uidLine); // debug line
				}
				
				// If we have all data, create a new Participant
				if (uidLine != null && teamLine != null && seValue != null
						&& uidLine.length() > 0 && teamLine.length() > 0 ) {
					Participant newP = new Participant(uidLine, seValue.doubleValue());
					newP.setTeam(teamLine);
					// Adding to list of all participants
					participants.add(newP);
					
					// Adding self-efficacy score
					selfEfficacyScores.add(new Double(newP.getSE()));
					
					// Adding to the Hashtable, so we can organize by Teams
					if (byTeam.get(newP.getTeam()) == null) { // creating new team
						byTeam.put(newP.getTeam(), new Pair<Participant>(newP));
					} else { // adding to existing team
						Pair<Participant> curPair = byTeam.get(newP.getTeam());
						curPair.addObject(newP);
						byTeam.put(newP.getTeam(), curPair);
					}
				} else {
					System.err.println("Error in processed row " + rowCount + " (null user param): u/" + uidLine + " t/" + teamLine + " se/" + seValue);
				}
			}
			in.close();
		} catch (Exception e) {	//Catch exception if any
			System.err.println("Error in row " + rowCount + ": " + e.getMessage() + "\n" + e);
		}
		
		Collections.sort(selfEfficacyScores);
		System.out.println("Number of rows: " + rowCount);
		return byTeam;
    }
	
}