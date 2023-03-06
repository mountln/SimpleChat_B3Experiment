public class ChatSystem {

    public static void sendTextMessage(String userNameOfDestination, Message message) {
        Account accountOfDestination = AccountSystem.getAccount(userNameOfDestination);
        if (AccountSystem.isOnline(accountOfDestination)) {
            ServerThread serverThread = AccountSystem.getThreadOfAccount(accountOfDestination);
            serverThread.sendMessageToClient(message);
        } else {
            AccountSystem.addOfflineMessage(accountOfDestination, message);
        }
    }
}
