package com.android.chapter29;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Model
{
    Mesh[]    m_meshs;
    int     m_meshCount;
    public static final float DTOR = (float)(Math.PI / 180.0f);
	static class Mesh
	{
		float[]	m_vertices;
		float[]	m_normals;
		float[]	m_uvs;
		short[]	m_colors;
		short[]	m_indices;
		float[]	m_position;
		float[]	m_rotate;
		float[]	m_scale;
		int		m_textureId;
		int		m_triangleNums;
		boolean	m_enabled;
		
		public Mesh()
		{
			m_vertices = (null);
			m_normals = (null);
			m_uvs = (null);
			m_colors = (null);
			m_indices = (null);
			m_position = (null);
			m_rotate = (null);
			m_scale = (null);
			m_textureId = (-1);
			m_triangleNums = (0);
			m_enabled = (false);
		}
		
		void setVertices(float[] vertices, int size)
		{
			if (m_vertices != null)
			{
				m_vertices = null;
			}
			m_vertices = new float[size];
			System.arraycopy(vertices, 0, m_vertices, 0, size);

			setEnabled(true);
		}

		void setNormals(float[] normals, int size)
		{
			if (m_normals != null)
			{
				m_normals = null;
			}
			m_normals = new float[size];
			System.arraycopy(normals, 0, m_normals, 0, size);
		}

		void setUvs(float[] uvs, int size)
		{
			if (m_uvs != null)
			{
				m_uvs = null;
			}
			m_uvs = new float[size];
			System.arraycopy(uvs, 0, m_uvs, 0, size);
		}
		
		void setColors(short[] colors, int size)
		{
			if (m_colors != null)
			{
				m_colors = null;
			}
			m_colors = new short[size];
			System.arraycopy(colors, 0, m_colors, 0, size);
		}

		void setIndices(short[] indices, int size)
		{
			if (m_indices != null)
			{
				m_indices = null;
			}
			m_indices = new short[size];
			System.arraycopy(indices, 0, m_indices, 0, size);
		}
		
		void setTextureId(int textureId)
		{
			m_textureId = textureId;
		}

		void setTriangleNums(int triangleNums)
		{
			m_triangleNums = triangleNums;
		}

		void setEnabled(boolean enabled)
		{
			m_enabled = enabled;
		}
		
		void setPosition(float x, float y, float z)
		{
			if (m_position == null)
			{
				m_position = new float[3];
			}

			m_position[0] = x;
			m_position[1] = y;
			m_position[2] = z;
		}
		
		void setRotate(float x, float y, float z)
		{
			if (m_rotate == null)
			{
				m_rotate = new float[3];
			}

			m_rotate[0] = x;
			m_rotate[1] = y;
			m_rotate[2] = z;
		}

		void setScale(float x, float y, float z)
		{
			if (m_scale == null)
			{
				m_scale = new float[3];
			}

			m_scale[0] = x;
			m_scale[1] = y;
			m_scale[2] = z;
		}
		
		void initGlCmds(GL10 gl)
		{
			if (m_vertices != null) gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

			if (m_normals != null) gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

			if (m_uvs != null)
			{
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glEnable(GL10.GL_TEXTURE_2D);
			}
			else
			{
				gl.glDisable(GL10.GL_TEXTURE_2D);
			}

			if (m_colors != null) gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		}
		
		void renderMesh(GL10 gl)
		{
			if (!m_enabled) return;

			// save current matrix
			gl.glPushMatrix();

			// enable or disable gl command status
			initGlCmds(gl);

			if (m_position != null)
			{
				gl.glTranslatef(m_position[0], m_position[1], m_position[2]);
			}

			if (m_rotate != null)
			{
				// ratate x axis
				if (m_rotate[0] != 0.0f) gl.glRotatef(m_rotate[0], 1.0f, 0.0f, 0.0f);
				// ratate y axis
				if (m_rotate[1] != 0.0f) gl.glRotatef(m_rotate[1], 0.0f, 1.0f, 0.0f);
				// ratate z axis
				if (m_rotate[2] != 0.0f) gl.glRotatef(m_rotate[2], 0.0f, 0.0f, 1.0f);
			}

			if (m_scale != null) gl.glScalef(m_scale[0], m_scale[1], m_scale[2]);

			if (m_vertices != null)
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(m_vertices));
			else
			{
				return;
			}

			if (m_normals != null) gl.glNormalPointer(GL10.GL_FLOAT, 0, makeFloatBuffer(m_normals));

			if (m_uvs != null && m_textureId != -1)
			{
				gl.glBindTexture(GL10.GL_TEXTURE_2D, m_textureId);
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(m_uvs));
			}
			else
			{
				gl.glDisable(GL10.GL_TEXTURE_2D);
				if ((m_uvs != null && m_textureId == -1) || (m_uvs == null && m_textureId != -1))
				{
				}
			}

			if (m_colors != null) gl.glColorPointer(4,
					GL10.GL_UNSIGNED_BYTE,
					0,
					makeShortBuffer(m_colors));

			if (m_indices != null)
				gl.glDrawElements(GL10.GL_TRIANGLES,
						m_triangleNums * 3,
						GL10.GL_UNSIGNED_SHORT,
						makeShortBuffer(m_indices));
			else gl.glDrawArrays(GL10.GL_TRIANGLES, 0, m_triangleNums * 3);
			// restore matrix
			gl.glPopMatrix();
		}

		protected FloatBuffer makeFloatBuffer(float[] arr)
		{
			ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
			bb.order(ByteOrder.nativeOrder());
			FloatBuffer fb = bb.asFloatBuffer();
			fb.put(arr);
			fb.position(0);
			return fb;
		}

		protected ShortBuffer makeShortBuffer(short[] arr)
		{
			ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
			bb.order(ByteOrder.nativeOrder());
			ShortBuffer sb = bb.asShortBuffer();
			sb.put(arr);
			sb.position(0);
			return sb;
		}
	}
	
	/***************************************/
	public Model()
	{
		m_meshs=(null);
        m_meshCount=(0); 
	}
	
	void setVertices(float[] vertices, int size, int meshIndex)
	{
		m_meshs[meshIndex].setVertices(vertices, size);
	}

	void setNormals(float[] normals, int size, int meshIndex)
	{
		m_meshs[meshIndex].setNormals(normals, size);
	}

	void setUvs(float[] uvs, int size, int meshIndex)
	{
		m_meshs[meshIndex].setUvs(uvs, size);
	}

	void setColors(short[] colors, int size, int meshIndex)
	{
		m_meshs[meshIndex].setColors(colors, size);
	}

	void setIndices(short[] indices, int size, int meshIndex)
	{
		m_meshs[meshIndex].setIndices(indices, size);
	}

	void setTriangleNums(int triangleNums, int meshIndex)
	{
		m_meshs[meshIndex].setTriangleNums(triangleNums);
	}

	void setEnabled(boolean enabled, int meshIndex)
	{
		m_meshs[meshIndex].setEnabled(enabled);
	}

	void setTextureId(int textureId, int meshIndex)
	{
		m_meshs[meshIndex].setTextureId(textureId);
	}
	
	void setPosition(float x, float y, float z)
	{
		for (int i = 0; i < m_meshCount; i++)
		{
			m_meshs[i].setPosition(x, y, z);
		}
	}
	
	void setRotate(float x, float y, float z)
	{
		for (int i = 0; i < m_meshCount; i++)
		{
			m_meshs[i].setRotate(x, y, z);
		}
	}

	void setScale(float x, float y, float z)
	{
		for (int i = 0; i < m_meshCount; i++)
		{
			m_meshs[i].setScale(x, y, z);
		}
	}

	void setMeshCount(int meshCount)
	{
		if (m_meshs != null)
		{
			m_meshs = null;
		}

		if (meshCount > 0)
		{
			m_meshCount = meshCount;
			// create meshs
			m_meshs = new Mesh[m_meshCount];
			for(int i = 0; i< m_meshCount ;i++)
			{
				m_meshs[i] = new Mesh();
			}
		}
	}

	int getMeshCount()
	{
		return m_meshCount;
	}
	
	void prepareFrame()
	{

	}

	void renderModel(GL10 gl)
	{
		// enable or disable gl command status
		prepareFrame();

		for (int i = 0; i < m_meshCount; i++)
			m_meshs[i].renderMesh(gl);
	}
}

