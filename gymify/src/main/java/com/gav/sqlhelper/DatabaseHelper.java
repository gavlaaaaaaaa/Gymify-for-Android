package com.gav.sqlhelper;

import java.util.ArrayList;
import java.util.List;

import com.gav.sqlmodel.Day;
import com.gav.sqlmodel.Exercise;
import com.gav.sqlmodel.Set;
import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	// DB Name and Version
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "gymify";
	
	//DB Table names
	private static final String TABLE_SET = "set_tbl";
	private static final String TABLE_EXERCISE = "exercise";
	private static final String TABLE_DAY = "day";
	private static final String TABLE_DAY_EXERCISE = "day_exercise";
	private static final String TABLE_EXERCISE_SET = "exercise_set";
	
	//Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	
	//SET Table column names
	private static final String KEY_CREATE_TIME = "create_time";
	private static final String KEY_REPS = "noReps";
	private static final String KEY_WEIGHT = "weight";
	private static final String KEY_DISTANCE = "distance";
	private static final String KEY_TIMESPENT = "timestamp";
	private static final String KEY_SET_TYPE = "set_type";
	
	//DAY table column names
	private static final String KEY_WEEKDAY = "weekday";
	
	//EXERCISE Table column names
	private static final String KEY_EXTYPE = "ex_type";
	private static final String KEY_EXAREA = "ex_area";
	private static final String KEY_DESC = "description";
	private static final String KEY_NO_SETS = "no_sets";
	private static final String KEY_DISTANCE_GOAL = "distance_goal";
	
	//DAY_EXERCISES Table column names
	private static final String KEY_DAY_ID = "day_id";
	private static final String KEY_EXERCISE_ID = "exercise_id";
	
	//EXERCISE_SETS Table column names
	//use KEY_EXERCISE_ID from above
	private static final String KEY_SET_ID = "set_id";
	
	//Table Create Statements
	//Set Table
	private static final String CREATE_TABLE_SET = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SET + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CREATE_TIME
            + " TEXT," + KEY_REPS + " INTEGER," + KEY_WEIGHT + " REAL,"
            + KEY_DISTANCE + " DOUBLE," + KEY_TIMESPENT + " TEXT, " + KEY_SET_TYPE + " TEXT " + ")";
	
	//Exercise Table
	private static final String CREATE_TABLE_EXERCISE = "CREATE TABLE "
            + TABLE_EXERCISE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_EXTYPE + " TEXT," + KEY_EXAREA + " TEXT," + KEY_DESC
            + " TEXT, " + KEY_NO_SETS + " TEXT," + KEY_DISTANCE_GOAL + " REAL" + ")";
	
	//Day Table
	private static final String CREATE_TABLE_DAY = "CREATE TABLE "
            + TABLE_DAY + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_WEEKDAY + " TEXT" + ")";
	
	//Day_Exercise Table
	private static final String CREATE_TABLE_DAY_EXERCISE = "CREATE TABLE "
            + TABLE_DAY_EXERCISE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DAY_ID
            + " INTEGER," + KEY_EXERCISE_ID + " INTEGER" + ")";
	
	//Exercise_Set Table
	private static final String CREATE_TABLE_EXERCISE_SET = "CREATE TABLE "
            + TABLE_EXERCISE_SET + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EXERCISE_ID
            + " INTEGER," + KEY_SET_ID + " INTEGER" + ")";
	
	public DatabaseHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	private Gson gson = new Gson();

	@Override
	public void onCreate(SQLiteDatabase db) {
		//create tables
		db.execSQL(CREATE_TABLE_EXERCISE);
		db.execSQL(CREATE_TABLE_SET);
		db.execSQL(CREATE_TABLE_DAY);
		db.execSQL(CREATE_TABLE_EXERCISE_SET);
		db.execSQL(CREATE_TABLE_DAY_EXERCISE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// drop old tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SET);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY_EXERCISE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_SET);
		
		//recreate db
		onCreate(db);
	}
	
	//close db
	public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
	
	//dump all data from database and start a fresh
	public void restartDB(){
		SQLiteDatabase db = this.getWritableDatabase();
		// drop old tables
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_SET);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY_EXERCISE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_SET);
		
		//recreate db
		onCreate(db);
	}
		
	//create day in db
	public long createDay(Day day, long[] exercise_ids){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, day.getName());
		values.put(KEY_WEEKDAY, day.getWeekday().getMask());
		//insert day row into db
		long day_id = db.insert(TABLE_DAY, null, values);
		
		//add exercises for specific day
		for(long exercise_id : exercise_ids){
			createDayExercise(day_id, exercise_id);
		}
		
		return day_id;
	}
	
	//get day from db
	public Day getDay(long day_id){
		SQLiteDatabase db = this.getReadableDatabase();
		
		String selectQuery = "SELECT * FROM " + TABLE_DAY + " WHERE "
				+ KEY_ID + " = " + day_id;
		
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c != null){
			c.moveToFirst();
		}
		
		Day day = new Day();
		day.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		day.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		day.setWeekday(Integer.valueOf(c.getString(c.getColumnIndex(KEY_WEEKDAY))));
		
		c.close();
		return day;
	}
	
	//get all days from db
	public List<Day> getAllDays() {
		List<Day> days = new ArrayList<Day>();
		String selectQuery = "SELECT * FROM " + TABLE_DAY;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//iterate through days and add them to days list
		if(c.moveToFirst()){
			do {
				Day day = new Day();
				day.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				day.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				day.setWeekday(Integer.valueOf(c.getString(c.getColumnIndex(KEY_WEEKDAY))));
				
				days.add(day);
			} while (c.moveToNext());
		}
		c.close();
		return days;
	}
	
	//update a day
	public int updateDay(Day day){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, day.getName());
		values.put(KEY_WEEKDAY, day.getWeekday().getMask());
		
		return db.update(TABLE_DAY, values, KEY_ID + " = ?", new String[] { String.valueOf(day.getId()) } );
	}
	
	//delete a day
	public void deleteDay(long day_id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_DAY, KEY_ID + " = ?", new String[] { String.valueOf(day_id)});
	}
	
	//assign exercise to day - call multiple times to add more than one
	public long createDayExercise(long day_id, long exercise_id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_DAY_ID, day_id);
		values.put(KEY_EXERCISE_ID, exercise_id);
		
		long id = db.insert(TABLE_DAY_EXERCISE, null, values);
		
		return id;
	}
	
	//remove exercise from day
	public int removeExerciseFromDay(long day_id, long exercise_id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		//delete relationship
		return db.delete(TABLE_DAY_EXERCISE, KEY_EXERCISE_ID + " = ?" , new String[] { String.valueOf(exercise_id)});
	}
	
	//create Exercise
	public long createExercise(Exercise exercise, long[] set_ids){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, exercise.getName());
		values.put(KEY_EXTYPE, exercise.getExerciseType());
		values.put(KEY_EXAREA, exercise.getExerciseArea());
		values.put(KEY_DESC, exercise.getDescription());
		values.put(KEY_NO_SETS, exercise.getNoSets());
		values.put(KEY_DISTANCE_GOAL, exercise.getDistanceGoal());
		//insert day row into db
		long ex_id = db.insert(TABLE_EXERCISE, null, values);
		
		//add exercises for specific day
		for(long set_id : set_ids){
			createExerciseSet(ex_id, set_id);
		}
		
		return ex_id;
	}

	//get Exercise
	public Exercise getExercise(long ex_id){
		SQLiteDatabase db = this.getReadableDatabase();
		
		String selectQuery = "SELECT * FROM " + TABLE_EXERCISE + " WHERE "
				+ KEY_ID + " = " + ex_id;
		
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c != null){
			c.moveToFirst();
		}

		Exercise ex = new Exercise();
		ex.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		ex.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		ex.setExerciseType(c.getString(c.getColumnIndex(KEY_EXTYPE)));
		ex.setExerciseArea(c.getString(c.getColumnIndex(KEY_EXAREA)));
		ex.setDescription(c.getString(c.getColumnIndex(KEY_DESC)));
		ex.setNoSets(Integer.parseInt(c.getString(c.getColumnIndex(KEY_NO_SETS))));
		ex.setDistanceGoal(Double.parseDouble(c.getString(c.getColumnIndex(KEY_DISTANCE_GOAL))));
		c.close();
		return ex;
	}

	
	//get all exercises
	public List<Exercise> getAllExercises() {
		List<Exercise> exercises = new ArrayList<Exercise>();
		String selectQuery = "SELECT * FROM " + TABLE_EXERCISE;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//iterate through days and add them to days list
		if(c.moveToFirst()){
			do {
				Exercise ex = new Exercise();
				ex.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				ex.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				ex.setExerciseType(c.getString(c.getColumnIndex(KEY_EXTYPE)));
				ex.setExerciseArea(c.getString(c.getColumnIndex(KEY_EXAREA)));
				ex.setDescription(c.getString(c.getColumnIndex(KEY_DESC)));
				ex.setNoSets(Integer.parseInt(c.getString(c.getColumnIndex(KEY_NO_SETS))));
				ex.setDistanceGoal(Double.parseDouble(c.getString(c.getColumnIndex(KEY_DISTANCE_GOAL))));
				exercises.add(ex);
			} while (c.moveToNext());
		}
		c.close();
		return exercises;
	}

	//get all weight exercises
	public List<Exercise> getAllWeightExercises() {
		List<Exercise> exercises = new ArrayList<Exercise>();
		String selectQuery = "SELECT * FROM " + TABLE_EXERCISE + " WHERE " + KEY_EXTYPE + " = 'WEIGHT'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		//iterate through days and add them to days list
		if(c.moveToFirst()){
			do {
				Exercise ex = new Exercise();
				ex.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				ex.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				ex.setExerciseType(c.getString(c.getColumnIndex(KEY_EXTYPE)));
				ex.setExerciseArea(c.getString(c.getColumnIndex(KEY_EXAREA)));
				ex.setDescription(c.getString(c.getColumnIndex(KEY_DESC)));
				ex.setNoSets(Integer.parseInt(c.getString(c.getColumnIndex(KEY_NO_SETS))));
				ex.setDistanceGoal(Double.parseDouble(c.getString(c.getColumnIndex(KEY_DISTANCE_GOAL))));
				exercises.add(ex);
			} while (c.moveToNext());
		}
		c.close();
		return exercises;
	}

	public List<Exercise> getAllCardioExercises() {
		List<Exercise> exercises = new ArrayList<Exercise>();
		String selectQuery = "SELECT * FROM " + TABLE_EXERCISE + " WHERE " + KEY_EXTYPE + " = 'CARDIO'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		//iterate through days and add them to days list
		if(c.moveToFirst()){
			do {
				Exercise ex = new Exercise();
				ex.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				ex.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				ex.setExerciseType(c.getString(c.getColumnIndex(KEY_EXTYPE)));
				ex.setExerciseArea(c.getString(c.getColumnIndex(KEY_EXAREA)));
				ex.setDescription(c.getString(c.getColumnIndex(KEY_DESC)));
				ex.setNoSets(Integer.parseInt(c.getString(c.getColumnIndex(KEY_NO_SETS))));
				ex.setDistanceGoal(Double.parseDouble(c.getString(c.getColumnIndex(KEY_DISTANCE_GOAL))));
				exercises.add(ex);
			} while (c.moveToNext());
		}
		c.close();
		return exercises;
	}
	
	//get exercise by day
	public List<Exercise> getExercisesByDay(long day_id){
		List<Exercise> exercises = new ArrayList<Exercise>();
		
		String selectQuery = "SELECT * FROM " + TABLE_DAY_EXERCISE + " WHERE " + KEY_DAY_ID + " = '" + day_id
				+"'";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if(c.moveToFirst()){
			do {
				Exercise ex = getExercise(c.getInt(c.getColumnIndex(KEY_EXERCISE_ID)));
				
				exercises.add(ex);
			} while (c.moveToNext());
		}
		c.close();
		return exercises;
	}
	
	//update exercise
	public int updateExercise(Exercise ex){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, ex.getName());
		values.put(KEY_EXTYPE, ex.getExerciseType());
		values.put(KEY_EXAREA, ex.getExerciseArea());
		values.put(KEY_DESC, ex.getDescription());
		values.put(KEY_NO_SETS, ex.getNoSets());
		values.put(KEY_DISTANCE_GOAL, ex.getDistanceGoal());
		
		return db.update(TABLE_EXERCISE, values, KEY_ID + " = ?", new String[] { String.valueOf(ex.getId()) } );
	}
	
	//delete exercise
	public void deleteExercise(long ex_id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_EXERCISE, KEY_ID + " = ?", new String[] { String.valueOf(ex_id)});
	}
	
	//assign set to exercise - call multiple times to add more than one
	public long createExerciseSet(long ex_id, long set_id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_EXERCISE_ID, ex_id);
		values.put(KEY_SET_ID, set_id);
		
		long id = db.insert(TABLE_EXERCISE_SET, null, values);
		
		return id;
	}
	
	//remove set from exercise
	public int removeSetFromExercise(long id, long set_id){
		SQLiteDatabase db = this.getWritableDatabase();
		
		
		//update the row
		return db.delete(TABLE_EXERCISE_SET, KEY_SET_ID + " = ?", new String[] { String.valueOf(id) });
	}

	//create Set
	public long createSet(Set set){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_CREATE_TIME, set.getCreate_time());
		values.put(KEY_WEIGHT, set.getWeight());
		values.put(KEY_REPS, set.getNoReps());
		values.put(KEY_SET_TYPE, set.getType());
		values.put(KEY_DISTANCE, set.getDistance());
		values.put(KEY_TIMESPENT, set.getTimespent());
		//insert day row into db
		long set_id = db.insert(TABLE_SET, null, values);
		
		return set_id;
	}

	//get Set
	public Set getSet(long set_id){
		SQLiteDatabase db = this.getReadableDatabase();
		
		String selectQuery = "SELECT * FROM " + TABLE_SET + " WHERE "
				+ KEY_ID + " = " + set_id;
		
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c != null){
			c.moveToFirst();
		}
		
		Set set = new Set();
		set.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		set.setCreate_time(c.getString(c.getColumnIndex(KEY_CREATE_TIME)));
		set.setWeight(Double.parseDouble(c.getString(c.getColumnIndex(KEY_WEIGHT))));
		set.setNoReps(Integer.parseInt(c.getString(c.getColumnIndex(KEY_REPS))));
		set.setType(c.getString(c.getColumnIndex(KEY_SET_TYPE)));
		set.setDistance(Double.parseDouble(c.getString(c.getColumnIndex(KEY_DISTANCE))));
		set.setTimespent(c.getString(c.getColumnIndex(KEY_TIMESPENT)));
		c.close();
		return set;
	}
	
	//get all Sets
	public List<Set> getAllSets() {
		List<Set> sets = new ArrayList<Set>();
		String selectQuery = "SELECT * FROM " + TABLE_SET;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//iterate through sets and add them to set list
		if(c.moveToFirst()){
			do {
				Set set = new Set();
				set.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				set.setCreate_time(c.getString(c.getColumnIndex(KEY_CREATE_TIME)));
				set.setWeight(Double.parseDouble(c.getString(c.getColumnIndex(KEY_WEIGHT))));
				set.setNoReps(Integer.parseInt(c.getString(c.getColumnIndex(KEY_REPS))));
				set.setType(c.getString(c.getColumnIndex(KEY_SET_TYPE)));
				set.setDistance(Double.parseDouble(c.getString(c.getColumnIndex(KEY_DISTANCE))));
				set.setTimespent(c.getString(c.getColumnIndex(KEY_TIMESPENT)));
				
				sets.add(set);
			} while (c.moveToNext());
		}
		c.close();
		return sets;
	}
	
	//get sets by exercise id
	public List<Set> getSetsByExercise(long exercise_id){
		List<Set> sets = new ArrayList<Set>();
		
		String selectQuery = "SELECT * FROM " + TABLE_EXERCISE_SET + " WHERE " + KEY_EXERCISE_ID + " = '" + exercise_id
				+"'";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if(c.moveToFirst()){
			do {
				Set s = getSet(c.getInt(c.getColumnIndex(KEY_SET_ID)));
				
				sets.add(s);
			} while (c.moveToNext());
		}
		c.close();
		return sets;
	}
	
	
	
	//update set
	public int updateSet(Set set){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_CREATE_TIME, set.getCreate_time());
		values.put(KEY_WEIGHT, set.getWeight());
		values.put(KEY_REPS, set.getNoReps());
		values.put(KEY_SET_TYPE, set.getType());
		values.put(KEY_DISTANCE, set.getDistance());
		values.put(KEY_TIMESPENT, set.getTimespent());
		
		return db.update(TABLE_SET, values, KEY_ID + " = ?", new String[] { String.valueOf(set.getId()) } );
	}
	
	//delete set
	public void deleteSet(long set_id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SET, KEY_ID + " = ?", new String[] { String.valueOf(set_id)});
	}
	

}
