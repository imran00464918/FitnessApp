public class DailyGoalTracker {

    // ===== Static target used by the Dashboard UI =====
    private static double defaultTarget = 500.0;

    public static double getDefaultTarget() {
        return defaultTarget;
    }

    public static void setDefaultTarget(double value) {
        if (value > 0) {
            defaultTarget = value;
        }
    }

    // ===== Instance target used for calculations =====
    private final double target;

    public DailyGoalTracker(double target) {
        this.target = target;
    }

    /**
     * Compare burned vs intake and return evaluation.
     */
    public String evaluate(double burned, double intake) {

        double diff = burned - intake;

        if (diff >= target) {
            return "Great job!\nYou met your daily goal.";
        } 
        if (diff >= target * 0.7) {
            return "You're on track!\nAlmost reached today's target.";
        } 
        if (diff >= 0) {
            return "Below target.\nTry to be a bit more active today.";
        }

        return "Calorie surplus.\nYou consumed more than you burned.";
    }
}
