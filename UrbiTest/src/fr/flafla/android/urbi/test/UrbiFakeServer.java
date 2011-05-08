/**
 * 
 */
package fr.flafla.android.urbi.test;

import static fr.flafla.android.urbi.log.LoggerFactory.logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is provided just for test.<br/>
 * This is a fake urbi server.
 * 
 * @author merlin
 * 
 */
public class UrbiFakeServer {
	/**
	 * Thread that manage one connection
	 */
	private final class UrbiFakeServerThread extends Thread {
		private final Socket socket;

		public UrbiFakeServerThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				runServer(socket);
			} catch (Exception e) {
				logger().e("UrbiFakeServer", "Error in server", e);
			}
		}
	}

	public static interface Handler {
		public void handle(String msg);
	}

	/** Server socket */
	private ServerSocket server;

	/** List of all handlers */
	public final List<Handler> handlers = new ArrayList<Handler>();

	private Thread mainThread;

	public UrbiFakeServer(int port) throws IOException {
		server = new ServerSocket(port);
		server.setReuseAddress(true);

		mainThread = new Thread() {
			public void run() {
				while (!server.isClosed()) {
					Socket socket;
					try {
						socket = server.accept();
						new UrbiFakeServerThread(socket).start();
					} catch (SocketException e) {
						logger().i("UrbiFakeServer", "Server closed");
					} catch (IOException e) {
						logger().e("UrbiFakeServer", "Error in server", e);
						throw new RuntimeException(e);
					}
				}
			}
		};

		mainThread.start();
	}

	public void stop() throws IOException {
		mainThread.interrupt();
		server.close();
	}

	/**
	 * The run server method.<br/>
	 * That accept a connection and read until socket disconnect
	 * @param server
	 * @throws SocketException
	 * @throws IOException
	 */
	private void runServer(Socket socket) throws SocketException, IOException {
		while (socket.isConnected()) {
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String msg = new String(input.readLine());
			for (Handler handler : handlers) {
				handler.handle(msg);
			}
		}
	}

}
