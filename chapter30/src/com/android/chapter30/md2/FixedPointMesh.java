package com.android.chapter30.md2;

import java.util.Iterator;
import java.util.Vector;

import com.android.chapter30.md2.ObjLoader.hand;

public abstract class FixedPointMesh extends Mesh
{
	// 添加法线
	public void addNormal(float[] normal)
	{
		addNormal(FixedPointUtils.toFixed(normal));
	}

	// 添加纹理坐标
	public void addTextureCoordinate(float[] coord)
	{
		addTextureCoordinate(FixedPointUtils.toFixed(coord));
	}

	// 添加顶点数组
	public void addVertex(float[] vertex)
	{
		addVertex(FixedPointUtils.toFixed(vertex));
	}
	
	
	protected abstract void clearFaceNormals();

	protected abstract void addFaceNormal(int[] norm);

	public void calculateFaceNormals(hand h)
	{
		clearFaceNormals();
		Iterator<int[]> it = faces.iterator();
		int[] temp1 = new int[3];
		int[] temp2 = new int[3];

		while (it.hasNext())
		{
			int[] face = it.next();
			int[] normal = new int[3];

			int[] p0, p1, p2;
			if (h == ObjLoader.hand.RIGHT)
			{
				p0 = getVertexx(face[0]);
				p1 = getVertexx(face[1]);
				p2 = getVertexx(face[2]);
			}
			else
			{
				p0 = getVertexx(face[2]);
				p1 = getVertexx(face[1]);
				p2 = getVertexx(face[0]);
			}
			MatrixUtils.minus(p0, p1, temp1);
			MatrixUtils.minus(p2, p1, temp2);
			MatrixUtils.cross(temp1, temp2, normal);
			MatrixUtils.normalize(normal);
			addFaceNormal(normal);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void calculateVertexNormals()
	{
		clearNormals();
		int ct = getVertexCount();
		Vector<int[]>[] norms = new Vector[ct];

		for (int i = 0; i < ct; i++)
			norms[i] = new Vector<int[]>();

		for (int i = 0; i < faces.size(); i++)
		{
			int[] face = faces.get(i);
			int[] norm = getFaceNormalx(i);
			norms[face[0]].add(norm);
			norms[face[1]].add(norm);
			norms[face[2]].add(norm);
		}

		for (int i = 0; i < norms.length; i++)
		{
			int[] norm = new int[3];
			for (int j = 0; j < norms[i].size(); j++)
			{
				MatrixUtils.plus(norm, (norms[i].get(j)), null);
			}
			MatrixUtils.normalize(norm);
			addNormal(norm);
		}
	}

	@Override
	public float[] getFaceNormalf(int ix)
	{
		return FixedPointUtils.toFloat(getFaceNormalx(ix));
	}

	@Override
	public float[] getNormalf(int ix)
	{
		return FixedPointUtils.toFloat(getNormalx(ix));
	}

	@Override
	public float[] getTextureCoordinatef(int ix)
	{
		return FixedPointUtils.toFloat(getTextureCoordinatex(ix));
	}

	@Override
	public float[] getVertexf(int ix)
	{
		return FixedPointUtils.toFloat(getVertexx(ix));
	}

	@Override
	public void scale(float scale)
	{
		scale(FixedPointUtils.toFixed(scale));
	}
}
