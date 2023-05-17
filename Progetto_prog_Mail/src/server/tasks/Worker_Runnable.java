package server.tasks;

import server.logger.Logger;
import email.Mail;
import email.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;

public class Worker_Runnable implements Runnable {

    private static final String tag = "WORKER";
    private Socket client_socket;

    public Worker_Runnable(Socket client_socket) {
        this.client_socket = client_socket;
    }

    public void run() {
        Logger.info(tag, Thread.currentThread().toString());   //stampa quale thread è in esecuzione sul logger
        try {

             // Ricaviamo gli stream di input che collegano client e server
             // input == richiesta == Get , Get New , Send , Set Read , Delete
            // INfatti nel client mettiamqo object output stream
            ObjectInputStream input = new ObjectInputStream(client_socket.getInputStream());
            Message request = (Message) input.readObject();
            Logger.info(tag, String.format("Richiesta ricevuta: %s", request.getComando()));
            switch (request.getComando()) {
                case "GET": {
                    String username = (String) request.getOggetto();
                    Logger.debug("GET", "Ottengo tutte le email di " + username);
                    new Get_All_Server(client_socket, username).run();
                }
                break;
                case "GET_NEW": {
                    Object args[] = (Object[]) request.getOggetto();
                    String owner = (String) args[0];
                    Date last = (Date) args[1];//inviami tutte le mail che hanno data maggior di questa email
                    Logger.debug("GET_NEW", "Ottengo le nuove email di " + owner);
                    new Get_New_Emails(client_socket, owner, last).run();
                }
                break;
                case "SEND": {
                    Logger.debug("SEND", "invio email");
                    Mail toSend = (Mail) request.getOggetto();
                    new Send_Mail_Server(client_socket, toSend).run();
                }
                break;
                case "SET_READ": {
                    Logger.debug("SET_READ", "imposto email come letta");
                    Object param[] = (Object[]) request.getOggetto();
                    String user = (String) param[0];
                    Mail toUpdate = (Mail) param[1];
                    new Set_Letta(client_socket, user, toUpdate).run();
                }
                break;
                case "DELETE": {
                    Object req[] = (Object[]) request.getOggetto();
                    String mail_address = (String) req[0];
                    Mail delete = (Mail) req[1];
                    Logger.debug("DELETE", "elimino la mail con Id: " + delete.getId());
                    new Delete_Mail_Server(client_socket, mail_address, delete).run();
                }
                break;
                case "ONLINE": {
                    try {
                        this.client_socket.close();
                        Logger.debug("ONLINE", "chiudo la socket di richiesta online");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            e.getMessage();
        }
    }
}