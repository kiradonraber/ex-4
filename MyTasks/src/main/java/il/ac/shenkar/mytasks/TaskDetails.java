package il.ac.shenkar.mytasks;

import android.widget.Toast;

/**
 * Created by raberkira on 11/28/13.
 */
public class TaskDetails {
    static int count = 0;
    private long id;
    private String name = null;
    private String description = null;
    private String dateTime;
    private String regularNotification = null;
    private String location = null;

    public TaskDetails(){}

    public TaskDetails(String name, String description, String dateAndTime){
        count++;
        this.name = name;
        this.description = description;
        this.id = count;
        this.dateTime = dateAndTime;
        this.location = null;
        this.regularNotification = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateAndTime) {
        this.dateTime = dateAndTime;
    }

    public void setRegularNotification (String notification){
        this.regularNotification = notification;
    }

    public String getRegularNotification (){
        return this.regularNotification;
    }

    public void setLocation (String location){
        this.location = location;
    }

    public String getLocation (){
        return this.location;
    }
}
