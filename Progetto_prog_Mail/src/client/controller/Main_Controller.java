package client.controller;

import client.model.Mailbox;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.util.Duration;
import email.Mail;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main_Controller implements Initializable {

    @FXML
    private AnchorPane anchor_pane_body;
    @FXML
    private Button button_tutte;
    @FXML
    private Button button_ricevute;
    @FXML
    private Button button_inviate;
    @FXML
    private Button button_scrivi;
    @FXML
    private Label label_sezione;

    protected static Mailbox mailbox;
    private FilteredList<Mail> filteredList;
    private String user;
    protected static SimpleStringProperty modalita;

    public Main_Controller(String user) {

        this.user = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.inizializza_dati();
        this.inizializza_view();
        this.inizializza_listeners();
        this.inizializza_aggiornamento();
    }

    public void inizializza_dati() {
        mailbox = new Mailbox(this.user);
        if (!mailbox.online()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ATTENZIONE");
            alert.setHeaderText("Server Chiuso");
            alert.setContentText("Impossibile contattare il server");
            alert.show();
        } else {
            this.filteredList = mailbox.getFilteredList();
            this.filteredList.setPredicate(elem -> (elem.getLetta() && !elem.getMittente().equals(user) || (elem.getMittente().equals(user) && elem.getCategoria().equals("ricevuta"))));
            this.filteredList.sort(Comparator.comparing(Mail::getData));
            modalita = new SimpleStringProperty("tutte");
            this.label_sezione.setText("Tutte le mail");
            modalita.addListener(new ChangeListener<String>() {

                // L’applicazione rappresenti il proprio stato in modo esplicito, attraverso opportune variabili
                //• Le azioni dell’utente sull’interfaccia grafica,
                // catturate dai Listener, cambino lo stato (cioè, il valore delle variabili) dell’applicazione
                @Override
                //modalità.get
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    switch (modalita.get()) {
                        case "scrivi": {
                            mostra_scrivi();
                            label_sezione.setText("Scrivi");
                        }
                        break;
                        case "tutte": {
                            mostra_leggi();
                            filteredList.setPredicate(elem -> (elem.getLetta() && !elem.getMittente().equals(user) || (elem.getMittente().equals(user) && elem.getCategoria().equals("ricevuta"))));
                            label_sezione.setText("Tutte le mail");
                        }
                        break;
                        case "ricevute": {
                            mostra_leggi();
                            filteredList.setPredicate(elem -> !elem.getLetta());
                            label_sezione.setText("Ricevute");
                        }
                        break;
                        case "inviate": {
                            mostra_leggi();
                            filteredList.setPredicate(elem -> elem.getMittente().equals(mailbox.getUser()) && elem.getCategoria().equals("inviata"));
                            label_sezione.setText("Inviate");
                        }
                        break;
                    }
                }
            });
        }
    }

    public void inizializza_view() {
        this.mostra_leggi();
    }

    //modalità.set
    public void inizializza_listeners() {
        this.button_scrivi.setOnMouseClicked(e -> modalita.set("scrivi"));
        this.button_tutte.setOnMouseClicked(e -> modalita.set("tutte"));
        this.button_ricevute.setOnMouseClicked(e -> modalita.set("ricevute"));
        this.button_inviate.setOnMouseClicked(e -> modalita.set("inviate"));
    }

    // Entrambi i metodi saranno lanciati non appena avranno acquisito il click del loro corrispettivo listener (
    // i listener dei button nel mio caso )
    public void mostra_scrivi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Scrivi_Mail_View.fxml"));
            Scrivi_Mail_Controller controller = new Scrivi_Mail_Controller();
            loader.setController(controller);
            Node load = loader.load();
            this.anchor_pane_body.getChildren().setAll(load);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void mostra_leggi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Leggi_Mail_View.fxml"));
            Leggi_Mail_Controller controller = new Leggi_Mail_Controller();
            loader.setController(controller);
            Node load = loader.load();
            this.anchor_pane_body.getChildren().setAll(load);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ScheduledFuture<?> aggiornamento_programmato;

    public void inizializza_aggiornamento() {
        ScheduledExecutorService servizio_schedulato = Executors.newScheduledThreadPool(1);
        Runnable runnable = () -> {
            Platform.runLater(() -> {
                if (mailbox.online()) {
                    int count = mailbox.aggiorna();
                    if (count > 0) {
                        Notifications.create().title("Nuove Mail").text("Hai ricevuto " + count + " nuove Email").hideAfter(new Duration(3000)).showInformation();
                    }
                    System.out.printf("Hai ottenuto {[%d]} nuovi messaggi\n", count);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("ATTENZIONE");
                    alert.setHeaderText("Server Chiuso");
                    alert.setContentText("Impossibile contattare il server");
                    servizio_schedulato.shutdown();
                    alert.showAndWait();
                }
            });
        };
        aggiornamento_programmato = servizio_schedulato.scheduleAtFixedRate(runnable, 1, 5, TimeUnit.SECONDS);
    }

}
