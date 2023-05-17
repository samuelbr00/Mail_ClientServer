package client;

import client.controller.Main_Controller;
import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Scanner;

public class Client extends Application {

    public static Scanner tastiera = new Scanner(System.in);

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Inserisci email: ");
        String email = tastiera.nextLine();
        while (!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            System.out.println("Errore: non hai inserito una mail valida. Reinserie: ");
            email = tastiera.nextLine();
        }
        primaryStage.setTitle("Email Client " + email);
        AnchorPane root = new AnchorPane();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/Main_View.fxml"));
        Main_Controller controller = new Main_Controller(email);
        loader.setController(controller);
        root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
