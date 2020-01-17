package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Vector3f;

//表示了某一帧时某个骨骼的旋转帧数据或平移帧数据
final class Keyframe {
	// 表示这一帧所处的时间，单位是秒。
	float mfTime;
	// 表示一个旋转或平移。如果是表示平移，那三个值分别是X,Y,Z轴上的平移值；如果是表示旋转，则这三个值表示了旋转的欧拉角
	Vector3f mvParam;

	public Keyframe() {
	}

	public void getValue(MS3DKeyFramePosition frame) {
		mfTime = frame.getTime();
		mvParam = new Vector3f(frame.getPosition().x, frame.getPosition().y, frame.getPosition().z);
	}

	public void getValue(MS3DKeyFrameRotation frame) {
		mfTime = frame.getTime();
		mvParam = new Vector3f(frame.getRotation().x, frame.getRotation().y, frame.getRotation().z);
	}

	public float getTime() {
		return mfTime;
	}

	public Vector3f getParameter() {
		return mvParam;
	}
}
