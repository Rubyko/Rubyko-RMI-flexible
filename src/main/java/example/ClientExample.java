package example;

import com.rubyko.rmi.RmiClient;

public class ClientExample {
 
	public static void main(String[] args) {
		// lookup the service with name "example", interface ServiceExample located at host localhost and port 6789
		final ServiceExample example = RmiClient.lookupService("192.168.9.77", 6789, "example", ServiceExample.class);
		// call the method concat and display the result
		try {
			System.out.println(1 == example.add(0, 1));
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

}
