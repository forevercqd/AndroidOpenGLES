package com.android.chapter15;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/* 光盘形状 */
public class GlDisk extends GlObject
{
	// 内半径
	private float			innerRadius;
	// 外半径
	private float			outerRadius;
	// 把这个圆形的光盘分成多少份
	private int				slices;
	// 绘制的次数
	private int				loops;
	
	// 开始角度
	private float			startAngle;
	// 停止角度
	private float			stopAngle;

	// 是否使用法线和纹理坐标
	private boolean			normals;
	private boolean			texCoords;

	// 循环缓冲区
	private FloatBuffer[]	loopsBuffers;
	// 法线数组
	private FloatBuffer[]	normalsBuffers;
	// 纹理坐标数组
	private FloatBuffer[]	texCoordsBuffers;


	public GlDisk(float innerRadius, float outerRadius, int slices, int loops, boolean genNormals, boolean genTexCoords)
	{
		this(innerRadius, outerRadius, slices, loops, 0.0f, (float) (2 * Math.PI), genNormals, genTexCoords);
	}


	public GlDisk(float innerRadius, float outerRadius, int slices, int loops, float startAngle, float stopAngle, boolean genNormals, boolean genTexCoords)
	{
		this.innerRadius = innerRadius;
		this.outerRadius = outerRadius;
		this.slices = slices;
		this.loops = loops;
		this.startAngle = startAngle;
		this.stopAngle = stopAngle;
		this.normals = genNormals;
		this.texCoords = genTexCoords;
		generateData();
	}

	// 生成数据
	private void generateData()
	{

		loopsBuffers = new FloatBuffer[loops];
		if (normals)
		{
			normalsBuffers = new FloatBuffer[loops];
		}
		if (texCoords)
		{
			texCoordsBuffers = new FloatBuffer[loops];
		}

		for (int i = 0; i < loops; i++)
		{

			float[] vertexCoords = new float[3 * 2 * (slices + 1)];
			float[] normalCoords = new float[3 * 2 * (slices + 1)];
			float[] textureCoords = new float[2 * 2 * (slices + 1)];

			float r0 = innerRadius + (outerRadius - innerRadius) * i / loops;
			float r1 = innerRadius + (outerRadius - innerRadius) * (i + 1) / loops;

			for (int j = 0; j <= slices; j++)
			{
				// 得出每一份的角度
				double alpha = startAngle + (stopAngle - startAngle) * j / slices;
				
				float sinAlpha = (float) Math.sin(alpha);
				float cosAlpha = (float) Math.cos(alpha);

				// 设置顶点数组
				Utils.setXYZ(vertexCoords, 6 * j, cosAlpha * r0, sinAlpha * r0, 0);
				Utils.setXYZ(vertexCoords, 6 * j + 3, cosAlpha * r1, sinAlpha * r1, 0);
				// 设置法线数组
				if (normals)
				{
					Utils.setXYZ(normalCoords, 6 * j, 0, 0, 1);
					Utils.setXYZ(normalCoords, 6 * j + 3, 0, 0, 1);
				}
				// 设置纹理坐标数组
				if (texCoords)
				{
					Utils.setXY(textureCoords, 4 * j, ((float) j) / slices, ((float) i) / loops);
					Utils.setXY(textureCoords, 4 * j + 2, ((float) j) / slices, ((float) i + 1) / loops);
				}
			}

			// 保存数据到缓冲区中
			loopsBuffers[i] = FloatBuffer.wrap(vertexCoords);
			if (normals)
			{
				normalsBuffers[i] = FloatBuffer.wrap(normalCoords);
			}
			if (texCoords)
			{
				texCoordsBuffers[i] = FloatBuffer.wrap(textureCoords);
			}
		}
	}


	@Override
	public void draw(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		if (normals)
		{
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		}
		if (texCoords)
		{
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}

		for (int i = 0; i < loops; i++)
		{ 
			// 绘制每一次
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, loopsBuffers[i]);
			if (normals)
			{
				gl.glNormalPointer(GL10.GL_FLOAT, 0, normalsBuffers[i]);
			}
			if (texCoords)
			{
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordsBuffers[i]);
			}
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 2 * (slices + 1));
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if (normals)
		{
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		if (texCoords)
		{
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
	}
}
