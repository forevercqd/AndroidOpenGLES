package com.android.chapter23;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public class BufferUtil
{
	public static final int	SIZEOF_BYTE		= 1;
	public static final int	SIZEOF_SHORT	= 2;
	public static final int	SIZEOF_INT		= 4;
	public static final int	SIZEOF_FLOAT	= 4;
	public static final int	SIZEOF_LONG		= 8;
	public static final int	SIZEOF_DOUBLE	= 8;


	public static ByteBuffer newByteBuffer(int paramInt)
	{
		ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(paramInt);
		localByteBuffer.order(ByteOrder.nativeOrder());
		return localByteBuffer;
	}


	public static DoubleBuffer newDoubleBuffer(int paramInt)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramInt * 8);
		return localByteBuffer.asDoubleBuffer();
	}


	public static FloatBuffer newFloatBuffer(int paramInt)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramInt * 4);
		return localByteBuffer.asFloatBuffer();
	}


	public static IntBuffer newIntBuffer(int paramInt)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramInt * 4);
		return localByteBuffer.asIntBuffer();
	}


	public static LongBuffer newLongBuffer(int paramInt)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramInt * 8);
		return localByteBuffer.asLongBuffer();
	}


	public static ShortBuffer newShortBuffer(int paramInt)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramInt * 2);
		return localByteBuffer.asShortBuffer();
	}


	public static ByteBuffer copyByteBuffer(ByteBuffer paramByteBuffer)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramByteBuffer.remaining());
		paramByteBuffer.mark();
		localByteBuffer.put(paramByteBuffer);
		paramByteBuffer.reset();
		localByteBuffer.rewind();
		return localByteBuffer;
	}


	public static DoubleBuffer copyDoubleBuffer(DoubleBuffer paramDoubleBuffer)
	{
		return copyDoubleBufferAsByteBuffer(paramDoubleBuffer).asDoubleBuffer();
	}


	public static FloatBuffer copyFloatBuffer(FloatBuffer paramFloatBuffer)
	{
		return copyFloatBufferAsByteBuffer(paramFloatBuffer).asFloatBuffer();
	}


	public static IntBuffer copyIntBuffer(IntBuffer paramIntBuffer)
	{
		return copyIntBufferAsByteBuffer(paramIntBuffer).asIntBuffer();
	}


	public static LongBuffer copyLongBuffer(LongBuffer paramLongBuffer)
	{
		return copyLongBufferAsByteBuffer(paramLongBuffer).asLongBuffer();
	}


	public static ShortBuffer copyShortBuffer(ShortBuffer paramShortBuffer)
	{
		return copyShortBufferAsByteBuffer(paramShortBuffer).asShortBuffer();
	}


	public static ByteBuffer copyDoubleBufferAsByteBuffer(DoubleBuffer paramDoubleBuffer)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramDoubleBuffer.remaining() * 8);
		paramDoubleBuffer.mark();
		localByteBuffer.asDoubleBuffer().put(paramDoubleBuffer);
		paramDoubleBuffer.reset();
		localByteBuffer.rewind();
		return localByteBuffer;
	}


	public static ByteBuffer copyFloatBufferAsByteBuffer(FloatBuffer paramFloatBuffer)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramFloatBuffer.remaining() * 4);
		paramFloatBuffer.mark();
		localByteBuffer.asFloatBuffer().put(paramFloatBuffer);
		paramFloatBuffer.reset();
		localByteBuffer.rewind();
		return localByteBuffer;
	}


	public static ByteBuffer copyIntBufferAsByteBuffer(IntBuffer paramIntBuffer)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramIntBuffer.remaining() * 4);
		paramIntBuffer.mark();
		localByteBuffer.asIntBuffer().put(paramIntBuffer);
		paramIntBuffer.reset();
		localByteBuffer.rewind();
		return localByteBuffer;
	}


	public static ByteBuffer copyLongBufferAsByteBuffer(LongBuffer paramLongBuffer)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramLongBuffer.remaining() * 8);
		paramLongBuffer.mark();
		localByteBuffer.asLongBuffer().put(paramLongBuffer);
		paramLongBuffer.reset();
		localByteBuffer.rewind();
		return localByteBuffer;
	}


	public static ByteBuffer copyShortBufferAsByteBuffer(ShortBuffer paramShortBuffer)
	{
		ByteBuffer localByteBuffer = newByteBuffer(paramShortBuffer.remaining() * 2);
		paramShortBuffer.mark();
		localByteBuffer.asShortBuffer().put(paramShortBuffer);
		paramShortBuffer.reset();
		localByteBuffer.rewind();
		return localByteBuffer;
	}
}