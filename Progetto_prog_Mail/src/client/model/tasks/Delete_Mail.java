package client.model.tasks;

import email.Mail;
import email.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Delete_Mail implements Runnable {

    private String user;
    private Mail mail;

    public Delete_Mail(String user, Mail mail) {
        this.user = user;
        this.mail = mail;
    }

    @Override
    public void run() {
        Object oggetto[] = {this.user, this.mail};
        System.out.println("Eliminazione mail con id = " + this.mail.hashCode());
        Message request = new Message("DELETE", oggetto);
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