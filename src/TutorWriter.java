import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import struct.Participant;

/**
 * Writes output for the tutor software to read.
 * Lines are in the following format: room, condition, target, other
 * With the 'room' starting at '1' and going incrementally from there.
 * 
 * @author iris
 *
 */
public class TutorWriter {
	private ArrayList<String> teamRows; // all previous rows, and the new ones!
	private ArrayList<String> newRows; // only the new rows!
	
	private int numNeutralLow = 0;
	private int numNeutralHigh = 0;
	private int numNeutralLowHigh = 0;
	private int numTargetLow = 0;
	private int numTargetHigh = 0;
	private int numTargetLowHigh = 0;
	
	
	private int numAllLow = 0;
	private int numAllMed = 0;
	private int numAllHigh = 0;
	private int numLowHigh = 0;
	private int numLowMed = 0;
	private int numMedHigh = 0;
	
	private int numNeutral = 0;
	private int numTarget = 0;
	
	public TutorWriter() {
		teamRows = new ArrayList<String>();
		newRows = new ArrayList<String>();
	}
	
	/**
	 * Adds a simple string to our list of rows to print
	 * @param line
	 */
	public void addString(String line) {
		teamRows.add(line);
		newRows.add(line);
	}
	
	public ArrayList<String> getAllRows() {
		return teamRows;
	}
	
	public int getAllLow() {
		return numAllLow;
	}
	public int getAllMed() {
		return numAllMed;
	}
	public int getAllHigh() {
		return numAllHigh;
	}
	public int getLowHigh() {
		return numLowHigh;
	}
	public int getMedHigh() {
		return numMedHigh;
	}
	public int getLowMed() {
		return numLowMed;
	}
	public int getTarget() {
		return numTarget;
	}
	public int getNeutral() {
		return numNeutral;
	}

	public String toString() {
		String str = "X\tLow-Low\t\tHigh-High\tLow-High\tTot\n";
		str += "Neutral\t" + numNeutralLow + "\t\t" + numNeutralHigh + "\t\t" + numNeutralLowHigh + "\t\t" + numNeutral + "\n";
		str += "Target\t" + numTargetLow + "\t\t" + numTargetHigh + "\t\t" + numTargetLowHigh + "\t\t" + numTarget + "\n";
		str += "Tot\t" + numAllLow + "\t\t" + numAllHigh + "\t\t" + numLowHigh + "\n";
	
		// TODO: print 'medium' splits?
		return str;
	}
	
	/**
	 * room,condition,target,other
	 * 1,target,s01,s02
	 * 2,target,s03,s04
	 * 3,neutral,s05,s06
	 * 4,target,s07,s08
	 * 5,target,s09,s10
	 * 6,neutral,s11,s12
	 * @param p1 the first Participant / team member
	 * @param p2 the second Participant / team member
	 * @param condition the condition ("target" or "neutral") of the team
	 * @param target whether the FIRST participant is the target or not
	 */
	public void addParticipant(Participant p1, Participant p2, String teamType, String condition, boolean target) {
		String str = "";
		str += p1.getTeam() + "," + condition + ",";
		if (p1.getTeam() == null) {
			System.err.println("TutorWriter: How is p1's team null?");
		}
		if (p2 == null) {
			str += p1.getUid() + ", ";
		} else if (target) {
			str += p1.getUid() + "," + p2.getUid();
		} else {
			str += p2.getUid() + "," + p1.getUid();
		}
		str += "\n";
		this.addString(str);
		
		// TODO: There's going to be some problem with 'unknown' participants
		// counting processes
		if (condition.equals(VarConstants.CONDITION_TARGET)) {
			numTarget++;
			
			if (teamType.equals("LowLow")) {
				numAllLow++;
				numTargetLow++;
			} else if (teamType.equals("HighHigh")) {
				numAllHigh++;
				numTargetHigh++;
			} else if (teamType.equals("LowHigh")) {
				numLowHigh++;
				numTargetLowHigh++;
			} else if (teamType.equals("MedMed")) { // TODO: implement this for more median splits
				numAllMed++;
			} else if (teamType.equals("LowMed")) {
				numLowMed++;
			} else if (teamType.equals("MedHigh")) {
				numMedHigh++;
			}
		} else {
			numNeutral++;
			
			if (teamType.equals("LowLow")) {
				numAllLow++;
				numNeutralLow++;
			} else if (teamType.equals("HighHigh")) {
				numAllHigh++;
				numNeutralHigh++;
			} else if (teamType.equals("LowHigh")) {
				numLowHigh++;
				numNeutralLowHigh++;
			} else if (teamType.equals("MedMed")) {
				numAllMed++;
			} else if (teamType.equals("LowMed")) {
				numLowMed++;
			} else if (teamType.equals("MedHigh")) {
				numMedHigh++;
			}
		}
	}
	
