import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatServer {
    private static ServerSocket server;
    private static final ArrayList<Socket> sockets = new ArrayList<>();

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        try {
            server = new ServerSocket(8080);
            AccountSystem.init();
            new CommandLineListener().start();
            while (true) {
                Socket socket = server.accept();
                sockets.add(socket);
                System.out.println("Connection accept: " + socket);
                new Thread(new ServerThread(socket)).start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static class CommandLineListener extends Thread {

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String command = scanner.next();
                if (command.equals("onlineUserCnt")) {
                    System.out.println("Online users: " + ServerThread.getCnt());
                } else if (command.equals("exit")) {
                    AccountSystem.close();
                    scanner.close();
                    try {
                        for (Socket socket : sockets) {
                            socket.close();
                        }
                        server.close();
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
