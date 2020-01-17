package com.android.chapter30.md2;

public interface MeshFactory
{
	// 创建一个模型
	public Mesh create();
	
	/**
	 * 创建一个模型
	 * @param vertexCount 模型的定点数
	 * @param texCoordCount 模型的纹理坐标数
	 * @param faceCount 模型的面数
	 * @return 创建的模型
	 */
	public Mesh create(int vertexCount, int texCoordCount, int faceCount);
}
