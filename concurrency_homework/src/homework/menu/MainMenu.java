package homework.menu;

import homework.organization.MainController;

import java.io.*;
import java.util.regex.Pattern;

public class MainMenu {
    private MainController controller;
    private BufferedReader reader;

    public static void main(String[] args) {
        new MainMenu();
    }

    public MainMenu(){
        controller = new MainController();
        try {
            start();
        }
        catch(IOException | InterruptedException exc){
            exc.printStackTrace();
        }
    }

    public void start()throws IOException, InterruptedException{
        reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        boolean cycleFlag = true;


        while(cycleFlag) {

            System.out.println("1. Начать рабочий день");
            System.out.println("2. Показать статистику эффективности рабочих за прошлые дни");
            System.out.println("Введите цифру или exit");

            while(!Pattern.matches("(1|2|exit)", (input = reader.readLine()))) {
                System.out.println("Неправильный ввод");
            }
            if (input.equals("1"))
                startWorkingDay();
            else if (input.equals("2"))
                showStatistics();
            else if (input.equals("exit"))
                cycleFlag = false;
        }
    }

    public void startWorkingDay()throws InterruptedException{
        System.out.println("Рабочий день начался");
        controller.startWorkingDay();
        System.out.println("Рабочий день закончился\nПоказать статистику прошедшего дня(Да\\Нет)");

        String input;
        try {
            while (!Pattern.matches("(Нет|Да)", (input = reader.readLine()))) {
                System.out.println("Неправильный ввод");
            }

            if(input.equals("Нет"))
                return;

            controller.showLastDay();
        }
        catch(IOException exc){
            System.out.println("In startWorkingDay");
            exc.printStackTrace();
        }


    }
    public void showStatistics(){
        try {
            controller.showFullStatistics();
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }
}
