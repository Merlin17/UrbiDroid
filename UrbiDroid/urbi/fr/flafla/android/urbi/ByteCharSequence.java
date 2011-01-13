/**
 * 
 */
package fr.flafla.android.urbi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;


/**
 * 8ko buffer readable
 * 
 * @author merlin
 * 
 */
public class ByteCharSequence implements CharSequence {

	/** Buffer */
	final ByteBuffer buffer;

	private static Charset charset = Charset.defaultCharset();
	public static CharsetDecoder decoder = charset.newDecoder();
	public static CharsetEncoder encoder = charset.newEncoder();

	// /** Offset **/
	// int offset;
	// /** Length */
	// int length;

	public ByteCharSequence() {
		buffer = ByteBuffer.allocate(1024);
		// length = 0;
		// offset = 0;
	}

	public ByteCharSequence(ByteBuffer buffer, int offset, int length) {
		this.buffer = buffer.duplicate();
		this.buffer.position(offset);
		this.buffer.limit(length);
	}

	public char charAt(int index) {
		return buffer.getChar(index);
	}

	public int length() {
		return buffer.limit();
	}

	public CharSequence subSequence(int start, int end) {
		if (end < length())
			return new ByteCharSequence(buffer, start, end);
		else
			throw new IndexOutOfBoundsException();
	}

	// public void read(InputStream is) throws IOException {
	// int read = is.read(buffer);
	// length = read;
	// offset = 0;
	// }

	@Override
	public String toString() {
		return new String(buffer.array());
	}

	public void read(SocketChannel channel) throws IOException {
		channel.read(buffer);
	}
}
