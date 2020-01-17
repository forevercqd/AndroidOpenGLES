package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Vector3f;

//骨骼的旋转帧数
final class MS3DKeyFrameRotation {

	// 这一帧所处的时间，单位是秒。
	private float mfTime;
	// 旋转的欧拉角
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