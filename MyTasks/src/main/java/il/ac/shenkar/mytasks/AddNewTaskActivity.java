package il.ac.shenkar.mytasks;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by raberkira on 11/30/13.
 */
public class AddNewTaskActivity extends Activity {

    public final static String EXTRA_LIST = "il.ac.shenkar.mytasks.LIST";

    private static final int LOCATION_ACTIVITY_REQUEST = 1;

    private TaskListModel taskList;
    View vView;
    Context context;
    public  int year,month,day,hour,minute;
    Calendar c;
    EditText taskNameField, taskDescriptionField;
    int taskPosition;
    long taskId;
    String taskWantedLocation = null;
    String tasklocation = null;
    Button locationButton;

    // variables for time and date picker
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID=1;

    // if the user wants remainder it will be true
    boolean remind_me =false;

    //if the task is already been created and needed to be edited it will be true
    boolean existedTask = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_task);

        //register parse models
        //ParseObject.registerSubclass(TaskCloud.class);
        //initialize parse account for managing task in cloud
        //Parse.initialize(this, "AtzJwc1aRas7hTP28zLUXjWzkZzn4ZZgKo7tS1Rq", "gsroJoc8emxMmGbt7vDEUjkhFzannW18ZOcbi4ey");


        getActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        //initialize the calendar variable to current date and time
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        // set name and description text fields
        taskNameField = (EditText) findViewById(R.id.new_task_name);
        taskDescriptionField = (EditText) findViewById(R.id.new_task_description);

        //set reminder buttons listeners
        Button timeReminderButton = (Button) findViewById(R.id.add_time_notification);
        timeReminderButton.setOnClickListener(TimeReminderButtonListener);
        Button dateReminderButton = (Button) findViewById(R.id.add_date_notification);
        dateReminderButton.setOnClickListener(DateReminderButtonListener);

        //set location button listener
        locationButton = (Button) findViewById(R.id.add_location);
        locationButton.setOnClickListener(LocationButtonListener);

        // check if task data has been forward to the activity
        Intent receiveDataIntent = getIntent();
        Bundle receivedDataBundle = receiveDataIntent.getExtras();
        if (receivedDataBundle != null){
            existedTask = true;
            setTaskDetails(receivedDataBundle);
        }
    }

    // set existing task details
    private void setTaskDetails(Bundle DataBundle){
        String receivedName = DataBundle.getString("name");
        String receivedDescription = DataBundle.getString("description");
        if (DataBundle.getString("location")!= null && DataBundle.getString("location")!="" && DataBundle.getString("location")!=" "){
            tasklocation = DataBundle.getString("location");
        }
        else{
            tasklocation = null;
        }
        taskPosition = DataBundle.getInt("position");
        taskId = DataBundle.getLong("id");
        taskNameField.setText(receivedName);
        taskDescriptionField.setText(receivedDescription);
        locationButton = (Button) findViewById(R.id.add_location);
        if ( tasklocation==null){
            locationButton.setText("Set Location");
        }
        else{
            locationButton.setText("Location Selected: " + tasklocation);
        }
    }

    // create new task / update existing task
    public void createOrUpdateTask(View view) {
        TextView taskNameTextView = (TextView) findViewById(R.id.new_task_name);
        String taskName = taskNameTextView.getText().toString();
        TextView taskDescriptionTextView = (TextView) findViewById(R.id.new_task_description);
        String taskDescription = taskDescriptionTextView.getText().toString();

        c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String taskDateAndTime = df.format(c.getTime());

        taskList = TaskListModel.getInstance(this);

        TaskDetails newOrUpdatedTask;

        if (existedTask){
            newOrUpdatedTask = new TaskDetails (taskName, taskDescription, taskDateAndTime);
            newOrUpdatedTask.setId(taskId);
        } else {
            newOrUpdatedTask = new TaskDetails(taskName, taskDescription, taskDateAndTime);
        }
        newOrUpdatedTask.setLocation(tasklocation);

        if(remind_me){
            c = Calendar.getInstance();
            c.set(year, month, day, hour, minute);
            c.set(Calendar.SECOND, 00);

            List<String> reminderList = new ArrayList<String>();
            reminderList.add(taskName);

            Intent intent = new Intent("com.rtt.reminder_broadcast");
            intent.putStringArrayListExtra(EXTRA_LIST, (ArrayList<String>) reminderList);
            PendingIntent pendingIntent =   PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

            String stringNotification = df.format(c.getTime());
            newOrUpdatedTask.setRegularNotification(stringNotification);
        }

        if (taskWantedLocation!="" && taskWantedLocation!=" " && taskWantedLocation!=null){
            newOrUpdatedTask.setLocation(taskWantedLocation);
        }

        if (existedTask){
            taskList.updateTask(newOrUpdatedTask,taskPosition);
            //updateTaskInCloud(newOrUpdatedTask);
            Toast.makeText(this, "The Sticky has been updated", Toast.LENGTH_LONG).show();
        } else {
            taskList.addTask(newOrUpdatedTask);
            //pushTaskToCloud(newOrUpdatedTask);
            Toast.makeText(this, "New Sticky has been added", Toast.LENGTH_LONG).show();
        }

        addEventAnalyticsTracker();
        finish();
    }

    // push task to parse cloud
    private void pushTaskToCloud (TaskDetails newTask){
        TaskCloud taskObject = new TaskCloud(newTask);
        taskObject.saveEventually();
    }

    // update task in parse cloud
    private void updateTaskInCloud (final TaskDetails Task){
        // Define the class we would like to query
        ParseQuery<TaskCloud> query = ParseQuery.getQuery(TaskCloud.class);
        // Define our query conditions
        query.whereEqualTo("id", Task.getId());
        // Execute the find asynchronously
        query.findInBackground(new FindCallback<TaskCloud>() {
            @Override
            public void done(List<TaskCloud> taskList, com.parse.ParseException e) {
                if (e == null) {

                    // Access result
                    TaskCloud taskToUpdate = taskList.get(0);
                    taskToUpdate.updateTask(Task);
                }
            }
        });
    }

    // location button
    private final View.OnClickListener LocationButtonListener;
    {
        LocationButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Bundle taskLocationBundle = new Bundle();
                taskLocationBundle.putString("location", tasklocation);
                Intent passDataIntent=(new Intent(AddNewTaskActivity.this, MapActivity.class));
                passDataIntent.putExtras(taskLocationBundle);
                startActivityForResult(passDataIntent, LOCATION_ACTIVITY_REQUEST);
            }
        };
    }

    // time reminder button
    private final View.OnClickListener TimeReminderButtonListener;
    {
        TimeReminderButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showDialog(TIME_DIALOG_ID);
            }
        };
    }

    // date reminder button
    private final View.OnClickListener DateReminderButtonListener;
    {
        DateReminderButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view){
                showDialog(DATE_DIALOG_ID);
            }
        };
    }

    // Register  DatePickerDialog listener
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        // the callback received when the user "sets" the Date in the DatePickerDialog
        public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
            year = yearSelected;
            month = monthOfYear;
            day = dayOfMonth;
            remind_me =true;
            // Set the Selected Date in Select date Button
            Button dateReminderButton = (Button) findViewById(R.id.add_date_notification);
            dateReminderButton.setText("Date selected: "+day+"."+month+"."+year);
        }
    };

    // Register  TimePickerDialog listener
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        // the callback received when the user "sets" the TimePickerDialog in the dialog
        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            hour = hourOfDay;
            minute = min;
            remind_me =true;
            // Set the Selected Date in Select date Button
            Button timeReminderButton = (Button) findViewById(R.id.add_time_notification);
            timeReminderButton.setText("Time selected: "+hour+":"+minute);
        }
    };


    // Method automatically gets Called when you call showDialog()  method
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // create a new DatePickerDialog
                DatePickerDialog datePicker = new DatePickerDialog(this, mDateSetListener,year, month, day);
                datePicker.setTitle("Pick A Date");
                return datePicker;
            // create a new TimePickerDialog
            case TIME_DIALOG_ID:
                TimePickerDialog timePicker = new TimePickerDialog(this,mTimeSetListener, hour, minute, false);
                timePicker.setTitle("Pick A Time");
                return timePicker;
        }
        return null;
    }

    // handle receiving data from map activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && requestCode == LOCATION_ACTIVITY_REQUEST ){
                    Bundle bundle = data.getExtras();
                    taskWantedLocation = bundle.getString("location");
                    //taskWantedLocation = tasklocation;
                    Button locationButton = (Button) findViewById(R.id.add_location);
                    if (taskWantedLocation == "" || taskWantedLocation == "" || taskWantedLocation == null){
                        locationButton.setText("Set Location");
                        taskWantedLocation = null;
                    }
                else{
                        locationButton.setText("Location Selected: " + taskWantedLocation);
                    }
            }
    }

    // google analytics functions
    public void addEventAnalyticsTracker(){
        //track add event to google analytics
        EasyTracker easyTracker = EasyTracker.getInstance(context);
        easyTracker.send(MapBuilder
                .createEvent("ui_action",     // Event category (required)
                        "button_press",  // Event action (required)
                        "add/edit task",   // Event label
                        null)            // Event value
                .build()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    // action bar menu functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_task_avtion_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save:
                createOrUpdateTask(vView);
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }
}