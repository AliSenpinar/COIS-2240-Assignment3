import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;


public class RentalSystem {
	private static RentalSystem instance;
	private List<Vehicle> vehicles;
    private List<Customer> customers;
    private RentalHistory rentalHistory;
    
	private RentalSystem() {
		vehicles = new ArrayList<>();
		customers = new ArrayList<>();
		rentalHistory = new RentalHistory();
		
	}
	public static RentalSystem getInstance() {
		if (instance == null) {
			instance = new RentalSystem();
		}
		return instance;
	}
	
	public void saveVehicle(Vehicle vehicle) {
		try (FileWriter writer = new FileWriter("vehicles.txt", true)) {
			writer.write(vehicle.getLicensePlate() + "," +
	                vehicle.getMake() + "," +
	                vehicle.getModel() + "," +
	                vehicle.getYear() + "," +
	                vehicle.getClass().getSimpleName() + "\n");
		} catch (IOException e) {
			System.out.println("Error saving vehicle: " + e.getMessage());
		}
	}

	public void saveCustomer(Customer customer) {
	    try (FileWriter writer = new FileWriter("customers.txt", true)) {
	        writer.write(customer.getCustomerId() + "," +
	                     customer.getCustomerName() + "\n");
	    } catch (IOException e) {
	        System.out.println("Error saving customer: " + e.getMessage());
	    }
	}

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }
    
    public void saveRecord(RentalRecord record) {
        try (FileWriter writer = new FileWriter("rental_records.txt", true)) {
            writer.write(record.getCustomer().getCustomerId() + "," +
                         record.getVehicle().getLicensePlate() + "," +
                         record.getDate() + "," +
                         record.getAmount() + "," +
                         record.getType() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayAvailableVehicles() {
    	System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
    	System.out.println("---------------------------------------------------------------------------------");
    	 
        for (Vehicle v : vehicles) {
            if (v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println("|     " + (v instanceof Car ? "Car          " : "Motorcycle   ") + "|\t" + v.getLicensePlate() + "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|\t");
            }
        }
        System.out.println();
    }
    
    public void displayAllVehicles() {
        for (Vehicle v : vehicles) {
            System.out.println("  " + v.getInfo());
        }
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }

    public Customer findCustomerByName(String name) {
        for (Customer c : customers)
            if (c.getCustomerName().equalsIgnoreCase(name))
                return c;
        return null;
    }
}