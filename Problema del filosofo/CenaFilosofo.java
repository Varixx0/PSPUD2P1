import java.util.Random;
import java.util.concurrent.Semaphore;

class Filosofo extends Thread {
    private int id;
    private Semaphore tenedores[];

    public Filosofo(int id, Semaphore[] tenedores) {
        this.id = id;
        this.tenedores = tenedores;
    }

    private void pensar() throws InterruptedException {
        System.out.println("El filosofo " + id + " esta Pensando");
        Random random = new Random();
        int tiempoPensando = random.nextInt(5000) + 1000; // Entre 1 y 5 segundos en milisegundos
        Thread.sleep(tiempoPensando);
    }

    private void comer() throws InterruptedException {
        System.out.println("El filosofo " + id + " esta Comiendo");
        Random random = new Random();
        int tiempoComiendo = random.nextInt(5000) + 1000; // Entre 1 y 5 segundos en milisegundos
        Thread.sleep(tiempoComiendo);
    }

    @Override
    public void run() {
        try {
            while (true) {
                pensar();
                if (tenedores[id].tryAcquire()) { // Toma el tenedor de la derecha
                    if (tenedores[(id + 1) % tenedores.length].tryAcquire()) { // Intenta tomar el tenedor de la izquierda
                        comer();

                        tenedores[id].release(); // Suelta el tenedor de la derecha
                        tenedores[(id + 1) % tenedores.length].release(); // Suelta el tenedor de la izquierda
                    } else {
                        // No pudo adquirir el tenedor de la izquierda, suelta el tenedor de la derecha
                        tenedores[id].release();
                        // Espera un tiempo aleatorio antes de intentar nuevamente
                        Thread.sleep(new Random().nextInt(2000) + 1000); // Entre 1 y 3 segundos en milisegundos
                    }
                } else {
                    // Espera un tiempo aleatorio antes de intentar nuevamente
                    Thread.sleep(new Random().nextInt(2000) + 1000); // Entre 1 y 3 segundos en milisegundos
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class CenaFilosofo{
    public static void main(String[] args) {
        int numFilosofos = 5;
        Semaphore[] tenedores = new Semaphore[numFilosofos];

        for (int i = 0; i < numFilosofos; i++) {
            tenedores[i] = new Semaphore(1); // Inicialmente, todos los tenedores están disponibles
        }

        Filosofo[] filosofos = new Filosofo[numFilosofos];

        for (int i = 0; i < numFilosofos; i++) {
            filosofos[i] = new Filosofo(i, tenedores);
            filosofos[i].start();
        }
    }
}