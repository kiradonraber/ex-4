package il.ac.shenkar.mytasks;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raberkira on 11/28/13.
 */
public class TaskListModel {

    private static TaskListModel instance = null;
    private List<TaskDetails> taskList;
    private Context context;

    //private creator
    private TaskListModel(Context context){
        this.context = context;
        taskList = new ArrayList<TaskDetails>();
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
        taskList.add(newTask);
    }

    //delete specific task
    public void deleteTask(TaskDetails taskToRemove){
        taskList.remove(taskToRemove);
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
