import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserStore {

    private final File file;
    private final List<User> users = new ArrayList<>();

    public UserStore(String filePath) {
        this.file = new File(filePath);
        load();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User findByEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public User findByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    /** Register a new user, returns the created user, or null if email/username exists. */
    public User register(User user) {
        if (findByEmail(user.getEmail()) != null) {
            return null; // email already used
        }
        if (findByUsername(user.getUsername()) != null) {
            return null; // username already used
        }
        users.add(user);
        save();
        return user;
    }

    /** Login using email + password. Returns user if correct, else null. */
    public User login(String email, String password) {
        User existing = findByEmail(email);
        if (existing == null) return null;
        if (!existing.getPassword().equals(password)) return null;
        return existing;
    }

    private void load() {
        users.clear();
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    users.add(User.fromCsvRow(line));
                } catch (Exception e) {
                    System.err.println("Skipping bad user row: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read users file: " + e.getMessage());
        }
    }

    public void save() {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
        } catch (Exception ignored) {}

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("# email;username;password;fullName;weightKg;heightCm;goal");
            bw.newLine();
            for (User u : users) {
                bw.write(u.toCsvRow());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not write users file: " + e.getMessage());
        }
    }
}
