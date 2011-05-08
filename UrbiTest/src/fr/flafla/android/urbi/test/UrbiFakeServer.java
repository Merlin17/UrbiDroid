/**
 * 
 */
package fr.flafla.android.urbi.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
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
	public static interface Handler {
		public void handle(String msg);
	}

	public final List<Handler> handlers = new ArrayList<Handler>();

	public UrbiFakeServer(int port) throws IOException {
		ServerSocket server = new ServerSocket(port) {
			@Override
			public Socket accept() throws IOException {
				System.out.println("UrbiFakeServer.UrbiFakeServer(...).new ServerSocket() {...}.accept()");

				Socket socket = super.accept();
				ByteBuffer buffer = ByteBuffer.allocate(getReceiveBufferSize());

				while (socket.isConnected()) {
					buffer.clear();

					// Wait request
					while (socket.getChannel().read(buffer) == 0)
						;
					buffer.flip();

					String msg = new String(buffer.array());
					for (Handler handler : handlers) {
						handler.handle(msg);
					}
				}

				return socket;
			}


			@Override
			public void bind(SocketAddress address, int port) throws IOException {
				System.out.println("UrbiFakeServer.UrbiFakeServer(...).new ServerSocket() {...}.bind(" + address + ")");
				super.bind(address, port);
			}

		};
	}

}
