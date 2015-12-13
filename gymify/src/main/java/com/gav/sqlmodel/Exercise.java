package com.gav.sqlmodel;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
	
	public enum MuscleGroup{
		CHEST(0), BICEP(1), TRICEP(2), BACK(3), SHOULDER(4), ABS(5), LEG(6);
		private final int mask;
		private MuscleGroup(int mask){
			this.mask = mask;
		}
		
		public int getMask(){
			return mask;
		}
	}
	
	public static String [] mgroups = {"Chest", "Biceps", "Triceps", "Back", "Shoulders", "Abs", "Legs"};
	
	private int id;
	private String name;
	private MuscleGroup mgroup;
	private String description;
	private int noSets;
	private List<Set> sets;
	private List<Set> lastSets;
	
	
	public Exercise(){
		id = 0; name = ""; mgroup = MuscleGroup.CHEST; description = ""; 
	}
	
	public Exercise(int id, String name, MuscleGroup mgroup, String description, int noSets) {
		this.id = id;
		this.name = name;
		this.mgroup = mgroup;
		this.description = description;
		this.noSets = noSets;
	}
	
	public Exercise(String name, MuscleGroup mgroup, String description, int noSets) {
		this.name = name;
		this.mgroup = mgroup;
		this.description = description;
		this.noSets = noSets;
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

	public MuscleGroup getMgroup() {
		return mgroup;
	}

	public void setMgroup(int mgroup) {
		this.mgroup = MuscleGroup.values()[mgroup];
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
