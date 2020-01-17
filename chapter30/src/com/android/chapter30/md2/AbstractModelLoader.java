package com.android.chapter30.md2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class AbstractModelLoader implements ModelLoader
{
	// ģ�͹�����������
	protected MeshFactory	factory;

	public void setFactory(MeshFactory f)
	{
		factory = f;
	}
	
	// װ��ģ��
	public Model load(String file) throws IOException
	{
		return load(new File(file));
	}

	public Model load(File f) throws IOException
	{
		return load(new FileInputStream(f));
	}
	
	// ����Ƿ����װ��
	public boolean canLoad(String f)
	{
		return canLoad(new File(f));
	}
}
