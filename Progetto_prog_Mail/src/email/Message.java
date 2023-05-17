package email;

import java.io.Serializable;

public class Message implements Serializable {

    private String comando;
    private Object oggetto;

    public Message(String comando, Object oggetto) {
        this.comando = comando;
        this.oggetto = oggetto;
    }

    public Message() {

    }

    public String getComando() {
        return this.comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public Object getOggetto() {
        return this.oggetto;
    }

    public void setOggetto(Object oggetto) {
        this.oggetto = oggetto;
    }
}
