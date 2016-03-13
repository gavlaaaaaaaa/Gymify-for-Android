package com.gav.sqlmodel;


import java.sql.Time;

public class Set {
	
	private int id;
	private String create_time;
	private int noReps;
	private double weight;
	private double distance;
	private Time timespent;
	private String type;
	
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

	public Set(int id, String create_time, double distance, Time timespent) {
		this.id = id;
		this.create_time = create_time;
		this.distance = distance;
		this.timespent = timespent;
		this.type = "Cardio";
	}

	public Set(String create_time, double distance, Time timespent) {
		this.create_time = create_time;
		this.distance = distance;
		this.timespent = timespent;
		this.type = "Cardio";
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

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getTimespent() {
		return timespent.toString();
	}

	public void setTimespent(String timespent) {
		this.timespent = Time.valueOf(timespent);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString(){
		return this.create_time + ": \n" + "Reps: " + this.noReps + "\t Weight: " + this.weight;
	}
	
	
}
