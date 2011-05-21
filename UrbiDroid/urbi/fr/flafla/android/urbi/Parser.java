/**
 * 
 */
package fr.flafla.android.urbi;

import static fr.flafla.android.urbi.log.LoggerFactory.logger;
import static java.lang.Math.min;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
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

	public static UMessage parse(ByteBuffer buffer, SocketChannel channel) throws ClosedChannelException {
		try {
			// int begin;

			// [0-9+:a-Z+] (BIN | msg)
			String deleted = getNextToken(channel, buffer, '[');
			if (deleted.length() > 0)
				logger().w("UrbiParser", "deleted : " + deleted);
			// Read time
			String time = getNextToken(channel, buffer, ':');
			// Read tag
			String tag = getNextToken(channel, buffer, ']');
			// Read Message
			String msg = getNextToken(channel, buffer, '\n');

			// Detect if message is binary or not
			Matcher matcher = detectBin.matcher(msg);
			if (matcher.find()) {
				// Read header
				Integer length = new Integer(matcher.group(1));
				msg = matcher.group(2);

				// Read binary
				return new UBinary(tag, time, msg, length, getArray(channel, buffer, length));
			} else {
				return new UMessage(tag, time, msg);
			}
		} catch (ClosedChannelException e) {
			throw e;
		} catch (BufferUnderflowException e) {
			logger().w("UrbiParser", "Buffer under flow on read urbi message", e);
			throw new UrbiException("Buffer under flow on read urbi message", e);
		} catch (IOException e) {
			logger().w("UrbiParser", "IOExcepetion was thrown on read urbi message", e);
			throw new UrbiException("IOExcepetion was thrown on read urbi message", e);
		}
	}

	/**
	 * This method get a string from buffer from begin to actual position
	 * @param buffer The buffer
	 * @param begin The offset position
	 * @return the string
	 */
	private static String getToken(ByteBuffer buffer, int begin) {
		return new String(buffer.array(), begin, buffer.arrayOffset() + buffer.position() - 1 - begin);
	}

	/**
	 * This method get the next token ended by the endChar.
	 * @param channel The socket channel
	 * @param buffer The byte buffer used to store
	 * @param endChar The last char (not included in the result)
	 * @return The token
	 * @throws IOException
	 */
	private static String getNextToken(SocketChannel channel, ByteBuffer buffer, char endChar) throws IOException {
		StringBuilder builder = new StringBuilder();
		// Read tag
		if (buffer.limit() == buffer.position()) {
			readNext(channel, buffer);
		}
		int begin = buffer.arrayOffset() + buffer.position();
		while (buffer.get() != endChar) {
			if (buffer.limit() == buffer.position()) {
				builder.append(getToken(buffer, begin));
				readNext(channel, buffer);
				begin = 0;
			}
		}

		builder.append(getToken(buffer, begin));

		return builder.toString();
	}

	private static void readNext(SocketChannel channel, ByteBuffer buffer) throws IOException {
		buffer.clear();
		while (channel.read(buffer) == 0)
			;
		buffer.flip();
	}

	/**
	 * This method read the channel to return a byte array corresponding to the length
	 * @param channel The socket channel to read
	 * @param buffer The byte buffer to help the read
	 * @param length The length of the returned array
	 * @return The byte array
	 * @throws IOException An {@link IOException} can be thrown
	 */
	private static byte[] getArray(SocketChannel channel, ByteBuffer buffer, int length) throws IOException {
		// 1. Read bytes in buffer
		byte[] bytes = new byte[length];
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
			buffer.get(bytes, pos, min(toRead, read));
			pos += read;
			toRead -= read;
		}

		return bytes;
	}
}
