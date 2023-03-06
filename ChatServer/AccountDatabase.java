import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AccountDatabase {
    private static ArrayList<Account> accounts;
    private static HashMap<Account, String> passwordTable;
    private static final File file = new File("accounts.dat");

    @SuppressWarnings("unchecked")
    public static void init() {
        // read data from file
        if (file.exists() && file.length() != 0) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                accounts = (ArrayList<Account>) ois.readObject();
                passwordTable = (HashMap<Account, String>) ois.readObject();
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
                accounts = new ArrayList<>();
                passwordTable = new HashMap<>();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveDataToFile() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(accounts);
            oos.writeObject(passwordTable);
            System.out.println("Account data saved");
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

    public static Account getAccount(String userName) {
        Account account = new Account(userName);
        if (!accounts.contains(account)) {
            return null; // account doesn't exist
        } else {
            return accounts.get(accounts.indexOf(account));
        }
    }

    public static void addAccount(Account account, String password) {
        assert !accounts.contains(account);
        accounts.add(account);
        passwordTable.put(account, password);
    }

    public static boolean isPasswordCorrect(Account account, String password) {
        return password.equals(passwordTable.get(account));
    }
}
