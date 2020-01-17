package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Matrix4f;
import com.android.chapter31.utils.Vector3f;

//关节
public class Joint {
	// 名字
	public String mName;
	// ID
	public int mId;
	// 父节点ID
	public int mParentId;
	// 旋转数据
	Vector3f mLocalRotation = new Vector3f();
	// 平移数据
	Vector3f mLocalTranslation = new Vector3f();
	// 绝对矩阵
	Matrix4f mMatJointAbsolute = new Matrix4f();
	// 相对矩阵
	Matrix4f mMatJointRelative = new Matrix4f();
	// 全局矩阵
	Matrix4f mMatGlobal = new Matrix4f();
	// 旋转帧的数量
	public int mNumRotationKeyframes;
	// 平移帧的数量
	public int mNumTranslationKeyframes;
	// 所有的旋转帧
	Keyframe[] mpTranslationKeyframes;
	// 所有平移帧数
	Keyframe[] mpRotationKeyframes;

	// 初始化-单位化矩阵
	public Joint() {
		mMatJointAbsolute.setIdentity();
		mMatJointRelative.setIdentity();
		mMatGlobal.setIdentity();
	}
}