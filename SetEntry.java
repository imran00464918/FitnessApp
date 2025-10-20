public class SetEntry {
    private final int setNumber;
    private final int reps;              // 0 for cardio intervals
    private final double weight;         // kg; 0 for bodyweight/cardio
    private final int durationSeconds;   // seconds

    public SetEntry(int setNumber, int reps, double weight, int durationSeconds) {
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
        this.durationSeconds = durationSeconds;
    }

    public int getSetNumber() { return setNumber; }
    public int getReps() { return reps; }
    public double getWeight() { return weight; }
    public int getDurationSeconds() { return durationSeconds; }
}
