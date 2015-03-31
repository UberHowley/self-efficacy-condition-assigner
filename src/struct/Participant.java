package struct;
import java.lang.Comparable;

/**
 * A Participant object stores all the valuable information necessary
 * for assigning a participant to a pair of a particular composition
 * (i.e. high-high, low-low, etc) self-efficacy median. This necessary
 * information includes: the user id, the self-efficacy value, and the self-efficacy
 * median score (high, low, med).
 * 
 * @author iris
 *
 */
public class Participant implements Comparable<Participant> {
	private String uid;
	private String team;
	private double selfEff;
	private String rest;
	private String seMedian;
	private String condition;
	
	public Participant() {
		setUid("00");
	}
	
	/**
	 * 
	 * @param u the user ID
	 * @param t the team ID
	 * @param se the self-efficacy score
	 */
	public Participant(String u, double se) {
		setUid(u);
		setSE(se);
	}

	public void setUid(String u) {
		uid = u;
	}	
	public String getUid() {
		return uid;
	}
	public void setTeam(String t) {
		team = t;
	}	
	public String getTeam() {
		return team;
	}
	public void setSEmedian(String sem) {
		seMedian = sem;
	}	
	public String getSEmedian() {
		return seMedian;
	}
	public void setRest(String r) {
		rest = r;
	}	
	public String getRest() {
		return rest;
	}
	public void setSE(double se) {
		selfEff = se;
	}	
	public double getSE() {
		return selfEff;
	}
	public void setCondition(String c) {
		condition = c;
	}
	public String getCondition() {
		return condition;
	}
	
	public boolean isSameTeam(Participant p) {
		if (this.getTeam().equals(p.getTeam())) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		String str = "";
		//str += getUid() + " se/" + getSE();
		str += getUid() + " se/" + getSEmedian();
		
		return str;
	}
	
	public int compareTo(Participant o) {
		
		// Compares Self-Efficacy
		if (this.getSE() > o.getSE()) {
			return 1;
		} else if (this.getUid() == o.getUid()) {
			return 0;
		} 
		return -1;
		
		// Compares Teams
		/**
		if (this.getTeam().equals(o.getTeam())) {
			return 0;
		} else { // they are not identical
			if (this.getTeam().charAt(1) == (o.getTeam().charAt(1))) { // the team letters are the same
				Integer tTeamNum = new Integer(this.getTeam().substring(2, this.getTeam().length()-1));
				Integer oTeamNum = new Integer(o.getTeam().substring(2, o.getTeam().length()-1));
				return tTeamNum.compareTo(oTeamNum);
			} else { // the team letters are different, compare them
				if (this.getTeam().charAt(1) > o.getTeam().charAt(1)) {
					return 1;
				} else {
					return -1;
				}
			}
			
		}*/
	}	
	
}
