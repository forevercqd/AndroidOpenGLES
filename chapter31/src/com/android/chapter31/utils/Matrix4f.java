package com.android.chapter31.utils;

// æÿ’Û
public class Matrix4f {
	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;

	public void setIdentity() {
		this.m00 = 1.0f;
		this.m01 = 0.0f;
		this.m02 = 0.0f;
		this.m03 = 0.0f;

		this.m10 = 0.0f;
		this.m11 = 1.0f;
		this.m12 = 0.0f;
		this.m13 = 0.0f;

		this.m20 = 0.0f;
		this.m21 = 0.0f;
		this.m22 = 1.0f;
		this.m23 = 0.0f;

		this.m30 = 0.0f;
		this.m31 = 0.0f;
		this.m32 = 0.0f;
		this.m33 = 1.0f;
	}

	public final void setTranslation(Vector3f trans) {
		m03 = trans.x;
		m13 = trans.y;
		m23 = trans.z;
	}
	
	public final void set(Matrix4f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = m1.m03;

        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = m1.m13;

        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = m1.m23;

        this.m30 = m1.m30;
        this.m31 = m1.m31;
        this.m32 = m1.m32;
        this.m33 = m1.m33;
    }
	
	public final void mul(Matrix4f m1) {
        float m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33;  // vars for temp result matrix

        m00 = this.m00 * m1.m00 + this.m01 * m1.m10 +
                this.m02 * m1.m20 + this.m03 * m1.m30;
        m01 = this.m00 * m1.m01 + this.m01 * m1.m11 +
                this.m02 * m1.m21 + this.m03 * m1.m31;
        m02 = this.m00 * m1.m02 + this.m01 * m1.m12 +
                this.m02 * m1.m22 + this.m03 * m1.m32;
        m03 = this.m00 * m1.m03 + this.m01 * m1.m13 +
                this.m02 * m1.m23 + this.m03 * m1.m33;

        m10 = this.m10 * m1.m00 + this.m11 * m1.m10 +
                this.m12 * m1.m20 + this.m13 * m1.m30;
        m11 = this.m10 * m1.m01 + this.m11 * m1.m11 +
                this.m12 * m1.m21 + this.m13 * m1.m31;
        m12 = this.m10 * m1.m02 + this.m11 * m1.m12 +
                this.m12 * m1.m22 + this.m13 * m1.m32;
        m13 = this.m10 * m1.m03 + this.m11 * m1.m13 +
                this.m12 * m1.m23 + this.m13 * m1.m33;

        m20 = this.m20 * m1.m00 + this.m21 * m1.m10 +
                this.m22 * m1.m20 + this.m23 * m1.m30;
        m21 = this.m20 * m1.m01 + this.m21 * m1.m11 +
                this.m22 * m1.m21 + this.m23 * m1.m31;
        m22 = this.m20 * m1.m02 + this.m21 * m1.m12 +
                this.m22 * m1.m22 + this.m23 * m1.m32;
        m23 = this.m20 * m1.m03 + this.m21 * m1.m13 +
                this.m22 * m1.m23 + this.m23 * m1.m33;

        m30 = this.m30 * m1.m00 + this.m31 * m1.m10 +
                this.m32 * m1.m20 + this.m33 * m1.m30;
        m31 = this.m30 * m1.m01 + this.m31 * m1.m11 +
                this.m32 * m1.m21 + this.m33 * m1.m31;
        m32 = this.m30 * m1.m02 + this.m31 * m1.m12 +
                this.m32 * m1.m22 + this.m33 * m1.m32;
        m33 = this.m30 * m1.m03 + this.m31 * m1.m13 +
                this.m32 * m1.m23 + this.m33 * m1.m33;

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }
	
