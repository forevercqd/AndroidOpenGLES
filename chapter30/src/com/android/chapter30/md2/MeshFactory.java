package com.android.chapter30.md2;

public interface MeshFactory
{
	// ����һ��ģ��
	public Mesh create();
	
	/**
	 * ����һ��ģ��
	 * @param vertexCount ģ�͵Ķ�����
	 * @param texCoordCount ģ�͵�����������
	 * @param faceCount ģ�͵�����
	 * @return ������ģ��
	 */
	public Mesh create(int vertexCount, int texCoordCount, int faceCount);
}
