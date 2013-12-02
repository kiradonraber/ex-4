package il.ac.shenkar.mytasks;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        //String task_dateAndTime = getDateAndTime();
        taskList = TaskListModel.getInstance(this);
        taskList.addTask(new TaskDetails(task_name, task_description //, task_dateAndTime
        ));
        Toast.makeText(this, task_name + " HAS BEEN ADDED TO THE LIST", Toast.LENGTH_LONG).show();
        finish();
    }

    public String getDateAndTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd/MM/yyyy, hh:mm:ss", Locale.getDefault());
        Date d = Calendar.getInstance().getTime();
        return dateFormat.format(d);
    }

}