	public final void mul(Matrix4f m1, Matrix4f m2) {
        if (this != m1 && this != m2) {

            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 +
                    m1.m02 * m2.m20 + m1.m03 * m2.m30;
            this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 +
                    m1.m02 * m2.m21 + m1.m03 * m2.m31;
            this.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 +
                    m1.m02 * m2.m22 + m1.m03 * m2.m32;
            this.m03 = m1.m00 * m2.m03 + m1.m01 * m2.m13 +
                    m1.m02 * m2.m23 + m1.m03 * m2.m33;

            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 +
                    m1.m12 * m2.m20 + m1.m13 * m2.m30;
            this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 +
                    m1.m12 * m2.m21 + m1.m13 * m2.m31;
            this.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 +
                    m1.m12 * m2.m22 + m1.m13 * m2.m32;
            this.m13 = m1.m10 * m2.m03 + m1.m11 * m2.m13 +
                    m1.m12 * m2.m23 + m1.m13 * m2.m33;

            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 +
                    m1.m22 * m2.m20 + m1.m23 * m2.m30;
            this.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 +
                    m1.m22 * m2.m21 + m1.m23 * m2.m31;
            this.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 +
                    m1.m22 * m2.m22 + m1.m23 * m2.m32;
            this.m23 = m1.m20 * m2.m03 + m1.m21 * m2.m13 +
                    m1.m22 * m2.m23 + m1.m23 * m2.m33;

            this.m30 = m1.m30 * m2.m00 + m1.m31 * m2.m10 +
                    m1.m32 * m2.m20 + m1.m33 * m2.m30;
            this.m31 = m1.m30 * m2.m01 + m1.m31 * m2.m11 +
                    m1.m32 * m2.m21 + m1.m33 * m2.m31;
            this.m32 = m1.m30 * m2.m02 + m1.m31 * m2.m12 +
                    m1.m32 * m2.m22 + m1.m33 * m2.m32;
            this.m33 = m1.m30 * m2.m03 + m1.m31 * m2.m13 +
                    m1.m32 * m2.m23 + m1.m33 * m2.m33;
        } else {
            float m00, m01, m02, m03,
                    m10, m11, m12, m13,
                    m20, m21, m22, m23,
                    m30, m31, m32, m33;  // vars for temp result matrix
            m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20 + m1.m03 * m2.m30;
            m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21 + m1.m03 * m2.m31;
            m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22 + m1.m03 * m2.m32;
            m03 = m1.m00 * m2.m03 + m1.m01 * m2.m13 + m1.m02 * m2.m23 + m1.m03 * m2.m33;

            m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20 + m1.m13 * m2.m30;
            m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21 + m1.m13 * m2.m31;
            m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22 + m1.m13 * m2.m32;
            m13 = m1.m10 * m2.m03 + m1.m11 * m2.m13 + m1.m12 * m2.m23 + m1.m13 * m2.m33;

            m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20 + m1.m23 * m2.m30;
            m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21 + m1.m23 * m2.m31;
            m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22 + m1.m23 * m2.m32;
            m23 = m1.m20 * m2.m03 + m1.m21 * m2.m13 + m1.m22 * m2.m23 + m1.m23 * m2.m33;

            m30 = m1.m30 * m2.m00 + m1.m31 * m2.m10 + m1.m32 * m2.m20 + m1.m33 * m2.m30;
            m31 = m1.m30 * m2.m01 + m1.m31 * m2.m11 + m1.m32 * m2.m21 + m1.m33 * m2.m31;
            m32 = m1.m30 * m2.m02 + m1.m31 * m2.m12 + m1.m32 * m2.m22 + m1.m33 * m2.m32;
            m33 = m1.m30 * m2.m03 + m1.m31 * m2.m13 + m1.m32 * m2.m23 + m1.m33 * m2.m33;

            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m03 = m03;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m30 = m30;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }
    }
	
	public final void set(Quat4f q1) {
        this.m00 = (1.0f - 2.0f * q1.y * q1.y - 2.0f * q1.z * q1.z);
        this.m10 = (2.0f * (q1.x * q1.y + q1.w * q1.z));
        this.m20 = (2.0f * (q1.x * q1.z - q1.w * q1.y));

        this.m01 = (2.0f * (q1.x * q1.y - q1.w * q1.z));
        this.m11 = (1.0f - 2.0f * q1.x * q1.x - 2.0f * q1.z * q1.z);
        this.m21 = (2.0f * (q1.y * q1.z + q1.w * q1.x));

        this.m02 = (2.0f * (q1.x * q1.z + q1.w * q1.y));
        this.m12 = (2.0f * (q1.y * q1.z - q1.w * q1.x));
        this.m22 = (1.0f - 2.0f * q1.x * q1.x - 2.0f * q1.y * q1.y);

        this.m03 = (float) 0.0;
        this.m13 = (float) 0.0;
        this.m23 = (float) 0.0;

        this.m30 = (float) 0.0;
        this.m31 = (float) 0.0;
        this.m32 = (float) 0.0;
        this.m33 = (float) 1.0;
    }
	
	public final void invTransform(Vector3f point, Vector3f pointOut) {
        Vector3f tmp = new Vector3f();
        tmp.x = point.x - m03;
        tmp.y = point.y - m13;
        tmp.z = point.z - m23;

        //transform normal
        invTransformRotate(tmp, pointOut);
    }
	
	public final void invTransformRotate(Vector3f normal, Vector3f normalOut) {
        float x, y;
        x = m00 * normal.x + m10 * normal.y + m20 * normal.z;
        y = m01 * normal.x + m11 * normal.y + m21 * normal.z;
        normalOut.z = m02 * normal.x + m12 * normal.y + m22 * normal.z;
        normalOut.x = x;
        normalOut.y = y;
    }
	
	public final void transform(Vector3f point, Vector3f pointOut) {
        float x, y;
        x = m00 * point.x + m01 * point.y + m02 * point.z + m03;
        y = m10 * point.x + m11 * point.y + m12 * point.z + m13;
        pointOut.z = m20 * point.x + m21 * point.y + m22 * point.z + m23;
        pointOut.x = x;
        pointOut.y = y;
    }
}
