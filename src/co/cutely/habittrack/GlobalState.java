package co.cutely.habittrack;

import android.app.Application;

public class GlobalState extends Application
{
	private TrackedHabit habitParameter = null;
	private int habitIndex = -1;
	
	public void setHabitParameter(final TrackedHabit habitParameter) {
		this.habitParameter = habitParameter;
	}
	public TrackedHabit getHabitParameter() {
		return this.habitParameter;
	}
	public int getHabitIndex() {
		return this.habitIndex;
	}
	public void setHabitIndex(int habitIndex) {
		this.habitIndex = habitIndex;
	}
}