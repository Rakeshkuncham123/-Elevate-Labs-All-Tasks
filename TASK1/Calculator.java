import java.util.Scanner;

public class Calculator {

    public static double add(double a, double b) {
        return a + b;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double divide(double a, double b) {
        if (b == 0) {
            System.out.println("Error: Division by zero is not allowed!");
            return Double.NaN;
        }
        return a / b;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        char choice;

        System.out.println("====================================");
        System.out.println("      JAVA CONSOLE CALCULATOR");
        System.out.println("====================================");

        do {
            System.out.println("\nChoose an Operation:");
            System.out.println("1. Addition (+)");
            System.out.println("2. Subtraction (-)");
            System.out.println("3. Multiplication (*)");
            System.out.println("4. Division (/)");
            System.out.print("Enter your choice (1-4): ");

            int option = sc.nextInt();

            System.out.print("Enter First Number: ");
            double num1 = sc.nextDouble();

            System.out.print("Enter Second Number: ");
            double num2 = sc.nextDouble();

            double result;

            switch (option) {
                case 1:
                    result = add(num1, num2);
                    System.out.println("Result = " + result);
                    break;

                case 2:
                    result = subtract(num1, num2);
                    System.out.println("Result = " + result);
                    break;

                case 3:
                    result = multiply(num1, num2);
                    System.out.println("Result = " + result);
                    break;

                case 4:
                    result = divide(num1, num2);
                    if (!Double.isNaN(result))
                        System.out.println("Result = " + result);
                    break;

                default:
                    System.out.println("Invalid Choice!");
            }

            System.out.print("\nDo you want another calculation? (Y/N): ");
            choice = sc.next().charAt(0);

        } while (choice == 'Y' || choice == 'y');

        System.out.println("\nThank You for Using Java Calculator!");
        sc.close();
    }
}