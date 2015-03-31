package struct;
/**
 * A generic 'Pair' datatype to store partners assigned to a team.
 * Something like this must already exist.
 * 
 * @author iris
 *
 * @param <T>
 */
public class Pair<T> {
	private T obj1;
	private T obj2;
	
	public Pair(T o) {
		obj1 = o;
	}
		
	public Pair(T o1, T o2) {
		setPair(o1, o2);
	}
	
	public void setPair(T o1, T o2) {
		obj1 = o1;
		obj2 = o2;
	}
	
	public void pushObject(T o) {
		if (obj1 == null) {
			obj1 = o;
		} else if (obj1 != null && obj2 != null){
			System.err.println("No null objects in pair, replacing obj2");
			obj2 = obj1;
			obj1 = o;
		} else { // (obj1 != null && obj2 == null)			
			obj1 = o;
		}
	}
	
	public void addObject(T o) {
		if (obj1 == null) {
			obj1 = o;
		} else if (obj2 == null) {
			obj2 = o;
		} else {
			System.err.println("No null objects in pair, replacing obj2");
			obj2 = o;
		}
	}
	
	public T getFirst() {
		return obj1;
	}
	public T getSecond() {
		return obj2;
	}
	
	public String toString() {
		String str = "";
		if (obj1 !=null)
			str += "\t"+obj1.toString();
		if (obj2 != null)
			str+= "\n\t"+obj2.toString();
		return str;
	}

}
