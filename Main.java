import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FitnessDesktopApp app = new FitnessDesktopApp();
            app.start();
        });
    }
}

// ======================= CORE APP CLASS =======================

class FitnessDesktopApp {

    private JFrame frame;
    private List<AppUser> users;
    private AppUser currentUser;

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private DashboardPanel dashboardPanel;
    private WorkoutsPanel workoutsPanel;
    private ExercisesPanel exercisesPanel;
    private ProgressPanel progressPanel;
    private ProfilePanel profilePanel;

    private List<AppWorkoutPlan> workoutPlans;
    private List<AppExercise> exercises;

    public void start() {
        users = AppUserStore.loadUsers();
        workoutPlans = AppSampleData.createWorkoutPlans();
        exercises = AppSampleData.createExercises();

        frame = new JFrame("Peak Physique - Fitness Desktop App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);

        showLoginScreen();

        frame.setVisible(true);
    }

    // ---------- LOGIN / AUTH ----------

    private void showLoginScreen() {
        JPanel loginPanel = new LoginPanel(this);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(loginPanel);
        frame.revalidate();
        frame.repaint();
    }

    void onLoginSuccess(AppUser user) {
        this.currentUser = user;
        showMainApp();
    }

    void logout() {
        currentUser = null;
        showLoginScreen();
    }

    AppUser getCurrentUser() {
        return currentUser;
    }

    List<AppWorkoutPlan> getWorkoutPlans() {
        return workoutPlans;
    }

    List<AppExercise> getExercises() {
        return exercises;
    }

    List<AppUser> getUsers() {
        return users;
    }

    void saveUsers() {
        AppUserStore.saveUsers(users);
    }

    // ---------- MAIN APP LAYOUT ----------

    private void showMainApp() {
        if (currentUser == null) return;

        JPanel root = new JPanel(new BorderLayout());

        JPanel sidebar = createSidebar();
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        dashboardPanel = new DashboardPanel(this);
        workoutsPanel = new WorkoutsPanel(this);
        exercisesPanel = new ExercisesPanel(this);
        progressPanel = new ProgressPanel(this);
        profilePanel = new ProfilePanel(this);

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(workoutsPanel, "WORKOUTS");
        contentPanel.add(exercisesPanel, "EXERCISES");
        contentPanel.add(progressPanel, "PROGRESS");
        contentPanel.add(profilePanel, "PROFILE");

        root.add(sidebar, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(root);
        frame.revalidate();
        frame.repaint();

        showPage("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 700));
        sidebar.setBackground(new Color(15, 15, 30));

        JLabel title = new JLabel("PEAK PHYSIQUE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        sidebar.add(title);

        sidebar.add(createNavButton("Dashboard", "DASHBOARD"));
        sidebar.add(createNavButton("Workouts", "WORKOUTS"));
        sidebar.add(createNavButton("Exercises", "EXERCISES"));
        sidebar.add(createNavButton("Progress", "PROGRESS"));
        sidebar.add(createNavButton("Profile", "PROFILE"));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> logout());
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> showPage(cardName));
        return btn;
    }

    void showPage(String name) {
        if (dashboardPanel != null && name.equals("DASHBOARD")) {
            dashboardPanel.refresh();
        }
        if (progressPanel != null && name.equals("PROGRESS")) {
            progressPanel.refresh();
        }
        if (profilePanel != null && name.equals("PROFILE")) {
            profilePanel.refresh();
        }
        cardLayout.show(contentPanel, name);
    }
}

// ======================= LOGIN PANEL =======================

class LoginPanel extends JPanel {

    private final FitnessDesktopApp app;
    private JTextField emailField;
    private JPasswordField passwordField;

    // Allowed email domains – adjust as you like
    private static final String[] ALLOWED_DOMAINS = {
            "gmail.com",
            "bsu.edu"
    };

    LoginPanel(FitnessDesktopApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(10, 10, 20));

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 350));
        card.setBackground(new Color(25, 25, 40));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to Peak Physique");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        card.add(emailLabel);
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(passLabel);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));

        JButton loginBtn = new JButton("Login");
        JButton createBtn = new JButton("Create Account");

        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener(this::handleLogin);
        createBtn.addActionListener(this::handleCreateAccount);

        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(createBtn);

        add(card);
    }

    // Email domain restriction
    private boolean isValidEmailDomain(String email) {
        if (email == null) return false;
        email = email.trim();
        if (email.isEmpty()) return false;

        int at = email.indexOf('@');
        int lastAt = email.lastIndexOf('@');
        if (at <= 0 || at != lastAt) {
            return false;
        }

        String domain = email.substring(at + 1).toLowerCase();
        if (!domain.contains(".")) {
            return false;
        }

        for (String allowed : ALLOWED_DOMAINS) {
            if (domain.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter email and password.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<AppUser> users = app.getUsers();
        if (users == null) {
            users = new ArrayList<>();
        }

        AppUser user = null;
        for (AppUser u : users) {
            if (u.email.equalsIgnoreCase(email)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "No account found for this email.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!user.password.equals(password)) {
            JOptionPane.showMessageDialog(this,
                    "Incorrect password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        app.onLoginSuccess(user);
    }

    private void handleCreateAccount(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter email and password first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmailDomain(email)) {
            JOptionPane.showMessageDialog(this,
                    "Please use a valid email ending with: @gmail.com or @bsu.edu",
                    "Invalid Email",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<AppUser> users = app.getUsers();
        if (users == null) {
            users = new ArrayList<>();
        }

        for (AppUser u : users) {
            if (u.email.equalsIgnoreCase(email)) {
                JOptionPane.showMessageDialog(this,
                        "Account already exists for this email.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String name = JOptionPane.showInputDialog(this,
                "Enter your name:");
        if (name == null || name.trim().isEmpty()) return;

        String ageStr = JOptionPane.showInputDialog(this,
                "Age (years):");
        if (ageStr == null) return;

        String heightStr = JOptionPane.showInputDialog(this,
                "Height (cm):");
        if (heightStr == null) return;

        String weightStr = JOptionPane.showInputDialog(this,
                "Current weight (kg):");
        if (weightStr == null) return;

        String goal = JOptionPane.showInputDialog(this,
                "Goal (e.g., Lose fat, Build muscle, Get strong):");
        if (goal == null) return;

        try {
            int age = Integer.parseInt(ageStr.trim());
            double height = Double.parseDouble(heightStr.trim());
            double weight = Double.parseDouble(weightStr.trim());

            AppUser newUser = new AppUser(email, password, name.trim(), age, height, weight, goal.trim());
            users.add(newUser);
            app.saveUsers();

            JOptionPane.showMessageDialog(this,
                    "Account created! Logging you in...",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            app.onLoginSuccess(newUser);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for age, height and weight.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

// ======================= DASHBOARD PANEL =======================

class DashboardPanel extends JPanel {

    private final FitnessDesktopApp app;
    private JLabel greetingLabel;
    private JLabel goalLabel;
    private JLabel bmiLabel;
    private JLabel todayWorkoutLabel;

    DashboardPanel(FitnessDesktopApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(12, 12, 24));

        JPanel top = new JPanel(new GridLayout(1, 2));
        top.setBackground(new Color(12, 12, 24));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(new Color(12, 12, 24));
        left.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        greetingLabel = new JLabel("Hello, User");
        greetingLabel.setForeground(Color.WHITE);
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        goalLabel = new JLabel("Goal: ");
        goalLabel.setForeground(Color.LIGHT_GRAY);

        bmiLabel = new JLabel("BMI: ");
        bmiLabel.setForeground(Color.LIGHT_GRAY);

        left.add(greetingLabel);
        left.add(Box.createVerticalStrut(10));
        left.add(goalLabel);
        left.add(Box.createVerticalStrut(10));
        left.add(bmiLabel);

        JPanel right = new JPanel();
        right.setBackground(new Color(20, 20, 40));
        right.setBorder(BorderFactory.createTitledBorder("Today's Workout"));
        right.setLayout(new BorderLayout());

        todayWorkoutLabel = new JLabel("No workout found.", SwingConstants.CENTER);
        todayWorkoutLabel.setForeground(Color.WHITE);
        right.add(todayWorkoutLabel, BorderLayout.CENTER);

        top.add(left);
        top.add(right);

        add(top, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(18, 18, 32));
        infoArea.setForeground(Color.WHITE);
        infoArea.setText(
                "Welcome to your Peak Physique dashboard!\n\n" +
                        "- Check 'Workouts' for your training plans\n" +
                        "- Explore 'Exercises' to learn correct form\n" +
                        "- Use 'Progress' to track your weight and journey\n" +
                        "- Edit your stats in 'Profile'\n\n" +
                        "This desktop app is a simplified Peak Physique-style trainer."
        );
        add(new JScrollPane(infoArea), BorderLayout.CENTER);
    }

    void refresh() {
        AppUser u = app.getCurrentUser();
        if (u == null) return;

        greetingLabel.setText("Hello, " + u.name);
        goalLabel.setText("Goal: " + u.goal);

        double bmi = u.calculateBMI();
        if (bmi > 0) {
            bmiLabel.setText(String.format("BMI: %.1f", bmi));
        } else {
            bmiLabel.setText("BMI: N/A");
        }

        List<AppWorkoutPlan> plans = app.getWorkoutPlans();
        if (!plans.isEmpty()) {
            AppWorkoutPlan p = plans.get(0);
            todayWorkoutLabel.setText("<html><center>" +
                    p.name +
                    "<br>(" + p.level + ")" +
                    "</center></html>");
        } else {
            todayWorkoutLabel.setText("No workout plan available.");
        }
    }
}

// ======================= WORKOUTS PANEL =======================

class WorkoutsPanel extends JPanel {

    private final FitnessDesktopApp app;
    private JList<AppWorkoutPlan> planList;
    private JTextArea detailsArea;

    WorkoutsPanel(FitnessDesktopApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(12, 12, 24));

        JLabel header = new JLabel("Workout Plans");
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(header, BorderLayout.NORTH);

        planList = new JList<>(app.getWorkoutPlans().toArray(new AppWorkoutPlan[0]));
        planList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        planList.setBackground(new Color(18, 18, 32));
        planList.setForeground(Color.WHITE);

        planList.addListSelectionListener(e -> showPlanDetails(planList.getSelectedValue()));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(18, 18, 32));
        detailsArea.setForeground(Color.WHITE);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(planList),
                new JScrollPane(detailsArea)
        );
        split.setDividerLocation(250);

        add(split, BorderLayout.CENTER);
    }

    private void showPlanDetails(AppWorkoutPlan plan) {
        if (plan == null) {
            detailsArea.setText("");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(plan.name).append("\n");
        sb.append("Level: ").append(plan.level).append("\n");
        sb.append("Goal: ").append(plan.goal).append("\n");
        sb.append("Days / week: ").append(plan.daysPerWeek).append("\n\n");
        sb.append("Description:\n").append(plan.description).append("\n\n");
        sb.append("Exercises:\n");
        for (String ex : plan.exercises) {
            sb.append(" - ").append(ex).append("\n");
        }

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }
}

// ======================= EXERCISES PANEL =======================

class ExercisesPanel extends JPanel {

    private final FitnessDesktopApp app;
    private JList<AppExercise> exerciseList;
    private JTextArea detailsArea;

    ExercisesPanel(FitnessDesktopApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(12, 12, 24));

        JLabel header = new JLabel("Exercise Library");
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(header, BorderLayout.NORTH);

        exerciseList = new JList<>(app.getExercises().toArray(new AppExercise[0]));
        exerciseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        exerciseList.setBackground(new Color(18, 18, 32));
        exerciseList.setForeground(Color.WHITE);

        exerciseList.addListSelectionListener(e -> showExerciseDetails(exerciseList.getSelectedValue()));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(18, 18, 32));
        detailsArea.setForeground(Color.WHITE);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(exerciseList),
                new JScrollPane(detailsArea)
        );
        split.setDividerLocation(300);

        add(split, BorderLayout.CENTER);
    }

    private void showExerciseDetails(AppExercise ex) {
        if (ex == null) {
            detailsArea.setText("");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(ex.name).append("\n");
        sb.append("Muscle Group: ").append(ex.muscleGroup).append("\n");
        sb.append("Difficulty: ").append(ex.difficulty).append("\n");
        sb.append("Equipment: ").append(ex.equipment).append("\n\n");
        sb.append("Instructions:\n").append(ex.instructions);

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }
}

// ======================= PROGRESS PANEL =======================

class ProgressPanel extends JPanel {

    private final FitnessDesktopApp app;
    private JTable table;
    private DefaultTableModel tableModel;

    ProgressPanel(FitnessDesktopApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(12, 12, 24));

        JLabel header = new JLabel("Progress Tracking");
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(header, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Date", "Weight (kg)", "Notes"}, 0);
        table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addLogBtn = new JButton("Log Today's Weight");
        addLogBtn.addActionListener(this::handleAddLog);

        JPanel bottom = new JPanel();
        bottom.add(addLogBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    void refresh() {
        AppUser u = app.getCurrentUser();
        if (u == null) return;

        List<AppWeightLog> logs = AppProgressStore.loadLogs(u);
        tableModel.setRowCount(0);
        for (AppWeightLog log : logs) {
            tableModel.addRow(new Object[]{log.date, log.weightKg, log.notes});
        }
    }

    private void handleAddLog(ActionEvent e) {
        AppUser u = app.getCurrentUser();
        if (u == null) return;

        String weightStr = JOptionPane.showInputDialog(this,
                "Enter today's weight in kg:",
                String.format("%.1f", u.weightKg));
        if (weightStr == null) return;

        String notes = JOptionPane.showInputDialog(this,
                "Notes (optional):", "");
        if (notes == null) notes = "";

        try {
            double w = Double.parseDouble(weightStr.trim());
            AppWeightLog log = new AppWeightLog(LocalDate.now().toString(), w, notes);
            AppProgressStore.addLog(u, log);
            u.weightKg = w;
            AppUserStore.saveSingleUser(u);
            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid weight value.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

// ======================= PROFILE PANEL =======================

class ProfilePanel extends JPanel {

    private final FitnessDesktopApp app;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField heightField;
    private JTextField weightField;
    private JTextField goalField;
    private JLabel bmiLabel;

    ProfilePanel(FitnessDesktopApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(12, 12, 24));

        JLabel header = new JLabel("Profile & Settings");
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setBackground(new Color(12, 12, 24));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(5, 5, 5, 5);

        nameField = addLabeledField("Name:", form, gc);
        ageField = addLabeledField("Age:", form, gc);
        heightField = addLabeledField("Height (cm):", form, gc);
        weightField = addLabeledField("Weight (kg):", form, gc);
        goalField = addLabeledField("Goal:", form, gc);

        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 2;
        bmiLabel = new JLabel("BMI: ");
        bmiLabel.setForeground(Color.WHITE);
        form.add(bmiLabel, gc);

        gc.gridy++;
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(this::handleSave);
        form.add(saveBtn, gc);

        add(form, BorderLayout.CENTER);
    }

    private JTextField addLabeledField(String label, JPanel panel, GridBagConstraints gc) {
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);

        gc.gridx = 0;
        gc.gridwidth = 1;
        panel.add(l, gc);

        gc.gridx = 1;
        JTextField field = new JTextField(15);
        panel.add(field, gc);

        gc.gridy++;
        return field;
    }

    void refresh() {
        AppUser u = app.getCurrentUser();
        if (u == null) return;

        nameField.setText(u.name);
        ageField.setText(String.valueOf(u.age));
        heightField.setText(String.valueOf(u.heightCm));
        weightField.setText(String.valueOf(u.weightKg));
        goalField.setText(u.goal);

        double bmi = u.calculateBMI();
        if (bmi > 0) {
            bmiLabel.setText(String.format("BMI: %.1f", bmi));
        } else {
            bmiLabel.setText("BMI: N/A");
        }
    }

    private void handleSave(ActionEvent e) {
        AppUser u = app.getCurrentUser();
        if (u == null) return;

        try {
            u.name = nameField.getText().trim();
            u.age = Integer.parseInt(ageField.getText().trim());
            u.heightCm = Double.parseDouble(heightField.getText().trim());
            u.weightKg = Double.parseDouble(weightField.getText().trim());
            u.goal = goalField.getText().trim();

            AppUserStore.saveSingleUser(u);
            refresh();

            JOptionPane.showMessageDialog(this,
                    "Profile saved.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for age, height and weight.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

// ======================= DATA MODELS =======================

class AppUser {
    String email;
    String password;
    String name;
    int age;
    double heightCm;
    double weightKg;
    String goal;

    AppUser(String email, String password, String name,
            int age, double heightCm, double weightKg, String goal) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.goal = goal;
    }

    double calculateBMI() {
        if (heightCm <= 0) return -1;
        double hMeters = heightCm / 100.0;
        return weightKg / (hMeters * hMeters);
    }

    String toCsv() {
        return email + "," + password + "," + name.replace(",", " ") + ","
                + age + "," + heightCm + "," + weightKg + "," + goal.replace(",", " ");
    }

    static AppUser fromCsv(String line) {
        String[] p = line.split(",");
        if (p.length < 7) return null;
        try {
            String email = p[0];
            String password = p[1];
            String name = p[2];
            int age = Integer.parseInt(p[3]);
            double height = Double.parseDouble(p[4]);
            double weight = Double.parseDouble(p[5]);
            String goal = p[6];
            return new AppUser(email, password, name, age, height, weight, goal);
        } catch (Exception e) {
            return null;
        }
    }
}

class AppWorkoutPlan {
    String name;
    String level;
    String goal;
    int daysPerWeek;
    String description;
    List<String> exercises;

    AppWorkoutPlan(String name, String level, String goal, int daysPerWeek,
                   String description, List<String> exercises) {
        this.name = name;
        this.level = level;
        this.goal = goal;
        this.daysPerWeek = daysPerWeek;
        this.description = description;
        this.exercises = exercises;
    }

    @Override
    public String toString() {
        return name + " (" + level + ")";
    }
}

class AppExercise {
    String name;
    String muscleGroup;
    String difficulty;
    String equipment;
    String instructions;

    AppExercise(String name, String muscleGroup, String difficulty,
                String equipment, String instructions) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.difficulty = difficulty;
        this.equipment = equipment;
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return name + " - " + muscleGroup;
    }
}

class AppWeightLog {
    String date;
    double weightKg;
    String notes;

    AppWeightLog(String date, double weightKg, String notes) {
        this.date = date;
        this.weightKg = weightKg;
        this.notes = notes;
    }

    static AppWeightLog fromCsv(String line) {
        String[] p = line.split(",", 3);
        if (p.length < 2) return null;
        try {
            String date = p[0];
            double w = Double.parseDouble(p[1]);
            String notes = p.length == 3 ? p[2] : "";
            return new AppWeightLog(date, w, notes);
        } catch (Exception e) {
            return null;
        }
    }

    String toCsv() {
        String safeNotes = notes == null ? "" : notes.replace("\n", " ").replace(",", " ");
        return date + "," + weightKg + "," + safeNotes;
    }
}

// ======================= STORAGE HELPERS =======================

class AppUserStore {
    private static final String FILE = "app_users.csv";

    static List<AppUser> loadUsers() {
        List<AppUser> list = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE))) {
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                AppUser u = AppUser.fromCsv(line);
                if (u != null) {
                    list.add(u);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    static void saveUsers(List<AppUser> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (AppUser u : users) {
                pw.println(u.toCsv());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveSingleUser(AppUser user) {
        List<AppUser> users = loadUsers();
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).email.equalsIgnoreCase(user.email)) {
                users.set(i, user);
                found = true;
                break;
            }
        }
        if (!found) {
            users.add(user);
        }
        saveUsers(users);
    }
}

class AppProgressStore {

    private static String fileForUser(AppUser u) {
        String safe = u.email.replace("@", "_at_").replace(".", "_");
        return "app_progress_" + safe + ".csv";
    }

    static List<AppWeightLog> loadLogs(AppUser u) {
        List<AppWeightLog> list = new ArrayList<>();
        String file = fileForUser(u);
        if (!Files.exists(Paths.get(file))) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                AppWeightLog log = AppWeightLog.fromCsv(line);
                if (log != null) {
                    list.add(log);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    static void addLog(AppUser u, AppWeightLog log) {
        String file = fileForUser(u);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println(log.toCsv());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// ======================= SAMPLE DATA =======================

class AppSampleData {

    static List<AppWorkoutPlan> createWorkoutPlans() {
        List<AppWorkoutPlan> list = new ArrayList<>();

        List<String> beginnerEx = new ArrayList<>();
        beginnerEx.add("Bodyweight Squat");
        beginnerEx.add("Push-up (Knee)");
        beginnerEx.add("Glute Bridge");
        beginnerEx.add("Plank");
        list.add(new AppWorkoutPlan(
                "Full Body Beginner",
                "Beginner",
                "General fitness",
                3,
                "A simple full-body routine to build basic strength and mobility.",
                beginnerEx
        ));

        List<String> fatLossEx = new ArrayList<>();
        fatLossEx.add("Jumping Jacks");
        fatLossEx.add("Mountain Climbers");
        fatLossEx.add("Bodyweight Lunge");
        fatLossEx.add("Bicycle Crunch");
        list.add(new AppWorkoutPlan(
                "Fat Loss Burner",
                "Intermediate",
                "Fat loss",
                4,
                "Circuit-style training to burn calories and improve conditioning.",
                fatLossEx
        ));

        List<String> muscleEx = new ArrayList<>();
        muscleEx.add("Barbell Squat");
        muscleEx.add("Bench Press");
        muscleEx.add("Bent-over Row");
        muscleEx.add("Overhead Press");
        list.add(new AppWorkoutPlan(
                "Muscle Builder",
                "Intermediate",
                "Build muscle",
                5,
                "Classic compound lifts to maximize muscle growth.",
                muscleEx
        ));

        List<String> strengthEx = new ArrayList<>();
        strengthEx.add("Deadlift");
        strengthEx.add("Front Squat");
        strengthEx.add("Weighted Pull-up");
        strengthEx.add("Military Press");
        list.add(new AppWorkoutPlan(
                "Strength Focus",
                "Advanced",
                "Strength",
                4,
                "Low-rep heavy lifting for pure strength development.",
                strengthEx
        ));

        return list;
    }

    static List<AppExercise> createExercises() {
        List<AppExercise> list = new ArrayList<>();

        list.add(new AppExercise(
                "Bodyweight Squat",
                "Legs",
                "Beginner",
                "None",
                "Stand with feet shoulder-width apart. Push hips back and bend knees to lower into a squat, keeping chest up. Push through heels to stand back up."
        ));
        list.add(new AppExercise(
                "Push-up (Knee)",
                "Chest",
                "Beginner",
                "None",
                "Start on your knees with hands slightly wider than shoulders. Lower chest toward the floor, keeping core tight, then push back up."
        ));
        list.add(new AppExercise(
                "Glute Bridge",
                "Glutes",
                "Beginner",
                "None",
                "Lie on your back, feet flat on floor. Squeeze glutes and lift hips until your body forms a straight line from shoulders to knees."
        ));
        list.add(new AppExercise(
                "Plank",
                "Core",
                "Beginner",
                "None",
                "Support your body on forearms and toes. Maintain a straight line from head to heels, keeping core tight and not letting hips drop."
        ));
        list.add(new AppExercise(
                "Jumping Jacks",
                "Full Body",
                "Beginner",
                "None",
                "Jump feet out while raising arms overhead, then jump back to starting position. Maintain a steady rhythm."
        ));
        list.add(new AppExercise(
                "Mountain Climbers",
                "Core / Cardio",
                "Intermediate",
                "None",
                "Start in a strong push-up position with your hands under your shoulders and your body in a straight line. Drive one knee toward your chest, then quickly switch legs in a fast, running-like motion while keeping your core tight and hips low. Breathe steadily and keep a smooth, controlled rhythm."
        ));
        list.add(new AppExercise(
                "Bodyweight Lunge",
                "Legs",
                "Beginner",
                "None",
                "Step forward with one leg and lower until both knees are about 90 degrees. Push back to standing and switch legs."
        ));
        list.add(new AppExercise(
                "Bicycle Crunch",
                "Abs",
                "Intermediate",
                "None",
                "Lie on your back, hands by your head. Bring opposite elbow to knee in a pedaling motion while extending the other leg."
        ));
        list.add(new AppExercise(
                "Barbell Squat",
                "Legs",
                "Intermediate",
                "Barbell & rack",
                "Place bar on upper back, stand with feet shoulder-width apart. Lower into a squat while keeping chest up, then drive up."
        ));
        list.add(new AppExercise(
                "Bench Press",
                "Chest",
                "Intermediate",
                "Barbell & bench",
                "Lie on a bench with feet flat. Lower the bar to mid-chest under control, then press back up to straight arms."
        ));
        list.add(new AppExercise(
                "Bent-over Row",
                "Back",
                "Intermediate",
                "Barbell",
                "Hinge at hips with flat back. Pull bar toward your lower ribs, squeezing shoulder blades together."
        ));
        list.add(new AppExercise(
                "Overhead Press",
                "Shoulders",
                "Intermediate",
                "Barbell or dumbbells",
                "Press weight from shoulders to overhead, keeping core braced and not over-arching lower back."
        ));
        list.add(new AppExercise(
                "Deadlift",
                "Posterior chain",
                "Advanced",
                "Barbell",
                "Stand with mid-foot under bar. Hinge at hips, grip bar, brace core, and stand up by driving hips forward."
        ));
        list.add(new AppExercise(
                "Weighted Pull-up",
                "Back",
                "Advanced",
                "Pull-up bar, optional belt",
                "Hang from bar with overhand grip. Pull chest toward bar while keeping core tight, then lower under control."
        ));
        list.add(new AppExercise(
                "Military Press",
                "Shoulders",
                "Advanced",
                "Barbell",
                "Press bar from upper chest to overhead in a straight line while standing tall and maintaining a tight core."
        ));

        return list;
    }
}
