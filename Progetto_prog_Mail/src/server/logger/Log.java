package server.logger;

public class Log {

    private String livello;
    private String tag;
    private String message;

    @Override
    public String toString() {
        return "LOG { LIVELLO='" + this.livello + '\'' + ", TAG='" + this.tag + '\'' + ", MESSAGGIO:='" + this.message + '\'' + '}';
    }

    public Log(String level, String tag, String message) {
        this.livello = level;
        this.tag = tag;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
