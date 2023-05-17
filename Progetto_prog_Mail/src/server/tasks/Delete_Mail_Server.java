package server.tasks;

import server.logger.Logger;
import email.Mail;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;

public class Delete_Mail_Server implements Runnable {
    private final Socket client_socket;
    private Mail to_delete;
    private String user;

    public Delete_Mail_Server(Socket clientSocket, String username, Mail toDelete) {
        this.client_socket = clientSocket;
        this.to_delete = toDelete;
        this.user = username;
    }

    @Override
    public void run() {
        File file = new File("mailfxserver/persistence/" + user);
        Logger.debug("DeleteMailServerTask", String.format("Eliminazione mail id[%d] dall'utente {%s}", to_delete.getId(), user));
        deleteFromDir(file);
        try {
            this.client_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFromDir(File file) {//funzione che elimina dalla cartella la mail
        for (File f : Objects.requireNonNull(file.listFiles())) {//controlla ogni file della cartella ricorsivamente
            if (f.isDirectory()) deleteFromDir(f);
            if (f.getName().replaceFirst("[.][^.]+$", "")
                    .equals(String.valueOf(to_delete.getId()))) {//quando trova la mail ne verifica l'uguaglianza
                try {
                    Files.deleteIfExists(f.toPath());//la elimina
                    System.out.println("Trovata; eliminazione");
                } catch (IOException e) {
                    Logger.error("DELETE", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
