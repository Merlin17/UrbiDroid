/**
 * 
 */
package fr.flafla.android.urbi.test;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
	/** Order expected by the first test */
	private static String[] TEST1_EXPECTED = new String[] {
			"trackL.val=5|&trackR.val=5|;", "trackL.val=0|&trackR.val=0|;"
	};
	/** Order expected by the second test */
	private static String[] TEST2_EXPECTED = new String[] {
			"var uimg = Channel.new(\"uimg\")|;", "camera.format = 1|;", "camera.getSlot(\"val\").notifyChange(uobjects_handle, function() {camera.val})|;", "every(0.1s) {uimg<<camera.val},"
	};

	private static final class AssertHandler implements Handler {
		/** Array of expected command */
		String[] excpected;
		/** The index of the expected command */
		int index = 0;

		/**
		 * Constructor
		 * @param excpectedList Array of strings expected
		 */
		public AssertHandler(String[] excpectedList) {
			this.excpected = excpectedList;
		}

		@Override
		public void handle(String msg) {
			Assert.assertEquals(excpected[index++], msg);
		}
	}

	/**
	 * This handler print all message
	 */
	private static final class PrintHandler implements Handler {
		@Override
		public void handle(String msg) {
			System.out.println(msg);
		}
	}

	private Spykee spykee;
	private UrbiFakeServer server;

	@Before
	public void setUp() throws IOException {
		final int port = 3000;
		server = new UrbiFakeServer(port);
		spykee = new Spykee("localhost", port);

		server.handlers.clear();
		server.handlers.add(new PrintHandler());
	}

	@After
	public void tearDown() throws IOException, InterruptedException {
		Thread.sleep(2000);
		server.stop();
	}

	/**
	 * Test spykee movement implementation
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testMovement() throws IOException, InterruptedException {
		server.handlers.add(new AssertHandler(TEST1_EXPECTED));

		spykee.getAxes()[0].y.value = 5;
		spykee.getAxes()[1].y.value = 5;
		spykee.move();

		Thread.sleep(2000);

		spykee.stop();
	}

	/**
	 * This method test the spykee's camera
	 */
	@Test
	public void testCamera() {
		server.handlers.add(new AssertHandler(TEST2_EXPECTED));
		spykee.getCameras()[0].start();
	}
}
