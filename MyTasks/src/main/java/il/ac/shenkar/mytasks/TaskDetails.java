package il.ac.shenkar.mytasks;

/**
 * Created by raberkira on 11/28/13.
 */
public class TaskDetails {
    private long id;
    private String name;
    private String description;

    public TaskDetails(){}

    public TaskDetails(String name, String description){
        this.name = name;
        this.description = description;
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
}
