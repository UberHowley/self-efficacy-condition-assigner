
/**
 * A bunch of variable constants that should remain the same
 * in between study-runs. Column headers, pre-determined median values, 
 * filenames, etc.
 * @author iris
 * 
 */
public class VarConstants {
	// Basic Details
	//public final static String readFile = "Thermo09Data_v05.csv"; // Testing filename
	public final static String readFile = "surveyResponses.csv"; // Survey Monkey filename
	public final static String writeFile = "thermo11_teams.csv"; // File in semi-tutor-readable format
	public final static String tutorFile = "thermo11_teamsTutor.csv"; // File for tutor to read
	public final static boolean  unprocessed = true; // whether we're analyzing an unprocessed file or not
	public final static int NUM_SPLIT = 1; // split = 1, no 'med'
	public final static double MEDIAN_VAL = 5.775; // last year's median split value (averaged with a year or two before)
	
	// Column headers
	public static final String[] SE_COL_HEADERS = {"MQ1", "MQ2", "MQ3", "MQ4"};
	public static final int NUM_SE_COLS = 4; // Number of cols for SE in SurveyMonkey	
	public static final String STUDENT_ID = "User ID:"; // Survey Monkey column header for the student id
	/*
	public static final String[] SE_COL_HEADERS = {"MQ1", "MQ2", "MQ3", "MQ4"}; // SurveyMonkey column headers for self-efficacy scores
	public static final int NUM_SE_COLS = 4; // Number of cols for SE in SurveyMonkey	
	public static final String STUDENT_ID = "StId"; // Survey Monkey column header for the student id
	*/
	public static final String TEAM_ID = "TeamId"; // Team ID col header for a pre-processed file
	
	// Standardizing Median Titles
	public static final String MEDIAN_HIGH = "high"; // what a high 'self-efficacy median' is
	public static final String MEDIAN_MED = "med";
	public static final String MEDIAN_LOW = "low";
	public static final String MEDIAN_UNKNOWN = "unk";
	public static final String CONDITION_TARGET = "target";
	public static final String CONDITION_TARGETHIGH = "targetHigh";
	public static final String CONDITION_TARGETLOW = "targetLow";
	public static final String CONDITION_NEUTRAL = "neutral";
}
