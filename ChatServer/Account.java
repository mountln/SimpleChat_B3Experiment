import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Account implements Serializable {
    private final String userName;
    private HashSet<Account> contacts;
    private ArrayList<Message> offlineMessages;

    public Account(String userName) {
        this.userName = userName;
        this.contacts = new HashSet<>();
        this.offlineMessages = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public HashSet<Account> getContacts() {
        return contacts;
    }

    public void setContacts(HashSet<Account> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<Message> getOfflineMessages() {
        return offlineMessages;
    }

    public void setOfflineMessages(ArrayList<Message> offlineMessages) {
        this.offlineMessages = offlineMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return userName.equals(account.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

    @Override
    public String toString() {
        return "Account{" +
                "userName='" + userName + '\'' +
                '}';
    }
}
