import java.util.concurrent.Semaphore;

public class HarbourShuttle {
    public static Semaphore northShoreSemaphore;
    public static Semaphore aucklandSemaphore;
    public static Semaphore offBoardSemaphore;
    public static Semaphore canLeaveSemaphore;
    public static Semaphore canLoadSemaphore;
    public static Semaphore countSemaphore;
    public static int numCustomers;
    public static int onBoardCount;
    public static int offBoardCount;
    public static boolean fromAuckland;
    public static Semaphore printSemaphore;
    
    public static void main(String[] args) throws InterruptedException {
            onBoardCount = 0;
            offBoardCount = 10;
            numCustomers = 100;
            northShoreSemaphore = new Semaphore(0);
            aucklandSemaphore = new Semaphore(0);
            fromAuckland = true;
            canLoadSemaphore = new Semaphore(1);
            canLeaveSemaphore = new Semaphore(0);
            countSemaphore = new Semaphore(1);
            offBoardSemaphore = new Semaphore(0);
            printSemaphore = new Semaphore(1);
            Shuttle shuttle = new Shuttle();
            Person[] persons = new Person[100];
            for(int index = 0; index < 50; ++index){
                persons[index] = new Person("Person " + Integer.valueOf(index + 1).toString(),
                        "Auckland", "the North Shore");
            }
            for(int index = 50; index < 100; ++index){
                persons[index] = new Person("Person " + Integer.valueOf(index + 1).toString(),
                        "the North Shore","Auckland");
            }
            for(int index = 0; index < 100; ++index){
                persons[index].start();
            }
            shuttle.start();
            shuttle.join();
        for(int index = 0; index < 100; ++index){
            persons[index].join();
        }
    }
    
    public static void myPrint(String msg) throws InterruptedException {
        printSemaphore.acquire();
        System.out.println(msg);
        System.out.flush();
        printSemaphore.release();
    }
}

class Shuttle extends  Thread{
    public void run(){
        try {
            while (HarbourShuttle.numCustomers > 0) {
                HarbourShuttle.canLoadSemaphore.acquire();
                if(HarbourShuttle.fromAuckland) {
                    while (HarbourShuttle.offBoardCount > 0) {
                        HarbourShuttle.aucklandSemaphore.release();
                        --HarbourShuttle.offBoardCount;
                    }
                }
                else{
                    while(HarbourShuttle.offBoardCount > 0){
                        HarbourShuttle.northShoreSemaphore.release();
                        --HarbourShuttle.offBoardCount;
                    }
                }
                if (HarbourShuttle.fromAuckland) {
                    HarbourShuttle.canLeaveSemaphore.acquire();
                    HarbourShuttle.myPrint("Shuttle is going from Auckland to the North Shore.");
                    HarbourShuttle.myPrint("Shuttle has arrived at the North Shore.");
                    while(HarbourShuttle.onBoardCount > 0){
                        HarbourShuttle.offBoardSemaphore.release();
                        --HarbourShuttle.onBoardCount;
                    }
                    HarbourShuttle.fromAuckland = false;

                }
                else{
                    HarbourShuttle.canLeaveSemaphore.acquire();
                    HarbourShuttle.myPrint("Shuttle is going from the North Shore to Auckland.");
                    HarbourShuttle.myPrint("Shuttle has arrived at Auckland.");
                    while(HarbourShuttle.onBoardCount > 0){
                        HarbourShuttle.offBoardSemaphore.release();
                        --HarbourShuttle.onBoardCount;
                    }
                    HarbourShuttle.fromAuckland = true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

class Person extends  Thread{
    private String source;
    private String destination;

    public Person(String name, String source, String destination) {
        super(name);
        this.source = source;
        this.destination = destination;
    }

    public void run(){
        try {
            if (source.compareToIgnoreCase("the North Shore") == 0) {
                HarbourShuttle.northShoreSemaphore.acquire();
                HarbourShuttle.myPrint(getName() + " is boarding the shuttle on the North Shore.");
                HarbourShuttle.countSemaphore.acquire();
                ++HarbourShuttle.onBoardCount;
                --HarbourShuttle.numCustomers;
                if(HarbourShuttle.onBoardCount == 10){
                    HarbourShuttle.canLeaveSemaphore.release();
                }
                HarbourShuttle.countSemaphore.release();
                HarbourShuttle.offBoardSemaphore.acquire();
                HarbourShuttle.myPrint(getName() + " is leaving the shuttle in Auckland.");
                HarbourShuttle.countSemaphore.acquire();
                ++HarbourShuttle.offBoardCount;
                if(HarbourShuttle.offBoardCount == 10){
                    HarbourShuttle.canLoadSemaphore.release();
                }
                HarbourShuttle.countSemaphore.release();

            } else if (source.compareToIgnoreCase("Auckland") == 0) {
                HarbourShuttle.aucklandSemaphore.acquire();
                HarbourShuttle.myPrint(getName() + " is boarding the shuttle on Auckland.");
                HarbourShuttle.countSemaphore.acquire();
                ++HarbourShuttle.onBoardCount;
                --HarbourShuttle.numCustomers;
                if(HarbourShuttle.onBoardCount == 10){
                    HarbourShuttle.canLeaveSemaphore.release();
                }
                HarbourShuttle.countSemaphore.release();
                HarbourShuttle.offBoardSemaphore.acquire();
                HarbourShuttle.myPrint(getName() + " is leaving the shuttle in " + destination);
                HarbourShuttle.countSemaphore.acquire();
                ++HarbourShuttle.offBoardCount;
                if(HarbourShuttle.offBoardCount == 10){
                    HarbourShuttle.canLoadSemaphore.release();
                }
                HarbourShuttle.countSemaphore.release();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
