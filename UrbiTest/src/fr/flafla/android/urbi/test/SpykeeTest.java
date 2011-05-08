/**
 * 
 */
package fr.flafla.android.urbi.test;

import java.io.IOException;

import org.junit.Test;

import fr.flafla.android.urbi.robot.Spykee;
import fr.flafla.android.urbi.test.UrbiFakeServer.Handler;

/**
 * Test the spykee client
 * 
 * @author merlin
 * 
 */
public class SpykeeTest {
	/**
	 * Test spykee movement implementation
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void test() throws IOException, InterruptedException {
		final int port = 3000;

		UrbiFakeServer server = new UrbiFakeServer(port);
		
		server.handlers.add(new Handler() {
			@Override
			public void handle(String msg) {
				System.out.println(msg);
			}
		});

		Spykee spykee = new Spykee("localhost", port);
		spykee.getAxes()[0].y.value = 5;
		spykee.getAxes()[1].y.value = 5;
		spykee.move();

		Thread.sleep(2000);

		spykee.stop();
	}
}
