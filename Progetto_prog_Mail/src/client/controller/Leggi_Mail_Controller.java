package client.controller;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import email.Mail;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static client.controller.Main_Controller.mailbox;
import static client.controller.Main_Controller.modalita;

public class Leggi_Mail_Controller implements Initializable {

    @FXML
    private AnchorPane main_anchor_pane;
    @FXML
    private ListView<Mail> list_view_mail;
    @FXML
    private Button button_rispondi;
    @FXML
    private Button button_rispondi_a_tutti;
    @FXML
    private Button button_inoltra;
    @FXML
    private Button button_elimina;
    @FXML
    private TextField text_mittente;
    @FXML
    private TextField text_destinatario;
    @FXML
    private TextField text_soggetto;
    @FXML
    private TextArea text_testo_mail;

    private FilteredList<Mail> filteredList;
    private Mail selected_mail;

    public Leggi_Mail_Controller() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.inizializza_dati();
        this.inizializza_view();
        this.inizializza_listeners();
    }

    public void inizializza_dati() {

        this.filteredList = mailbox.getFilteredList();
    }

    public void inizializza_view() {
        this.list_view_mail.setItems(this.filteredList);
        list_view_mail.setCellFactory(param -> new ListCell<Mail>() {
            @Override
            protected void updateItem(Mail item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getMittente() + "\n" + item.getData() + "\n" + item.getSubject());
                }
            }
        });
    }


    public void inizializza_listeners() {
        this.list_view_mail.setOnMouseClicked(e -> this.mostra());
        this.button_rispondi.setOnMouseClicked(e -> this.rispondi(this.selected_mail));
        this.button_rispondi_a_tutti.setOnMouseClicked(e -> this.rispondi_a_tutti(this.selected_mail));
        this.button_inoltra.setOnMouseClicked(e -> this.inoltra(this.selected_mail));
        this.button_elimina.setOnMouseClicked(e -> this.elimina(this.selected_mail));
    }

    public void mostra() {
        this.selected_mail = list_view_mail.getSelectionModel().getSelectedItem();
        if (this.selected_mail != null) {
            if (!this.selected_mail.getLetta()) {
                mailbox.letta(this.selected_mail);
            }
            this.text_mittente.setText(this.selected_mail.getMittente());
            this.text_destinatario.setText(this.selected_mail.getDestinatari().toString());
            this.text_soggetto.setText(this.selected_mail.getSubject());
            this.text_testo_mail.setText(this.selected_mail.getTesto());
            this.button_elimina.setDisable(false);
            this.button_rispondi.setDisable(false);
            this.button_rispondi_a_tutti.setDisable(false);
            this.button_inoltra.setDisable(false);
        }
    }

    public void rispondi(Mail mail) {
        try {
            modalita.set("rispondi");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Scrivi_Mail_View.fxml"));
            Scrivi_Mail_Controller controller = new Scrivi_Mail_Controller(mail);
            loader.setController(controller);
            Node node = loader.load();
            this.main_anchor_pane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void elimina(Mail mail) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Elimina Mail");
        alert.setContentText("Sei sicuro di voler eliminare la mail?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            mailbox.delete(mail);
            this.text_mittente.setText("");
            this.text_destinatario.setText("");
            this.text_soggetto.setText("");
            this.text_testo_mail.setText("");
            this.button_elimina.setDisable(true);
            this.button_rispondi.setDisable(true);
            this.button_rispondi_a_tutti.setDisable(true);
            this.button_inoltra.setDisable(true);
            Notifications.create().title("Mail Eiminata").text("Mail eliminata con successo").hideAfter(new Duration(3000)).showInformation();
        }
    }

    public void inoltra(Mail mail) {
        try {
            modalita.set("inoltra");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Scrivi_Mail_View.fxml"));
            Scrivi_Mail_Controller controller = new Scrivi_Mail_Controller(mail);
            loader.setController(controller);
            Node node = loader.load();
            this.main_anchor_pane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rispondi_a_tutti(Mail mail) {
        try {
            modalita.set("rispondi_a_tutti");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Scrivi_Mail_View.fxml"));
            Scrivi_Mail_Controller controller = new Scrivi_Mail_Controller(mail);
            loader.setController(controller);
            Node node = loader.load();
            this.main_anchor_pane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
