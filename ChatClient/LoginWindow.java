import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginWindow {
    private final JFrame frame = new JFrame("Login");
    private final JLabel userLabel = new JLabel("User Name:");
    private final JLabel passwordLabel = new JLabel("Password:");
    private final JTextField userText = new JTextField(20);
    private final JPasswordField passwordText = new JPasswordField(20);
    private final JButton loginButton = new JButton("Log In");
    private final JButton createAccountButton = new JButton("Create Account");

    public LoginWindow() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(370, 120));
        panel.setLayout(null);
        if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
            setFont();
        }
        userLabel.setBounds(20,20,80,25);
        panel.add(userLabel);
        userText.setBounds(100,20,250,25);
        panel.add(userText);
        passwordLabel.setBounds(20,50,80,25);
        panel.add(passwordLabel);
        passwordText.setBounds(100,50,250,25);
        panel.add(passwordText);
        loginButton.setBounds(240, 80, 110, 25);
        panel.add(loginButton);
        createAccountButton.setBounds(100, 80, 130, 25);
        panel.add(createAccountButton);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void setFont() {
        Font font = new Font("Consolas", Font.PLAIN, 11);
        userLabel.setFont(font);
        passwordLabel.setFont(font);
        createAccountButton.setFont(font);
        loginButton.setFont(font);
    }

    public JFrame getFrame() {
        return frame;
    }

    public String getUserName() {
        return userText.getText();
    }

    public char[] getPassword() {
        return passwordText.getPassword();
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getCreateAccountButton() {
        return createAccountButton;
    }

    public void showInfo(String info) {
        JOptionPane.showMessageDialog(null, info, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }
}