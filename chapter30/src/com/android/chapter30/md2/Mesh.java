package com.android.chapter30.md2;

import java.util.Hashtable;
import java.util.Vector;

public abstract class Mesh
{
	Mesh					indexDelegate	= null;
	protected boolean		sharedVertexNormals;
	protected boolean		sharedTextureCoords;

	Hashtable<Edge, int[]>	neighbors;

	// 面
	Vector<int[]>			faces;
	// 法向量索引
	Vector<int[]>			face_normal_ix;
	// 纹理坐标索引
	Vector<int[]>			face_tx_ix;
	// 纹理文件
	String					texture_file;

	public Mesh()
	{
		sharedVertexNormals = false;
		sharedTextureCoords = false;
		faces = new Vector<int[]>();
		face_normal_ix = new Vector<int[]>();
		face_tx_ix = new Vector<int[]>();

		neighbors = new Hashtable<Edge, int[]>();
	}

	public void setIndexDelegate(Mesh m)
	{
		indexDelegate = m;
	}

	public void setSharedVertexNormals(boolean b)
	{
		this.sharedVertexNormals = b;
	}

	public void calculateNeighbors()
	{
		neighbors.clear();
		for (int i = 0; i < faces.size(); i++)
		{
			int[] fce = faces.get(i);
			for (int j = 0; j < 3; j++)
			{
				Edge e = new Edge(fce[j], fce[(j + 1) % 3]);
				int[] vec = neighbors.get(e);
				if (vec == null)
				{
					vec = new int[2];
					vec[0] = i;
					vec[1] = -1;
					neighbors.put(e, vec);
				}
				else
				{
					if (vec[1] != -1) System.err
							.println("Warning: edge shared by three triangles!");
					vec[1] = i;
				}
			}
		}
	}

	public int getNeighbor(Edge e, int v)
	{
		if (indexDelegate != null) return indexDelegate.getNeighbor(e, v);

		int[] ns = neighbors.get(e);
		if (ns == null) return -1;
		if (ns[0] == v)
			return ns[1];
		else
			return ns[0];
	}

	public int[] getNeighbors(Edge e)
	{
		if (indexDelegate != null) return indexDelegate.getNeighbors(e);
		return neighbors.get(e);
	}

	public abstract void calculateFaceNormals(ObjLoader.hand h);

	public abstract void calculateVertexNormals();

	public abstract void scale(float scale);

	public abstract void scale(int scale);

	public int getFaceCount()
	{
		if (indexDelegate != null) return indexDelegate.getFaceCount();
		return faces.size();
	}

	public int[] getFace(int ix)
	{
		if (indexDelegate != null) return indexDelegate.getFace(ix);
		return faces.get(ix);
	}

	public abstract float[] getFaceNormalf(int ix);

	public abstract int[] getFaceNormalx(int ix);

	public abstract float[] getVertexf(int ix);

	public abstract int[] getVertexx(int ix);

	public abstract int getVertexCount();

	public abstract float[] getNormalf(int ix);

	public abstract int[] getNormalx(int ix);

	public int[] getFaceNormals(int ix)
	{
		if (indexDelegate != null)
			return indexDelegate.getFaceNormals(ix);
		else if (sharedVertexNormals)
		{
			return getFace(ix);
		}
		else
		{
			if (ix < 0 || ix >= face_normal_ix.size()) { return null; }
			return face_normal_ix.get(ix);
		}
	}

	public abstract void addNormal(float[] normal);

	public abstract void addNormal(int[] normal);

	public void copyNormals()
	{
		for (int i = 0; i < faces.size(); i++)
		{
			face_normal_ix.add(faces.get(i));
		}
		for (int i = 0; i < getVertexCount(); i++)
		{
			addNormal(getNormalx(i));
		}
	}

	public abstract void addVertex(float[] vertex);

	public abstract void addVertex(int[] vertex);

	public int[] getFaceTextures(int ix)
	{
		if (indexDelegate != null)
			return indexDelegate.getFaceTextures(ix);
		else if (sharedTextureCoords)
		{
			return getFace(ix);
		}
		else if (ix < 0 || ix >= face_tx_ix.size())
			return null;
		else
			return face_tx_ix.get(ix);
	}

	public void addTextureIndices(int[] ixs)
	{
		int[] ixs2 = new int[ixs.length];
		for (int i = 0; i < ixs.length; i++)
		{
			ixs2[i] = ixs[i];
		}
		face_tx_ix.add(ixs2);
	}

	public abstract void addTextureCoordinate(float[] coord);

	public abstract void addTextureCoordinate(int[] coord);

	public abstract float[] getTextureCoordinatef(int ix);

	public abstract int[] getTextureCoordinatex(int ix);

	public void addFace(int[] face)
	{
		int[] fce = new int[3];
		for (int i = 0; i < 3; i++)
			fce[i] = face[i];
		faces.add(fce);
	}

	public void addFaceNormals(int[] normals)
	{
		int[] fce = new int[3];
		for (int i = 0; i < 3; i++)
			fce[i] = normals[i];
		face_normal_ix.add(fce);
	}

	public String getTextureFile()
	{
		return texture_file;
	}

	public void setTextureFile(String texture)
	{
		this.texture_file = texture;
	}

	public void reorder()
	{
		short ct = 0;
		Vector<int[]> verticesL = new Vector<int[]>();
		Vector<int[]> normalsL = new Vector<int[]>();
		Vector<int[]> texCoordsL = new Vector<int[]>();
		Vector<int[]> indices = new Vector<int[]>();

		for (int i = 0; i < getFaceCount(); i++)
		{
			int[] face = getFace(i);
			int[] face_n = getFaceNormals(i);
			int[] face_tx = getFaceTextures(i);
			int[] index = new int[3];
			for (int j = 0; j < 3; j++)
			{
				int[] n = getNormalx(face_n[j]);
				int[] v = getVertexx(face[j]);
				int[] tx = getTextureCoordinatex(face_tx[j]);
				verticesL.add(v);
				normalsL.add(n);
				texCoordsL.add(tx);
				index[j] = ct++;
			}
			indices.add(index);
		}

		clearVertices();
		clearNormals();
		clearTexCoords();

		for (int i = 0; i < verticesL.size(); i++)
		{
			addVertex(verticesL.get(i));
			addNormal(normalsL.get(i));
			addTextureCoordinate(texCoordsL.get(i));
		}

		clearFaces();
		for (int i = 0; i < indices.size(); i++)
		{
			addFace(indices.get(i));
		}
		this.face_tx_ix.clear();
		sharedVertexNormals = true;
		sharedTextureCoords = true;
	}

	protected abstract void clearVertices();

	protected abstract void clearNormals();

	protected abstract void clearTexCoords();

	protected void clearFaces()
	{
		faces.clear();
	}
}
