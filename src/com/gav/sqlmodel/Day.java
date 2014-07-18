package com.gav.sqlmodel;

import java.util.List;

public class Day {

	public enum WeekDay {
		MONDAY(0), TUESDAY(1), WEDNESDAY(2), THURSDAY(3), FRIDAY(4), SATURDAY(5), SUNDAY(6);
		private final int mask;
		private WeekDay(int mask){
			this.mask = mask;
		}
		
		public int getMask(){
			return mask;
		}
	}
	private int id;
	private String name;
	private WeekDay weekday;
	
	private List<Exercise> exercises;
	
	public Day(){}
	
	public Day(int id, String name, WeekDay weekday) {
		this.id = id;
		this.name = name;
		this.weekday = weekday;
	}
	
	public Day(String name, WeekDay weekday) {
		this.name = name;
		this.weekday = weekday;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WeekDay getWeekday() {
		return weekday;
	}

	public void setWeekday(int weekday) {
		this.weekday = WeekDay.values()[weekday];
	}

	public void setExercises(List<Exercise> exercises) {
		this.exercises = exercises;
	}

	public List<Exercise> getExercises() {
		return exercises;
	}

	public void addExercise(Exercise exercise) {
		exercises.add(exercise);
	}
	
	public void removeExercise(Exercise exercise){
		exercises.remove(exercise);
	}
	
	public String toString(){
		return name;
	}
}
