package com.android.chapter29;

public class Plane extends Model
{
	public Plane(int width, int height, float scale)
	{
		int vt_idx = 0, nm_idx = 0, uv_idx = 0;

		float[] vertices = new float[width * height * 18];
		float[] normals = new float[width * height * 18];
		float[] uvs = new float[width * height * 12];

		setMeshCount(1);

		for (int x = 0; x < width; x++)
		{
			for (int z = 0; z < height; z++)
			{
				// triangle 1 first point
				vertices[vt_idx++] = x * scale;
				vertices[vt_idx++] = 0.0f;
				vertices[vt_idx++] = z * scale;

				normals[nm_idx++] = 0.0f;
				normals[nm_idx++] = 1.0f;
				normals[nm_idx++] = 0.0f;

				uvs[uv_idx++] = 0.0f;
				uvs[uv_idx++] = 0.0f;
				// second point
				vertices[vt_idx++] = (x + 1) * scale;
				vertices[vt_idx++] = 0.0f;
				vertices[vt_idx++] = z * scale;

				normals[nm_idx++] = 0.0f;
				normals[nm_idx++] = 1.0f;
				normals[nm_idx++] = 0.0f;

				uvs[uv_idx++] = 1.0f;
				uvs[uv_idx++] = 0.0f;
				// third point
				vertices[vt_idx++] = (x + 1) * scale;
				vertices[vt_idx++] = 0.0f;
				vertices[vt_idx++] = (z + 1) * scale;

				normals[nm_idx++] = 0.0f;
				normals[nm_idx++] = 1.0f;
				normals[nm_idx++] = 0.0f;

				uvs[uv_idx++] = 1.0f;
				uvs[uv_idx++] = 1.0f;
				// triangle 1 first point
				vertices[vt_idx++] = x * scale;
				vertices[vt_idx++] = 0.0f;
				vertices[vt_idx++] = z * scale;

				normals[nm_idx++] = 0.0f;
				normals[nm_idx++] = 1.0f;
				normals[nm_idx++] = 0.0f;

				uvs[uv_idx++] = 0.0f;
				uvs[uv_idx++] = 0.0f;
				// third point
				vertices[vt_idx++] = (x + 1) * scale;
				vertices[vt_idx++] = 0.0f;
				vertices[vt_idx++] = (z + 1) * scale;

				normals[nm_idx++] = 0.0f;
				normals[nm_idx++] = 1.0f;
				normals[nm_idx++] = 0.0f;

				uvs[uv_idx++] = 1.0f;
				uvs[uv_idx++] = 1.0f;
				// fourth pint
				vertices[vt_idx++] = x * scale;
				vertices[vt_idx++] = 0.0f;
				vertices[vt_idx++] = (z + 1) * scale;

				normals[nm_idx++] = 0.0f;
				normals[nm_idx++] = 1.0f;
				normals[nm_idx++] = 0.0f;

				uvs[uv_idx++] = 0.0f;
				uvs[uv_idx++] = 1.0f;
			}
		}
		// set data to model
		setVertices(vertices, width * height * 18, 0);
		setNormals(normals, width * height * 18, 0);
		setUvs(uvs, width * height * 12, 0);
		setTriangleNums(width * height * 2, 0);

		if (vertices != null)
		{
			vertices = null;
		}
		if (uvs != null)
		{
			uvs = null;
		}
		if (normals != null)
		{
			normals = null;
		}
	}
}
