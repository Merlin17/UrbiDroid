package fr.flafla.android.urbi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
import fr.flafla.android.urbi.robot.RobotException;

/**
 * This class implement an urbi client.
 * 
 * @author merlin
 * 
 */
public class UClient {

	/** Default urbi server port */
	public static final int PORT = 54000;
	/** Default urbi server ip */
	public static final String IP = "172.17.6.1";

	/** The charset decoder and decoder */
	private static Charset charset = Charset.defaultCharset();
	/** Charset decoder */
	public static CharsetDecoder decoder = charset.newDecoder();
	/** Charset encoder */
	public static CharsetEncoder encoder = charset.newEncoder();

	/** Socket */
	private SocketChannel channel;
	/** Indicate if the socket is initialized */
	protected boolean init = false;
	
	/** The urbi server port */
	protected final int port;
	/** The urbi server ip */
	protected final String ip;

	/** Thread to read the socket */
	Thread thread;

	/** Callback map */
	Map<String, List<UCallback>> callbacks = new HashMap<String, List<UCallback>>();

	/**
	 * Constructor with the default ip and port
	 */
	public UClient() {
		this(IP, PORT);
	}

	/**
	 * Constructor with ip and port
	 * @param ip The ip of the urbi server
	 * @param port The port of the urbi port
	 */
	public UClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}


	/**
	 * Ensure that the socket is initialized
	 */
	protected void ensureSocket() {
		try {
			if (!init) {
				// Ouverture du socket
				channel = SocketChannel.open();
				channel.configureBlocking(false);
				channel.connect(new InetSocketAddress(InetAddress.getByName(ip), port));
				init = channel.finishConnect();
			}
		} catch (UnknownHostException e) {
			Log.e("Robot", "Impossible de trouver le robot", e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Robot", "Erreur d'accès au robot", e);
		}
	}

	public void sendScript(String script) {
		try {
			ensureConnection();
			channel.write(ByteCharSequence.encoder.encode(CharBuffer.wrap(script + "\n")));
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Erreur d'accès au robot", e);
		}
	}

	/**
	 * Ensure that the socket is alive
	 * @throws IOException
	 */
	protected void ensureConnection() throws IOException {
		ensureSocket();
		if (channel == null || !channel.isConnected())
			throw new RobotException("No connection");
	}

	/**
	 * Add a callback to the internal list
	 * @param tag The tag that throw callback
	 * @param callback The callback thrown
	 */
	public void addCallback(String tag, UCallback callback) {
		synchronized (callbacks) {
			List<UCallback> list = callbacks.get(tag);
			if (list == null) {
				list = new ArrayList<UCallback>();
				list.add(callback);
				callbacks.put(tag, list);
			} else {
				list.add(callback);
			}
		}
		listenCallback();
	}

	/**
	 * Remove a callback from a tag
	 * @param tag The tag
	 * @param callback The callback
	 */
	public void removeCallback(String tag, UCallback callback) {
		synchronized (callbacks) {
			List<UCallback> list = callbacks.get(tag);
			if (list != null) {
				list.remove(callback);
				if (list.isEmpty())
					callbacks.remove(tag);
			}
		}
	}

	/**
	 * Launch the thread that read the socket and create UMessage
	 */
	protected void listenCallback() {
		// TODO stop thread
		if (thread == null || !thread.isAlive()) {
			ensureSocket();

			thread = new Thread() {
				public void run() {
					read();
				}
			};
		}
		if (!thread.isAlive()) {
			thread.start();
			System.out.println("thread launched");
		}
	}

	/**
	 * Loop that read the buffer
	 */
	protected void read() {
		try {
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				// Read the socket
				buffer.clear();
				while (channel.read(buffer) == 0);
				buffer.flip();
				CharBuffer charBuffer = decoder.decode(buffer);

				// Create the UMessage
				Pattern linePattern = Pattern.compile("\\[([0-9]+):([a-zA-Z]+)\\](.+)");
				Matcher matcher = linePattern.matcher(charBuffer.toString());
				if (matcher.find()) {
					int c = 1;
					String time = matcher.group(c++);
					String tag = matcher.group(c++);
					String msg = matcher.group(c++);
					notifyCallback(new UMessage(tag, time, msg));
				}
			}
		} catch (IOException e) {
			throw new UrbiException("Error thrown on read socket", e);
		}
	}

	/**
	 * Notify callback
	 * @param msg
	 */
	protected void notifyCallback(UMessage msg) {
		// Notify callback
		List<UCallback> listCallback = null;
		synchronized (callbacks) {
			List<UCallback> list = callbacks.get(msg.tag);
			if (list != null)
				listCallback = Collections.unmodifiableList(list);
		}
		if (listCallback != null) {
			for (UCallback callback : listCallback) {
				callback.handle(msg);
			}
		}
	}
}