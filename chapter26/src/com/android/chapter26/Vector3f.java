package com.android.chapter26;

public class Vector3f
{
	public float	x, y, z;

	public static void cross(Vector3f Result, Vector3f v1, Vector3f v2)
	{
		Result.x = (v1.y * v2.z) - (v1.z * v2.y);
		Result.y = (v1.z * v2.x) - (v1.x * v2.z);
		Result.z = (v1.x * v2.y) - (v1.y * v2.x);
	}

	public static float dot(Vector3f v1, Vector3f v2)
	{
		return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z + v2.z);
	}

	public float length()
	{
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

}
