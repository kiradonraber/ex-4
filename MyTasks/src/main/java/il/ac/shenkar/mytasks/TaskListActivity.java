package il.ac.shenkar.mytasks;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * Created by raberkira on 11/28/13.
 */
public class TaskListActivity extends Activity {

    Context context;
    TaskListAdapter taskAdapter = null;
    private ActionMode mActionMode;
    TaskListModel taskListModel;
    int selectedItemPosition;
    Bundle taskDetailsBundle;
    Intent passDataIntent;
    private ShareActionProvider mShareActionProvider;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //register parse models
        //ParseObject.registerSubclass(TaskCloud.class);
        //initialize parse account for managing task in cloud
        //Parse.initialize(this, "AtzJwc1aRas7hTP28zLUXjWzkZzn4ZZgKo7tS1Rq", "gsroJoc8emxMmGbt7vDEUjkhFzannW18ZOcbi4ey");

        context = this;

        taskListModel = TaskListModel.getInstance(this);

        //set task list
        ListView taskList = (ListView) findViewById(android.R.id.list);

        //set task list adapter
        taskAdapter = new TaskListAdapter(this);
        taskList.setAdapter(taskAdapter);

        //set clickable empty list
        taskList.setEmptyView(findViewById(R.id.empty));
        LinearLayout layout = (LinearLayout) findViewById(R.id.empty);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddNewTaskActivity.class));
           }
         });

        // set swipe task to dismiss option
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener
                       (taskList, new SwipeDismissListViewTouchListener.OnDismissCallback() {
                                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                        for (int position : reverseSortedPositions)
                                        {
                                            Toast.makeText(getBaseContext(), "Sticky has been deleted", Toast.LENGTH_SHORT).show();
                                            // delete task from cloud
                                            //deleteTaskFromCloud(taskListModel.getSpecificTask(position));
                                            // delete task from task list
                                            taskListModel.deleteTask(position);
                                            taskAdapter.notifyDataSetChanged();
                                       }
                                    }
                        });

        // set task clicks - short and long
        taskList.setOnTouchListener(touchListener);
        taskList.setOnScrollListener(touchListener.makeScrollListener());

         // React to user short clicks on task - list item
         taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                 selectedItemPosition = position;
                 editTask(position);
            }
         });

        // React to user long clicks on task - list item - open action mode menu
        taskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onItemLongClick(AdapterView<?> p, View view, int pos, long id) {
                    selectedItemPosition = pos;
                    if (mActionMode != null) {
                        return false;
                    }
                    // Start the CAB using the ActionMode.Callback defined above
                    mActionMode = startActionMode( mActionModeCallback );
                    view.setSelected(true);
                    return true;
            }
        });
    }

    public void deleteTaskFromCloud (TaskDetails Task){
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
                    taskToUpdate.deleteEventually();
                }
            }
        });
    }

    // create bundle and start edit task activity
    public void editTask (int position){
        //create the bundle in order to pass the wanted information to the edit activity
        taskDetailsBundle = new Bundle();
        taskDetailsBundle.putString("name",taskListModel.getSpecificTask(position).getName());
        taskDetailsBundle.putString("description",taskListModel.getSpecificTask(position).getDescription());
        taskDetailsBundle.putString("location", taskListModel.getSpecificTask(position).getLocation());
        taskDetailsBundle.putInt("position",position);
        taskDetailsBundle.putLong("id",taskListModel.getSpecificTask(position).getId());
        passDataIntent=new Intent(getApplicationContext(),AddNewTaskActivity.class);
        passDataIntent.putExtras(taskDetailsBundle);
        //start edit selected list item activity
        startActivity(passDataIntent);
    }

    //action mode menu functions
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.selected_item_menu, menu);
            return true;
        }

        // Called each time the action mode is shown
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_share:
                    //Getting the action provider associated with the menu item whose id is share
                    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
                    //Setting a share intent
                    mShareActionProvider.setShareIntent(createShareIntent());
                    //track share event to google analytics
                    shareEventAnalyticsTracker();
                    return true;

                case R.id.action_delete:
                    // delete task from cloud
                    //deleteTaskFromCloud(taskListModel.getSpecificTask(selectedItemPosition));
                    //delete the list item in the selected position
                    taskListModel.deleteTask(selectedItemPosition);
                    Toast.makeText(getBaseContext(), "Sticky has been deleted", Toast.LENGTH_SHORT).show();
                    taskAdapter.notifyDataSetChanged();
                    mode.finish(); // Action picked, so close the CAB
                    return true;

                case R.id.action_edit: //edit the list item in the selected position
                    //create the bundle in order to pass the wanted information to the edit activity
                    editTask(selectedItemPosition);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Returns share intent
        private Intent createShareIntent(){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sticky Share");
            intent.putExtra(Intent.EXTRA_TEXT, "\n"
                    + taskListModel.getSpecificTask(selectedItemPosition).getName()
                    + "\n"
                    + taskListModel.getSpecificTask(selectedItemPosition).getDescription());
            return intent;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    public void shareEventAnalyticsTracker(){
        //track add event to google analytics
        EasyTracker easyTracker = EasyTracker.getInstance(context);
        easyTracker.send(MapBuilder
                .createEvent("ui_action",     // Event category (required)
                        "button_press",  // Event action (required)
                        "share",   // Event label
                        null)            // Event value
                .build()
        );
    }

    // action bar functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new:
                startActivity(new Intent(context, AddNewTaskActivity.class));
                break;
        }
        return true;
    }

    //on resume activity function
    @Override
    public void onResume(){
        super.onResume();
        taskAdapter.notifyDataSetChanged();
    }

    // google analytics functions - onStart & onStop
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

}