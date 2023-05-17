package client.model;

import client.model.tasks.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import email.Mail;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Mailbox {

     // Le azioni dell’utente sull’interfaccia grafica,
    // catturate dai Listener, cambino lo stato (cioè, il valore delle variabili) dell’applicazione
    //L’applicazione rappresenti il proprio stato in modo esplicito, attraverso opportune variabili

    public static ObservableList<Mail> list = FXCollections.observableArrayList();

    private FilteredList<Mail> filteredList = new FilteredList<Mail>(list, e -> true);

    private String user;

    public Mailbox(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void initialize() {
        list.setAll(new Get_All_Emails(this.user).call());
        list.sort(Comparator.comparing(Mail::getData));
    }

    public void delete(Mail mail) {
        new Delete_Mail(this.user, mail).run();
        list.remove(mail);
    }

    public void send(Mail mail) {
        System.out.println("Inviata: " + mail);
        new Send_Mail(mail).run();
        mail.setCategoria("inviata");
        mail.setLetta(true);
        list.add(mail);
    }

    public void letta(Mail mail) {
        mail.setLetta(true);
        list.set(list.indexOf(mail), mail);
        new Set_Letta(this.user, mail).run();
    }

    public int aggiorna() {

          /// chiama New Aggiornamento <<Callable>>
        if (list == null || list.isEmpty()) {
            this.initialize();
        } else {
            Date data = list.stream()     //della lista di mail prendimi la data maggiore, ovvero la data dell'ultima mail ricevuta
                    .filter(el -> el.getCategoria().equals("ricevuta"))
                    .map(Mail::getData)
                    .max(Date::compareTo)
                    .orElse(new Date(0));
            System.out.println("Update - Date: " + data);
            List<Mail> call = new Aggiornamento(this.user, data).call();
            System.out.println("Lista di mail: " + call);
            if (call.size() > 0)
                list.addAll(call);
            return call.size();
        }
        return 0;
    }

    public boolean online() {
        return new Online().call();
    }

    public FilteredList<Mail> getFilteredList() {

        return this.filteredList;
    }

}
