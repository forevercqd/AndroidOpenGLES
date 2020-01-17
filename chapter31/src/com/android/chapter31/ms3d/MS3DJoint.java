package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Vector3f;

//骨骼
final class MS3DJoint {

	// 编辑器标志
	private byte mFlags;
	// Joint的名字
	private String mName;
	// Joint的父Joint名字
	private String mParent;
	// 初始旋转
	private Vector3f mvRotation;
	// 初始平移
	private Vector3f mvPosition;
	// 旋转帧的数量
	private short mNumKeyFramesRot;
	// 平移帧的数量
	private short mNumKeyFramesTrans;
	// 所有的旋转帧(数组大小取决于第mNumKeyFramesRot的值)
	private MS3DKeyFrameRotation mpKeyFramesRot[];
	// 所有的平移帧(数组大小取决于第mNumKeyFramesTrans的值)
	private MS3DKeyFramePosition mpKeyFramesPos[];

	private String mComment;

	public String getComment() {
		return mComment;
	}

	public void setComment(String comment) {
		this.mComment = comment;
	}

	MS3DJoint() {
	}

	MS3DJoint(final String name, final String parent, final Vector3f rotation, final Vector3f position, final MS3DKeyFrameRotation keyFramesRot[],
	        final MS3DKeyFramePosition keyFramesPos[]) {
		setName(name);
		setParentName(parent);
		setRotation(rotation);
		setPosition(position);
		setRotationKeyFrames(keyFramesRot);
		setPositionKeyFrames(keyFramesPos);
	}

	final String getName() {
		return this.mName;
	}

	final void setName(final String name) {
		if (name.length() > 32) {
			throw new IllegalArgumentException("MS3DJoint: name is " + name.length() + ", can only be 32");
		}
		this.mName = name;
	}

	final String getParentName() {
		if (mName.length() > 32) {
			throw new IllegalArgumentException("MS3DJoint: parent name is " + mName.length() + ", can only be 32");
		}
		return this.mParent;
	}

	final void setParentName(final String name) {
		this.mParent = name;
	}

	final byte getFlags() {
		return this.mFlags;
	}

	final void setFlags(final byte flags) {
		this.mFlags = flags;
	}

	final Vector3f getPosition() {
		return this.mvPosition;
	}

	final void setPosition(final Vector3f position) {
		this.mvPosition = position;
	}

	final Vector3f getRotation() {
		return this.mvRotation;
	}

	final void setRotation(final Vector3f rotation) {
		this.mvRotation = rotation;
	}

	final short getRotationKeyFramesCount() {
		return (short) this.mpKeyFramesRot.length;
	}

	final short getPositionKeyFramesCount() {
		return (short) this.mpKeyFramesPos.length;
	}

	final MS3DKeyFrameRotation[] getRotationKeyFrames() {
		return this.mpKeyFramesRot;
	}

	final void setRotationKeyFrames(final MS3DKeyFrameRotation frames[]) {
		this.mpKeyFramesRot = frames;
	}

	final MS3DKeyFramePosition[] getPositionKeyFrames() {
		return this.mpKeyFramesPos;
	}

	final void setPositionKeyFrames(final MS3DKeyFramePosition frames[]) {
		this.mpKeyFramesPos = frames;
	}
}