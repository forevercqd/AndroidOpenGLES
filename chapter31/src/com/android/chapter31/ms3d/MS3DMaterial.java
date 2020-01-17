package com.android.chapter31.ms3d;

import com.android.chapter31.utils.Color4f;

// ���ʽṹ
final class MS3DMaterial {

	// ������
	private String mName;
	// ������
	private Color4f mAmbient;
	// ɢ���
	private Color4f mDiffuse;
	// �߹�
	private Color4f mSpecular;
	// �Է���
	private Color4f mEmissive;
	// 0-128(����ֵ��Ӱ��߹��Ч������ֵԽ�ͣ��߹�Խ����)
	private float mfShininess;
	// ͸���� 0-1
	private float mfTransparency;
	// δʹ��
	private byte mMode;
	// ��ͼ�ļ���
	private String mTextureName;
	// ͸����ͼ�ļ���
	private String mAlpha;

	private String mComment;

	public String getComment() {
		return mComment;
	}

	public void setComment(String comment) {
		this.mComment = comment;
	}

	MS3DMaterial() {
	}

	MS3DMaterial(final String name, final Color4f ambient, final Color4f diffuse, final Color4f specular, final Color4f emissive, final float shininess,
	        final float transparency, final byte mode, final String texture, final String alpha) {
		setName(name);
		setAmbient(ambient);
		setDiffuse(diffuse);
		setSpecular(specular);
		setEmissive(emissive);
		setShininess(shininess);
		setTransparency(transparency);
		setMode(mode);
		setTextureName(texture);
		setAlphaMap(alpha);
	}

	final String getName() {
		return this.mName;
	}

	final void setName(final String name) {
		this.mName = name;
	}

	final Color4f getAmbient() {
		return this.mAmbient;
	}

	final void setAmbient(final Color4f ambient) {
		this.mAmbient = ambient;
	}

	final Color4f getDiffuse() {
		return this.mDiffuse;
	}

	final void setDiffuse(final Color4f diffuse) {
		this.mDiffuse = diffuse;
	}

	final Color4f getSpecular() {
		return this.mSpecular;
	}

	final void setSpecular(final Color4f specular) {
		this.mSpecular = specular;
	}

	final Color4f getEmissive() {
		return this.mEmissive;
	}

	final void setEmissive(final Color4f emissive) {
		this.mEmissive = emissive;
	}

	final float getShininess() {
		return this.mfShininess;
	}

	final void setShininess(final float shininess) {
		this.mfShininess = shininess;
	}

	final float getTransparency() {
		return this.mfTransparency;
	}

	final void setTransparency(final float transparency) {
		this.mfTransparency = transparency;
	}

	final byte getMode() {
		return this.mMode;
	}

	final void setMode(final byte mode) {
		this.mMode = mode;
	}

	final String getTextureName() {
		return this.mTextureName;
	}

	final void setTextureName(final String texture) {
		this.mTextureName = convertSlash(texture);
	}

	final String getAlphaMap() {
		return this.mAlpha;
	}

	final void setAlphaMap(final String alpha) {
		this.mAlpha = convertSlash(alpha);
	}

	private static final String convertSlash(final String filename) {
		final int len = filename.length();
		char temp[] = new char[len];
		filename.getChars(0, len, temp, 0);
		for (int x = 0; x < len; x++) {
			if (temp[x] == '\\') {
				temp[x] = java.io.File.separatorChar;
			}
		}
		return new String(temp);
	}
}