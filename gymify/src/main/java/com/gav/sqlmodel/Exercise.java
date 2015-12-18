package com.gav.sqlmodel;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Exercise {

	public static class ExerciseType implements Serializable {
		private String type;
		private MuscleGroup mgroup;
		private CardioGroup cgroup;

		public static String [] mgroups = {"Chest", "Biceps", "Triceps", "Back", "Shoulders", "Abs", "Legs"};
		public static String [] cgroups = {"Run", "Cycle", "Row"};
		Gson gson = new Gson();


		public ExerciseType(){}
		public ExerciseType(MuscleGroup group){
			this.type = "WEIGHT";
			this.mgroup = group;
		}
		public ExerciseType(CardioGroup group){
			this.type = "CARDIO";
			this.cgroup= group;
		}

		public enum MuscleGroup {
			CHEST(0), BICEP(1), TRICEP(2), BACK(3), SHOULDER(4), ABS(5), LEG(6);
			private final int mask;

			private MuscleGroup(int mask) {
				this.mask = mask;
			}

			public int getMask() {
				return mask;
			}
		}

		public enum CardioGroup {
			RUN(0), CYCLE(1), ROW(2);
			private final int mask;

			private CardioGroup(int mask) {
				this.mask = mask;
			}

			public int getMask() {
				return mask;
			}
		}

		public String getType(){return this.type; }

		public String getJson(){
			return new String(gson.toJson(this));
		}

		public int getGroup(){
			if (this.type.equals("WEIGHT")){
				return this.mgroup.getMask();
			}
			else if(this.type.equals("CARDIO") ){
				return this.cgroup.getMask();
			}
			else{
				return 0;
			}

		}

		public String getGroupString(){
			if (this.type.equals("WEIGHT")){
				return mgroups[this.mgroup.getMask()];
			}
			else if(this.type.equals("CARDIO") ){
				return cgroups[this.cgroup.getMask()];
			}
			else{
				return "";
			}
		}
	}

	


	private int id;
	private String name;
	private ExerciseType exerciseType;
	private String description;
	private int noSets;
	private List<Set> sets;
	private List<Set> lastSets;
	
	
	public Exercise(){
		id = 0; name = ""; exerciseType=new ExerciseType(); description = "";
	}
	
	public Exercise(int id, String name, ExerciseType type, String description, int noSets) {
		this.id = id;
		this.name = name;
		this.exerciseType = type;
		this.description = description;
		this.noSets = noSets;
	}
	
	public Exercise(String name, ExerciseType type, String description, int noSets) {
		this.name = name;
		this.exerciseType = type;
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

	public ExerciseType getExerciseType() {
		return exerciseType;
	}

	public void setExerciseType(ExerciseType type) {
		this.exerciseType = type;
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
