import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkoutSession {
    private final Workout workout;
    private final LocalDateTime start = LocalDateTime.now();
    private LocalDateTime end;
    private final Map<Exercise, List<SetEntry>> sets = new LinkedHashMap<>();

    public WorkoutSession(Workout workout) { this.workout = workout; }

    public void addSet(Exercise e, SetEntry s) {
        sets.computeIfAbsent(e, k -> new ArrayList<>()).add(s);
    }

    public void complete() { this.end = LocalDateTime.now(); }

    public double totalCalories() {
        double sum = 0;
        for (Map.Entry<Exercise, List<SetEntry>> entry : sets.entrySet()) {
            for (SetEntry set : entry.getValue()) {
                sum += entry.getKey().estimateCalories(set);
            }
        }
        return sum;
    }

    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Workout Session: ").append(workout.getTitle()).append("\n");
        for (Map.Entry<Exercise, List<SetEntry>> entry : sets.entrySet()) {
            Exercise e = entry.getKey();
            sb.append("  ").append(e.getName()).append("\n");
            for (SetEntry s : entry.getValue()) {
                sb.append(String.format("    Set %d: reps=%d, weight=%.1f kg, time=%ds\n",
                        s.getSetNumber(), s.getReps(), s.getWeight(), s.getDurationSeconds()));
            }
        }
        sb.append(String.format("Total Calories: %.0f kcal\n", totalCalories()));
        return sb.toString();
    }

    // ======== GETTERS (used by persistence) ========
    public Workout getWorkout() { return workout; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEndOrNow() { return (end != null) ? end : LocalDateTime.now(); }
    public Map<Exercise, List<SetEntry>> getSets() { return sets; }
}
