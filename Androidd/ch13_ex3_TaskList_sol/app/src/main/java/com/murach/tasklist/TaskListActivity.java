package com.murach.tasklist;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class TaskListActivity extends Activity {
    Button btn = (Button) findViewById(R.id.button);
    CheckBox done = (CheckBox) findViewById(R.id.checkBox);
    CheckBox hidden = (CheckBox) findViewById(R.id.checkBox2);
    EditText taskn = (EditText) findViewById(R.id.editText);
    EditText infon = (EditText) findViewById(R.id.editText2);
    private Task task;
    //TaskListDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        
        // get db and StringBuilder objects
        TaskListDB db = new TaskListDB(this);
        StringBuilder sb = new StringBuilder();
        
        // insert a task
        Task task = new Task(1, "Make dentist appointment", "", 0, 0);
        long insertId = db.insertTask(task);
        if (insertId > 0) {
            sb.append("Row inserted! Insert Id: " + insertId + "\n");
        }
        
        // update a task
        task.setId((int) insertId);
        task.setName("Update test");
        int updateCount = db.updateTask(task);
        if (updateCount == 1) {
            sb.append("Task updated! Update count: " + updateCount + "\n");
        }
        
        // delete a task
        int deleteCount = db.deleteTask(insertId);
        if (deleteCount == 1) {
            sb.append("Task deleted! Delete count: " + deleteCount + "\n\n");
        }
        
        // display all tasks (id + name)
        ArrayList<Task> tasks = db.getTasks("Personal");
        for (Task t : tasks) {
            sb.append(t.getId() + "|" + t.getName() + "\n");
        }
        
        // display string on UI
        TextView taskListTextView = (TextView) 
                findViewById (R.id.taskListTextView);
        taskListTextView.setText(sb.toString());

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Task task = new Task(1, taskn.getText().toString(), infon.getText().toString(), 0, 0);
                if(done.isChecked()){
                    task.getCompletedDate();
                }
                if(hidden.isChecked()){
                    task.setHidden(1);
                }
                long insertId = db.insertTask(task);
                if (insertId > 0) {
                    sb.append("Row inserted! Insert Id: " + insertId + "\n");
                }


            }
        });
    }

      }