package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Vector3f;

//��ʾ��ĳһ֡ʱĳ����������ת֡���ݻ�ƽ��֡����
final class Keyframe {
	// ��ʾ��һ֡������ʱ�䣬��λ���롣
	float mfTime;
	// ��ʾһ����ת��ƽ�ơ�����Ǳ�ʾƽ�ƣ�������ֵ�ֱ���X,Y,Z���ϵ�ƽ��ֵ������Ǳ�ʾ��ת����������ֵ��ʾ����ת��ŷ����
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
