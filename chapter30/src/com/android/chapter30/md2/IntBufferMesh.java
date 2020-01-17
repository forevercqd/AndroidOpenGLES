package com.android.chapter30.md2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class IntBufferMesh extends FixedPointMesh
{
	IntBuffer					vertices;
	IntBuffer					normals;
	IntBuffer					texCoords;
	IntBuffer					face_normals;

	int							vertexCount;

	private static MeshFactory	mf	= null;

	public static MeshFactory factory()
	{
		if (mf == null)
		{
			mf = new MeshFactory() {
				public Mesh create()
				{
					System.err.println("Error, unsupported create method, must use parameters...");
					return null;
				}

				public Mesh create(int v, int t, int f)
				{
					return new IntBufferMesh(v, t, f);
				}
			};
		}
		return mf;
	}

	public IntBuffer makeIntBuffer(int size)
	{
		ByteBuffer bb;
		bb = ByteBuffer.allocateDirect(size * 4);
		bb.order(ByteOrder.nativeOrder());
		return bb.asIntBuffer();
	}

	public IntBufferMesh(int vertexCount, int texCoordCount, int faceCount)
	{
		vertices = makeIntBuffer(vertexCount * 3);
		normals = makeIntBuffer(vertexCount * 3);
		texCoords = makeIntBuffer(texCoordCount * 2);
		face_normals = makeIntBuffer(faceCount * 3);

		this.vertexCount = vertexCount;
	}

	public void addNormal(int[] normal)
	{

		normals.put(normal[0]);
		normals.put(normal[1]);
		normals.put(normal[2]);
	}

	public void addTextureCoordinate(int[] coord)
	{
		texCoords.put(coord[0]);
		texCoords.put(coord[1]);
	}

	public void addVertex(int[] vertex)
	{
		vertices.put(vertex[0]);
		vertices.put(vertex[1]);
		vertices.put(vertex[2]);
	}

	public int[] getFaceNormalx(int ix)
	{
		int[] norm = new int[3];
		face_normals.get(norm, ix * 3, 3);
		return norm;
	}

	public int[] getNormalx(int ix)
	{
		if (ix * 3 < normals.capacity())
		{
			int[] norm = new int[3];
			normals.position(ix * 3);
			normals.get(norm);
			return norm;
		}
		else
		{
			return null;
		}
	}

	public int[] getTextureCoordinatex(int ix)
	{
		int[] coord = new int[2];
		texCoords.position(ix * 2);
		texCoords.get(coord);
		return coord;
	}

	public int getVertexCount()
	{
		return vertices.capacity() / 3;
	}

	public int[] getVertexx(int ix)
	{
		int[] vert = new int[3];
		vertices.position(ix * 3);
		vertices.get(vert);
		return vert;
	}

	public void scale(int scale)
	{
		for (int i = 0; i < vertices.capacity(); i++)
		{
			int x = vertices.get(i);
			vertices.put(i, (int) ((((long) x) * ((long) scale)) >> 16));
		}
	}

	public IntBuffer getVertexBuffer()
	{
		return vertices;
	}

	public IntBuffer getNormalBuffer()
	{
		return normals;
	}

	public IntBuffer getTexCoordBuffer()
	{
		return texCoords;
	}

	protected void addFaceNormal(int[] norm)
	{
		face_normals.put(norm[0]);
		face_normals.put(norm[1]);
		face_normals.put(norm[2]);
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

	public void reorder()
	{
		int newsize = getFaceCount() * 3 * 3;
		if (vertices.capacity() < newsize)
		{
			ByteBuffer bb = ByteBuffer.allocateDirect(newsize * 4);
			IntBuffer newVert = bb.asIntBuffer();
			vertices.position(0);
			newVert.put(vertices);
			if (newVert.position() < vertices.position()) newVert.position(vertices.position());
			vertices.clear();
			vertices = newVert;
		}
		if (normals.capacity() < newsize)
		{
			ByteBuffer bb = ByteBuffer.allocateDirect(newsize * 4);
			IntBuffer newNorm = bb.asIntBuffer();
			normals.position(0);
			newNorm.put(normals);
			if (newNorm.position() < normals.position()) newNorm.position(normals.position());
			normals.clear();
			normals = newNorm;
		}
		if (texCoords.capacity() < newsize)
		{
			ByteBuffer bb = ByteBuffer.allocateDirect(newsize * 4);
			IntBuffer newTex = bb.asIntBuffer();
			texCoords.position(0);
			newTex.put(texCoords);
			if (newTex.position() < texCoords.position()) newTex.position(texCoords.position());
			texCoords.clear();
			texCoords = newTex;
		}
		super.reorder();
	}
}
