package server.tasks;

import com.google.gson.Gson;
import server.logger.Logger;
import email.Mail;
import email.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Get_New_Emails implements Runnable {
    private final Socket client_socket;
    private final String username;
    String tag;
    private Date last;

    public Get_New_Emails(Socket clientSocket, String username, Date last) {
        this.client_socket = clientSocket;
        this.username = username;
        tag = clientSocket.toString();
        this.last = last;
    }

    @Override
    public void run() {
        List<File> list = listf("mailfxserver/persistence/" + this.username + "/ricevuta");//funzione che recupera la lista di file ricevuti
        Message response = new Message();
        List<Mail> list_of_user_mail;
        list_of_user_mail = get_list_of_user_mail(list);//funzione che ritorna la lista di mail ottenuta dalla lista di file
        if (list_of_user_mail == null || list_of_user_mail.isEmpty()) {
            response.setComando("ERROR");
        } else {
            response.setComando("OK");
            Logger.debug(tag, "OTTENUTE " + list_of_user_mail.size() + " NUOVE MAIL PER " + this.username);
        }
        response.setOggetto(list_of_user_mail);
        try {
            ObjectOutputStream out = new ObjectOutputStream(client_socket.getOutputStream());
            out.writeObject(response);
            out.flush();
            out.close();
            client_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<File> listf(String directoryName) {//funzione che ritorna la lista di file dalla cartella
        File directory = new File(directoryName);
        List<File> resultList = new ArrayList<>();
        File[] fList = directory.listFiles();
        if (fList == null)
            return resultList;
        for (File file : fList) {//per ogni file controlla se è un file e di conseguenza lo aggiunge alla lista
            if (file.isFile()) {
                resultList.add(file);
            } else if (file.isDirectory()) {//se non è un file, è una directory, quindi chiama ricorsivamente il metodo sulla directory stessa
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        return resultList;
    }

    public List<Mail> get_list_of_user_mail(List<File> list) {  //funzione che ritorna la lista di mail dalla lista di file
        BufferedReader br;
        Gson gson = new Gson();
        List<Mail> new_emails = new ArrayList<>();
        for (File f : list) {
            try {
                br = new BufferedReader(new FileReader(f));
                Mail item = gson.fromJson(br, Mail.class);      //legge il file e lo trasforma in un oggetto di classe Mail
                if (item.getData().compareTo(last) > 0) {       //se la data è maggiore della data di aggiornamento, sigifica che la mail è nuova
                    new_emails.add(item);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                return new_emails;
            }
        }
        return new_emails;
    }
}
