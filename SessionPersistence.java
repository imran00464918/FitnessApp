import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class SessionPersistence {
    private static final DateTimeFormatter TS = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /** Append a session to CSV. One line per set. Auto-creates parent folder. */
    public static void saveSessionAsCSV(WorkoutSession session, String fileName) {
        try {
            ensureParentDir(fileName);
            boolean newFile = Files.notExists(Path.of(fileName));
            try (var fw = new FileWriter(fileName, true);
                 var bw = new BufferedWriter(fw);
                 var out = new PrintWriter(bw)) {

                if (newFile) {
                    out.println("workoutTitle,start,end,exercise,setNumber,reps,weightKg,durationSec");
                }
                for (Map.Entry<Exercise, List<SetEntry>> e : session.getSets().entrySet()) {
                    String exercise = e.getKey().getName();
                    for (SetEntry s : e.getValue()) {
                        out.printf("%s,%s,%s,%s,%d,%d,%.2f,%d%n",
                                session.getWorkout().getTitle(),
                                session.getStart().format(TS),
                                session.getEndOrNow().format(TS),
                                csvEscape(exercise),
                                s.getSetNumber(),
                                s.getReps(),
                                s.getWeight(),
                                s.getDurationSeconds()
                        );
                    }
                }
            }
            System.out.println("✅ Session saved to CSV: " + fileName);
        } catch (IOException ex) {
            System.out.println("CSV save failed: " + ex.getMessage());
        }
    }

    /** Write the whole session as compact JSON (no libs). Auto-creates parent folder. */
    public static void saveSessionAsJSON(WorkoutSession session, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(jsonPair("workoutTitle", session.getWorkout().getTitle())).append(",");
        sb.append(jsonPair("start", session.getStart().format(TS))).append(",");
        sb.append(jsonPair("end", session.getEndOrNow().format(TS))).append(",");
        sb.append("\"totalCalories\":").append(String.format("%.0f", session.totalCalories())).append(",");
        sb.append("\"sets\":[");
        boolean firstE = true;
        for (Map.Entry<Exercise, List<SetEntry>> e : session.getSets().entrySet()) {
            if (!firstE) sb.append(",");
            firstE = false;
            sb.append("{");
            sb.append(jsonPair("exercise", e.getKey().getName())).append(",");
            sb.append("\"entries\":[");
            boolean firstS = true;
            for (SetEntry s : e.getValue()) {
                if (!firstS) sb.append(",");
                firstS = false;
                sb.append("{")
                  .append("\"setNumber\":").append(s.getSetNumber()).append(",")
                  .append("\"reps\":").append(s.getReps()).append(",")
                  .append("\"weightKg\":").append(String.format("%.1f", s.getWeight())).append(",")
                  .append("\"durationSec\":").append(s.getDurationSeconds())
                  .append("}");
            }
            sb.append("]}");
        }
        sb.append("]}");

        try {
            ensureParentDir(fileName);
            Files.writeString(Path.of(fileName), sb.toString(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("✅ Session saved to JSON: " + fileName);
        } catch (IOException ex) {
            System.out.println("JSON save failed: " + ex.getMessage());
        }
    }

    // --- helpers
    private static void ensureParentDir(String fileName) throws IOException {
        Path p = Path.of(fileName).toAbsolutePath();
        Path parent = p.getParent();
        if (parent != null && Files.notExists(parent)) {
            Files.createDirectories(parent);
        }
    }
    private static String csvEscape(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    private static String jsonPair(String k, String v) {
        return "\"" + k + "\":\"" + v.replace("\"", "\\\"") + "\"";
    }
}
