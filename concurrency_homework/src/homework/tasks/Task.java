package homework.tasks;

public class Task {
    private int id;
    private String title;

    public Task(int id, String title){
        this.id = id;
        this.title = title;
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }
}
