package com.android.chapter30.md2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

///ģ��װ����
public interface ModelLoader
{
	// ����ģ�͹���
	public void setFactory(MeshFactory f);

	// װ��ģ��
	public Model load(String file) throws IOException;

	public Model load(File f) throws IOException;

	public Model load(InputStream is) throws IOException;

	// ����ܷ�װ��
	public boolean canLoad(File f);

	public boolean canLoad(String f);
}
