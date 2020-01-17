package com.android.chapter30.md2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

///模型装载类
public interface ModelLoader
{
	// 设置模型工厂
	public void setFactory(MeshFactory f);

	// 装在模型
	public Model load(String file) throws IOException;

	public Model load(File f) throws IOException;

	public Model load(InputStream is) throws IOException;

	// 检测能否装载
	public boolean canLoad(File f);

	public boolean canLoad(String f);
}
