package com.gav.sqlmodel;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Exercise {


	private int id;
	private String name;
	private String exerciseType;
	private String exerciseArea;
	private String description;
	private int noSets;
	private double distanceGoal;
	private List<Set> sets;
	private List<Set> lastSets;

	public static String [] mgroups = {"Chest", "Biceps", "Triceps", "Back", "Shoulders", "Abs", "Legs"};
	public static String [] cgroups = {"Run", "Cycle", "Row"};
	
	public Exercise(){
		id = 0; name = ""; exerciseType=""; description = "";
	}
	
	public Exercise(int id, String name, String exerciseArea, String description, int noSets) {
		this.id = id;
		this.name = name;
		this.exerciseType = "WEIGHT";
		this.description = description;
		this.exerciseArea = exerciseArea;
		this.noSets = noSets;
	}
	
	public Exercise(String name, String exerciseArea, String description, int noSets) {
		this.name = name;
		this.exerciseType = "WEIGHT";
		this.description = description;
		this.exerciseArea = exerciseArea;
		this.noSets = noSets;
	}

	public Exercise(int id, String name, String exerciseArea, String description, double distanceGoal) {
		this.id = id;
		this.name = name;
		this.exerciseType = "CARDIO";
		this.description = description;
		this.exerciseArea = exerciseArea;
		this.distanceGoal = distanceGoal;
	}

	public Exercise(String name, String exerciseArea, String description, double distanceGoal) {
		this.name = name;
		this.exerciseType = "CARDIO";
		this.description = description;
		this.exerciseArea = exerciseArea;
		this.distanceGoal = distanceGoal;
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

	public String getExerciseType() {
		return exerciseType;
	}

	public void setExerciseType(String type) {
		this.exerciseType = type;
	}

	public String getExerciseArea() {
		return exerciseArea;
	}

	public void setExerciseArea(String exerciseArea) {
		this.exerciseArea = exerciseArea;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getNoSets() {
		return noSets;
	}

	public void setNoSets(int noSets) {
		this.noSets = noSets;
	}

	public void addSet(Set s){
		sets.add(s);
	}
	
	public void removeSet(Set s){
		sets.remove(s);
	}

	public double getDistanceGoal() {
		return distanceGoal;
	}

	public void setDistanceGoal(double distanceGoal) {
		this.distanceGoal = distanceGoal;
	}
	
	public List<Set> getSets(){
		return sets;
	}
	
	public void setLastSets(List<Set> lastSets){
		this.lastSets = new ArrayList<Set>(lastSets);
	}
	
	public List<Set> getLastsSets(){
		return lastSets;
	}
	
	public void setSets(List<Set> sets) {
		this.sets = sets;
	}

	public String toString(){
		return this.name;
	}
}
