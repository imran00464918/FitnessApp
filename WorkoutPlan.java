import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkoutPlan {
    private final String name;
    private final List<Workout> workouts = new ArrayList<>();

    public WorkoutPlan(String name) { this.name = name; }

    public void addWorkout(Workout w) { workouts.add(w); }
    public List<Workout> getWorkouts() { return Collections.unmodifiableList(workouts); }
    public String getName() { return name; }
}
