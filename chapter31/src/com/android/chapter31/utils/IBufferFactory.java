package com.android.chapter31.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

// Buffer工厂（工具）类，申请Buffer空间
public class IBufferFactory {
	/**
	 * 创建新的FloatBuffer对象
	 * @param numElements - float元素的个数
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
