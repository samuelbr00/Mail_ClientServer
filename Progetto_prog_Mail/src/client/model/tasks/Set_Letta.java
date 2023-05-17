package client.model.tasks;

import email.Mail;
import email.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Set_Letta implements Runnable {

    private String user;
    private Mail mail;

    public Set_Letta(String user, Mail mail) {
        this.user = user;
        this.mail = mail;
    }

    @Override
    public void run() {
        Object args[] = {this.user, this.mail};
        Message request = new Message("SET_READ", args);
        System.out.println("Mail impostata come letta " + this.mail);
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 6789);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(request);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
