package client.model.tasks;

import java.net.InetAddress;
import javafx.util.Duration;
import email.Mail;
import email.Message;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Get_All_Emails implements Callable<List<Mail>> {

    private String user;
    private Socket socket;
    private ObjectInputStream in;

    public Get_All_Emails(String user) {
        this.user = user;
    }

    @Override
    public List<Mail> call() {
        List<Mail> mails = new ArrayList<Mail>();
        Message request = new Message("GET", this.user);
        try {
            socket = new Socket(InetAddress.getLocalHost(), 6789);
            ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
            out.writeObject(request);
            out.flush();
            this.in = new ObjectInputStream(this.socket.getInputStream());
            Message response = (Message) this.in.readObject();
            if (response.getComando().equals("OK")) {
                mails = (List<Mail>) response.getOggetto();
            } else {
                Notifications.create().title("ERRORE").text("Non è stato possibile ottenere tutte le mail").hideAfter(new Duration(3000)).showInformation();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return mails;
    }
}
