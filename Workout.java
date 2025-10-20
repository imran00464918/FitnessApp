import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Workout {
    private final String title;
    private final DayOfWeek day;
    private final List<Exercise> ordered = new ArrayList<>();

    public Workout(String title, DayOfWeek day) {
        this.title = title;
        this.day = day;
    }

    public void addExercise(Exercise e) { ordered.add(e); }
    public List<Exercise> getExercises() { return Collections.unmodifiableList(ordered); }
    public WorkoutSession startSession() { return new WorkoutSession(this); }
    public String getTitle() { return title; }
}
