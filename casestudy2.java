import java.sql.Connection; //to connect mysql with java
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner; //for input 
public class casestudy2 {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/food_delivery_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "rhehsjjfh"; //providing gibberish password 
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Number of customers (N): ");
            int totalCustomers = scanner.nextInt();
            System.out.print("Number of drivers (M): ");
            int totalDrivers = scanner.nextInt();
            List<int[]> orderInputs = new ArrayList<>();
            System.out.println("\nEnter the details for each customer:");
            for (int i = 0; i < totalCustomers; i++) {
                System.out.printf("Enter Order Time and Travel Time for Customer C%d like 10 10: ", i + 1);
                int orderTime = scanner.nextInt();
                int travelTime = scanner.nextInt();
                orderInputs.add(new int[]{orderTime, travelTime});}
            Delivery(totalCustomers, totalDrivers, orderInputs);} 
            catch (InputMismatchException e) {
            System.err.println("Dont put quotes ,put numbers only with space in between");} 
            catch (Exception e) {
            System.err.println("Error hogaya");
            e.printStackTrace();}}
    public static void Delivery(int totalCustomers, int totalDrivers, List<int[]> orderInputs) {
        try (Connection dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("\n  Connected to the database.");
            Statement statement = dbConnection.createStatement();
            System.out.println("New inputs to be taken , so removing the old records:");
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            statement.executeUpdate("TRUNCATE TABLE assignments"); 
            statement.executeUpdate("TRUNCATE TABLE customers");
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
            System.out.println("Insert new record..nice");
            String insertCustomerSql = "INSERT INTO customers (customer_id, order_placement_time, travel_time) VALUES (?, ?, ?)";
            try (PreparedStatement prepStatement = dbConnection.prepareStatement(insertCustomerSql)) {
                for (int i = 0; i < totalCustomers; i++) {
                    prepStatement.setInt(1, i + 1);
                    prepStatement.setInt(2, orderInputs.get(i)[0]);
                    prepStatement.setInt(3, orderInputs.get(i)[1]);
                    prepStatement.addBatch();
                }
                prepStatement.executeBatch();
            }
            System.out.println("Ready..steady..");
            int[] driverAvailabilityTimes = new int[totalDrivers];
            String fetchCustomersSql = "SELECT customer_id, order_placement_time, travel_time FROM customers ORDER BY customer_id";
            ResultSet resultSet = statement.executeQuery(fetchCustomersSql);
            String insertAssignmentSql = "INSERT INTO assignments (customer_id, assignment_result) VALUES (?, ?)";
            PreparedStatement assignmentStatement = dbConnection.prepareStatement(insertAssignmentSql);
            while (resultSet.next()) {
                int customerId = resultSet.getInt("customer_id");
                int orderTime = resultSet.getInt("order_placement_time");
                int travelTime = resultSet.getInt("travel_time");
                boolean wasDriverAssigned = false;
                for (int driverIndex = 0; driverIndex < totalDrivers; driverIndex++) {
                    if (driverAvailabilityTimes[driverIndex] <= orderTime) { 
                        int driverId = driverIndex + 1;
                        System.out.printf("   - Assigning Driver D%d to Customer C%d.%n", driverId, customerId);
                        driverAvailabilityTimes[driverIndex] = orderTime + travelTime;
                        assignmentStatement.setInt(1, customerId);
                        assignmentStatement.setString(2, "D" + driverId);
                        assignmentStatement.executeUpdate();
                        wasDriverAssigned = true;
                        break;
                    }}
                if (!wasDriverAssigned) {
                    System.out.printf("   - No drivers available for Customer C%d. Order cancelled.%n", customerId);
                    assignmentStatement.setInt(1, customerId);
                    assignmentStatement.setString(2, "No Food, sorry");
                    assignmentStatement.executeUpdate();
                }}
            System.out.println("Each record complete");
            String fetchResultsSql = "SELECT customer_id, assignment_result FROM assignments ORDER BY customer_id";
            ResultSet finalResults = statement.executeQuery(fetchResultsSql);
            while (finalResults.next()) {
                System.out.printf("C%d - %s%n",
                        finalResults.getInt("customer_id"),
                        finalResults.getString("assignment_result"));}
            finalResults.close();
            assignmentStatement.close();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Database operation error");
            e.printStackTrace();}}
}


