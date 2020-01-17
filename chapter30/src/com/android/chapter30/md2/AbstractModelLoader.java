package com.android.chapter30.md2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class AbstractModelLoader implements ModelLoader
{
	// 模型工厂（容器）
	protected MeshFactory	factory;

	public void setFactory(MeshFactory f)
	{
		factory = f;
	}
	
	// 装载模型
	public Model load(String file) throws IOException
	{
		return load(new File(file));
	}

	public Model load(File f) throws IOException
	{
		return load(new FileInputStream(f));
	}
	
	// 检测是否可以装载
	public boolean canLoad(String f)
	{
		return canLoad(new File(f));
	}
}
