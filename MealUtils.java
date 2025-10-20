import java.util.List;

public class MealUtils {
    public static double totalCalories(List<Meal> meals) {
        double sum = 0;
        for (Meal m : meals) sum += m.totalCalories();
        return sum;
    }
}
