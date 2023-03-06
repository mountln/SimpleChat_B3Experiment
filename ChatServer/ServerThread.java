import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread implements Runnable{
    private static int cnt = 0;
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ServerThread(Socket socket) {
        this.socket = socket;
        cnt++;
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean exitFlag = false;
        Account account = null;
        while (!exitFlag) {
            Message message = null;
            String messageSource = null;
            String messageDestination = null;
            String messageType = null;
            Object messageDetails = null;
            try {
                message = readMessageFromClient();
                messageSource = message.getSource();
                messageDestination = message.getDestination();
                messageType = message.getType();
                messageDetails = message.getDetails();
            } catch (NullPointerException e) {
                System.out.println("Offline: " + socket);
                if (account != null) {
                    AccountSystem.logout(account.getUserName());
                }
                exitFlag = true;
            }
            if (exitFlag) break;
            switch (messageType) {
                case "CreateAccountRequest":
                    String password = (String)messageDetails;
                    AccountSystem.createAccount(messageSource, password, this);
                    break;
                case "LoginRequest":
                    password = (String)messageDetails;
                    String loginResult = AccountSystem.login(messageSource, password, this);
                    if (loginResult.equals("Login successfully.")) account = AccountSystem.getAccount(messageSource);
                    break;
                case "AddFriendRequest":
                    String userNameOfContact = (String) messageDetails;
                    AccountSystem.addFriendToContacts(messageSource, userNameOfContact);
                    break;
                case "TextMessage":
                    ChatSystem.sendTextMessage(messageDestination, message);
                    break;
                case "Logout":
                    AccountSystem.logout(messageSource);
                    exitFlag = true;
                    break;
                default:
                    System.out.println("Received unknown message: " + message);
                    break;
            }
        }
        try {
            socket.close();
            in.close();
            out.close();
            cnt--;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getCnt() {
        return cnt;
    }

    private Message readMessageFromClient() {
        Message newMessage = null;
        try {
            newMessage = (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Cannot read message: " + socket);
        }
        return newMessage;
    }

    public void sendMessageToClient(Message message) {
        try {
            out.writeObject(message);
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ServerThread{" +
                "socket=" + socket +
                '}';
    }
}
