package com.android.chapter31.utils;

//ÈýÎ¬ÏòÁ¿
public class Vector3f
{
	public float	x, y, z;

	public Vector3f()
	{

	}

	public Vector3f(float x, float y, float z)
	{
		set(x, y, z);
	}

	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(Vector3f v)
	{
		set(v.x, v.y, v.z);
	}

	public final void interpolate(Vector3f t1, Vector3f t2, float alpha)
	{
		this.x = (1 - alpha) * t1.x + alpha * t2.x;
		this.y = (1 - alpha) * t1.y + alpha * t2.y;
		this.z = (1 - alpha) * t1.z + alpha * t2.z;
	}

	public final void add(Vector3f t1)
	{
		this.x += t1.x;
		this.y += t1.y;
		this.z += t1.z;
	}

	public final void sub(Vector3f t1, Vector3f t2)
	{
		this.x = t1.x - t2.x;
		this.y = t1.y - t2.y;
		this.z = t1.z - t2.z;
	}

	public final void scale(float s)
	{
		this.x *= s;
		this.y *= s;
		this.z *= s;
	}

	public final void cross(Vector3f v1, Vector3f v2)
	{
		float x, y;

		x = v1.y * v2.z - v1.z * v2.y;
		y = v2.x * v1.z - v2.z * v1.x;
		this.z = v1.x * v2.y - v1.y * v2.x;
		this.x = x;
		this.y = y;
	}

	public void zero()
	{
		x = y = z = 0.0f;
	}

	public static float distance(Vector3f v0, Vector3f v1)
	{
		float dx = v0.x - v1.x;
		float dy = v0.y - v1.y;
		float dz = v0.z - v1.z;

		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}
