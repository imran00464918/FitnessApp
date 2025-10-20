import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Meal {
    private final UUID id = UUID.randomUUID();
    private final LocalDate date = LocalDate.now();
    private final String name; // Breakfast/Lunch/Dinner/Snack/custom
    private final Map<FoodItem, Double> items = new LinkedHashMap<>(); // grams

    public Meal(String name) { this.name = name; }

    public void addFood(FoodItem fi, double grams) {
        if (grams <= 0) throw new IllegalArgumentException("grams must be > 0");
        items.merge(fi, grams, Double::sum);
    }

    public double totalCalories() {
        double cal = 0;
        for (var e : items.entrySet()) {
            double g = e.getValue() / 100.0;
            cal += e.getKey().getCaloriesPer100g() * g;
        }
        return cal;
    }

    public Macros totalMacros() {
        double p=0, c=0, f=0;
        for (var e : items.entrySet()) {
            double g = e.getValue() / 100.0;
            var fi = e.getKey();
            p += fi.getProteinPer100g() * g;
            c += fi.getCarbsPer100g()   * g;
            f += fi.getFatPer100g()     * g;
        }
        return new Macros(p, c, f);
    }

    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public Map<FoodItem, Double> getItems() { return items; } // <-- needed for export

    public static class Macros {
        public final double protein, carbs, fat;
        public Macros(double protein, double carbs, double fat) {
            this.protein = protein; this.carbs = carbs; this.fat = fat;
        }
    }

    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Meal: ").append(name).append(" (").append(date).append(")\n");
        for (var e : items.entrySet()) {
            sb.append(" - ").append(e.getKey().getName())
              .append(" : ").append(e.getValue().intValue()).append(" g\n");
        }
        Macros m = totalMacros();
        sb.append(String.format("Totals → Calories: %.0f kcal | Protein: %.1f g | Carbs: %.1f g | Fat: %.1f g",
                totalCalories(), m.protein, m.carbs, m.fat));
        return sb.toString();
    }
}
