package com.android.chapter30.md2;

public class Frame
{
	// ����
	String	name;
	// ģ��
	Mesh	mesh;

	public Frame(String name, Mesh mesh)
	{
		this.name = name;
		this.mesh = mesh;
	}

	public String getName()
	{
		return name;
	}

	public Mesh getMesh()
	{
		return mesh;
	}
}
