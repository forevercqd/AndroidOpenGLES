package com.android.chapter23;

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
import android.util.Log;

public class GLRender implements Renderer
{
	private Context	mContext;

	// ������������
    static float texcoords[][]=
    {
        {1.0f, 1.0f}, {0.0f, 1.0f},
        {1.0f, 0.0f}, {0.0f, 0.0f},
        {1.0f, 1.0f}, {0.0f, 1.0f},
        {1.0f, 0.0f}, {0.0f, 0.0f},
        {1.0f, 1.0f}, {0.0f, 1.0f},
        {1.0f, 0.0f}, {0.0f, 0.0f},
        {0.0f, 0.0f}, {1.0f, 0.0f},
        {0.0f, 1.0f}, {1.0f, 1.0f},
        {1.0f, 1.0f}, {0.0f, 1.0f},
        {1.0f, 0.0f}, {0.0f, 0.0f},
        {0.0f, 0.0f}, {1.0f, 0.0f},
        {0.0f, 1.0f}, {1.0f, 1.0f}
    };

    // ��������
    static  float vertices[][]=
    {
        { 1.0f,  1.0f,  1.0f}, {-1.0f,  1.0f,  1.0f},
        { 1.0f, -1.0f,  1.0f}, {-1.0f, -1.0f,  1.0f},
        {-1.0f,  1.0f, -1.0f}, { 1.0f,  1.0f, -1.0f},
        {-1.0f, -1.0f, -1.0f}, { 1.0f, -1.0f, -1.0f},
        { 1.0f,  1.0f, -1.0f}, {-1.0f,  1.0f, -1.0f},
        { 1.0f,  1.0f,  1.0f}, {-1.0f,  1.0f,  1.0f},
        { 1.0f, -1.0f,  1.0f}, {-1.0f, -1.0f,  1.0f},
        { 1.0f, -1.0f, -1.0f}, {-1.0f, -1.0f, -1.0f},
        { 1.0f, -1.0f, -1.0f}, { 1.0f,  1.0f, -1.0f},
        { 1.0f, -1.0f,  1.0f}, { 1.0f,  1.0f,  1.0f},
        {-1.0f, -1.0f, -1.0f}, {-1.0f, -1.0f,  1.0f},
        {-1.0f,  1.0f, -1.0f}, {-1.0f,  1.0f,  1.0f},
    };
    
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
		Draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		if (height == 0)
			height = 1;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		init(gl);
	}

	/**********************************************************/
	private class TextureImage
	{
		// ��͸�
		int			width;	
		int			height; 
		// ���ظ�ʽ
		int			format; 
		// ��������
		ByteBuffer	data;	
	}

	private float	xrot		= 0f;			
	private float	yrot		= 0f;			
	private float	zrot		= 0f;			

	private int		textures[]	= new int[1];


	//����ͼ��(����)������
	private TextureImage allocateTextureBuffer(int w, int h, int f)
	{
		TextureImage ti = new TextureImage();
		ti.width = w;
		ti.height = h;
		ti.format = f;
		ti.data = BufferUtil.newByteBuffer(w * h * f);
		ti.data.position(0);
		ti.data.limit(ti.data.capacity());
		return ti;
	}

	// ��ȡ��������
	// ��ȡ*.RAW�ļ�������ͼ���ļ����·�תһ����OpenGL��ʹ�ø�ʽ��
	private void readTextureData(String filename, TextureImage buffer) throws IOException
	{
		int i, j, k, done = 0;
		// ��¼ÿһ�еĿ�ȣ����ֽ�Ϊ��λ
		int stride = buffer.width * buffer.format;

		// �õ�������
		InputStream inputStream = getFile(filename);

		for (i = buffer.height - 1; i >= 0; i--)
		{
			int p = i * stride;
			// ��ȡÿһ�е�����
			for (j = 0; j < buffer.width; j++)
			{
				for (k = 0; k < buffer.format - 1; k++, p++, done++)
				{
					// ��ȡһ���ֽ�
					buffer.data.put(p, (byte) inputStream.read());
				}
				// ��255�洢��alphaͨ����
				buffer.data.put(p, (byte) 255);
				p++;
			}
		}
		// �ر���
		inputStream.close();
	}

	// ����2D����
	private void buildTexture(GL10 gl, TextureImage tex)
	{
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		// ����ָ�����ݼ����ʽ��������
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, tex.width, tex.height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, tex.data);    
	}
	/*
	���ڵ���blitter�����ĵط��ˣ����������һ��ͼ������ⲿ�ָ��Ƶ���һ��ͼ������ⲿ�֣�����ϡ�
	srcΪԭͼ��
	dstΪĿ��ͼ��
	src_xstart,src_ystartΪҪ���ƵĲ�����ԭͼ���е�λ��
	src_width,src_heightΪҪ���ƵĲ��ֵĿ�Ⱥ͸߶�
	dst_xstart,dst_ystartΪ���Ƶ�Ŀ��ͼ��ʱ����ʼλ��
	�������˼�ǰ�ԭͼ���е�(src_xstart,src_ystart)-(src_width,src_height)���Ƶ�Ŀ��ͼ����(dst_xstart,dst_ystart)-(src_width,src_height)
	blend�����Ƿ����û�ϣ�0Ϊ�����ã�1Ϊ����
	alpha����Դͼ������ɫ�ڻ��ʱ��ռ�İٷֱ�
	*/
	private void blit(TextureImage src, TextureImage dst, int src_xstart, int src_ystart, int src_width, int src_height, int dst_xstart, int dst_ystart, boolean blend,
		int alpha)
	{
		int i, j, k;
		int s, d;
		// ����alpha��ֵ
		if (alpha > 255)
			alpha = 255;
		if (alpha < 0)
			alpha = 0;

		// Ҫ���Ƶ�������Ŀ��ͼ�������еĿ�ʼλ��
		d = (dst_ystart * dst.width * dst.format);
		// Ҫ���Ƶ�������Դͼ�������еĿ�ʼλ��
		s = (src_ystart * src.width * src.format);

		// ѭ��ÿһ��
		for (i = 0; i < src_height; i++)
		{
			// �ƶ�����һ������
			s = s + (src_xstart * src.format);
			d = d + (dst_xstart * dst.format);
			// ѭ������һ��
			for (j = 0; j < src_width; j++)
			{
				for (k = 0; k < src.format; k++, d++, s++)
				{
					// �ж��Ƿ����û��
					// ����ÿһ���ֽ�
					// ��������˻��,���ݻ�ϸ�����ɫ
					if (blend)
						dst.data.put(d, (byte) (((src.data.get(s) * alpha) + (dst.data.get(d) * (255 - alpha))) >> 8));
					else/*����ֱ�Ӹ���*/
						dst.data.put(d, src.data.get(s));
				}
			}
			// �ƶ�����һ��
			d = d + (dst.width - (src_width + dst_xstart)) * dst.format;
			s = s + (src.width - (src_width + src_xstart)) * src.format;
		}
	}


	public void init(GL10 gl)
	{

		TextureImage t1 = allocateTextureBuffer(256, 256, 4);
		try
		{
			readTextureData("monitor.raw", t1);
		}
		catch (IOException e)
		{
			//Log.d(tag, msg)
			System.out.println("Could not read monitor.raw");
			throw new RuntimeException(e);
		}

		TextureImage t2 = allocateTextureBuffer(256, 256, 4);
		try
		{
			readTextureData("gl.raw", t2);
		}
		catch (IOException e)
		{
			System.out.println("Could not read gl.raw");
			throw new RuntimeException(e);
		}

		blit(t2, t1, 127, 127, 128, 128, 64, 64, true, 127);

		buildTexture(gl, t1);

		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.5f, 1.0f);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LESS);
	}

	private void Draw(GL10 gl)
	{
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);		
        gl.glLoadIdentity();		
        gl.glTranslatef(0.0f, 0.0f, -6.0f);

        gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(vertices));
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        /*  Front Face */
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        /*  Back Face */
        gl.glNormal3f(0.0f, 0.0f, -1.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);

        /*  Top Face */
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);

        /*  Bottom Face */
        gl.glNormal3f(0.0f,-1.0f, 0.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);

        /*  Right Face */
        gl.glNormal3f(1.0f, 0.0f, 0.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);

        /*  Left Face */
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        xrot+=0.6f;
        yrot+=0.5f;
        zrot+=0.7f;
	}
	
	protected static FloatBuffer makeFloatBuffer(float[][] arr)
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
