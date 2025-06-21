package homework.tasks;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class TaskController {

    private HashMap<Integer, String> tasksMap;//количество часов(ну и по совместительству id) - описание задачи

    private static TaskController instance;

    public static TaskController getInstance(){
        if(instance == null)
            instance = new TaskController();

        return instance;
    }
    public TaskController(){
        if(instance != null)
            throw new IllegalStateException("Объект TaskController уже создан");

        tasksMap = new HashMap<>();
        parseTasks();

    }

    private void parseTasks(){
        try {
            Workbook wb = WorkbookFactory.create(new File("data/tasks/stuffTasks.xlsx"));

            Sheet sheet = wb.getSheetAt(0);

            int hours = 0;
            String description;
            for(Row row: sheet){

                hours = (int) row.getCell(0).getNumericCellValue();
                description = row.getCell(1).getStringCellValue();
                tasksMap.put(hours, description);
            }


        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }

    public HashMap<Integer, String> getTasksMap(){
        return tasksMap;
    }

    public synchronized Task getRandomTask(){
        int id = (int) (Math.random() * 16 + 1);

        return new Task(id, tasksMap.get(id));
    }

    public String getTaskTitle(int id){
        return tasksMap.get(id);
    }


}
