import java.util.Random;
import java.util.concurrent.Semaphore;

class Philosopher extends Thread {
    //Atributos of  filosofo
    private int id;
    private Semaphore forks[];

    public Philosopher(int id, Semaphore[] forks) {
        this.id = id;
        this.forks = forks;
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking");
        Random random = new Random();
        int waitTime = random.nextInt(5000) + 1000; // Wait between 1 and 5 miliseconds
        Thread.sleep(waitTime); // Thread sleeps the time generated before
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is eating");
        Random random = new Random();
        int waitTime = random.nextInt(5000) + 1000; // Wait between 1 and 5 miliseconds
        Thread.sleep(waitTime); // Thread sleeps the time generated before
    }

    @Override
    public void run() {
        try {
            while (true) {
                // They all start thinking
                think();
                if (forks[id].tryAcquire()) { // Takes the rigth fork
                    if (forks[(id + 1) % forks.length].tryAcquire()) { // Tries to take the left fork
                        eat();

                        forks[id].release(); // Leaves the rigth fork till he finishes eating
                        forks[(id + 1) % forks.length].release(); // Leaves the left fork
                    } else {
                        // Leaves the rigth fork if he couldn't take the left one
                        forks[id].release();
                        // Tries again
                        Thread.sleep(new Random().nextInt(2000) + 1000);  // Wait between 1 and 3 miliseconds
                    }
                } else {
                    // Waits random time till he tries again
                    Thread.sleep(new Random().nextInt(2000) + 1000); // Wait between 1 and 3 miliseconds
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class PhilosopherDinner{
    public static void main(String[] args) {
        int numPhilosopher = 5;
        Semaphore[] forks = new Semaphore[numPhilosopher];
        Philosopher[] philosophers = new Philosopher[numPhilosopher];

        for (int i = 0; i < numPhilosopher; i++) {
            forks[i] = new Semaphore(1); // Start all forks to available
        }

        for (int i = 0; i < numPhilosopher; i++) {
            philosophers[i] = new Philosopher(i, forks);
            philosophers[i].start();
        }
    }
}