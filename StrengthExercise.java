public class StrengthExercise extends Exercise {
    private final boolean usesWeights;

    public StrengthExercise(String name, String targetMuscle, String instructions, boolean usesWeights) {
        super(name, targetMuscle, instructions);
        this.usesWeights = usesWeights;
    }

    // Very rough approximation for demo
    @Override
    public double estimateCalories(SetEntry s) {
        double byLoad = (s.getReps() * Math.max(0, s.getWeight())) * 0.1;
        double byTime = (s.getDurationSeconds() / 6.0);
        return byLoad + byTime;
    }
}
