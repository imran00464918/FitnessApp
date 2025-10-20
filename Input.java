import java.util.Scanner;

public class Input {
    private final Scanner sc = new Scanner(System.in);

    public String line(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }
    public int intVal(String prompt) {
        while (true) {
            try { return Integer.parseInt(line(prompt).trim()); }
            catch (Exception e) { System.out.println("Enter a whole number."); }
        }
    }
    public double dblVal(String prompt) {
        while (true) {
            try { return Double.parseDouble(line(prompt).trim()); }
            catch (Exception e) { System.out.println("Enter a number (e.g., 120.5)."); }
        }
    }
    public void close() { sc.close(); }
}
