import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ChatClient {
    private String userName = null;
    private HashMap<String, Friend> contacts;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ChatClient(String host, int port) {
        this.contacts = new HashMap<>();
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToFile() {
        File file = new File(this.userName + ".dat");
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(contacts);
            System.out.println("Contacts data saved");
        } catch (IOException e) {
            System.out.println("Failed to save data");
        } finally {
            try {
                assert oos != null;
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void readDataFromFile() {
        File file = new File(this.userName + ".dat");
        if (file.exists() && file.length() != 0) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                contacts = (HashMap<String, Friend>) ois.readObject();
            } catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            } finally {
                try {
                    assert ois != null;
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                if(file.createNewFile()) {
                    System.out.println("Created new data file.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessageToServer(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message getMessageFromServer() {
        Message newMessage = null;
        try {
            newMessage = (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newMessage;
    }

    @SuppressWarnings("unchecked")
    public String handleEvent() {
        Message message;
        String messageType;
        Object messageDetails;
        try {
            message = getMessageFromServer();
            messageType = message.getType();
            messageDetails = message.getDetails();
        } catch (NullPointerException e) {
            return "Offline: " + socket;
        }
        switch (messageType) {
            case "AddFriendResult":
                return "AddFriendResult: " + messageDetails;
            case "UpdateContacts":
                updateContacts((HashSet<String>) messageDetails);
                updateOnlineStateOfContacts((HashSet<String>) messageDetails);
                return "Updated contacts.";
            case "OfflineMessages":
                updateOfflineMessages((ArrayList<Message>) messageDetails);
                return "Received offline messages.";
            case "UpdateOnlineStateOfContacts":
                updateOnlineStateOfContacts((HashSet<String>) messageDetails);
                return "Updated online state.";
            case "TextMessage":
                receiveTextMessage(message);
                return "Received a message.";
            default:
                return "Unknown message: " + message;
        }
    }

    public String createAccount(String userName, char[] password) {
        if (checkUserNameAndPassword(userName, password) != null) {
            return checkUserNameAndPassword(userName, password);
        } else {
            sendMessageToServer(new Message(userName, "Server",
                    "CreateAccountRequest", Arrays.toString(password)));
            Message messageFromServer = getMessageFromServer();
            if (messageFromServer.getType().equals("CreateAccountResult")) {
                return (String) messageFromServer.getDetails();
            } else {
                return "Unknown exception. ";
            }
        }
    }

    public String login(String userName, char[] password) {
        if (checkUserNameAndPassword(userName, password) != null) {
            return checkUserNameAndPassword(userName, password);
        } else {
            sendMessageToServer(new Message(userName, "Server",
                    "LoginRequest", Arrays.toString(password)));
            Message messageFromServer = getMessageFromServer();
            if (messageFromServer.getType().equals("LoginResult")) {
                if (messageFromServer.getDetails().equals("Login successfully.")) {
                    this.userName = userName;
                    readDataFromFile();
                }
                return (String) messageFromServer.getDetails();
            } else {
                System.out.println(messageFromServer);
                return "Unknown exception. ";
            }
        }
    }

    public void addFriend(String userNameOfFriend) {
        sendMessageToServer(new Message(userName, "Server",
                "AddFriendRequest", userNameOfFriend));
    }

    public void updateContacts(HashSet<String> contacts) {
        for (String userNameOfFriend : contacts) {
            if (!this.contacts.containsKey(userNameOfFriend)) {
                this.contacts.put(userNameOfFriend, new Friend(userNameOfFriend));
            }
        }
        saveDataToFile();
    }

    public void updateOnlineStateOfContacts(HashSet<String> userNameOfOnlineFriends) {
        for (Friend friend : this.contacts.values()) {
            friend.setOnline(userNameOfOnlineFriends.contains(friend.getUserName()));
        }
    }

    public void updateOfflineMessages(ArrayList<Message> offlineMessages) {
        for (Message offlineMessage : offlineMessages) {
            contacts.get(offlineMessage.getSource())
                    .addTextMessage(offlineMessage.getSource(), (String) offlineMessage.getDetails());
        }
    }

    public void sendTextMessage(String userNameOfDestination, String textMessage) {
        if (!userNameOfDestination.equals(userName)) {
            sendMessageToServer(new Message(userName, userNameOfDestination, "TextMessage", textMessage));
        } else {
            contacts.get(userNameOfDestination).addTextMessage(userName, textMessage);
        }
        contacts.get(userNameOfDestination).addTextMessage(userName, textMessage);
        contacts.get(userNameOfDestination).setAmountOfUnreadMessages(0);
    }

    public void receiveTextMessage(Message message) {
        String source = message.getSource();
        String textMessage = (String) message.getDetails();
        contacts.get(source).addTextMessage(source, textMessage);
    }

    public void logout() {
        sendMessageToServer(new Message(userName, "Server", "Logout", "null"));
        saveDataToFile();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return this.userName;
    }

    public HashMap<String, Friend> getContacts() {
        return contacts;
    }

    private String checkUserNameAndPassword(String userName, char[] password) {
        if (userName.length() >= 20) {
            return "User name is too long.";
        } else if (userName.length() <= 1) {
            return "User name is too short.";
        } else if (password.length >= 20) {
            return "Password is too long.";
        } else if (password.length <= 1){
            return "Password is too short.";
        } else {
            return null;
        }
    }


}
