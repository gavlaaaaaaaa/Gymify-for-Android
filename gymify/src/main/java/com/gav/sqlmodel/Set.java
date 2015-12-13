package com.gav.sqlmodel;


public class Set {
	
	private int id;
	private String create_time;
	private int noReps;
	private double weight;
	
	public Set(){}
	
	public Set(int id, String create_time, int noReps, double weight) {
		this.id = id;
		this.create_time = create_time;
		this.noReps = noReps;
		this.weight = weight;
	}
	
	public Set(String create_time, int noReps, double weight) {
		this.create_time = create_time;
		this.noReps = noReps;
		this.weight = weight;
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
	
	public String toString(){
		return this.create_time + ": \n" + "Reps: " + this.noReps + "\t Weight: " + this.weight;
	}
	
	
}
