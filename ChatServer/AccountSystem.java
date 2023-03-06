import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class AccountSystem {
    private static Hashtable<Account, ServerThread> onlineAccountThreadTable;

    public static void init() {
        AccountDatabase.init();
        onlineAccountThreadTable = new Hashtable<>();
    }

    public static void close() {
        AccountDatabase.saveDataToFile();
    }

    public static void createAccount(String userName, String password, ServerThread serverThread) {
        Account account = new Account(userName);
        String result;
        if (AccountDatabase.getAccount(userName) != null) {
            result = "Account already exists.";
        } else {
            result = "Created account successfully.";
            AccountDatabase.addAccount(account, password);
            AccountDatabase.saveDataToFile();
            System.out.println("Account created: " + userName);
        }
        serverThread.sendMessageToClient(new Message("Server", userName,
                "CreateAccountResult", result));
    }

    public static String login(String userName, String password, ServerThread serverThread) {
        Account account = AccountDatabase.getAccount(userName);
        String result;
        if (account == null) {
            result = "Account doesn't exist.";
            serverThread.sendMessageToClient(new Message("Server", userName,
                    "LoginResult", result));
        } else if (onlineAccountThreadTable.containsKey(account)) {
            result = "Account already logged in.";
            serverThread.sendMessageToClient(new Message("Server", userName,
                    "LoginResult", result));
        } else {
            if (AccountDatabase.isPasswordCorrect(account, password)) {
                result = "Login successfully.";
                serverThread.sendMessageToClient(new Message("Server", userName,
                        "LoginResult", result));
                onlineAccountThreadTable.put(account, serverThread);
                updateContacts(account);
                sendOfflineMessages(account);
                updateOnlineStateOfContacts(account);
                for (Account accountOfFriend : account.getContacts()) {
                    updateOnlineStateOfContacts(accountOfFriend);
                }
                System.out.println("Logged In: " + account + " : " + serverThread);
                System.out.println("Amount of online users: " + onlineAccountThreadTable.size());
            } else {
                result = "Incorrect password.";
                serverThread.sendMessageToClient(new Message("Server", userName,
                        "LoginResult", result));
            }
        }
        return result;
    }

    public static void addFriendToContacts(String userName, String userNameOfFriend) {
        Account account = AccountDatabase.getAccount(userName);
        Account accountOfFriend = AccountDatabase.getAccount(userNameOfFriend);
        ServerThread serverThread = getThreadOfAccount(account);
        String result;
        assert account != null;
        HashSet<Account> contacts = account.getContacts();
        if (accountOfFriend == null) {
            result = "The user doesn't exist.";
        } else if (contacts.contains(accountOfFriend)) {
            result = "The user has been added to contacts.";
        } else {
            contacts.add(accountOfFriend);
            account.setContacts(contacts);
            updateContacts(account);
            updateOnlineStateOfContacts(account);
            HashSet<Account> contactsOfFriend = accountOfFriend.getContacts();
            contactsOfFriend.add(account);
            accountOfFriend.setContacts(contactsOfFriend);
            updateContacts(accountOfFriend);
            AccountDatabase.saveDataToFile();
            result = "Added to contacts successfully.";
        }
        serverThread.sendMessageToClient(new Message("Server", userName,
                "AddFriendResult", result));
    }

    public static void logout(String userName) {
        Account account = AccountDatabase.getAccount(userName);
        assert account != null;
        onlineAccountThreadTable.remove(account);
        for (Account accountOfFriend : account.getContacts()) {
            updateOnlineStateOfContacts(accountOfFriend);
        }
        System.out.println("Logged Out: " + userName);
    }

    private static void updateContacts(Account account) {
        ServerThread serverThread = onlineAccountThreadTable.get(account);
        HashSet<Account> contacts = account.getContacts();
        HashSet<String> contactsByString = new HashSet<>();
        for (Account accountOfContact : contacts) {
            contactsByString.add(accountOfContact.getUserName());
        }
        if (onlineAccountThreadTable.containsKey(account)) {
            serverThread.sendMessageToClient(new Message("Server", account.getUserName(),
                    "UpdateContacts", contactsByString));
        }
    }

    private static void sendOfflineMessages(Account account) {
        ServerThread serverThread = onlineAccountThreadTable.get(account);
        serverThread.sendMessageToClient(new Message("Server", account.getUserName(),
                "OfflineMessages", account.getOfflineMessages()));
        account.setOfflineMessages(new ArrayList<>());
    }

    private static void updateOnlineStateOfContacts(Account account) {
        HashSet<String> onlineAccountSet = new HashSet<>();
        for (Account accountOfFriend : account.getContacts()) {
            if (onlineAccountThreadTable.containsKey(accountOfFriend)) {
                onlineAccountSet.add(accountOfFriend.getUserName());
            }
        }
        ServerThread serverThread = onlineAccountThreadTable.get(account);
        if (onlineAccountThreadTable.containsKey(account)) {
            serverThread.sendMessageToClient(new Message("Server", account.getUserName(),
                    "UpdateOnlineStateOfContacts", onlineAccountSet));
        }
    }

    public static void addOfflineMessage(Account account, Message message) {
        ArrayList<Message> offlineMessages = account.getOfflineMessages();
        offlineMessages.add(message);
        account.setOfflineMessages(offlineMessages);
    }

    public static Account getAccount(String userName) {
        return AccountDatabase.getAccount(userName);
    }

    public static ServerThread getThreadOfAccount(Account account) {
        return onlineAccountThreadTable.get(account);
    }

    public static boolean isOnline(Account account) {
        return onlineAccountThreadTable.containsKey(account);
    }
}
