package il.ac.shenkar.mytasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by raberkira on 11/28/13.
 */
public class TaskListActivity extends Activity {

    Context context;
    TaskListAdapter taskAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = this;

       TaskListModel taskListModel = TaskListModel.getInstance(this);

        ListView taskList = (ListView) findViewById(R.id.task_details_view);
        taskAdapter = new TaskListAdapter(this, taskListModel);
        taskList.setAdapter(taskAdapter);

        Button addNewTaskButton = (Button) findViewById(R.id.new_task_button);
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddNewTaskActivity.class));
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        taskAdapter.notifyDataSetChanged();
    }
}