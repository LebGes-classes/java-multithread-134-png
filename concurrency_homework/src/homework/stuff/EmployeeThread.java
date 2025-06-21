package homework.stuff;

public class EmployeeThread extends Thread{
    private Employee empl;

    public EmployeeThread(Employee empl){
        super(empl);
    }

}
