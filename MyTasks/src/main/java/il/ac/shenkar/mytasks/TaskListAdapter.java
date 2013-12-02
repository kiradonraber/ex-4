package il.ac.shenkar.mytasks;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by raberkira on 11/29/13.
 */
public class TaskListAdapter extends BaseAdapter {
    private String TAG = "RTT_TaskListAdapter";

    private Context context;
    private LayoutInflater l_Inflater;
    private TaskListModel taskList;
    private static ArrayList<TaskDetails> taskDetailsArrayList;

    public TaskListAdapter(android.content.Context context, TaskListModel tasks) {
        this.context = context;
        taskList=tasks;
        taskDetailsArrayList = tasks.getAllTasks();
        this.l_Inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return taskList.getListSize();
    }

    @Override
    public TaskDetails getItem(int position) {
      return taskList.getSpecificTask(position);
    }

   @Override
    public long getItemId(int position) {
       return getItem(position).getId();
    }

    private final View.OnClickListener doneButtonOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            int position = (Integer) view.getTag();
            Log.d(TAG, getItem(position).getName()  + " HAS BEEN REMOVED ");
            taskList.deleteTask(getItem(position));
            notifyDataSetChanged();
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.task_details_list, null);
            holder = new TaskHolder();
            holder.taskName = (TextView) convertView.findViewById(R.id.task_name);
            holder.taskDescription = (TextView) convertView.findViewById(R.id.task_description);
            holder.taskDateAndTime = (TextView) convertView.findViewById(R.id.task_date_time);
            holder.doneButton = (Button) convertView.findViewById(R.id.done_button);
            holder.doneButton.setOnClickListener(doneButtonOnClickListener);
            convertView.setTag(holder);
        } else {
            holder = (TaskHolder) convertView.getTag();
        }

        holder.taskName.setText(getItem(position).getName());
        holder.taskDescription.setText(getItem(position).getDescription());
        holder.taskDateAndTime.setText(getItem(position).getDate());
        holder.doneButton.setTag(position);

        return convertView;
    }

    static class TaskHolder {
        TextView taskName;
        TextView taskDescription;
        TextView taskDateAndTime;
        Button doneButton;
    }
}