	/**
	 * This is for teams with only one member, or 'unknown' self-efficacy types.
	 * In short, for teams we don't care to count.
	 * room,condition,target,other
	 * 1,target,s01,s02
	 * 2,target,s03,s04
	 * 3,neutral,s05,s06
	 * 4,target,s07,s08
	 * 5,target,s09,s10
	 * 6,neutral,s11,s12
	 * @param p1 the first Participant / team member
	 * @param p2 the second Participant / team member
	 * @param condition the condition ("target" or "neutral") of the team
	 * @param target whether the FIRST participant is the target or not
	 */
	public void addSimpleTeam(Participant p1, Participant p2, String condition) {
		String str = p1.getTeam() + "," + condition + "," + p1.getUid() + ",";
		if (p2 != null) {
			str+= p2.getUid();
		}
		str +="\n";		
		this.addString(str);		
	}
	
	public ArrayList<String> readLines(String inFile) {
		
		try {
			FileInputStream fstream = new FileInputStream(inFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			// X, 		LowLow, HighHigh, LowHigh, MedMed, LowMed, MedHigh
			// Neutral, .., .., ..
			// Target, .., .., ..
			if ((strLine = br.readLine()) != null) { // first line, just has column headers
				String[] firstRow = strLine.split(",");				
			}
			if ((strLine = br.readLine()) != null) { // second line
				String[] secRow = strLine.split(",");
				numNeutralLow = Integer.parseInt(secRow[1]);
				numNeutralHigh = Integer.parseInt(secRow[2]);
				numNeutralLowHigh = Integer.parseInt(secRow[3]);
				
				numNeutral = numNeutralLow + numNeutralHigh + numNeutralLowHigh;
			}
			if ((strLine = br.readLine()) != null) { // third line
				String[] thirdRow = strLine.split(",");
				
				numTargetLow = Integer.parseInt(thirdRow[1]);
				numTargetHigh = Integer.parseInt(thirdRow[2]);
				numTargetLowHigh = Integer.parseInt(thirdRow[3]);
				
				numTarget = numTargetLow + numTargetHigh + numTargetLowHigh;
			}
			
			numAllLow = numTargetLow + numNeutralLow;
			numAllHigh = numTargetHigh + numNeutralHigh;
			numLowHigh = numTargetLowHigh + numNeutralLowHigh;
			
			// TODO: implement functionality for 'med', 'low med', and 'med high'
			
			// TODO: remove the iteration through the rest of the lines. Maybe?
			while ((strLine = br.readLine()) != null) { // rest of the lines
				teamRows.add(strLine + "\n"); // I think this extra newline is necessary
			}
		} catch (Exception e) {
			System.err.println("Error reading past lines: " + e);
		}
		
		return teamRows;
	}
	
	public void writeFile() {
		try {
			// Create file 
			BufferedWriter out = new BufferedWriter(new FileWriter(VarConstants.writeFile,false));
			BufferedWriter tutorOut = new BufferedWriter(new FileWriter(VarConstants.tutorFile,false));
			
			// Neutral, Target, LowLow, HighHigh, LowHigh, 
			out.write("X, LowLow, HighHigh, LowHigh\n");
			out.write("Neutral," + numNeutralLow + "," +numNeutralHigh + "," +numNeutralLowHigh+"\n");
			out.write("Target," + numTargetLow + "," +numTargetHigh + "," +numTargetLowHigh+"\n");
			
			// write it!
			int roomNum = 1;
			for (String l : newRows) {
				int firstCommaInd = l.indexOf(',');
				tutorOut.write(roomNum + l.substring(firstCommaInd));
				roomNum++;
			}
			
			// Write out all lines ever, to keep track
			for (String l : this.getAllRows()) {
				out.write(l); 
			}
						
			//Close the output streams
			out.close();
			tutorOut.close();
		} catch (Exception e) {	//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
			 
	}	
}
