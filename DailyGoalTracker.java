public class DailyGoalTracker {
    private final int targetCalories; // desired net calories (burned - intake)

    public DailyGoalTracker(int targetCalories) {
        this.targetCalories = Math.max(0, targetCalories);
    }

    public String evaluate(double sessionCalories, double mealsCalories) {
        double net = sessionCalories - mealsCalories;
        String status = (net >= targetCalories) ? "✅ On track" : "⚠️ Below target";
        return String.format(
            "Daily Target: %d kcal | Burned: %.0f | Intake: %.0f | Net: %.0f → %s",
            targetCalories, sessionCalories, mealsCalories, net, status
        );
    }
}
