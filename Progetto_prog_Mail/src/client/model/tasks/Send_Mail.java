package client.model.tasks;

import email.Mail;
import email.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Send_Mail implements Runnable {

    private Mail mail;

    public Send_Mail(Mail mail) {
        this.mail = mail;
    }

    @Override
    public void run() {
        Message request = new Message("SEND", this.mail);
        System.out.println("Id mail: " + this.mail.getId());
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
