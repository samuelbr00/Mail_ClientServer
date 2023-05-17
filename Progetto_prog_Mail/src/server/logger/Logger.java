package server.logger;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Logger {

    private static ObservableList<Log> logBook = FXCollections.observableArrayList();
    private ListProperty<Log> list_property = new SimpleListProperty<>(logBook);

    public static void debug(String tag, String message) {
        Platform.runLater(() -> logBook.add(0, new Log("DEBUG", tag, message)));
    }

    public static void info(String tag, String message) {

       // Platform.runLater(() enables parallel execution
        Platform.runLater(() -> logBook.add(0, new Log("INFO", tag, message)));
    }

    public static void error(String tag, String message) {
        Platform.runLater(() -> logBook.add(0, new Log("ERROR", tag, message)));
    }

    public ListProperty<Log> listPropertyProperty() {
        return list_property;
    }
}
