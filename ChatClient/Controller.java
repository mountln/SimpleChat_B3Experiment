import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Controller {
    private static LoginWindow loginWindow;
    private static ChatsWindow chatsWindow;
    private static ChatClient client;

    public static void main(String[] args) {
        loginWindow = new LoginWindow();
        chatsWindow = new ChatsWindow();
        client = new ChatClient("127.0.0.1", 8080);
        new ViewActionListener().start();
        new ServerEventListener().start();
    }

    static void createAccountResponse() {
        String result = client.createAccount(loginWindow.getUserName(), loginWindow.getPassword());
        loginWindow.showInfo(result);
    }

    static void loginResponse() {
        String userName = loginWindow.getUserName();
        String result = client.login(userName, loginWindow.getPassword());
        loginWindow.showInfo(result);
        if (result.equals("Login successfully.")) {
            loginWindow.getFrame().dispose();
            chatsWindow.getFrame().setTitle("Chat: " + userName);
            chatsWindow.getFrame().setVisible(true);
            chatsWindow.updateContacts(new ArrayList<>(client.getContacts().values()));
        }
    }

    static void addFriendResponse() {
        String userNameOfFriend = chatsWindow.getInput("Friend's username: ");
        client.addFriend(userNameOfFriend);
    }

    static void sendTextMessageResponse() {
        if (chatsWindow.getContactsBox().getSelectedValue() == null) {
            chatsWindow.showInfo("Please select a friend to send the message.");
            chatsWindow.textBoxClear();
            return;
        }
        String textMessage = chatsWindow.getTextOfTextBox();
        String destination = chatsWindow.getContactsBox().getSelectedValue().getUserName();
        client.sendTextMessage(destination, textMessage);
        chatsWindow.textBoxClear();
        chatsWindow.updateMessagesBox();
    }

    static void contactSelectionResponse() {
        chatsWindow.updateMessagesBox();
        client.getContacts().get(chatsWindow.getContactsBox().getSelectedValue()
                .getUserName()).setAmountOfUnreadMessages(0);
        chatsWindow.updateContacts(new ArrayList<>(client.getContacts().values()));
    }

    private static class ServerEventListener extends Thread {
        @Override
        public void run() {
            while (client.getUserName() == null) {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (true) {
                try {
                    String event = client.handleEvent();
                    chatsWindow.updateContacts(new ArrayList<>(client.getContacts().values()));
                    if (event.contains("Received a message.")) {
                        chatsWindow.updateMessagesBox();
                    } else if (event.contains("AddFriendResult")) {
                        chatsWindow.showInfo(event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private static class ViewActionListener extends Thread {
        @Override
        public void run() {
            loginWindow.getCreateAccountButton().addActionListener(e -> createAccountResponse());
            loginWindow.getLoginButton().addActionListener(e -> loginResponse());
            chatsWindow.getAddFriendButton().addActionListener(e -> addFriendResponse());
            chatsWindow.getSendButton().addActionListener(e -> sendTextMessageResponse());
            chatsWindow.getContactsBox().addListSelectionListener(e -> contactSelectionResponse());
            chatsWindow.getFrame().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Controller.client.logout();
                    super.windowClosing(e);
                }
            });
        }
    }
}

