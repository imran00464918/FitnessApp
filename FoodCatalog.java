import java.util.ArrayList;
import java.util.List;

public class FoodCatalog {
    private final List<FoodItem> items = new ArrayList<>();

    public FoodCatalog() {
        // per 100g: calories, protein, carbs, fat
        items.add(new FoodItem("Apple",               52, 0.3, 14, 0.2));
        items.add(new FoodItem("Banana",              89, 1.1, 23, 0.3));
        items.add(new FoodItem("Rice (cooked)",      130, 2.7, 28, 0.3));
        items.add(new FoodItem("Chicken Breast",     165, 31, 0,  3.6));
        items.add(new FoodItem("Oats (dry)",         389, 17, 66, 7));
        items.add(new FoodItem("Whole Milk",          61, 3.2, 5,  3.3));
        items.add(new FoodItem("Egg (whole)",        155, 13, 1.1, 11));
        items.add(new FoodItem("Broccoli",            34, 2.8, 7,  0.4));
        items.add(new FoodItem("Peanut Butter",      588, 25, 20, 50));
        items.add(new FoodItem("Greek Yogurt (plain)", 59, 10, 3.6, 0.4));
    }

    public List<FoodItem> all() { return items; }
}
