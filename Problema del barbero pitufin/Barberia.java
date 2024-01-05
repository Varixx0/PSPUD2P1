import java.util.concurrent.Semaphore;

class Barberia {
    private static final int MAX_CLIENTES = 5;
    private static final int NUM_SILLAS = 3;
    private int clientes = 0;
    private boolean barberoDuerme = true;
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore sillasEspera = new Semaphore(NUM_SILLAS);

    public void llegarCliente() throws InterruptedException {
        mutex.acquire();
        if (clientes < MAX_CLIENTES) {
            clientes++;
            System.out.println("Llega un cliente. Clientes en espera: " + clientes);
            if (barberoDuerme) {
                barberoDuerme = false;
                mutex.release();
                new Thread(this::cortarPelo).start();
            } else {
                sillasEspera.acquire();
                mutex.release();
                new Thread(this::cortarPelo).start();
            }
        } else {
            System.out.println("La barbería está llena. El cliente se va.");
            mutex.release();
        }
    }

    public void cortarPelo() {
        try {
            Thread.sleep((long) (Math.random() * 3000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("¡El cliente se está cortando el pelo!");
        try {
            mutex.acquire();
            clientes--;
            sillasEspera.release();
            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void trabajar() {
        while (true) {
            try {
                mutex.acquire();
                if (sillasEspera.availablePermits() == NUM_SILLAS) {
                    barberoDuerme = true;
                    mutex.release();
                    System.out.println("El barbero se ha dormido.");
                    Thread.currentThread().join();
                } else {
                    mutex.release();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
