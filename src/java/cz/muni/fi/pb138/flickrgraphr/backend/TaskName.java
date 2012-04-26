package cz.muni.fi.pb138.flickrgraphr.backend;

/**
 * Envelope for name and id of task as both should be cared in hashCode (due using HashMap)
 * @author Jan Drabek
 */
public class TaskName {
	private int id;
	private String name;
	/**
	 * Create instance of TaskName (immutable pattern)
	 * @param id
	 * @param name 
	 */
	public TaskName(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof TaskName)) return false;
		TaskName temp = (TaskName) o;
		return (id == temp.id && temp.name == name);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 83 * hash + this.id;
		hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}
	
	/**
	 * Return name
	 * @return Name
	 */
	public String getName() {
		return name;
	}
}