import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // ====== Setup ======
        FoodCatalog catalog = new FoodCatalog();
        Input in = new Input();
        List<Meal> meals = new ArrayList<>();

        // Prepare a demo workout session (so you can still save sessions as before)
        StrengthExercise bench = new StrengthExercise("Bench Press", "Chest", "Controlled tempo", true);
        StrengthExercise squat = new StrengthExercise("Back Squat", "Legs", "Full depth", true);
        CardioExercise run     = new CardioExercise("Treadmill Run", "Cardio", "Steady pace", 8.0);

        Workout workout = new Workout("Push + Run", DayOfWeek.THURSDAY);
        workout.addExercise(bench);
        workout.addExercise(squat);
        workout.addExercise(run);

        WorkoutSession session = workout.startSession();
        session.addSet(bench, new SetEntry(1, 10, 40, 45));
        session.addSet(bench, new SetEntry(2, 8, 50, 45));
        session.addSet(squat, new SetEntry(1, 8, 70, 60));
        session.addSet(run,   new SetEntry(1, 0, 0, 1200));
        session.complete();

        // ====== Menu Loop ======
        boolean running = true;
        while (running) {
            System.out.println("\n=== Fitness App Menu ===");
            System.out.println("1) Create a meal");
            System.out.println("2) Add food to a meal (from catalog)");
            System.out.println("3) Add food to a meal (custom item)");
            System.out.println("4) Show all meals & totals");
            System.out.println("5) Save meals to CSV (data/meals-YYYY-MM-DD.csv)");
            System.out.println("6) Show workout session summary");
            System.out.println("7) Save workout session (CSV + JSON in data/)");
            System.out.println("8) Daily goal check (burned - intake)");
            System.out.println("9) Exit");
            String choice = in.line("Choose: ").trim();

            switch (choice) {
                case "1" -> {
                    String name = in.line("Meal name (e.g., Breakfast): ");
                    meals.add(new Meal(name));
                    System.out.println("Created meal: " + name);
                }

                case "2" -> {
                    if (meals.isEmpty()) { System.out.println("Create a meal first (option 1)."); break; }
                    int mi = pickMeal(in, meals);
                    if (mi == -1) break;

                    // pick from catalog
                    listCatalog(catalog);
                    int ci = in.intVal("Pick food #: ") - 1;
                    if (ci < 0 || ci >= catalog.all().size()) { System.out.println("Invalid choice."); break; }
                    FoodItem fi = catalog.all().get(ci);
                    double grams = in.dblVal("Grams (e.g., 150): ");
                    meals.get(mi).addFood(fi, grams);
                    System.out.println("Added " + grams + " g of " + fi.getName() + " to " + meals.get(mi).getName());
                }

                case "3" -> {
                    if (meals.isEmpty()) { System.out.println("Create a meal first (option 1)."); break; }
                    int mi = pickMeal(in, meals);
                    if (mi == -1) break;

                    String fname = in.line("Food name: ");
                    double cal = in.dblVal("kcal per 100g: ");
                    double p   = in.dblVal("Protein g per 100g: ");
                    double c   = in.dblVal("Carbs g per 100g: ");
                    double f   = in.dblVal("Fat g per 100g: ");
                    double grams = in.dblVal("Grams (e.g., 120): ");

                    FoodItem custom = new FoodItem(fname, cal, p, c, f);
                    meals.get(mi).addFood(custom, grams);
                    System.out.println("Added custom item to " + meals.get(mi).getName());
                }

                case "4" -> {
                    if (meals.isEmpty()) { System.out.println("No meals yet."); break; }
                    double total = 0;
                    for (Meal m : meals) {
                        System.out.println(m.summary());
                        System.out.println();
                        total += m.totalCalories();
                    }
                    System.out.printf("Daily Intake Total: %.0f kcal%n", total);
                }

                case "5" -> {
                    if (meals.isEmpty()) { System.out.println("No meals to save."); break; }
                    String date = LocalDate.now().toString();
                    String path = "data/meals-" + date + ".csv";
                    MealPersistence.saveMealsCSV(meals, path);
                }

                case "6" -> {
                    System.out.println(session.summary());
                }

                case "7" -> {
                    // timestamped save (same style as before)
                    String dataDir = "data";
                    String date = LocalDate.now().toString();
                    String ts   = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
                    String csvPath  = dataDir + "/sessions-" + date + ".csv";
                    String jsonPath = dataDir + "/session-"  + ts   + ".json";
                    SessionPersistence.saveSessionAsCSV(session, csvPath);
                    SessionPersistence.saveSessionAsJSON(session, jsonPath);
                }

                case "8" -> {
                    double burned = session.totalCalories();
                    double intake = MealUtils.totalCalories(meals);
                    DailyGoalTracker tracker = new DailyGoalTracker(500); // target net +500 kcal/day
                    System.out.println(tracker.evaluate(burned, intake));
                }

                case "9" -> {
                    running = false;
                    in.close();
                    System.out.println("Goodbye! 👋");
                }

                default -> System.out.println("Enter 1–9.");
            }
        }
    }

    // ----- helpers -----
    private static int pickMeal(Input in, List<Meal> meals) {
        for (int i = 0; i < meals.size(); i++) {
            System.out.println((i + 1) + ") " + meals.get(i).getName());
        }
        int idx = in.intVal("Pick meal #: ") - 1;
        if (idx < 0 || idx >= meals.size()) { System.out.println("Invalid choice."); return -1; }
        return idx;
    }

    private static void listCatalog(FoodCatalog catalog) {
        System.out.println("-- Catalog --");
        for (int i = 0; i < catalog.all().size(); i++) {
            FoodItem f = catalog.all().get(i);
            System.out.printf("%d) %s (%.0f kcal/100g, P%.1f/C%.1f/F%.1f)%n",
                    i + 1, f.getName(), f.getCaloriesPer100g(), f.getProteinPer100g(),
                    f.getCarbsPer100g(), f.getFatPer100g());
        }
    }
}
