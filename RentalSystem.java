import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;


public class RentalSystem {
	private static RentalSystem instance;
	private List<Vehicle> vehicles;
    private List<Customer> customers;
    private RentalHistory rentalHistory;
    
	private RentalSystem() {
		vehicles = new ArrayList<>();
		customers = new ArrayList<>();
		rentalHistory = new RentalHistory();
		loadData();
		
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

	public boolean addVehicle(Vehicle vehicle) {
	    if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
	        System.out.println("Duplicate license plate. Vehicle not added.");
	        return false;
	    }

	    vehicles.add(vehicle);
	    saveVehicle(vehicle);
	    return true;
	}

	public boolean addCustomer(Customer customer) {
	    if (findCustomerById(customer.getCustomerId()) != null) {
	        System.out.println("Duplicate customer ID. Customer not added.");
	        return false;
	    }

	    customers.add(customer);
	    saveCustomer(customer);
	    return true;
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
    
    private void loadData() {
    	loadVehicles();
    	loadCustomers();
    	loadRentalRecords();
    }
    
    private void loadVehicles() {
        try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String plate = parts[0];
                String make = parts[1];
                String model = parts[2];
                int year = Integer.parseInt(parts[3]);
                String type = parts[4];

                Vehicle vehicle = null;
                switch (type) {
                    case "Car":
                        vehicle = new Car(make, model, year, 5); // default seat count
                        break;
                    case "Motorcycle":
                        vehicle = new Motorcycle(make, model, year, false); // default sidecar
                        break;
                    case "Truck":
                        vehicle = new Truck(make, model, year, 1000); // default cargo
                        break;
                }

                if (vehicle != null) {
                    vehicle.setLicensePlate(plate);
                    vehicles.add(vehicle);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    }
    
    private void loadCustomers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];

                customers.add(new Customer(id, name));
            }
        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }
    
    private void loadRentalRecords() {
        try (BufferedReader reader = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int customerId = Integer.parseInt(parts[0]);
                String plate = parts[1];
                LocalDate date = LocalDate.parse(parts[2]);
                double amount = Double.parseDouble(parts[3]);
                String type = parts[4];

                Customer customer = findCustomerById(customerId);
                Vehicle vehicle = findVehicleByPlate(plate);

                if (vehicle != null && customer != null) {
                    RentalRecord record = new RentalRecord(vehicle, customer, date, amount, type);
                    rentalHistory.addRecord(record);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        }
    }
    
    
}