public class CardioExercise extends Exercise {
    private final double met; // intensity (e.g., 6 = light jog, 8 = run)

    public CardioExercise(String name, String targetMuscle, String instructions, double met) {
        super(name, targetMuscle, instructions);
        this.met = met;
    }

    /** Simple estimate assuming 70kg body weight: kcal/min ≈ 0.0175 * MET * 70 */
    @Override
    public double estimateCalories(SetEntry s) {
        double minutes = s.getDurationSeconds() / 60.0;
        return 0.0175 * met * 70.0 * minutes;
    }
}
