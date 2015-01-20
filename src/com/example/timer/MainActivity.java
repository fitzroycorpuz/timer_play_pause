package com.example.timer;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity {

	private TextView timerView;				// text on screen containing the time
	private TextView lapView;				// text on screen containing the lap times
	private long accumTime = 0;				// elapsed time in ms since start or reset pressed minus time the time stopped
	private long startTime = 0;				// time of day in ms that start of reset pressed
	private long lapStartTime = 0;			// time of day in ms that start, reset, or lap pressed
	private Handler timerHandler;			// handler called periodically by system timer
	private Laps laps;						// ArrayList containing the lap information
	final private long timerInterval = 100;	// time in ms between executions of the timer handler
	private boolean timerRunning = false;	// true if timer is running
	ToggleButton tglPlayPause;				//toggle play pause button
	
	
 /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) 
 {
     super.onCreate(savedInstanceState);
     
     // Set view to that defined in XML file in layout/main.xml
     setContentView(R.layout.activity_main);
     
     // Locate the view to the elapsed time on screen and initialize
     timerView = (TextView) findViewById(R.id.time);
     timerView.setText(formatTime(0));
     
     // Initialize an ArrayList to hold the laps
     laps = new Laps();

     // Locate the view to the laps on screen and initialize
     lapView = (TextView) findViewById(R.id.laps);
     lapView.setText(laps.toString());
     
     // Initialize a Handler for the System to periodically call
     timerHandler = new Handler();
     
     
 	tglPlayPause = (ToggleButton) findViewById(R.id.tglPlayPause);
	tglPlayPause.setOnCheckedChangeListener(new OnCheckedChangeListener(){
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			long now = System.currentTimeMillis();
			
			if (tglPlayPause !=null){
				if (isChecked)
					
					startButtonClick(now);	
					
				else
	
				stopButtonClick(now);
			}
		}
		
	});
     
     // Bind the method to be called when the start button pressed
     View startButton = findViewById(R.id.start);
     startButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
		        // get time button pressed
				long now = System.currentTimeMillis();
				startButtonClick(now);		
			}
		});

     // Bind the method to be called when the stop button pressed        
     View stopButton = findViewById(R.id.stop);
     stopButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
		        // get time button pressed
				long now = System.currentTimeMillis();
				stopButtonClick(now);		
			}
		});
     
     // Bind the method to be called when the reset button pressed        
     View resetButton = findViewById(R.id.reset);
     resetButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
		        // get time button pressed
				long now = System.currentTimeMillis();
				resetButtonClick(now);		
			}
		});
     
     // Bind the method to be called when the lap button pressed        
     View lapButton = findViewById(R.id.lap);
     lapButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
		        // get time button pressed
				long now = System.currentTimeMillis();
				lapButtonClick(now);		
			}
		});
     
     // Bind the method to be called when the send email button pressed        
     View sendButton = findViewById(R.id.send);
     sendButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				sendButtonClick();		
			}
		});
     
     // Bind the method to be called when the send email button pressed        
     View smsButton = findViewById(R.id.sms);
     smsButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				smsButtonClick();		
			}
		});         
     
 }
 
 // Method called when the start buttons pressed
 private void startButtonClick(long now)
 {       
 	// Remove any timers in progress
     timerHandler.removeCallbacks(updateTimerTask);
     
     // Update the TOD the handler started and the TOD for the first lap started
     startTime = now;
     lapStartTime = now;
     
     // Note the accumTime (elapsed time) not reset since maybe restarting without reseting
     
     // Start timer
     timerRunning = true;
     timerHandler.postDelayed(updateTimerTask, timerInterval);
 }

 // Method called when the stop button pressed
 private void stopButtonClick(long now)
 {		
 	// ignore if stop button pressed when timer already stopped
 	if (timerRunning == false)
 	{
 		return;
 	}             

 	// Stop the timer
     timerHandler.removeCallbacks(updateTimerTask);       

     // Call the lap button function to close out the last lap
     lapButtonClick(now);
 	
		// calculate the elapsed time since timer started and add to accumulated timer time
     // note that if the timer pops after we have started this method then startTime will 
     // 		 be greater than now resulting in any additional time being subtracted off
		accumTime = accumTime + (now - startTime); 
		// updated the display of the accumulated time
		timerView.setText(formatTime(accumTime));   
		        
     // indicate timer stopped
     timerRunning = false;
 }

 // Method called when the reset button pressed    
 private void resetButtonClick(long now)
 {
 	// Set the elapsed time to 0
 	accumTime = 0;   	

 	// remove all laps
 	laps.clear();
 	lapView.setText(laps.toString());
 	
 	// Initialize the elapsed time
 	timerView.setText(formatTime(0));
 	
     // Update the TOD the handler started and the TOD the first lap started
 	// Note that this is only necessary if reset pressed while timer running
 	if (timerRunning)
 	{
         startTime = now;
         lapStartTime = now;
 	}
 }
 
 // Method called when the lap or stop buttons pressed
 private void lapButtonClick(long now)
 {    	
 	// ignore if lap button pressed when timer stopped
 	if (timerRunning == false)
 	{
 		return;
 	}
 	
 	// determine elapsed time since lap started
		long lapTime = now - lapStartTime; 
		
		// set start time of next lap
		lapStartTime = now;
		
		// add this lap to ArrayList of laps
 	laps.add(lapTime);
 	
 	// update display of laps
 	lapView.setText(laps.toString());   	
 }
 
 // method called when the send email button is clicked
 private void sendButtonClick()
 {
 	Intent intent = new Intent(Intent.ACTION_SEND);
 	intent.setType("message/rfc822");
 	intent.putExtra(Intent.EXTRA_SUBJECT, "Stop Watch Laps");
 	intent.putExtra(Intent.EXTRA_TEXT, laps.toString());
 	
 	try {
 		startActivity(Intent.createChooser(intent, "Send mail..."));
 	}
 	catch (android.content.ActivityNotFoundException ex)
 	{
 		Toast.makeText(MainActivity.this, "There are no email clients", Toast.LENGTH_SHORT).show();
 	}
 }
 
 // method called when the send sms button is clicked
 private void smsButtonClick()
 {
 	Intent intent = new Intent(Intent.ACTION_VIEW);
 	intent.setType("vnd.android-dir/mms-sms");
 	intent.putExtra("sms_body", laps.toString());
 	
 	try {
 		startActivity(Intent.createChooser(intent, "Send SMS..."));
 	}
 	catch (android.content.ActivityNotFoundException ex)
 	{
 		Toast.makeText(MainActivity.this, "There are no SMS clients", Toast.LENGTH_SHORT).show();
 	}
 }
 
 // task executed after timer expired
 private Runnable updateTimerTask = new Runnable() 
 {	
		public void run() 
		{
			// calculate the elapsed time since timer started and add to accumulated timer time
			long now = System.currentTimeMillis();
			accumTime = accumTime + (now - startTime);  
			
			// set the time the timer restarted
			startTime = now;
			        
			// updated the display of the accumulated time that the timer has been running
			timerView.setText(formatTime(accumTime));                   
			
			// restart the timer
			timerHandler.postDelayed(updateTimerTask, timerInterval);
			
		}  	
	
 };

	// convert the time in milliseconds to a string of hours, minutes, seconds, and hundreds (thousands rounded up or down)
	private String formatTime(long timeMs)
	{
		String retValue;
		
		// add 5 ms to round thousands up or down (i.e. 5ms -> 0.01)
		timeMs = timeMs + 5;
		
		// seconds equals milliseconds / 1000
		int seconds = (int) (timeMs / 1000); 	
		// minutes equals second / 60
		int minutes = seconds / 60;  		
		// hours equal minutes / 60
		int hours = minutes / 60;
		// find the left over minutes
		minutes = minutes % 60;
		// find the left over seconds
		seconds = seconds % 60;
		// calculate the hundreds rounding the thousands up or down after previously adding 5
		int hundreds = (int)(timeMs % 1000) / 10;
		
		// create the string of hours, minutes, seconds, and hundreds
		retValue = String.format("%d:%02d:%02d.%02d", hours, minutes, seconds, hundreds);       
		
		return retValue;
	}
 
 // private class extending ArrayList to hold laps
 private class Laps extends ArrayList<Long>
 {   	
		private static final long serialVersionUID = 1L;

		// number of laps to display
		private static final int numberToDisplay = 30;
		
		// Method to format the laps for display or sending    	
 	public String toString()
 	{
 		long totalTime = 0;
 		
     	// update display of laps
     	String lapsString = "LAPS:";
     	
     	// display only the last laps
     	int startLap = 0;
     	int size = this.size();
     	if (size > numberToDisplay)
     	{
     		startLap = size - numberToDisplay;
     	}
     	
     	for (int i = 0; i < size; i++)
     	{

     		long lapTime = this.get(i);
     		totalTime = totalTime + lapTime;        		
     		
     		// only print last numberToDisplay laps
     		if (i >= startLap) 
     		{
     			// two laps per line
     			if ((i%2) == (startLap%2))
     			{
     				lapsString = lapsString + "\n";
     			}
     			// display lap number and time

     			lapsString = lapsString + "\t\t"+ String.format("%02d", i+1) + "\t" + formatTime(lapTime);
     		}

     	}
     	
     	// add the total time to the end of the laps
     	lapsString = lapsString + "\n\n" + "Total Lap Times: " + formatTime(totalTime);
     	
     	// return the string with the last laps
     	return lapsString;
 	}
 }
 
}