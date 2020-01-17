package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Vector3f;

// 顶点
final class MS3DVertex {

	// 编辑器用标志
	private byte mFlags;
	// x,y,z的坐标
	private Vector3f mvLocation;
	// 该顶点所绑定的骨骼的ID号 （-1 ,没有骨头）
	private byte mBoneID;
	// 该顶点被引用的次数
	private byte mRefCount;
	public Vector3f mvTransformedLocation = new Vector3f();

	// 该顶点所绑定的骨骼的ID号索引
	public byte[] mpBoneIndexes;
	// 权重
	public byte[] mpWeights;

	public int mExtra;

	MS3DVertex() {
	}

	MS3DVertex(final float x, final float y, final float z, final byte boneID, final byte refCount, final byte flags) {
		this(new Vector3f(x, y, z), boneID, refCount, flags);
	}

	MS3DVertex(final Vector3f location, final byte boneID, final byte refCount, final byte flags) {
		setLocation(location);
		setBoneID(boneID);
		setRefCount(refCount);
		setFlags(flags);
	}

	// 得到顶点的位置
	final Vector3f getLocation() {
		return this.mvLocation;
	}

	// 设置顶点的位置
	final void setLocation(final Vector3f location) {
		this.mvLocation = location;
		mvTransformedLocation.set(location);
	}

	final byte getBoneID() {
		return this.mBoneID;
	}

	final void setBoneID(final byte boneID) {
		this.mBoneID = boneID;
	}

	final byte getRefCount() {
		return this.mRefCount;
	}

	final void setRefCount(final byte refCount) {
		this.mRefCount = refCount;
	}

	final byte getFlags() {
		return this.mFlags;
	}

	final void setFlags(final byte flags) {
		this.mFlags = flags;
	}
}