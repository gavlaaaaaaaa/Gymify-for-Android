Gymify-for-Android
==================
Android App to keep track of gym routine.

First page - List view:
  Add a 'Day' - A day is used to seperate exercises throughout the week. 
    So Monday could be Chest and Triceps day, Tuesday Legs and Abs etc etc...
    
Clicking a day takes you to the second page 'Exercise' - this page allows exercises to be added for the day.

By Clicking an exercise the 'Set' page is displayed, this page does the following:
  - Records the weight and amount of reps for this exercise for each set; Clicking the tick begins the rest period.
  - The rest period is a count down set at 60 seconds. A notification sound and vibration sets off when the count down 
    has finished allowing the user to put there phone back in there pocket and prepare for the next set and be notified when
    their rest period is over.
  - After the specified number of sets has been completed - and the countdown timer finishes - this view will automatically
    move onto the next exercise in the list and start from set 1.
  - previous set data such as weight and rep numbers are automatically filled in based on previous entries (first time 
    set to 0 obviously)
  - On the very last set of the last exercise, when the tick button is pressed the user is asked if they wish to end the
    workout, if so they are shown the report view.
    
[Experimental] The report view (accessible from the first page by pressing the 'Info' button) shows a graph:
  - Currently the graph displays the weight data for each exercise from the last 5 sets.
  - In the future this will show more meaningful data based on each exercise muscle group and weight lifted over time.
