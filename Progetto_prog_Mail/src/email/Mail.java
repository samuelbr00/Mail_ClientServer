package email;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Mail implements Serializable {

    private int id;
    private String mittente;
    private List<String> destinatari;
    private String subject;
    private String testo;
    private Date data;
    private String categoria;//inviata oppure ricevuta
    private boolean letta;

    public Mail(String mittente, List<String> destinatari, String subject, String testo, Date data, String categoria, boolean letta) {
        this.mittente = mittente;
        this.destinatari = destinatari;
        this.subject = subject;
        this.testo = testo;
        this.data = data;
        this.categoria = categoria;
        this.letta = letta;
        this.id = Objects.hash(this.mittente, this.destinatari, this.subject, this.testo, this.data, this.categoria);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMittente() {
        return this.mittente;
    }

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public List<String> getDestinatari() {
        return this.destinatari;
    }

    public void setDestinatari(List<String> destinatari) {
        this.destinatari = destinatari;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTesto() {
        return this.testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Date getData() {
        return this.data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getCategoria() {
        return this.categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean getLetta() {
        return this.letta;
    }

    public void setLetta(boolean letta) {
        this.letta = letta;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "id=" + id +
                ", mittente='" + mittente + '\'' +
                ", destinatari=" + destinatari +
                ", subject='" + subject + '\'' +
                ", testo='" + testo + '\'' +
                ", data=" + data +
                ", categoria='" + categoria + '\'' +
                ", letta=" + letta +
                '}';
    }
}
