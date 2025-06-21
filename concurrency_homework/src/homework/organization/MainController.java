package homework.organization;

import homework.stuff.Employee;
import homework.stuff.EmployeeThread;
import homework.tasks.TaskController;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainController {

    private TaskController tasks;
    private List<Employee> employees;

    private int lastWorkDaysNum;

    public MainController(){
        tasks = new TaskController();
        employees = new ArrayList<>();

        try {
            parseEmployees();
        }
        catch(IOException exc){
            System.out.println("In MainController");
            exc.printStackTrace();
        }
    }

    private void parseEmployees()throws IOException {
        File f = new File("data/last_days");
        File[] list = f.listFiles();
        lastWorkDaysNum = list.length;

        if(list.length == 0) {
            createStuff();
            return;
        }

        List<File> files = Stream.<File>of(list).sorted((f1, f2) ->{
            return f1.getName().compareTo(f2.getName());
        }).collect(Collectors.toUnmodifiableList());

        File lastDay = files.get(files.size() - 1);

        Workbook wb = WorkbookFactory.create(lastDay);

        Sheet sheet = wb.getSheetAt(0);

        Iterator<Row> iterator = sheet.iterator();

        //прошедшие рабочие дни будут хранится в excel в формате
        //empl_id task_id worked_hours skipped_hours remained_hours
        while(iterator.hasNext()){
            Row currRow = iterator.next();

            int empl_id = Integer.parseInt(currRow.getCell(0).getStringCellValue());
            int task_id = Integer.parseInt(currRow.getCell(1).getStringCellValue());
            int worked_hours = Integer.parseInt(currRow.getCell(2).getStringCellValue());
            int skipped_hours = Integer.parseInt(currRow.getCell(3).getStringCellValue());
            int remained_hours = Integer.parseInt(currRow.getCell(4).getStringCellValue());

            employees.add(new Employee(empl_id, task_id, worked_hours, skipped_hours, remained_hours));

        }

    }
    private void createStuff()throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        System.out.println("Введи количество нужных сотрудников");
        while(!Pattern.matches("\\d+", (input = reader.readLine()))){

            System.out.println("Неправильный ввод");
        }

        int n = Integer.parseInt(input);
        for(int i = 0; i < n; i++){
            employees.add(new Employee(i + 1));
        }
    }

    private void writeLastDay(){
        Workbook wb = new XSSFWorkbook();
        Sheet sheet1 = wb.createSheet("statistics");

        List<Employee> sortedList = employees.stream().sorted((e1, e2)->{
            return Integer.compare(e1.getEmpl_id(), e2.getEmpl_id());
        }).toList();
        //empl_id task_id worked_hours skipped_hours remained_hours
        for(Employee employee: sortedList){

            Row row = sheet1.createRow(employee.getEmpl_id() - 1);
            row.createCell(0).setCellValue(String.valueOf(employee.getEmpl_id()));
            row.createCell(1).setCellValue(String.valueOf(employee.getTask_id()));
            row.createCell(2).setCellValue(String.valueOf(employee.getWorked_hours()));
            row.createCell(3).setCellValue(String.valueOf(employee.getSkipped_hours()));
            row.createCell(4).setCellValue(String.valueOf(employee.getRemained_hours()));
        }

        try(FileOutputStream out = new FileOutputStream("data/last_days/day" + lastWorkDaysNum + ".xlsx")){
            wb.write(out);
        }
        catch(IOException exc){
            System.out.println("In writeLastDay");
        }
    }
    public void startWorkingDay()throws InterruptedException{
        List<Thread> thrs = new ArrayList<>();

        lastWorkDaysNum++;
        for(Employee empl: employees){
            Thread thr = new EmployeeThread(empl);
            thr.start();
            thrs.add(thr);
        }
        for(Thread thr: thrs){
            thr.join();
        }

        writeLastDay();
        for(Employee empl: employees){
            empl.clearData();
        }
    }
    public void showLastDay()throws IOException{
        showDay(lastWorkDaysNum);
    }
    public void showFullStatistics()throws IOException{
        if(lastWorkDaysNum == 0)
            System.out.println("Пока не было рабочих дней");

        for(int i = 1; i <= lastWorkDaysNum; i++){
            showDay(i);
        }
    }
    private void showDay(int num)throws IOException{
        if(lastWorkDaysNum == 0) {
            System.out.println("Пока не было рабочих дней");
            return;
        }

        if(num < 1 || num > lastWorkDaysNum)
            throw new IllegalStateException();

        List<List<String>> data = new ArrayList<>();

        File f = new File("data/last_days");
        File[] list = f.listFiles();

        File currFile = null;

        for(File file: list){
            if(file.getName().equals("day%d.xlsx".formatted(num)))
                currFile = file;
        }

        Workbook wb = WorkbookFactory.create(currFile);

        Sheet sheet = wb.getSheetAt(0);

        //прошедшие рабочие дни будут хранится в excel в формате
        //empl_id task_id worked_hours skipped_hours remained_hours
        System.out.println("День " + num);
        for(Row currRow: sheet){


            int empl_id = Integer.parseInt(currRow.getCell(0).getStringCellValue());
            int task_id = Integer.parseInt(currRow.getCell(1).getStringCellValue());
            int worked_hours = Integer.parseInt(currRow.getCell(2).getStringCellValue());
            int skipped_hours = Integer.parseInt(currRow.getCell(3).getStringCellValue());

            System.out.println("Сотрудник " + empl_id + " выполнял задачу:\n" + tasks.getTaskTitle(task_id));
            System.out.println("Проработал " + worked_hours + "часов, пропустил " + skipped_hours);

        }

    }

}
