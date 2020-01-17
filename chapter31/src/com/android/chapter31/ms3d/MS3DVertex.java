package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Vector3f;

// ����
final class MS3DVertex {

	// �༭���ñ�־
	private byte mFlags;
	// x,y,z������
	private Vector3f mvLocation;
	// �ö������󶨵Ĺ�����ID�� ��-1 ,û�й�ͷ��
	private byte mBoneID;
	// �ö��㱻���õĴ���
	private byte mRefCount;
	public Vector3f mvTransformedLocation = new Vector3f();

	// �ö������󶨵Ĺ�����ID������
	public byte[] mpBoneIndexes;
	// Ȩ��
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

	// �õ������λ��
	final Vector3f getLocation() {
		return this.mvLocation;
	}

	// ���ö����λ��
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