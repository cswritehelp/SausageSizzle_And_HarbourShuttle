import java.util.Random;

public class SausageSizzle {
    public static int sausages = 0;
    public static int numCustomers = 100;

    public static void main(String[] args) throws InterruptedException {
        Customer[] customers = new Customer[100];
        for(int index = 0; index < 100; ++index){
            customers[index] = new Customer("Customer " + (index+1));
        }
        Barbecue[] barbecues = new Barbecue[2];
        for(int index = 0; index < 2; ++index){
            barbecues[index] = new Barbecue("Barbecue " + (index + 1));
        }
        for(int index = 0; index < 100; ++index){
            customers[index].start();
        }
        for(int index = 0; index < 2; ++index){
            barbecues[index].start();
        }
        for(int index = 0; index < 100; ++index){
            customers[index].join();
        }
        for(int index = 0; index < 2; ++index){
            barbecues[index].join();
        }
    }
}

class Customer extends Thread{
    private int wantSausages;

    public Customer(String name) {
        super(name);
        this.wantSausages = new Random().nextInt(3) + 1;
    }

    public void run(){
        boolean isSatisfied = false;
        while(!isSatisfied) {
            synchronized (SausageSizzle.class) {
                if(SausageSizzle.sausages >= wantSausages){
                    SausageSizzle.sausages -= wantSausages;
                    System.out.println(getName() + " buys " + wantSausages + " sausages.");
                    --SausageSizzle.numCustomers;
                    isSatisfied = true;
                }
            }
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Barbecue extends Thread{

    public Barbecue(String name) {
        super(name);
    }

    public void run(){
        boolean  wantRequest = true;
        while(wantRequest){
            synchronized (SausageSizzle.class){
                if(SausageSizzle.numCustomers > 0){
                    System.out.println(getName() + " has another sausage ready.");
                    ++SausageSizzle.sausages;
                }
                else{
                    wantRequest = false;
                }
            }
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
