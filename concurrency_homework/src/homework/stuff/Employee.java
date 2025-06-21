package homework.stuff;

import homework.tasks.Task;
import homework.tasks.TaskController;

public class Employee implements Runnable{
    private int empl_id;
    private int task_id;
    private int worked_hours;
    private int skipped_hours;
    private int remained_hours;

    private String taskTitle;
    private TaskController controller;

    public Employee(int empl_id){
        this(empl_id, -1, -1, -1, -1);
    }
    public Employee(int empl_id, int task_id, int worked_hours, int skipped_hours, int remained_hours){
        this.empl_id = empl_id;
        this.task_id = task_id;
        this.worked_hours = worked_hours;
        this.skipped_hours = skipped_hours;
        this.remained_hours = remained_hours;

        controller = TaskController.getInstance();
        taskTitle = controller.getTaskTitle(task_id);
    }

    public int getEmpl_id(){
        return empl_id;
    }

    public int getTask_id(){
        return task_id;
    }

    public int getWorked_hours(){
        return worked_hours;
    }

    public int getSkipped_hours(){
        return skipped_hours;
    }

    public int getRemained_hours(){
        return remained_hours;
    }

    public void run(){
        worked_hours = 0;
        skipped_hours = 0;

        if(task_id == -1){
            Task new_task = controller.getRandomTask();
            task_id = new_task.getId();
            taskTitle = new_task.getTitle();

            remained_hours = new_task.getId();//у нас id и время выполнения задачи
            System.out.println("Сотрудник " + empl_id + " взял задачу:\n" + taskTitle + "\nвремя выполнения: " + remained_hours);
        }

        if(remained_hours > 8){
            remained_hours -= 8;
            worked_hours += 8;
            System.out.println("Сегодня сотрудник " + empl_id + " выполнил часть своей задачи");
        }
        else {
            worked_hours += remained_hours;
            skipped_hours = 8 - worked_hours;
            remained_hours = 0;
            System.out.println("Сегодня сотрудник " + empl_id + " закончил задачу за " + worked_hours);

        }
    }

    public void clearData(){
        if(remained_hours == 0){
            task_id = -1;
        }
    }
}
