public class User {
    private String email;
    private String username;
    private String password;   // plain text (okay for school project)
    private String fullName;
    private double bodyWeightKg;
    private double heightCm;
    private String goal;

    public User(String email,
                String username,
                String password,
                String fullName,
                double bodyWeightKg,
                double heightCm,
                String goal) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.bodyWeightKg = bodyWeightKg;
        this.heightCm = heightCm;
        this.goal = goal;
    }

    public User(String email, String username, String password) {
        this(email, username, password, "", 0.0, 0.0, "");
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public double getBodyWeightKg() {
        return bodyWeightKg;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public String getGoal() {
        return goal;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBodyWeightKg(double bodyWeightKg) {
        this.bodyWeightKg = bodyWeightKg;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    // ===== CSV helpers =====
    // email;username;password;fullName;weightKg;heightCm;goal
    public String toCsvRow() {
        return escape(email) + ";" +
               escape(username) + ";" +
               escape(password) + ";" +
               escape(fullName) + ";" +
               bodyWeightKg + ";" +
               heightCm + ";" +
               escape(goal);
    }

    public static User fromCsvRow(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 7) {
            throw new IllegalArgumentException("Invalid user row: " + line);
        }
        String email = unescape(parts[0]);
        String username = unescape(parts[1]);
        String password = unescape(parts[2]);
        String fullName = unescape(parts[3]);
        double weight = Double.parseDouble(parts[4].isEmpty() ? "0" : parts[4]);
        double height = Double.parseDouble(parts[5].isEmpty() ? "0" : parts[5]);
        String goal = unescape(parts[6]);
        return new User(email, username, password, fullName, weight, height, goal);
    }

    // Simple escaping so we can safely store ; and \ in text
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(";", "\\;");
    }

    private static String unescape(String s) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (escaping) {
                result.append(c);
                escaping = false;
            } else if (c == '\\') {
                escaping = true;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
