package server.console;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import server.logger.Log;
import server.logger.Logger;
import server.Multi_Threaded_Server;

import java.net.URL;
import java.util.ResourceBundle;

public class Console_View implements Initializable {

    @FXML
    private ListView<Log> list_view;       //lista di log che appare nella ListView

    Logger logger;
    public static Multi_Threaded_Server server;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        server = new Multi_Threaded_Server(6789);

        Thread thread = new Thread(server);

        logger = server.getLogger();  //ottiene il Logger del server

        list_view.itemsProperty().bind(logger.listPropertyProperty());//collega la nostra lista con la lista della classe Logger

        thread.start();
    }
}
