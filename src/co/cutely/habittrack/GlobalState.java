package co.cutely.habittrack;

import android.app.Application;

public class GlobalState extends Application
{
	private TrackedHabit mHabitParameter = null;
	private int mHabitIndex = -1;
	
	public void setHabitParameter(final TrackedHabit habitParameter) {
		this.mHabitParameter = habitParameter;
	}
	public TrackedHabit getHabitParameter() {
		return this.mHabitParameter;
	}
	public int getHabitIndex() {
		return this.mHabitIndex;
	}
	public void setHabitIndex(int habitIndex) {
		this.mHabitIndex = habitIndex;
	}
}