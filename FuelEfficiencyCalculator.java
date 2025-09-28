import java.util.Scanner;

// Class to represent a Car
class Car {
    private String model;
    private double distance;  // in kilometers
    private double fuelUsed;  // in litres

    // Constructor
    public Car(String model, double distance, double fuelUsed) {
        this.model = model;
        this.distance = distance;
        this.fuelUsed = fuelUsed;
    }

    // Method to calculate fuel efficiency
    public double calculateFuelEfficiency() {
        if (fuelUsed == 0) {
            return 0; // avoid division by zero
        }
        return distance / fuelUsed; // km per litre
    }

    // Method to display details
    public void displayDetails() {
        System.out.println("Car Model: " + model);
        System.out.println("Distance Travelled: " + distance + " km");
        System.out.println("Fuel Consumed: " + fuelUsed + " litres");
        System.out.println("Fuel Efficiency: " + calculateFuelEfficiency() + " km/l");
    }
}

// Main class
public class FuelEfficiencyCalculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter car model: ");
        String model = sc.nextLine();

        System.out.print("Enter distance travelled (in km): ");
        double distance = sc.nextDouble();

        System.out.print("Enter fuel used (in litres): ");
        double fuel = sc.nextDouble();

        // Create Car object
        Car car = new Car(model, distance, fuel);

        // Show results
        car.displayDetails();
    }
}
