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

	// ��x,y,z�ֱ�˳����ŵ�������
	public static void setXYZ(float[] vector, int offset, float x, float y, float z)
	{
		vector[offset] = x;
		vector[offset + 1] = y;
		vector[offset + 2] = z;
	}

	// ��x,y,z�ֱ��Ӧ����������ֵ�Լ�����ֵ����������
	public static void setXYZn(float[] vector, int offset, float x, float y, float z)
	{
		float r = (float) Math.sqrt(x * x + y * y + z * z);
		setXYZ(vector, offset, x / r, y / r, z / r);
	}

	// ��x,y�ֱ�˳����ŵ�������
	public static void setXY(float[] vector, int offset, float x, float y)
	{
		vector[offset] = x;
		vector[offset + 1] = y;
	}
}
