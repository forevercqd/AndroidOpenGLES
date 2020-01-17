package com.android.chapter19;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{
	private Context mcontext;
	private Bitmap mBitmapTexture;
	
	float lightAmbient[] = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };
	float lightDiffuse[] = new float[] { 1f, 1f, 1f, 1.0f };
	float[] lightPos = new float[] {0,0,3,1};
	
	float matAmbient[] = new float[] { 1f, 1f, 1f, 1.0f };
	float matDiffuse[] = new float[] { 1f, 1f, 1f, 1.0f };
		
	BitmapFont font;
	
	float xrot = 0.0f;
	float yrot = 0.0f;
	
	public GLRender(Context context)
	{
		mcontext = context;
		mBitmapTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.font);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		// TODO Auto-generated method stub
		
		// 首先清理屏幕
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//重置矩阵
		gl.glLoadIdentity();
		
		// 视点变换
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);
		
		gl.glTranslatef(0, 0, -10);
		
		gl.glRotatef(xrot, 1, 0, 0);
		gl.glRotatef(yrot, 0, 1, 0);
		
		gl.glActiveTexture(GL10.GL_TEXTURE0); 
		gl.glClientActiveTexture(GL10.GL_TEXTURE0);
		
		font.draw(gl, "Hello Android");
		
		yrot += 1.0;
		xrot += 1.0;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// TODO Auto-generated method stub
		
		float ratio = (float) width / height;
		
		// 设置视口(OpenGL场景的大小)
		gl.glViewport(0, 0, width, height);

		// 设置投影矩阵为透视投影
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		// 重置投影矩阵（置为单位矩阵）
		gl.glLoadIdentity();
		
		//创建一个透视投影矩阵（设置视口大小）
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//告诉系统需要对透视进行修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//设置清理屏幕的颜色
		gl.glClearColor(0, 0, 0, 1);
		
		//启用深度缓存
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glClearColor(0.0f, 0.0f, 1.0f, 0.0f);
		gl.glClearDepthf(1.0f);
		
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		font = new BitmapFont(loadTexture(gl));
	}

	private int loadTexture(GL10 gl)
	{
		int[] tmp_tex = new int[1];

		gl.glGenTextures(1, tmp_tex, 0);
		int tex = tmp_tex[0];
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, mBitmapTexture.getWidth(), mBitmapTexture.getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
		gl.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0,mBitmapTexture.getWidth(), mBitmapTexture.getHeight(), GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, makeByteBuffer(mBitmapTexture));
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		
		mBitmapTexture.recycle();
		mBitmapTexture = null;
		
		return tex;
	}
	
	protected static FloatBuffer makeFloatBuffer(float[] arr)
	{
		
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}


	protected static IntBuffer makeIntBuffer(int[] arr)
	{
		// 分配指定大小的缓冲区
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		//修改此缓冲区的字节顺序(获取底层平台的本机字节顺序)
		bb.order(ByteOrder.nativeOrder());
		//创建此字节缓冲区的视图，作为 int 缓冲区
		IntBuffer ib = bb.asIntBuffer();
		// 将数据写入缓冲区
		ib.put(arr);
		// 缓冲区的位置设置为0
		ib.position(0);
		// 返回此缓冲区的位置
		return ib;
	}

	
	protected static ByteBuffer makeByteBuffer(Bitmap bmp)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
		// 修改缓冲区的字节顺序
		bb.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = bb.asIntBuffer();

		for (int y = 0; y < bmp.getHeight(); y++)
			for (int x = 0; x < bmp.getWidth(); x++)
			{
				int pix = bmp.getPixel(x, bmp.getHeight() - y - 1);
				// Convert ARGB -> RGBA
				byte alpha = (byte) ((pix >> 24) & 0xFF);
				byte red = (byte) ((pix >> 16) & 0xFF);
				byte green = (byte) ((pix >> 8) & 0xFF);
				byte blue = (byte) ((pix) & 0xFF);

				ib.put(((red & 0xFF) << 24) | ((green & 0xFF) << 16) | ((blue & 0xFF) << 8) | ((alpha & 0xFF)));
			}
		ib.position(0);
		bb.position(0);
		return bb;
	}
}