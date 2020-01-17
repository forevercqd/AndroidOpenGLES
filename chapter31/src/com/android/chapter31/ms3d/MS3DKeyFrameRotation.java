package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Vector3f;

//��������ת֡��
final class MS3DKeyFrameRotation {

	// ��һ֡������ʱ�䣬��λ���롣
	private float mfTime;
	// ��ת��ŷ����
	private Vector3f mvRotation;

	MS3DKeyFrameRotation() {
	}

	MS3DKeyFrameRotation(final float x, final float y, final float z, final float time) {
		this(new Vector3f(x, y, z), time);
	}

	MS3DKeyFrameRotation(final Vector3f rotation, final float time) {
		setRotation(rotation);
		setTime(time);
	}

	final float getTime() {
		return this.mfTime;
	}

	final void setTime(final float time) {
		this.mfTime = time;
	}

	final Vector3f getRotation() {
		return this.mvRotation;
	}

	final void setRotation(final Vector3f rotation) {
		this.mvRotation = rotation;
	}
}