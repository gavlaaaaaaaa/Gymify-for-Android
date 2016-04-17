package com.gav.sqlmodel;


import java.util.concurrent.TimeUnit;

public class Set {
	
	private int id;
	private String create_time;
	private int noReps;
	private double weight;
	private float distance;
	private long timespent;
	private String type;
	private long steps;
	
	public Set(){}
	
	public Set(int id, String create_time, int noReps, double weight) {
		this.id = id;
		this.create_time = create_time;
		this.noReps = noReps;
		this.weight = weight;
		this.type = "Weight";
	}
	
	public Set(String create_time, int noReps, double weight) {
		this.create_time = create_time;
		this.noReps = noReps;
		this.weight = weight;
		this.type = "Weight";
	}

	public Set(int id, String create_time, float distance, long timespent, long steps) {
		this.id = id;
		this.create_time = create_time;
		this.distance = distance;
		this.timespent = timespent;
		this.type = "Cardio";
		this.steps = steps;
	}

	public Set(String create_time, float distance, long timespent, long steps) {
		this.create_time = create_time;
		this.distance = distance;
		this.timespent = timespent;
		this.type = "Cardio";
		this.steps = steps;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public int getNoReps() {
		return noReps;
	}

	public void setNoReps(int noReps) {
		this.noReps = noReps;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public long getTimespent() {
		return timespent;
	}

	public String getTimespentAsString(){
		//stole from StackOverflow
		String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timespent),
				TimeUnit.MILLISECONDS.toMinutes(timespent) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(timespent) % TimeUnit.MINUTES.toSeconds(1));
		return hms;
	}

	public void setTimespent(long timespent) {
		this.timespent = timespent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getSteps() {
		return steps;
	}

	public void setSteps(long steps) {
		this.steps = steps;
	}

	public String toString(){
		return this.create_time + ": \n" + "Reps: " + this.noReps + "\t Weight: " + this.weight;
	}
	
	
}
