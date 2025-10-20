import java.util.UUID;

public class FoodItem {
    private final UUID id = UUID.randomUUID();
    private final String name;
    private final double caloriesPer100g;
    private final double proteinPer100g;
    private final double carbsPer100g;
    private final double fatPer100g;

    public FoodItem(String name, double caloriesPer100g, double proteinPer100g,
                    double carbsPer100g, double fatPer100g) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatPer100g = fatPer100g;
    }

    public String getName() { return name; }
    public double getCaloriesPer100g() { return caloriesPer100g; }
    public double getProteinPer100g() { return proteinPer100g; }
    public double getCarbsPer100g() { return carbsPer100g; }
    public double getFatPer100g() { return fatPer100g; }

    @Override public String toString() { return name; }
}
