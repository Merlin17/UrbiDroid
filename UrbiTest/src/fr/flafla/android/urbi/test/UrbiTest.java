package fr.flafla.android.urbi.test;

import junit.framework.TestCase;
import fr.flafla.android.urbi.UCallback;
import fr.flafla.android.urbi.UClient;
import fr.flafla.android.urbi.UMessage;

public class UrbiTest extends TestCase {
	private UClient client;

	@Override
	protected void setUp() throws Exception {
		client = new UClient("localhost", UClient.PORT);
	}

	public void test1() throws Exception {
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

	public static void main(String[] args) throws Exception {
		UrbiTest test = new UrbiTest();
		test.setUp();
		test.test1();

	}

}
