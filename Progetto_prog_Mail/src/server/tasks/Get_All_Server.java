package server.tasks;

import com.google.gson.Gson;
import server.logger.Logger;
import email.Mail;
import email.Message;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Get_All_Server implements Runnable {
    private final Socket client_socket;
    private final String username;
    String tag;

    public Get_All_Server(Socket client_socket, String username) {
        this.client_socket = client_socket;
        this.username = username;
        tag = client_socket.toString();
    }

    @Override
    public void run() {
        Logger.debug(tag, "Ottenendo tutte le mail di " + username);
        List<File> list = listf("mailfxserver/persistence/" + username);//funzione che ottiene la lista di file dal path
        Message response = new Message();
        List<Mail> listOfUserMail;
        if (list == null || list.isEmpty()) {
            response.setComando("ERROR");
        } else {
            listOfUserMail = get_list_of_user_mail(list);//funzione che ritorna la lista di mail dalla lista di file
            response = new Message("OK", listOfUserMail);
        }
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

    public static List<File> listf(String directoryName) {//funzione che ritorna la lista di file dal path
        File directory = new File(directoryName);//crea il file cartella relativo al path
        List<File> resultList = new ArrayList<>();
        File[] fList = directory.listFiles();
        if (fList == null)
            return null;
        for (File file : fList) {//oer ogni file verifica che sia un file, con conseguente aggiunta alla lista
            if (file.isFile()) {
                resultList.add(file);
            } else if (file.isDirectory()) {//se non è un file, viene richiamata ricorsivamente la funzione su quella directory
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        return resultList;
    }

    public static List<Mail> get_list_of_user_mail(List<File> list) {//funzione che ritorna la lista di mail dalla lista di file
        BufferedReader br;
        Gson gson = new Gson();
        List<Mail> result = new ArrayList<>();
        for (File f : list) {//per ogni file nella lista
            try {
                br = new BufferedReader(new FileReader(f));    //lo legge e lo trasforma in una mail
                Mail obj = gson.fromJson(br, Mail.class);    //trasforma l'oggetto da GSON a una oggetto di classe Mail
                result.add(obj);
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
