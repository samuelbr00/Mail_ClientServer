package server;

import server.logger.Logger;
import server.tasks.Worker_Runnable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Multi_Threaded_Server implements Runnable {

    protected int porta_server;
    protected ServerSocket server_socket = null;
    protected boolean is_stopped = false;
    protected Thread running_thread = null;
    protected ExecutorService thread_pool = Executors.newFixedThreadPool(6); // Array

    Logger logger = new Logger();

    public Logger getLogger() {
        return logger;
    }

    public Multi_Threaded_Server(int porta) {

        this.porta_server = porta;
    }
//per garantire che un thread alla volta possa modificare tale ogg
    public void run() {
        synchronized (this) {
            this.running_thread = Thread.currentThread();
        }
        this.open_server_socket();
        while (!isStopped()) {
            Socket client_socket = null;
            try {
                Logger.debug("ASCOLTO", "In ascolto sulla porta: " + porta_server);

                // Per far si che il server riesca a servire più clienti contemporaneamente

                // a servire il client (di questo se ne occupa il nuovo thread creato)
                // e può in questo modo ritornare al metodo accept  della java.net.ServerSocket
                // per aspettare le connessione di eventuali nuovi client.

                client_socket = this.server_socket.accept();  //attende una richiesta di connessione
            } catch (IOException e) {
                if (isStopped()) {
                    return;
                }
                throw new RuntimeException("Errore nell'accettazione della socket utente", e);
            } 
            Logger.debug("CONNESSIONE", "Accettata nuova connessione: " + client_socket.toString());
            this.thread_pool.execute(new Worker_Runnable(client_socket));
        }
        Logger.debug("SPEGNIMENTO", "Il server si spegne");
        thread_pool.shutdown();
    }

    private synchronized boolean isStopped() {

        return this.is_stopped;

    }

    public synchronized void stop() {
        this.is_stopped = true;
        try {
            this.server_socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Errore nella chiusura del server", e);
        }
    }

    private void open_server_socket() {
        try {
            this.server_socket = new ServerSocket(this.porta_server);
        } catch (IOException e) {
            throw new RuntimeException("Impossibile aprire la porta " + this.porta_server, e);
        }
    }
}
