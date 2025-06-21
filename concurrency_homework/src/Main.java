
import java.util.Arrays;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static int NACCOUNTS = 100;
    public static int INITIAL_BALANCE = 1000;
    public static int MAX_AMOUNT = 1000;
    public static int DELAY = 10;

    public static void main(String[] args) throws InterruptedException{

        Boom obj = new Boom();

        Thread t1 = new Thread(()->{
            try {
                obj.lockObj();
            }
            catch(InterruptedException exc){
                exc.printStackTrace();
            }
        });
        Thread t2 = new Thread(()->{
            try {
                obj.lockObj();
            }
            catch(InterruptedException exc){
                exc.printStackTrace();
            }
        });
        Thread t3 = new Thread(()->{
            try {
                obj.lockObj();
            }
            catch(InterruptedException exc){
                exc.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t3.start();

    }
    public static void startBankOperations(){
        Bank bank = new Bank(NACCOUNTS, INITIAL_BALANCE);

        for(int i = 0; i < NACCOUNTS; i++){

            Runnable r = new Runnable() {

                @Override
                public void run() {

                                  try {
                        while(true) {
                            int from = (int) (NACCOUNTS * Math.random());
                            int to = (int) (NACCOUNTS * Math.random());

                            double amount = MAX_AMOUNT * Math.random();
                            bank.transfer(from, to, amount);
                            Thread.sleep((int) (DELAY * Math.random()));
                        }
                    }
                    catch(InterruptedException exc){
                        exc.printStackTrace();
                    }
                }
            };

            new Thread(r).start();
        }
    }


    static class Boom{
        private Semaphore sem = new Semaphore(2);

        void lockObj()throws InterruptedException{

            if(!sem.tryAcquire()){

                System.out.println("Занято все, поток " + Thread.currentThread() + " не получил блокировку");
            }
            else{
                System.out.println("Поток " + Thread.currentThread() + " получил разрешение");
                while(true){}
            }


        }

    }
}

class Bank{

    private double[] accounts;
    private ReentrantLock bankLock;
    private Condition condition;

    Bank(int num, int initBalance){
        bankLock = new ReentrantLock();
        condition = bankLock.newCondition();
        accounts = new double[num];
        Arrays.fill(accounts, initBalance);
    }

    public void transfer(int from, int to, double amount){
        synchronized(bankLock) {
            try {

                while (accounts[from] < amount) {
                    System.out.println("Unable now from " + from + " to " + to);
                    bankLock.wait();
                    System.out.println("good for: from " + from + " to " + to);
                }

                System.out.println(Thread.currentThread());
                accounts[from] -= amount;
                accounts[to] += amount;

                System.out.printf("%10.2f from %d to %d", amount, from, to);
                System.out.println("Total balance " + getTotalBalance());

                bankLock.notifyAll();
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }
    }

    public int getTotalBalance(){
        bankLock.lock();

        try {
            int sum = 0;

            for (double d : accounts)
                sum += d;

            return sum;
        }
        finally{
            bankLock.unlock();
        }

    }

    public int size(){
        return accounts.length;
    }
}


