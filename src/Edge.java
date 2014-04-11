
public class Edge {
	private float maxCapacity; // should be private
	private float capacityUsed;
	private float length; // should be private for good practice
	private int id;
	
	public Edge(int id,float length, int maxCapacity) {
	this.setId(id); // This is defined in the outer class.
	this.setLength(length);
	this.setMaxCapacity(maxCapacity);
	}
	public String toString() { // Always good for debugging
	//return "E"+getId();
		return Float.toString(getWeight());
	}
	
	public void setCapacityUsed(float capacityUsed)
	{
		this.capacityUsed=capacityUsed;
	}
	public float getCongestion()
	{
		return capacityUsed/getMaxCapacity();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getWeight()
	{
		return getLength()*(1+getCongestion());
	}
	public float getLength() {
		return length;
	}
	public void setLength(float length) {
		this.length = length;
	}
	public float getMaxCapacity() {
		return maxCapacity;
	}
	public void setMaxCapacity(float maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
}
