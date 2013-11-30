package il.ac.shenkar.mytasks;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by raberkira on 11/30/13.
 */
public class AddNewTaskActivity extends Activity {
    private TaskListModel taskList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_task);
        Button createNewTask = (Button) findViewById(R.id.create_new_task_button);
        createNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTask(view);
            }
        });
    }

    public void createNewTask(View view) {
        TextView taskName = (TextView) findViewById(R.id.new_task_name);
        String task_name = taskName.getText().toString();
        TextView taskDescription = (TextView) findViewById(R.id.new_task_description);
        String task_description = taskDescription.getText().toString();
        taskList = TaskListModel.getInstance(this);
        taskList.addTask(new TaskDetails(task_name, task_description));
        Toast.makeText(this, task_name + "HAS BEEN ADDED TO THE LIST", Toast.LENGTH_LONG).show();
        finish();
    }

}