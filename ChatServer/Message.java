public class Message implements java.io.Serializable {
    private final String source;
    private final String destination;
    private final String type;
    private final Object details;

    public Message(String source, String destination, String type, Object details) {
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.details = details;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getType() {
        return type;
    }

    public Object getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "Message{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
