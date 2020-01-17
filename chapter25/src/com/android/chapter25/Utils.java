package com.android.chapter25;

import android.graphics.Matrix;

public final class Utils
{
	private static Matrix	yFlipMatrix;

	static
	{
		yFlipMatrix = new Matrix();
		yFlipMatrix.postScale(-1, 1); // flip Y axis
	}

	// 将x,y,z分别按顺序填放到数组中
	public static void setXYZ(float[] vector, int offset, float x, float y, float z)
	{
		vector[offset] = x;
		vector[offset + 1] = y;
		vector[offset + 2] = z;
	}

	// 将x,y,z分别对应的正、余弦值以及正切值放入数组中
	public static void setXYZn(float[] vector, int offset, float x, float y, float z)
	{
		float r = (float) Math.sqrt(x * x + y * y + z * z);
		setXYZ(vector, offset, x / r, y / r, z / r);
	}

	// 将x,y分别按顺序填放到数组中
	public static void setXY(float[] vector, int offset, float x, float y)
	{
		vector[offset] = x;
		vector[offset + 1] = y;
	}
}
