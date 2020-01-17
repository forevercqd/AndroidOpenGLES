package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Matrix4f;
import com.android.chapter31.utils.Vector3f;

//�ؽ�
public class Joint {
	// ����
	public String mName;
	// ID
	public int mId;
	// ���ڵ�ID
	public int mParentId;
	// ��ת����
	Vector3f mLocalRotation = new Vector3f();
	// ƽ������
	Vector3f mLocalTranslation = new Vector3f();
	// ���Ծ���
	Matrix4f mMatJointAbsolute = new Matrix4f();
	// ��Ծ���
	Matrix4f mMatJointRelative = new Matrix4f();
	// ȫ�־���
	Matrix4f mMatGlobal = new Matrix4f();
	// ��ת֡������
	public int mNumRotationKeyframes;
	// ƽ��֡������
	public int mNumTranslationKeyframes;
	// ���е���ת֡
	Keyframe[] mpTranslationKeyframes;
	// ����ƽ��֡��
	Keyframe[] mpRotationKeyframes;

	// ��ʼ��-��λ������
	public Joint() {
		mMatJointAbsolute.setIdentity();
		mMatJointRelative.setIdentity();
		mMatGlobal.setIdentity();
	}
}