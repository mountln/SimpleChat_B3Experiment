import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Friend implements Serializable, Comparable<Friend> {
    private final String userName;
    private boolean online;
    private int amountOfUnreadMessages;
    private final ArrayList<String[]> textMessages;

    public Friend(String userName) {
        this.userName = userName;
        this.online = false;
        this.amountOfUnreadMessages = 0;
        this.textMessages = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getAmountOfUnreadMessages() {
        return amountOfUnreadMessages;
    }

    public void setAmountOfUnreadMessages(int amountOfUnreadMessages) {
        this.amountOfUnreadMessages = amountOfUnreadMessages;
    }

    public ArrayList<String[]> getTextMessages() {
        return textMessages;
    }

    public void addTextMessage(String source, String textMessage) {
        textMessages.add(new String[]{source, textMessage});
        amountOfUnreadMessages++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return userName.equals(friend.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

    @Override
    public String toString() {
        return "(" + amountOfUnreadMessages + ")" +
                userName + ": " + (online ? "Online" : "Offline");
    }

    @Override
    public int compareTo(Friend o) {
        Friend f = o;
        if (this.online == f.online) {
            if (f.amountOfUnreadMessages == this.amountOfUnreadMessages) {
                return this.getUserName().compareTo(f.getUserName());
            } else {
                return f.amountOfUnreadMessages - this.amountOfUnreadMessages;
            }
        } else {
            return this.online ? -1 : 1;
        }
    }
}
