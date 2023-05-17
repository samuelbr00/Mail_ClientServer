package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import email.Mail;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static client.controller.Main_Controller.mailbox;
import static client.controller.Main_Controller.modalita;

public class Scrivi_Mail_Controller implements Initializable {

    @FXML
    private Button button_invia;
    @FXML
    private TextField text_mittente;
    @FXML
    private TextField text_destinatario;
    @FXML
    private TextField text_soggetto;
    @FXML
    private TextArea text_testo_mail;

    private Mail mail;

    private static final String EMAIL_PATTERN_VALIDATE =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public Scrivi_Mail_Controller() {

    }

    public Scrivi_Mail_Controller(Mail mail) {
        this.mail = mail;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.text_mittente.setText(mailbox.getUser());
        this.text_mittente.setEditable(false);
        this.button_invia.setOnMouseClicked(e -> send_mail());
        switch (modalita.get()) {
            case "rispondi": {
                this.text_destinatario.setText(mail.getMittente());
            }
            break;
            case "inoltra": {
                this.text_soggetto.setText(mail.getSubject());
                this.text_testo_mail.setText(mail.getTesto());
            }
            break;
            case "rispondi_a_tutti": {
                String s = mail.getMittente();
                List<String> list = mail.getDestinatari();
                for (int i = 0; i < list.size(); i++) {
                    s = s + "," + list.get(i);
                }
                this.text_destinatario.setText(s);
            }
            break;
        }
    }


    public void send_mail() {
        if (this.text_destinatario.getText().equals("")) {
            Notifications.create().title("Error").text("Email senza destinatario").hideAfter(new Duration(2000)).showInformation();
        } else {
            List<String> destinatari = new ArrayList<String>();
            String dest[] = this.text_destinatario.getText().split(",");
            for (int i = 0; i < dest.length; i++) {
                if (!dest[i].matches(EMAIL_PATTERN_VALIDATE)) {
                    Notifications.create().title("Error").text(dest[i] + " non è una mail valida.").hideAfter(new Duration(2000)).showInformation();
                } else {
                    destinatari.add(dest[i]);
                }
            }
            if (destinatari.size() == 0) {
                Notifications.create().title("Error").text("Destinatari non validi").hideAfter(new Duration(3000)).showInformation();
            } else {
                mailbox.send(new Mail(mailbox.getUser(), destinatari, this.text_soggetto.getText(), this.text_testo_mail.getText(), new Date(), "inviata", false));
                Notifications.create().title("Invia email").text("Email inviata correttamente").hideAfter(new Duration(3000)).showInformation();
            }
        }
    }

}
