import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MealPlan {
    private final UUID id = UUID.randomUUID();
    private String name;
    private int dailyCalorieTarget;
    private final List<Meal> meals = new ArrayList<>();

    public MealPlan(String name, int dailyCalorieTarget) {
        this.name = name;
        this.dailyCalorieTarget = dailyCalorieTarget;
    }

    public void addMeal(Meal m) { meals.add(m); }
    public List<Meal> getMeals() { return Collections.unmodifiableList(meals); }
}
