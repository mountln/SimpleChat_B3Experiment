import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ChatsWindow {
    private final JFrame frame;
    private final JTextPane messagesBox;
    private final JTextArea textBox;
    private final JList<Friend> contactsBox;
    private final DefaultListModel<Friend> contactsModel;
    private final JButton sendButton;
    private final JButton addFriendButton;

    public ChatsWindow() {
        frame = new JFrame("ChatsWindow");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(600, 500));

        messagesBox = new JTextPane();
        messagesBox.setEditable(false);
        messagesBox.setBorder(new EmptyBorder(0, 2, 0, 0));
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(messagesBox);
        JScrollPane messageScrollPane = new JScrollPane(messagePanel);
        messageScrollPane.setBounds(5, 5, 395, 295);
        messageScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(messageScrollPane);

        textBox = new JTextArea(10, 30);
        JScrollPane textBoxScrollPane = new JScrollPane(textBox);
        textBox.setLineWrap(true);
        textBoxScrollPane.setBounds(5, 305, 395, 165);
        panel.add(textBoxScrollPane);

        contactsModel = new DefaultListModel<>();
        contactsBox = new JList<>(contactsModel);
        contactsBox.setLayoutOrientation(JList.VERTICAL);
        contactsBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsBox.setCellRenderer(new CellRenderer());
        JScrollPane contractScrollPane = new JScrollPane(contactsBox);
        contractScrollPane.setBounds(405, 5, 190, 465);
        panel.add(contractScrollPane);

        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(5, 475, 110, 20);
        clearButton.addActionListener(e -> textBox.setText(""));
        panel.add(clearButton);

        sendButton = new JButton("Send");
        sendButton.setBounds(290, 475, 110, 20);
        panel.add(sendButton);

        addFriendButton = new JButton("Add Friend");
        addFriendButton.setBounds(405, 475, 190, 20);
        panel.add(addFriendButton);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    public JFrame getFrame() {
        return frame;
    }

    public String getTextOfTextBox() { return textBox.getText(); }

    public void textBoxClear() {
        textBox.setText("");
    }

    public JList<Friend> getContactsBox() {
        return contactsBox;
    }

    public JButton getAddFriendButton() {
        return addFriendButton;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public void showInfo(String info) {
        JOptionPane.showMessageDialog(null, info, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public String getInput(String message) {
        return JOptionPane.showInputDialog(message);
    }

    public void updateContacts(ArrayList<Friend> contacts) {
        Collections.sort(contacts);
        Friend selectedFriend = contactsBox.getSelectedValue();
        for (int i = 0; i < contacts.size(); i++) {
            if (contactsModel.size() <= i) {
                contactsModel.add(i, contacts.get(i));
            } else {
                contactsModel.set(i, contacts.get(i));
            }
        }
        contactsBox.setSelectedValue(selectedFriend, true);
    }

    public void updateMessagesBox() {
        Friend selectedFriend = contactsBox.getSelectedValue();
        if (selectedFriend == null) {
            messagesBox.removeAll();
            return;
        }
        String userNameOfFriend = selectedFriend.getUserName();
        ArrayList<String[]> messages = selectedFriend.getTextMessages();
        SimpleAttributeSet attributeSetForNameOfFriend = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSetForNameOfFriend, Color.gray);
        StyleConstants.setBold(attributeSetForNameOfFriend, true);

        SimpleAttributeSet attributeSetForNameOfUser = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSetForNameOfUser, Color.blue);
        StyleConstants.setBold(attributeSetForNameOfUser, true);

        SimpleAttributeSet attributeSetForText = new SimpleAttributeSet();

        SimpleAttributeSet attributeSetForDivider = new SimpleAttributeSet();
        StyleConstants.setFontSize(attributeSetForDivider, 3);

        Document document = messagesBox.getDocument();

        try {
            document.remove(0, document.getLength());
            for (String[] message : messages) {
                if (message[0].equals(userNameOfFriend)) {
                    document.insertString(document.getLength(), message[0] + ":\n", attributeSetForNameOfFriend);
                } else {
                    document.insertString(document.getLength(), message[0] + ":\n", attributeSetForNameOfUser);
                }
                document.insertString(document.getLength(), message[1] + "\n", attributeSetForText);
                document.insertString(document.getLength(), "\n", attributeSetForDivider);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        messagesBox.setCaretPosition(document.getLength());
    }

    private static class CellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Friend friend = (Friend) value;
            ImageIcon onlineIcon = new ImageIcon("online_mark.png");
            Image onlineImage = onlineIcon.getImage().getScaledInstance(8, 8, Image.SCALE_DEFAULT);
            onlineIcon.setImage(onlineImage);
            ImageIcon offlineIcon = new ImageIcon("offline_mark.png");
            Image offlineImage = offlineIcon.getImage().getScaledInstance(8, 8, Image.SCALE_DEFAULT);
            offlineIcon.setImage(offlineImage);

            if (friend.getAmountOfUnreadMessages() == 0) {
                setText(friend.getUserName());
            } else {
                setText("(" + friend.getAmountOfUnreadMessages() + ")" + friend.getUserName());
            }

            if (!friend.isOnline()) {
                setForeground(Color.gray);
                setIcon(offlineIcon);
            } else {
                setForeground(Color.black);
                setIcon(onlineIcon);
            }
            setBackground(Color.white);

            if (isSelected) {
                setForeground(Color.white);
                setBackground(Color.blue);
            }

            return this;
        }
    }
}
