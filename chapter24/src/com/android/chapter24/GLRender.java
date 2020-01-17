package com.android.chapter24;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{
	private Context		mContext;

	private Texture[]	texture	= new Texture[2];

	private float		spin;

	public GLRender(Context context)
	{
		mContext = context;
	}

	private InputStream getFile(String name)
	{
		AssetManager am = mContext.getResources().getAssets();
		try
		{
			return am.open(name);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void onDrawFrame(GL10 gl)
	{
		// TODO Auto-generated method stub

		// 首先清理屏幕
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// 重置矩阵
		gl.glLoadIdentity();

		// 视点变换
		GLU.gluLookAt(gl, 0, 0, 1, 0, 0, 0, 0, 1, 0);

		Draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		float ratio = (float) width / height;
		gl.glViewport(0, 0, width, width);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.5f, 0.5f);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// 装载-并绑定贴图
		try
		{
			loadGLTextures(gl);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}

	// 装载-并绑定贴图
	private void loadGLTextures(GL10 gl) throws IOException
	{
		texture[0] = new Texture();
		texture[1] = new Texture();

		// 装载TGA(分别为未压缩的和压缩的)
		TGALoader.loadTGA(texture[0], getFile("uncompressed.tga"));
		TGALoader.loadTGA(texture[1], getFile("compressed.tga"));

		for (int loop = 0; loop < 2; loop++)
		{
			// 绑定纹理贴图
			gl.glGenTextures(1, texture[loop].texID, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[loop].texID[0]);
			int internalformat = texture[loop].bpp == 24 ? GL10.GL_RGB : GL10.GL_RGBA;
			gl.glTexImage2D(GL10.GL_TEXTURE_2D,
					0,
					internalformat,
					texture[loop].width,
					texture[loop].height,
					0,
					texture[loop].type,
					GL10.GL_UNSIGNED_BYTE,
					texture[loop].imageData);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		}
	}

	private void Draw(GL10 gl)
	{
		int loop;
		float texcoords[][] = new float[4][2];
		float vertices[][] = new float[4][3];
		// 索引数组
		byte indices[] = {0, 1, 3, 2};

		gl.glTranslatef(0.0f, 2.0f, -7.0f);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		spin += 10.0f;

		for (loop = 0; loop < 20; loop++)
		{
			gl.glPushMatrix();
			gl.glRotatef(spin + loop * 18.0f, 1.0f, 0.0f, 0.0f);
			gl.glTranslatef(-2.0f, 2.0f, 0.0f);

			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0].texID[0]);

			texcoords[0][0] = 0.0f;
			texcoords[0][1] = 1.0f;
			vertices[0][0] = -1.0f;
			vertices[0][1] = 1.0f;
			vertices[0][2] = 0.0f;
			texcoords[1][0] = 1.0f;
			texcoords[1][1] = 1.0f;
			vertices[1][0] = 1.0f;
			vertices[1][1] = 1.0f;
			vertices[1][2] = 0.0f;
			texcoords[2][0] = 1.0f;
			texcoords[2][1] = 0.0f;
			vertices[2][0] = 1.0f;
			vertices[2][1] = -1.0f;
			vertices[2][2] = 0.0f;
			texcoords[3][0] = 0.0f;
			texcoords[3][1] = 0.0f;
			vertices[3][0] = -1.0f;
			vertices[3][1] = -1.0f;
			vertices[3][2] = 0.0f;

			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(vertices));
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));

			gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
					.wrap(indices));

			gl.glPopMatrix();

			gl.glPushMatrix();

			gl.glTranslatef(2.0f, 0.0f, 0.0f);
			gl.glRotatef(spin + loop * 36.0f, 0.0f, 1.0f, 0.0f);
			gl.glTranslatef(1.0f, 0.0f, 0.0f);

			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1].texID[0]);

			texcoords[0][0] = 0.0f;
			texcoords[0][1] = 0.0f;
			vertices[0][0] = -1.0f;
			vertices[0][1] = 1.0f;
			vertices[0][2] = 0.0f;
			texcoords[1][0] = 1.0f;
			texcoords[1][1] = 0.0f;
			vertices[1][0] = 1.0f;
			vertices[1][1] = 1.0f;
			vertices[1][2] = 0.0f;
			texcoords[2][0] = 1.0f;
			texcoords[2][1] = 1.0f;
			vertices[2][0] = 1.0f;
			vertices[2][1] = -1.0f;
			vertices[2][2] = 0.0f;
			texcoords[3][0] = 0.0f;
			texcoords[3][1] = 1.0f;
			vertices[3][0] = -1.0f;
			vertices[3][1] = -1.0f;
			vertices[3][2] = 0.0f;

			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(vertices));
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
			gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
					.wrap(indices));

			gl.glPopMatrix();
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
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

	protected FloatBuffer makeFloatBuffer(float[][] arr)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * arr[0].length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			fb.put(arr[i]);
		}
		fb.position(0);
		return fb;
	}
}
