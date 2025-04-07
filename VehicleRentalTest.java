
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

public class VehicleRentalTest {
	
	@Test
	public void testLicensePlateValidation() {
		assertDoesNotThrow(() -> {
	        Vehicle v1 = new Car("Toyota", "Camry", 2020, 4);
	        v1.setLicensePlate("AAA100");

	        Vehicle v2 = new Motorcycle("Yamaha", "R3", 2022, false);
	        v2.setLicensePlate("ABC567");

	        Vehicle v3 = new Truck("Ford", "F150", 2019, 1200);
	        v3.setLicensePlate("ZZZ999");
	    });
		
		assertThrows(IllegalArgumentException.class, () -> {
	        Vehicle v = new Car("Ford", "Focus", 2018, 5);
	        v.setLicensePlate("");
	    });

	    assertThrows(IllegalArgumentException.class, () -> {
	        Vehicle v = new Car("Ford", "Focus", 2018, 5);
	        v.setLicensePlate(null);
	    });

	    assertThrows(IllegalArgumentException.class, () -> {
	        Vehicle v = new Car("Ford", "Focus", 2018, 5);
	        v.setLicensePlate("AAA1000");
	    });

	    assertThrows(IllegalArgumentException.class, () -> {
	        Vehicle v = new Car("Ford", "Focus", 2018, 5);
	        v.setLicensePlate("ZZZ99");
	    });
	}
	
	@Test
	public void testRentAndReturnVehicle() {
		Vehicle car = new Car("Toyota", "Camry", 2022, 5);
	    car.setLicensePlate("TST123");
	    Customer customer = new Customer(1, "Test User");
	    
	    RentalSystem rentalSystem = RentalSystem.getInstance();
	    
	    rentalSystem.addVehicle(car);
	    rentalSystem.addCustomer(customer);
	    
	    assertEquals(Vehicle.VehicleStatus.AVAILABLE, car.getStatus());
	    
	    boolean rentSuccess = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 100.0);
	    assertTrue(rentSuccess);
	    assertEquals(Vehicle.VehicleStatus.RENTED, car.getStatus());
	    
	    boolean rentAgain = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 100.0);
	    assertFalse(rentAgain);
	    
	    boolean returnSuccess = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 0.0);
	    assertTrue(returnSuccess);
	    assertEquals(Vehicle.VehicleStatus.AVAILABLE, car.getStatus());
	    
	    boolean returnAgain = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 0.0);
	    assertFalse(returnAgain);
	}
	
	@Test
	public void testSingletonRentalSystem() throws Exception {
		Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		
		constructor.setAccessible(true);
		
		try {
			constructor.newInstance();
			fail("Expected an exception when instantiating RentalSystem via reflection");
		} catch (Exception e) {
			assertTrue(true);
		}
		
		RentalSystem instance = RentalSystem.getInstance();
		assertNotNull(instance);
	}
}