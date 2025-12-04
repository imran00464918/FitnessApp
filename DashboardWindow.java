import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Interactive dashboard window for the Fitness App.
 * Opens after login. Shows "Me" and "Meals" screens similar to a mobile fitness app.
 */
public class DashboardWindow extends JFrame {

    private final User user;
    private final List<Meal> meals;
    private final WorkoutSession session;

    private JLabel weightValueLabel;
    private JLabel workoutsValueLabel;
    private JLabel minutesValueLabel;

    public DashboardWindow(User user, List<Meal> meals, WorkoutSession session) {
        super("Fitness App — Dashboard (" + user.getUsername() + ")");
        this.user = user;
        this.meals = meals;
        this.session = session;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 550);
        setLocationRelativeTo(null);

        buildUI();
    }

    // ================= UI BUILDING =================

    private void buildUI() {
        setLayout(new BorderLayout());

        // Top title bar
        JLabel title = new JLabel("Fitness App", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Main content as tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Me", buildMePanel());
        tabs.addTab("Meals", buildMealsPanel());
        tabs.addTab("My program", buildPlaceholderPanel("Program screen coming soon"));
        tabs.addTab("Explore", buildPlaceholderPanel("Explore workouts & plans (coming soon)"));

        add(tabs, BorderLayout.CENTER);
    }

    // ---------- "Me" tab ----------

    private JPanel buildMePanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Activity card
        JPanel activityCard = createCardPanel("Activity");
        activityCard.setLayout(new GridLayout(2, 2));

        workoutsValueLabel = new JLabel("0", SwingConstants.CENTER);
        workoutsValueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel workoutsLabel = new JLabel("WORKOUTS", SwingConstants.CENTER);

        minutesValueLabel = new JLabel("0", SwingConstants.CENTER);
        minutesValueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel minutesLabel = new JLabel("MINUTES", SwingConstants.CENTER);

        activityCard.add(workoutsValueLabel);
        activityCard.add(minutesValueLabel);
        activityCard.add(workoutsLabel);
        activityCard.add(minutesLabel);

        // Weight card
        JPanel weightCard = createCardPanel("Weight");
        weightCard.setLayout(new BorderLayout());

        double weightKg = user.getBodyWeightKg();
        String weightText;
        if (weightKg > 0) {
            double lbs = weightKg * 2.20462;
            weightText = String.format("%.1f lbs", lbs);
        } else {
            weightText = "Not set";
        }

        weightValueLabel = new JLabel(weightText, SwingConstants.LEFT);
        weightValueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        weightValueLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel weightTop = new JPanel(new BorderLayout());
        weightTop.add(weightValueLabel, BorderLayout.WEST);

        JButton trackBtn = new JButton("Track");
        trackBtn.addActionListener(e -> trackWeight());
        weightTop.add(trackBtn, BorderLayout.EAST);

        weightCard.add(weightTop, BorderLayout.NORTH);

        // simple timeline label
        JLabel timelineLabel = new JLabel("Progress (start → now)", SwingConstants.CENTER);
        weightCard.add(timelineLabel, BorderLayout.CENTER);

        // Performance / sync card
        JPanel perfCard = createCardPanel("Performance metrics");
        perfCard.setLayout(new BorderLayout());

        JLabel syncText = new JLabel(
                "Sync with Apple Health / device to receive data",
                SwingConstants.CENTER
        );
        perfCard.add(syncText, BorderLayout.CENTER);

        JButton syncBtn = new JButton("Sync data");
        syncBtn.setBackground(new Color(255, 90, 0));
        syncBtn.setForeground(Color.WHITE);
        syncBtn.setFocusPainted(false);
        syncBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Pretending to sync with Apple Health…\n" +
                        "In a real app this would connect to the device API.",
                "Sync data",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JPanel syncBtnPanel = new JPanel();
        syncBtnPanel.add(syncBtn);
        perfCard.add(syncBtnPanel, BorderLayout.SOUTH);

        // Add cards to root
        root.add(activityCard);
        root.add(Box.createVerticalStrut(10));
        root.add(weightCard);
        root.add(Box.createVerticalStrut(10));
        root.add(perfCard);

        // Fill in the activity numbers based on the session
        refreshActivityFromSession();

        return root;
    }

    private void refreshActivityFromSession() {
        if (session != null) {
            // For this project we keep it simple:
            // 1 workout = the current session, minutes approximated from calories.
            workoutsValueLabel.setText("1");
            // Rough fake: 1 kcal ≈ 0.1 min just to show a non-zero number.
            int minutes = (int) Math.round(session.totalCalories() * 0.1);
            minutesValueLabel.setText(String.valueOf(minutes));
        } else {
            workoutsValueLabel.setText("0");
            minutesValueLabel.setText("0");
        }
    }

    private void trackWeight() {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter your current weight in lbs:",
                "Update weight",
                JOptionPane.PLAIN_MESSAGE
        );
        if (input == null || input.trim().isEmpty()) return;

        try {
            double lbs = Double.parseDouble(input.trim());
            if (lbs <= 0) throw new NumberFormatException();
            double kg = lbs / 2.20462;
            user.setBodyWeightKg(kg);
            weightValueLabel.setText(String.format("%.1f lbs", lbs));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid positive number.",
                    "Invalid weight",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ---------- "Meals" tab ----------

    private JPanel buildMealsPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Meals — Today", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        if (meals.isEmpty()) {
            listPanel.add(buildMealCard(
                    "No meals yet",
                    "Use the console menu to add meals, then reopen the dashboard.",
                    0
            ));
        } else {
            for (Meal m : meals) {
                double kcal = m.totalCalories();
                listPanel.add(buildMealCard(m.getName(), "Meal from your plan", kcal));
                listPanel.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        root.add(scroll, BorderLayout.CENTER);

        return root;
    }

    private JPanel buildMealCard(String title, String subtitle, double kcal) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(new Color(30, 30, 30));

        // Title + subtitle
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel(subtitle + (kcal > 0 ? String.format(" • %.0f kcal", kcal) : ""));
        subLabel.setForeground(new Color(200, 200, 200));

        textPanel.add(titleLabel);
        textPanel.add(subLabel);

        card.add(textPanel, BorderLayout.CENTER);

        // Buttons at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton cookedBtn = new JButton("Mark as eaten");
        cookedBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Marked \"" + title + "\" as eaten.",
                "Meal eaten",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JButton swapBtn = new JButton("Swap");
        swapBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Here you could choose an alternative meal.\n" +
                        "For now this is just a demo interaction.",
                "Swap meal",
                JOptionPane.INFORMATION_MESSAGE
        ));

        buttonPanel.add(cookedBtn);
        buttonPanel.add(swapBtn);

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    // ---------- Helper for placeholder tabs ----------

    private JPanel buildPlaceholderPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.ITALIC, 16));
        p.add(label, BorderLayout.CENTER);
        return p;
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(new Color(25, 25, 25));
        return panel;
    }
}
