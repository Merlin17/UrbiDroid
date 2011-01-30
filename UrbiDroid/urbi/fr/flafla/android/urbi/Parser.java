/**
 * 
 */
package fr.flafla.android.urbi;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a parser to read message from urbi
 * 
 * @author merlin
 * 
 */
public final class Parser {
	public static final Pattern detectBin = Pattern.compile("BIN ([0-9]+) (.*)");

	public static UMessage parse(ByteBuffer buffer, SocketChannel channel) {
		// TODO manage BufferUnderflowException
		try {
			int begin;

			// [0-9+:a-Z+] (BIN | msg)
			while (buffer.get() != '[')
				;
			begin = buffer.arrayOffset() + buffer.position();

			// Read time
			char c;
			while ((c = (char) buffer.get()) != ':')
				// Check char is ascii number
				if (c < '0' || c > '9')
					return null;
				;
			String time = getToken(buffer, begin);

			// Read tag
			begin = buffer.arrayOffset() + buffer.position();
			while (buffer.get() != ']')
				;
			String tag = getToken(buffer, begin);

			// Read Message
			begin = buffer.arrayOffset() + buffer.position();
			while (buffer.position() < buffer.limit() && buffer.get() != '\n')
				;
			String msg = getToken(buffer, begin);

			// Detect if message is binary or not
			Matcher matcher = detectBin.matcher(msg);
			if (matcher.find()) {
				// Read header
				Integer length = new Integer(matcher.group(1));
				msg = matcher.group(2);

				// Read binary

				// 1. Read bytes in buffer
				byte[] bytes = new byte[length + 1];
				int len = buffer.limit() - buffer.position();
				buffer.get(bytes, 0, len);

				// 2. Read socket to get the end of the binary stream
				int pos = len;
				int toRead = length - len;
				while (toRead > 0) {
					buffer.clear();
					int read;
					while ((read = channel.read(buffer)) == 0)
						;
					buffer.flip();
					buffer.get(bytes, pos, read);
					pos += read;
					toRead -= read;
				}
				return new UBinary(tag, time, msg, length, bytes);
			} else {
				return new UMessage(tag, time, msg);
			}
		} catch (BufferUnderflowException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getToken(ByteBuffer buffer, int begin) {
		return new String(buffer.array(), begin, buffer.arrayOffset() + buffer.position() - 1 - begin);
	}
}
