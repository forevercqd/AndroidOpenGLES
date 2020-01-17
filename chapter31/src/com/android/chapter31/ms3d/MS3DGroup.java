package com.android.chapter31.ms3d;

// �����飩
public class MS3DGroup {

	// �༭���ñ�־
	private byte mFlags;
	// ������
	public String mName;
	// ����������
	private int mpTriangleIndicies[];
	// ����������-1Ϊû�в���
	private byte mMaterialIndex;
	private String mComment;

	public String getComment() {
		return mComment;
	}

	public void setComment(String comment) {
		this.mComment = comment;
	}

	MS3DGroup() {
	}

	MS3DGroup(final String name, final int[] triangleIndicies, final byte materialIndex, final byte flags) {
		setName(name);
		setTriangleIndicies(triangleIndicies);
		setMaterialIndex(materialIndex);
		setFlags(flags);
	}

	final String getName() {
		return this.mName;
	}

	final void setName(final String name) {
		this.mName = name;
	}

	final byte getFlags() {
		return this.mFlags;
	}

	final void setFlags(final byte flags) {
	}

	final int[] getTriangleIndicies() {
		return this.mpTriangleIndicies;
	}

	final void setTriangleIndicies(final int indicies[]) {
		this.mpTriangleIndicies = indicies;
	}

	public final int getTriangleCount() {
		return this.mpTriangleIndicies.length;
	}

	final byte getMaterialIndex() {
		return this.mMaterialIndex;
	}

	final void setMaterialIndex(final byte index) {
		this.mMaterialIndex = index;
	}
}