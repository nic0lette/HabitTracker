package co.cutely.habittrack;

import java.io.Serializable;

/**
 * A tracked habbit "bean"
 * @author niya
 *
 */
public class TrackedHabit implements Serializable {
	
	/**
	 * Serial version ID for serializing 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String name;
	protected int count;
	
	public String getName() {
		return this.name;
	}
	public void setName(final String name) {
		this.name = name;
	}
	
	public int getCount() {
		return this.count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
