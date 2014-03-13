package il.ac.shenkar.mytasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raberkira on 11/28/13.
 */
public class TaskListModel {

    private static TaskListModel instance = null;
    private List<TaskDetails> taskList;
    private Context context;

    private SQLiteDatabase db;
    private DatabaseHandler dbHandler;

    //private creator
    private TaskListModel(Context context){
        this.context = context;
        dbHandler = new DatabaseHandler(context);
        taskList = new ArrayList<TaskDetails>();
        taskList = getAllTasks();
    }

    //public access
    public synchronized static TaskListModel getInstance(Context context){
        if (instance==null){
            instance = new TaskListModel(context);
        }
        return instance;
    }

    //add new task
    public void addTask(TaskDetails newTask){
        //add new task to the list
        taskList.add(newTask);

        db = dbHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(dbHandler.KEY_NAME, newTask.getName());
        values.put(dbHandler.KEY_DESCRIPTION, newTask.getDescription());
        values.put(dbHandler.KEY_DATEANDTIME, newTask.getDate());
        values.put (dbHandler.KEY_REGULARNOTIFICATION, newTask.getRegularNotification());
        values.put (dbHandler.KEY_LOCATION, newTask.getLocation());
        db.insert(dbHandler.TABLE_TASKS, null, values);
        db.close();
    }

    //delete specific task
    public void deleteTask(TaskDetails taskToRemove){
        taskList.remove(taskToRemove);

        db = dbHandler.getWritableDatabase();
        db.delete(dbHandler.TABLE_TASKS, dbHandler.KEY_ID + " = ?", new String[] { String.valueOf(taskToRemove.getId()) });
        db.close();
    }

    //delete task at specific position
    public void deleteTask(int position){
        TaskDetails taskToRemove = taskList.get(position);
        deleteTask(taskToRemove);
    }

    // update specific task
    public void updateTask(TaskDetails updatedTask, int position){
        taskList.set (position, updatedTask);

        db = dbHandler.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(dbHandler.KEY_NAME, updatedTask.getName());
        data.put(dbHandler.KEY_DESCRIPTION, updatedTask.getDescription());
        data.put(dbHandler.KEY_DATEANDTIME, updatedTask.getDate());
        data.put(dbHandler.KEY_REGULARNOTIFICATION, updatedTask.getRegularNotification());
        data.put(dbHandler.KEY_LOCATION, updatedTask.getLocation());
        db.update(dbHandler.TABLE_TASKS, data, dbHandler.KEY_ID + "=" + updatedTask.getId(), null);
        db.close();
    }

    // Getting All Contacts
    public ArrayList<TaskDetails> getAllTasks() {

        String selectQuery = "SELECT  * FROM " + dbHandler.TABLE_TASKS;
        db = dbHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TaskDetails Task = new TaskDetails();
                Task.setId(Integer.parseInt(cursor.getString(0)));
                Task.setName(cursor.getString(1));
                Task.setDescription(cursor.getString(2));
                Task.setDate(cursor.getString(3));
                Task.setRegularNotification(cursor.getString(4));
                Task.setLocation(cursor.getString(5));
                taskList.add(Task);
                } while (cursor.moveToNext());
        }
        db.close();
        return (ArrayList<TaskDetails>)taskList;
    }

    //get list size
    public int getListSize(){
        return taskList.size();
    }

    //get specific task by position
    public TaskDetails getSpecificTask (int position){
        return taskList.get(position);
    }
}
