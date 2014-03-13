package il.ac.shenkar.mytasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * Created by raberkira on 11/29/13.
 */
public class TaskListAdapter extends BaseAdapter {
    private String TAG = "RTT_TaskListAdapter";

    private Context context;
    private LayoutInflater l_Inflater;
    private TaskListModel taskList;

    public TaskListAdapter(android.content.Context context) {
        this.context = context;
        this.taskList = TaskListModel.getInstance(context);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.task_details_list, null);
            holder = new TaskHolder();
            holder.taskName = (TextView) convertView.findViewById(R.id.task_name);
            holder.taskDescription = (TextView) convertView.findViewById(R.id.task_description);
            holder.taskDateAndTime = (TextView) convertView.findViewById(R.id.task_date_time);
            holder.taskRegularNotification = (TextView) convertView.findViewById(R.id.time_date_notification);
            holder.taskLocalNotification = (TextView) convertView.findViewById(R.id.local_notification);
            holder.divider = (TextView) convertView.findViewById(R.id.divider);
            convertView.setTag(holder);
        } else {
            holder = (TaskHolder) convertView.getTag();
        }

        holder.taskName.setText(getItem(position).getName());
        holder.taskDescription.setText(getItem(position).getDescription());
        holder.taskDateAndTime.setText(getItem(position).getDate());
        if (getItem(position).getRegularNotification()!=null){
            holder.divider.setVisibility(View.VISIBLE);
            holder.taskRegularNotification.setVisibility(View.VISIBLE);
            holder.taskRegularNotification.setText("Time reminder set to: "+getItem(position).getRegularNotification());
        }
        String tempLocation = getItem(position).getLocation();
        if (tempLocation != null && tempLocation != "" && tempLocation!=" "){
                holder.taskLocalNotification.setVisibility(View.VISIBLE);
                holder.taskLocalNotification.setText("Location set to: "+ getItem(position).getLocation());
                holder.divider.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    static class TaskHolder {
        TextView taskName;
        TextView taskDescription;
        TextView taskDateAndTime;
        TextView taskRegularNotification;
        TextView taskLocalNotification;
        TextView divider;

    }

}
