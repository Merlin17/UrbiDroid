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
 * @author merlin
 *
 */
public class Parser {
	public static final Pattern detectBin = Pattern.compile("BIN ([0-9]+) (.*)");

	public static UMessage parse(ByteBuffer buffer, SocketChannel channel) {
		// TODO manage BufferUnderflowException
		System.out.println("parse");

		try {
			int begin;

			// [0-9+:a-Z+] (BIN | msg)
			while (buffer.get() != '[')
				;
			begin = buffer.arrayOffset() + buffer.position();
			// TODO check char is ascii number
			char c;
			while ((c = (char) buffer.get()) != ':')
				if (c < '0' || c > '9')
					return null;
				;
			String time = getToken(buffer, begin);

			begin = buffer.arrayOffset() + buffer.position();
			while (buffer.get() != ']')
				;
			String tag = getToken(buffer, begin);

			begin = buffer.arrayOffset() + buffer.position();
			while (buffer.position() < buffer.limit() && buffer.get() != '\n')
				;
			String msg = getToken(buffer, begin);

			System.out.println("time : " + time + ", tag : " + tag + ", msg : " + msg);

			// Detect if message is binary or not
			Matcher matcher = detectBin.matcher(msg);
			if (matcher.find()) {
				// Read header
				Integer length = new Integer(matcher.group(1));
				System.out.println("binary : " + length + ", " + buffer);
				msg = matcher.group(2);

				// Read binary
				byte[] bytes = new byte[length];
				int toRead = length;
				int len = buffer.limit() - buffer.position();
				int pos = 0;
				buffer.get(bytes, pos, len);
				toRead -= len;

				while (toRead > 0) {
					buffer.clear();
					int read;
					while ((read = channel.read(buffer)) == 0)
						;
					System.out.println("read : " + buffer.limit() + ", " + read);
					buffer.flip();

					buffer.get(bytes, pos, read);
					pos += read;
					toRead -= read;
				}
				return new UBinary(tag, time, msg, bytes);
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
