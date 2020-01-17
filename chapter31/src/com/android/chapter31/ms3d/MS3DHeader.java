package com.android.chapter31.ms3d;

// 头文件
public final class MS3DHeader {

	// ID号(MS3D000000)
	private byte mpIds[];
	// 版本号，一般为3或者4
	private int mVersion;

	MS3DHeader() {
	}

	public MS3DHeader(final byte id[], final int version) {
		setID(id);
		setVersion(version);
	}

	// 得到ID号
	final byte[] getID() {
		return this.mpIds;
	}

	// 设置ID号
	final void setID(final byte id[]) {
		if (id.length != 10) {
			throw new IllegalArgumentException("MS3DHeader id length should be 10, is " + id.length);
		}
		if (!"MS3D000000".equals(new String(id))) {
			throw new IllegalArgumentException("MS3DHeader id \"" + id + "\" invalid");
		}
		this.mpIds = id;
	}

	// 得到模型文件的版本
	final int getVersion() {
		return this.mVersion;
	}

	// 设置版本号
	final void setVersion(final int version) {
		if (version < 3 | version > 4) {
			throw new IllegalArgumentException("MS3DHeader version " + version + " unsupported");
		}
		this.mVersion = version;
	}
}