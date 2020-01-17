package com.android.chapter30.md2;

import java.io.IOException;
import java.io.InputStream;

/**
 * 读取各种类型
 */
public class LittleEndianInputStream
{
	InputStream	is;

	public LittleEndianInputStream(InputStream is)
	{
		this.is = is;
	}

	// 读取整型
	public int readInt() throws IOException
	{
		int b1 = is.read();
		int b2 = is.read();
		int b3 = is.read();
		int b4 = is.read();

		return (b4 << 24) + ((b3 << 24) >>> 8) + ((b2 << 24) >>> 16) + ((b1 << 24) >>> 24);
	}

	public short readUnsignedShort() throws IOException
	{
		int b1 = is.read();
		int b2 = is.read();
		if (b1 < 0) b1 += 256;
		if (b2 < 0) b2 += 256;

		return (short) (b2 * 256 + b1);
	}

	public short readShort() throws IOException
	{
		int b1 = is.read();
		int b2 = is.read();
		if (b1 < 0) b1 += 256;

		return (short) (b2 * 256 + b1);
	}

	public char readUnsignedChar() throws IOException
	{
		int b = is.read();
		if (b < 0) b += 256;
		return (char) b;
	}

	public final float readFloat() throws IOException
	{
		return Float.intBitsToFloat(this.readInt());
	}

	public int read(byte[] buff) throws IOException
	{
		return is.read(buff);
	}

	public String readString(int length) throws IOException
	{
		byte[] buff = new byte[length];
		is.read(buff);
		return new String(buff);
	}
}
