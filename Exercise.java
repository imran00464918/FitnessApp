import java.util.UUID;

public abstract class Exercise {
    protected final UUID id = UUID.randomUUID();
    protected final String name;
    protected final String targetMuscle;
    protected final String instructions;

    protected Exercise(String name, String targetMuscle, String instructions) {
        this.name = name;
        this.targetMuscle = targetMuscle;
        this.instructions = instructions;
    }

    /** Estimate calories for one set/interval of this exercise. */
    public abstract double estimateCalories(SetEntry set);

    public String getName() { return name; }
    @Override public String toString() { return name; }
}
