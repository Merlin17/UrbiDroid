package fr.flafla.android.urbi.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import fr.flafla.android.urbi.UCallback;
import fr.flafla.android.urbi.UClient;
import fr.flafla.android.urbi.UMessage;
import fr.flafla.android.urbi.control.Camera.ImageHandler;
import fr.flafla.android.urbi.robot.Spykee;

public class UrbiTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
	}

	public void test1() throws Exception {
		UClient client = new UClient("localhost", UClient.PORT);
		client.addCallback("uimg", new UCallback() {
			@Override
			public boolean handle(UMessage msg) {
				System.out.println("changement de la valeur uimg : " + msg.tag + ", " + msg.time + ", " + new String(msg.msg));
				return true;
			}
		});

		System.out.println("retour");

		client.sendScript("var uimg = Channel.new(\"uimg\")|;");
		client.sendScript("var t=UObject.new|;");
		client.sendScript("t.setSlot(\"test\",1)|;");
		client.sendScript("tag:every (1s) {t.test=t.test+1;uimg<<t.test};");

		return;
	}

	public void testSpykee() throws Exception {
		// Directory to test images
		final File dir = new File("/tmp/test/");
		dir.mkdirs();

		// Init
		Spykee robot = new Spykee("192.168.1.14", UClient.PORT);
		robot.getCameras()[0].addHandler(new ImageHandler() {
			long last = System.currentTimeMillis();
			int img = 1;
			@Override
			public void handle(InputStream bitmap) {
				System.out.println("New bitmap from spykee camera");
				try {
					FileOutputStream out = new FileOutputStream(new File(dir, "img" + (img++) + ".jpg"));
					byte[] b = new byte[1024];
					while ((bitmap.read(b)) > 0)
						out.write(b);
					out.close();
					System.out.println("time : " + (System.currentTimeMillis() - last));
					last = System.currentTimeMillis();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
		robot.getCameras()[0].start(25);
		





		// Move
		try {
			for (int i = 0; i < 10; ++i) {
				robot.getAxes()[0].y.value = 10 * (i % 2 * 2 - 1);
				robot.getAxes()[1].y.value = 10 * (i % 2 * 2 - 1);
				robot.move();
				Thread.sleep(250);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Thread.sleep(1000);

		robot.stop();

		robot.getCameras()[0].stop();
	}

	public static void main(String[] args) throws Exception {
		UrbiTest test = new UrbiTest();
		// test.setUp();
		// test.test1();
		test.testSpykee();

	}

}
