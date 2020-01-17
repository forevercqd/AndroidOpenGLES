package com.android.chapter31.ms3d;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

// 在不同平台读取各个数据类型的数据
public class LittleEndianDataInputStream extends FilterInputStream implements DataInput {

	private DataInputStream mDis;

	private int mPosition;

	public int getPosition() {
		return mPosition;
	}

	public LittleEndianDataInputStream(InputStream in) {
		super(in);
		mDis = new DataInputStream(in);

		mPosition = 0;
	}

	public boolean readBoolean() throws IOException {
		mPosition += 1;
		return this.mDis.readBoolean();
	}

	public byte readByte() throws IOException {
		mPosition += 1;
		return this.mDis.readByte();
	}

	public float readFloat() throws IOException, EOFException {
		mPosition += 4;
		return Float.intBitsToFloat(this.readInt());
	}

	public void readFully(byte[] b) throws IOException, EOFException {
		mPosition += b.length;
		this.mDis.readFully(b);
	}

	public void readFully(byte[] b, int off, int len) throws IOException, EOFException {
		mPosition += len;
		this.mDis.readFully(b, off, len);
	}

	public int readInt() throws IOException, EOFException {
		int res = 0;
		for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
			res |= (this.mDis.readByte() & 0xff) << shiftBy;
		}
		mPosition += 4;
		return res;
	}

	public long readLong() throws IOException, EOFException {
		long res = 0;
		for (int shiftBy = 0; shiftBy < 64; shiftBy += 8) {
			res |= (this.mDis.readByte() & 0xff) << shiftBy;
		}
		mPosition += 8;
		return res;
	}

	public short readShort() throws IOException, EOFException {
		final int low = readByte() & 0xff;
		final int high = readByte() & 0xff;
		mPosition += 2;
		return (short) (high << 8 | low);
	}

	public int readUnsignedByte() throws IOException, EOFException {
		mPosition += 1;
		return this.mDis.readUnsignedByte();
	}

	public int readUnsignedShort() throws IOException {
		final int low = readByte() & 0xff;
		final int high = readByte() & 0xff;
		mPosition += 2;
		return (high << 8 | low);
	}

	public int skipBytes(int n) throws IOException {
		mPosition += n;
		return this.mDis.skipBytes(n);
	}

	public char readChar() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public double readDouble() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String readLine() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String readUTF() throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}