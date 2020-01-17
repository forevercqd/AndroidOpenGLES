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

	// 纹理坐标数据
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

    // 顶点数据
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
		// 宽和高
		int			width;	
		int			height; 
		// 像素格式
		int			format; 
		// 纹理数据
		ByteBuffer	data;	
	}

	private float	xrot		= 0f;			
	private float	yrot		= 0f;			
	private float	zrot		= 0f;			

	private int		textures[]	= new int[1];


	//申请图像(纹理)缓冲区
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

	// 读取纹理数据
	// 读取*.RAW文件，并把图像文件上下翻转一符合OpenGL的使用格式。
	private void readTextureData(String filename, TextureImage buffer) throws IOException
	{
		int i, j, k, done = 0;
		// 记录每一行的宽度，以字节为单位
		int stride = buffer.width * buffer.format;

		// 得到纹理流
		InputStream inputStream = getFile(filename);

		for (i = buffer.height - 1; i >= 0; i--)
		{
			int p = i * stride;
			// 读取每一行的数据
			for (j = 0; j < buffer.width; j++)
			{
				for (k = 0; k < buffer.format - 1; k++, p++, done++)
				{
					// 读取一个字节
					buffer.data.put(p, (byte) inputStream.read());
				}
				// 把255存储在alpha通道中
				buffer.data.put(p, (byte) 255);
				p++;
			}
		}
		// 关闭流
		inputStream.close();
	}

	// 创建2D纹理
	private void buildTexture(GL10 gl, TextureImage tex)
	{
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		// 根据指定数据及其格式创建纹理
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, tex.width, tex.height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, tex.data);    
	}
	/*
	现在到了blitter函数的地方了，他运行你把一个图像的任意部分复制到另一个图像的任意部分，并混合。
	src为原图像
	dst为目标图像
	src_xstart,src_ystart为要复制的部分在原图像中的位置
	src_width,src_height为要复制的部分的宽度和高度
	dst_xstart,dst_ystart为复制到目标图像时的起始位置
	上面的意思是把原图像中的(src_xstart,src_ystart)-(src_width,src_height)复制到目标图像中(dst_xstart,dst_ystart)-(src_width,src_height)
	blend设置是否启用混合，0为不启用，1为启用
	alpha设置源图像中颜色在混合时所占的百分比
	*/
	private void blit(TextureImage src, TextureImage dst, int src_xstart, int src_ystart, int src_width, int src_height, int dst_xstart, int dst_ystart, boolean blend,
		int alpha)
	{
		int i, j, k;
		int s, d;
		// 掐断alpha的值
		if (alpha > 255)
			alpha = 255;
		if (alpha < 0)
			alpha = 0;

		// 要复制的像素在目标图像数据中的开始位置
		d = (dst_ystart * dst.width * dst.format);
		// 要复制的像素在源图像数据中的开始位置
		s = (src_ystart * src.width * src.format);

		// 循环每一行
		for (i = 0; i < src_height; i++)
		{
			// 移动到下一个像素
			s = s + (src_xstart * src.format);
			d = d + (dst_xstart * dst.format);
			// 循环复制一行
			for (j = 0; j < src_width; j++)
			{
				for (k = 0; k < src.format; k++, d++, s++)
				{
					// 判断是否启用混合
					// 复制每一个字节
					// 如果启用了混合,根据混合复制颜色
					if (blend)
						dst.data.put(d, (byte) (((src.data.get(s) * alpha) + (dst.data.get(d) * (255 - alpha))) >> 8));
					else/*否则直接复制*/
						dst.data.put(d, src.data.get(s));
				}
			}
			// 移动到下一行
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
