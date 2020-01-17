package com.android.chapter31.ms3d;

// ͷ�ļ�
public final class MS3DHeader {

	// ID��(MS3D000000)
	private byte mpIds[];
	// �汾�ţ�һ��Ϊ3����4
	private int mVersion;

	MS3DHeader() {
	}

	public MS3DHeader(final byte id[], final int version) {
		setID(id);
		setVersion(version);
	}

	// �õ�ID��
	final byte[] getID() {
		return this.mpIds;
	}

	// ����ID��
	final void setID(final byte id[]) {
		if (id.length != 10) {
			throw new IllegalArgumentException("MS3DHeader id length should be 10, is " + id.length);
		}
		if (!"MS3D000000".equals(new String(id))) {
			throw new IllegalArgumentException("MS3DHeader id \"" + id + "\" invalid");
		}
		this.mpIds = id;
	}

	// �õ�ģ���ļ��İ汾
	final int getVersion() {
		return this.mVersion;
	}

	// ���ð汾��
	final void setVersion(final int version) {
		if (version < 3 | version > 4) {
			throw new IllegalArgumentException("MS3DHeader version " + version + " unsupported");
		}
		this.mVersion = version;
	}
}