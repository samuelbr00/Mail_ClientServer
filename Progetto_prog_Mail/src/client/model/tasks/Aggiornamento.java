package client.model.tasks;

import email.Mail;
import email.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class Aggiornamento implements Callable<List<Mail>> {

    private String user;
    private Socket socket;
    private ObjectInputStream in;
    private Date data;

    public Aggiornamento(String user, Date data) {
        this.user = user;
        this.data = data;
    }

    @Override
    public List<Mail> call() {
        ///

        List<Mail> mail = new ArrayList<>();
        Object args[] = {this.user, this.data};
        Message request = new Message("GET_NEW", args);
        try {
            this.socket = new Socket(InetAddress.getLocalHost(), 6789);
            ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
            out.writeObject(request);
            out.flush();
            this.in = new ObjectInputStream(this.socket.getInputStream());
            Message response = (Message) this.in.readObject();
            mail = (List<Mail>) response.getOggetto();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return mail;
    }
}