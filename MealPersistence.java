import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public class MealPersistence {
    public static void saveMealsCSV(Iterable<Meal> meals, String fileName) {
        try {
            ensureParentDir(fileName);
            boolean newFile = Files.notExists(Path.of(fileName));
            try (var fw = new FileWriter(fileName, true);
                 var bw = new BufferedWriter(fw);
                 var out = new PrintWriter(bw)) {

                if (newFile) {
                    out.println("date,meal,food,grams,cal_per_100g,calories,protein_g,carbs_g,fat_g");
                }

                for (Meal meal : meals) {
                    LocalDate date = meal.getDate();
                    for (Map.Entry<FoodItem, Double> e : meal.getItems().entrySet()) {
                        FoodItem fi = e.getKey();
                        double grams = e.getValue();
                        double factor = grams / 100.0;
                        double cal = fi.getCaloriesPer100g() * factor;
                        double p = fi.getProteinPer100g() * factor;
                        double c = fi.getCarbsPer100g() * factor;
                        double f = fi.getFatPer100g() * factor;

                        out.printf("%s,%s,%s,%.0f,%.0f,%.0f,%.1f,%.1f,%.1f%n",
                                date, csv(meal.getName()), csv(fi.getName()), grams,
                                fi.getCaloriesPer100g(), cal, p, c, f);
                    }
                }
            }
            System.out.println("✅ Meals saved to CSV: " + fileName);
        } catch (Exception ex) {
            System.out.println("Meal CSV save failed: " + ex.getMessage());
        }
    }

    private static void ensureParentDir(String fileName) throws Exception {
        Path p = Path.of(fileName).toAbsolutePath();
        if (p.getParent() != null && Files.notExists(p.getParent())) {
            Files.createDirectories(p.getParent());
        }
    }
    private static String csv(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
