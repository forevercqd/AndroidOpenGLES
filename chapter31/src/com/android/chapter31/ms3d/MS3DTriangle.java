package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Vector3f;

// ms3d������
final class MS3DTriangle {

	// �༭���ñ�־
	private int mFlags;
	// ��������
	private int mpVertexIndices[];
	// ���㷨��
	private Vector3f mpVertexNormals[];
	// �������꣨UV��
	private float mps[];
	private float mpt[];
	// �����ε����ڵ�������
	private byte mSmoothingGroup;
	// ������
	private byte mGroupIndex;

	MS3DTriangle() {
	}

	final int getFlags() {
		return this.mFlags;
	}

	final void setFlags(final int flags) {
		this.mFlags = flags;
	}

	final int[] getVertexIndicies() {
		return this.mpVertexIndices;
	}

	final void setVertexIndicies(final int indicies[]) {
		if (indicies.length != 3) {
			throw new IllegalArgumentException("MS3DTriangle: indicies must be a length of 3");
		}
		this.mpVertexIndices = indicies;
	}

	final float[] getS() {
		return this.mps;
	}

	final void setS(final float s[]) {
		if (s.length != 3) {
			throw new IllegalArgumentException("MS3DTriangle: s must be a length of 3");
		}
		this.mps = s;
	}

	final byte getSmoothingGroup() {
		return this.mSmoothingGroup;
	}

	final void setSmoothingGroup(final byte smooth) {
		this.mSmoothingGroup = smooth;
	}

	final byte getGroupIndex() {
		return this.mGroupIndex;
	}

	final void setGroupIndex(final byte index) {
		this.mGroupIndex = index;
	}

	final float[] getT() {
		return this.mpt;
	}

	final void setT(final float t[]) {
		if (t.length != 3) {
			throw new IllegalArgumentException("MS3DTriangle: t must be a length of 3");
		}
		this.mpt = t;
	}

	final Vector3f[] getVertexNormals() {
		return this.mpVertexNormals;
	}

	final void setVertexNormals(final Vector3f normals[]) {
		if (normals.length != 3) {
			throw new IllegalArgumentException("MS3DTriangle: normals must be a length of 3");
		}
		this.mpVertexNormals = normals;
	}
}