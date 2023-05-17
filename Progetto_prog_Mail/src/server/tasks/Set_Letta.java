package server.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.logger.Logger;
import email.Mail;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;

public class Set_Letta implements Runnable {
    private final Socket client_socket;
    private Mail mail;
    private String username;
    File resource_directory;

    public Set_Letta(Socket client_socket, String username, Mail mail) {
        this.client_socket = client_socket;
        this.mail = mail;
        this.username = username;
    }

    @Override
    public void run() {
        resource_directory = new File("mailfxserver/persistence/" + username);
        Logger.debug("SetReadTask", String.format("mail {id: %d, user: %s}", mail.getId(), username));
        deleteFromDir(resource_directory);//elimina la mail
        mail.setLetta(true);//la imposta letta
        addToPersistence(mail);//la salva come file
        try {
            this.client_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFromDir(File file) {//funzione che elimina dalla cartella la mail
        for (File f : Objects.requireNonNull(file.listFiles())) {//controlla ogni file della cartella ricorsivamente
            if (f.isDirectory()) deleteFromDir(f);//ricorsione
            if (f.getName().replaceFirst("[.][^.]+$", "")
                    .equals(String.valueOf(mail.getId()))) {//quando trova la mail ne verifica l'uguaglianza
                try {
                    Files.deleteIfExists(f.toPath());//la elimina
                } catch (IOException e) {
                    Logger.error("UPDATE", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void addToPersistence(Mail toSend) {//Funzione che aggiunge la mail alla cartella ->persistenza
        File path = new File("mailfxserver/persistence/" +
                username.concat("/" + mail.getCategoria() + "/" + toSend.getId()).concat(".json"));
        //crea un file .json con id pari all'id della mail
        path.getParentFile().mkdirs();
        FileOutputStream os;
        BufferedWriter bw;
        try {
            os = new FileOutputStream(path, false);
            bw = new BufferedWriter(new OutputStreamWriter(os));
            Gson gson = new GsonBuilder().create();
            String temp = gson.toJson(toSend);
            System.out.println("Aggiungo " + temp);
            System.out.println("Al file " + path.getName());
            bw.write(temp);
            bw.flush();
            os.close();
            bw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
