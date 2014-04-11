import java.awt.geom.Point2D;


public class Node implements java.lang.Comparable{
	private int id; // good coding practice would have this as private
	private float hazardLevel;
	private float capacityUsed;
	private float maxCapacity;
	private boolean isExit;
	private Point2D.Double coordinates;
	
	public Node(int id, Point2D.Double coordinates, int maxCapacity, boolean isExit) {
	this.setId(id);
	this.setCoordinates(coordinates);
	this.setMaxCapacity(maxCapacity);
	this.setExit(isExit);
	}
	public String toString() { // Always a good idea for debuging
	return "V"+getId(); // JUNG2 makes good use of these.
	}
	
	public void setCapacityUsed(float capacityUsed)
	{
		this.capacityUsed=capacityUsed;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public int compareTo(Object n) {
		 final int BEFORE = -1;
		    final int EQUAL = 0;
		    final int AFTER = 1;

		    //this optimization is usually worthwhile, and can
		    //always be added
		    if (this == n) return EQUAL;

		    //primitive numbers follow this form
		    if (this.getId() < ((Node) n).getId()) return BEFORE;
		    if (this.getId() > ((Node) n).getId()) return AFTER;
		   // if (this.getId() == n.getId()) return EQUAL;
		  

		    //all comparisons have yielded equality
		    //verify that compareTo is consistent with equals (optional)
		    assert this.equals(n) : "compareTo inconsistent with equals.";

		    return EQUAL;
	}
	boolean isExit() {
		return isExit;
	}
	void setExit(boolean isExit) {
		this.isExit = isExit;
	}
	public Point2D.Double getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(Point2D.Double coordinates) {
		this.coordinates = coordinates;
	}
	public float getCapacityUsed() {
		return capacityUsed;
	}
	public float getHazardLevel() {
		return hazardLevel;
	}
	public void setHazardLevel(float hazardLevel) {
		this.hazardLevel = hazardLevel;
	}
	public float getMaxCapacity() {
		return maxCapacity;
	}
	public void setMaxCapacity(float maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

}
