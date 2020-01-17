package com.android.chapter31.utils;

// 四维向量
public class Quat4f {
	public float x, y, z, w;

	// 将一个Vector3f转换为Quat4f
	public final void set(Vector3f euler) {
		float angle = 0.0f;
		float sr, sp, sy, cr, cp, cy;

		angle = euler.z * 0.5f;
		sy = (float) Math.sin(angle);
		cy = (float) Math.cos(angle);
		angle = euler.y * 0.5f;
		sp = (float) Math.sin(angle);
		cp = (float) Math.cos(angle);
		angle = euler.x * 0.5f;
		sr = (float) Math.sin(angle);
		cr = (float) Math.cos(angle);

		x = sr * cp * cy - cr * sp * sy; // X
		y = cr * sp * cy + sr * cp * sy; // Y
		z = cr * cp * sy - sr * sp * cy; // Z
		w = cr * cp * cy + sr * sp * sy; // W
	}

	public final void interpolate(Quat4f q1, Quat4f q2, float alpha) {
		double dot, s1, s2, om, sinom;

		dot = q2.x * q1.x + q2.y * q1.y + q2.z * q1.z + q2.w * q1.w;

		if (dot < 0) {
			q1.x = -q1.x;
			q1.y = -q1.y;
			q1.z = -q1.z;
			q1.w = -q1.w;
			dot = -dot;
		}

		if ((1.0 - dot) > 0.000001f) {
			om = Math.acos(dot);
			sinom = Math.sin(om);
			s1 = Math.sin((1.0 - alpha) * om) / sinom;
			s2 = Math.sin(alpha * om) / sinom;
		} else {
			s1 = 1.0 - alpha;
			s2 = alpha;
		}
		w = (float) (s1 * q1.w + s2 * q2.w);
		x = (float) (s1 * q1.x + s2 * q2.x);
		y = (float) (s1 * q1.y + s2 * q2.y);
		z = (float) (s1 * q1.z + s2 * q2.z);
	}
}
