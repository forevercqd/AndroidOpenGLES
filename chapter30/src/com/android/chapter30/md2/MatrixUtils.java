package com.android.chapter30.md2;

public class MatrixUtils
{
	//ת�þ���(��m��ŵ�result��)
	public static void transpose(float[][] m, float[][] result)
	{
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				result[j][i] = m[i][j];
	}

	// ��ʽ������,��λ��
	public static void normalize(float[] vector)
	{
		scalarMultiply(vector, 1 / magnitude(vector));
	}
	// ��ʽ������,��λ��
	public static void normalize(int[] vector)
	{
		scalarMultiply(vector, 1 / magnitude(vector));
	}
	// ��������
	public static void copy(float[] from, float[] to)
	{
		for (int i = 0; i < from.length; i++)
		{
			to[i] = from[i];
		}
	}

	// �������,������õ�result��
	public static void multiply(float[][] m1, float[][] m2, float[][] result)
	{
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				result[i][j] = m1[i][0] * m2[0][j] + m1[i][1] * m2[1][j] + m1[i][2] * m2[2][j] + m1[i][3] * m2[3][j];
			}
		}
	}
	
	// �������
	public static void scalarMultiply(float[] vector, float scalar)
	{
		for (int i = 0; i < vector.length; i++)
			vector[i] *= scalar;
	}
	// �������
	public static void scalarMultiply(int[] vector, int scalar)
	{
		for (int i = 0; i < vector.length; i++)
			vector[i] = FixedPointUtils.multiply(vector[i], scalar);
	}

	// ��ͬӳ�䣬��ͬ�任
	public static void identity(float[][] matrix)
	{
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				matrix[i][j] = (i == j) ? 1 : 0;
	}
	// ����˷�
	public static float dot(float[] v1, float[] v2)
	{
		float res = 0;
		for (int i = 0; i < v1.length; i++)
			res += v1[i] * v2[i];
		return res;
	}

	// ����Գ�
	public static void cross(float[] p1, float[] p2, float[] result)
	{
		result[0] = p1[1] * p2[2] - p2[1] * p1[2];
		result[1] = p1[2] * p2[0] - p2[2] * p1[0];
		result[2] = p1[0] * p2[1] - p2[0] * p1[1];
	}

	public static void cross(int[] p1, int[] p2, int[] result)
	{
		result[0] = FixedPointUtils.multiply(p1[1], p2[2]) - FixedPointUtils.multiply(p2[1], p1[2]);
		result[1] = FixedPointUtils.multiply(p1[2], p2[0]) - FixedPointUtils.multiply(p2[2], p1[0]);
		result[2] = FixedPointUtils.multiply(p1[0], p2[1]) - FixedPointUtils.multiply(p2[0], p1[1]);
	}

	// ����
	public static float magnitude(float[] vector)
	{
		return (float) Math
				.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
	}

	public static int magnitude(int[] vector)
	{
		return FixedPointUtils
				.sqrt(FixedPointUtils.multiply(vector[0], vector[0]) + FixedPointUtils
						.multiply(vector[1], vector[1]) + FixedPointUtils.multiply(vector[2],
						vector[2]));
	}
	
	// ���
	public static void multiply(float[][] matrix, float[] vector, float[] res)
	{
		for (int i = 0; i < 4; i++)
		{
			res[i] = matrix[i][0] * vector[0] + matrix[i][1] * vector[1] + matrix[i][2] * vector[2] + matrix[i][3] * vector[3];
		}
	}
	
	// �������
	public static void printMatrix(float[][] matrix)
	{
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
				System.out.print(matrix[i][j] + "\t");
			System.out.println();
		}
	}

	// ��λ��
	public static void homogenize(float[] pt)
	{
		scalarMultiply(pt, 1 / pt[3]);
	}

	// �������
	public static void printVector(float[] vec)
	{
		for (int i = 0; i < vec.length; i++)
			System.out.println(vec[i]);
	}

	// ��
	public static void minus(float[] a, float[] b, float[] result)
	{
		float[] res = (result == null) ? a : result;
		for (int i = 0; i < Math.min(a.length, b.length); i++)
			res[i] = a[i] - b[i];
	}

	public static void minus(int[] a, int[] b, int[] result)
	{
		int[] res = (result == null) ? a : result;
		for (int i = 0; i < Math.min(a.length, b.length); i++)
			res[i] = a[i] - b[i];
	}

	// ��
	public static void plus(float[] a, float[] b, float[] result)
	{
		float[] res = (result == null) ? a : result;
		for (int i = 0; i < a.length; i++)
			res[i] = a[i] + b[i];
	}

	public static void plus(int[] a, int[] b, int[] result)
	{
		int[] res = (result == null) ? a : result;
		for (int i = 0; i < a.length; i++)
			res[i] = a[i] + b[i];
	}
}
