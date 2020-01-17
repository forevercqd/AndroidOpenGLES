package com.android.chapter31.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

// Buffer���������ߣ��࣬����Buffer�ռ�
public class IBufferFactory {
	/**
	 * �����µ�FloatBuffer����
	 * @param numElements - floatԪ�صĸ���
	 * @return Buffer
	 */
	public static FloatBuffer newFloatBuffer(int numElements) {
		ByteBuffer bb = ByteBuffer.allocateDirect(numElements * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
        fb.position(0);
        return fb;
    }
}
