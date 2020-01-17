package com.android.chapter30.md2;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class FloatMesh extends Mesh
{
	Vector<float[]>			vertices;
	Vector<float[]>			face_normals;
	Vector<float[]>			vertex_normals;
	Vector<float[]>			normals;
	Vector<float[]>			texture_coords;
	Vector<float[]>			face_planes;
	Hashtable<Edge, int[]>	neighbors;

	public FloatMesh()
	{
		this.vertices = new Vector<float[]>();
		this.face_normals = new Vector<float[]>();
		this.vertex_normals = new Vector<float[]>();
		this.normals = new Vector<float[]>();
		this.texture_coords = new Vector<float[]>();
		this.face_planes = new Vector<float[]>();
	}

	public void calculateFacePlanes(ObjLoader.hand h)
	{
		face_planes.clear();
		Iterator<int[]> it = faces.iterator();
		float[] temp1 = new float[3];
		float[] temp2 = new float[3];

		while (it.hasNext())
		{
			int[] face = it.next();
			float[] normal = new float[3];
			float[] plane = new float[4];

			float[] p0, p1, p2;
			if (h == ObjLoader.hand.RIGHT)
			{
				p0 = vertices.get(face[0]);
				p1 = vertices.get(face[1]);
				p2 = vertices.get(face[2]);
			}
			else
			{
				p0 = vertices.get(face[2]);
				p1 = vertices.get(face[1]);
				p2 = vertices.get(face[0]);
			}
			MatrixUtils.minus(p0, p1, temp1);
			MatrixUtils.minus(p2, p1, temp2);
			MatrixUtils.cross(temp1, temp2, normal);
			MatrixUtils.normalize(normal);

			plane[0] = normal[0];
			plane[1] = normal[1];
			plane[2] = normal[2];
			plane[3] = -(plane[0] * p0[0] + plane[1] * p0[1] + plane[2] * p0[2]);
			face_planes.add(plane);
		}
	}

	public void calculateFaceNormals(ObjLoader.hand h)
	{
		face_normals.clear();
		Iterator<int[]> it = faces.iterator();
		float[] temp1 = new float[3];
		float[] temp2 = new float[3];

		while (it.hasNext())
		{
			int[] face = it.next();
			float[] normal = new float[3];

			float[] p0, p1, p2;
			if (h == ObjLoader.hand.RIGHT)
			{
				p0 = vertices.get(face[0]);
				p1 = vertices.get(face[1]);
				p2 = vertices.get(face[2]);
			}
			else
			{
				p0 = vertices.get(face[2]);
				p1 = vertices.get(face[1]);
				p2 = vertices.get(face[0]);
			}
			MatrixUtils.minus(p0, p1, temp1);
			MatrixUtils.minus(p2, p1, temp2);
			MatrixUtils.cross(temp1, temp2, normal);
			MatrixUtils.normalize(normal);
			face_normals.add(normal);
		}
	}

	public void calculateVertexNormals()
	{
		vertex_normals.clear();
		Vector<float[]>[] norms = new Vector[vertices.size()];

		for (int i = 0; i < vertices.size(); i++)
			norms[i] = new Vector<float[]>();
		for (int i = 0; i < faces.size(); i++)
		{
			int[] face = faces.get(i);
			float[] norm = face_normals.get(i);
			norms[face[0]].add(norm);
			norms[face[1]].add(norm);
			norms[face[2]].add(norm);
		}
		for (int i = 0; i < norms.length; i++)
		{
			float[] norm = new float[3];
			for (int k = 0; k < norms[i].size(); k++)
			{
				MatrixUtils.plus(norm, (norms[i].get(k)), null);
			}
			MatrixUtils.normalize(norm);
			vertex_normals.add(norm);
		}
	}

	public void scale(float scale)
	{
		for (int i = 0; i < vertices.size(); i++)
		{
			vertices.get(i)[0] *= scale;
			vertices.get(i)[1] *= scale;
			vertices.get(i)[2] *= scale;
		}
	}

	public int getFaceCount()
	{
		return faces.size();
	}

	public int[] getFace(int ix)
	{
		return faces.get(ix);
	}

	public float[] getFaceNormalf(int ix)
	{
		if (ix < 0 || ix >= face_normals.size()) return null;
		return face_normals.get(ix);
	}

	public float[] getVertexf(int ix)
	{
		return vertices.get(ix);
	}

	public int getVertexCount()
	{
		return vertices.size();
	}

	public float[] getNormalf(int ix)
	{
		if (ix < 0 || ix >= normals.size()) return null;
		return normals.get(ix);
	}

	public int[] getFaceNormals(int ix)
	{
		if (sharedVertexNormals)
		{
			return getFace(ix);
		}
		else
		{
			if (ix < 0 || ix >= face_normal_ix.size()) { return null; }
			return face_normal_ix.get(ix);
		}
	}

	public void addNormal(float[] normal)
	{
		normals.add(normal);
	}

	public void copyNormals()
	{
		for (int i = 0; i < faces.size(); i++)
		{
			face_normal_ix.add(faces.get(i));
		}
		for (int i = 0; i < vertex_normals.size(); i++)
		{
			normals.add(vertex_normals.get(i));
		}
	}

	public void addVertex(float[] vertex)
	{
		vertices.add(vertex);
	}

	public int[] getFaceTextures(int ix)
	{
		if (ix < 0 || ix >= face_tx_ix.size()) return null;
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

	public void addTextureCoordinate(float[] coord)
	{
		float[] crd = new float[coord.length];
		for (int i = 0; i < crd.length; i++)
		{
			crd[i] = coord[i];
		}
		texture_coords.add(crd);
	}

	public float[] getTextureCoordinatef(int ix)
	{
		if (ix < 0 || ix >= texture_coords.size())
		{
			System.out.println("No such coord!");
			return null;
		}
		return texture_coords.get(ix);
	}

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



	public void addNormal(int[] normal)
	{
		addNormal(FixedPointUtils.toFloat(normal));
	}

	public void addTextureCoordinate(int[] coord)
	{
		addTextureCoordinate(FixedPointUtils.toFloat(coord));
	}

	public void addVertex(int[] vertex)
	{
		addVertex(FixedPointUtils.toFloat(vertex));
	}

	public int[] getFaceNormalx(int ix)
	{
		return FixedPointUtils.toFixed(getFaceNormalf(ix));
	}

	public int[] getNormalx(int ix)
	{
		return FixedPointUtils.toFixed(getNormalf(ix));
	}

	public int[] getTextureCoordinatex(int ix)
	{
		return FixedPointUtils.toFixed(getTextureCoordinatef(ix));
	}

	public int[] getVertexx(int ix)
	{
		return FixedPointUtils.toFixed(getVertexf(ix));
	}

	public void scale(int scale)
	{
		scale(FixedPointUtils.toFixed(scale));
	}

	protected void clearNormals()
	{
		normals.clear();

	}

	protected void clearTexCoords()
	{
		texture_coords.clear();
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
					return new FloatMesh();
				}

				public Mesh create(int v, int t, int f)
				{
					return new FloatMesh();
				}
			};
		}
		return mf;
	}
}
