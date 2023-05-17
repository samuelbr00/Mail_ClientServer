package server.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.logger.Logger;
import email.Mail;

import java.io.*;
import java.net.Socket;

public class Send_Mail_Server implements Runnable {
    private final Socket client_socket;
    private final Mail to_send;
    File file;

    public Send_Mail_Server(Socket client_socket, Mail mail) {
        this.client_socket = client_socket;
        this.to_send = mail;
    }

    @Override
    public void run() {
        Logger.debug("SEND MAIL", Thread.currentThread().toString());
        for (String destinatario : to_send.getDestinatari()) {   //per ogni destinatario nella mail
            add_to_received(to_send, destinatario);            //aggiungi tale mail alla lista delle loro mail ricevute
            Logger.debug("SEND MAIL", "email ricevuta");
        }
        to_send.setLetta(true);
        add_to_sent(to_send, to_send.getMittente());      //aggiungi la mail alla lista delle mail inviate dal mittente
        Logger.debug("SEND_MAIL", "email inviata");
        try {
            this.client_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void add_to_received(Mail to_send, String destinatario) {
        to_send.setCategoria("ricevuta");
        file = new File("mailfxserver/persistence/" + destinatario + "/ricevuta/" + to_send.getId() + ".json");
        file.getParentFile().mkdirs();//crea il file nel path specificato
        add_to_persistence(to_send);
    }

    private void add_to_sent(Mail toSend, String mittente) {
        toSend.setCategoria("inviata");
        file = new File("mailfxserver/persistence/" + mittente + "/inviata/" + toSend.getId() + ".json");
        file.getParentFile().mkdirs();
        add_to_persistence(toSend);
    }

    private void add_to_persistence(Mail toSend) {
        FileOutputStream os;
        BufferedWriter bw;
        try {
            os = new FileOutputStream(file, false);  //imposta l'output dello stream sul file json appena creato
            bw = new BufferedWriter(new OutputStreamWriter(os));
            Gson gson = new GsonBuilder().create();
            String temp = gson.toJson(toSend);
            System.out.println("adding " + temp);
            System.out.println("To file " + file.getName());
            bw.write(temp);
            bw.flush();
            os.close();
            bw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}