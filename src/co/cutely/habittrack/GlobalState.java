package co.cutely.habittrack;

import java.util.ArrayList;

import android.app.Application;

public class GlobalState extends Application
{
	private TrackedHabit mHabitParameter = null;
	private int mHabitIndex = -1;
	private ArrayList<TrackedHabit> mInitialList = null;
	private boolean mFirstRun = false;
	
	public boolean isFirstRun() {
		return this.mFirstRun;
	}
	public void setFirstRun(boolean firstRun) {
		this.mFirstRun = firstRun;
	}
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
	public ArrayList<TrackedHabit> getInitialList() {
		return this.mInitialList;
	}
	public void setInitialList(ArrayList<TrackedHabit> initialList) {
		this.mInitialList = initialList;
	}
	
}