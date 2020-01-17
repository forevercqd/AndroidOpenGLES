package com.android.chapter30.md2;

import java.util.Vector;

import com.android.chapter30.md2.ObjLoader.hand;

public class IntMesh extends FixedPointMesh
{
	Vector<int[]>	vertices;
	Vector<int[]>	normals;
	Vector<int[]>	texCoords;
	Vector<int[]>	face_normals;

	public IntMesh()
	{
		vertices = new Vector<int[]>();
		normals = new Vector<int[]>();
		texCoords = new Vector<int[]>();
		face_normals = new Vector<int[]>();
	}

	public void addNormal(int[] normal)
	{
		normals.add(normal);
	}

	public void addTextureCoordinate(int[] coord)
	{
		texCoords.add(coord);
	}

	public void addVertex(int[] vertex)
	{
		vertices.add(vertex);
	}

	public int[] getFaceNormalx(int ix)
	{
		return face_normals.get(ix);
	}

	public int[] getNormalx(int ix)
	{
		return normals.get(ix);
	}

	public int[] getTextureCoordinatex(int ix)
	{
		return texCoords.get(ix);
	}

	public int getVertexCount()
	{
		return vertices.size();
	}

	public int[] getVertexx(int ix)
	{
		return vertices.get(ix);
	}

	public void scale(int scale)
	{
		for (int i = 0; i < vertices.size(); i++)
		{
			int[] vx = vertices.get(i);
			vx[0] = (int) ((((long) vx[0]) * ((long) scale)) >> 16);
			vx[1] = (int) ((((long) vx[1]) * ((long) scale)) >> 16);
			vx[2] = (int) ((((long) vx[2]) * ((long) scale)) >> 16);
		}
	}

	protected void addFaceNormal(int[] norm)
	{
		face_normals.add(norm);
	}

	protected void clearFaceNormals()
	{
		face_normals.clear();
	}

	protected void clearNormals()
	{
		normals.clear();
	}

	protected void clearTexCoords()
	{
		texCoords.clear();
	}

	protected void clearVertices()
	{
		vertices.clear();
	}

	private static MeshFactory	mf	= null;

	public static MeshFactory factory()
	{
		if (mf == null)
		{
			mf = new MeshFactory() {
				public Mesh create()
				{
					return new IntMesh();
				}

				public Mesh create(int v, int t, int f)
				{
					return new IntMesh();
				}
			};
		}
		return mf;
	}

	@Override
	public void calculateFaceNormals(hand h)
	{
		// TODO Auto-generated method stub
		
	}
}
