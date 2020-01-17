package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Vector3f;

//骨骼的平移帧数
final class MS3DKeyFramePosition {

	// 这一帧所处的时间，单位是秒。
	private float mfTime;
	// X,Y,Z轴上的平移值；
	private Vector3f mvPosition;

	MS3DKeyFramePosition() {
	}

	MS3DKeyFramePosition(final float x, final float y, final float z, final float time) {
		this(new Vector3f(x, y, z), time);
	}

	MS3DKeyFramePosition(final Vector3f position, final float time) {
		setPosition(position);
		setTime(time);
	}

	final float getTime() {
		return this.mfTime;
	}

	final void setTime(final float time) {
		this.mfTime = time;
	}

	final Vector3f getPosition() {
		return this.mvPosition;
	}

	final void setPosition(final Vector3f position) {
		this.mvPosition = position;
	}
}