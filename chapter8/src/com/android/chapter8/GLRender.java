package com.android.chapter8;

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
import android.view.KeyEvent;

public class GLRender implements Renderer
{

	private Bitmap		mBitmapTexture	= null;

	int					mTexture[];

	private Tunnel3D	tunnel;
	private boolean		created;
	
	private float  centerX = 0.0f;
	private float  centerY = 0.0f;

	public GLRender(Context context)
	{
		mBitmapTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.img);
		mTexture = new int[1];

		tunnel = new Tunnel3D(10, 20);
		created = false;
	}


	@Override
	public void onDrawFrame(GL10 gl)
	{
		// TODO Auto-generated method stub

		// ����Ƿ񴴽�egl
		boolean c = false;
		synchronized (this)
		{
			c = created;
		}
		if (!c)
			return;

		// ����������Ļ
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// ����ģ����ͼ����
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// ���þ���
		gl.glLoadIdentity();

		// �ӵ�任
		GLU.gluLookAt(gl, 0, 0, 1, centerX, centerY, 0, 0, 1, 0);

		//������Ⱦģʽ
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		// �������ö�������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// ����������ɫ����
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		// ��������������������
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// ��Ⱦ���
		tunnel.render(gl, -0.6f);

		// �������
		tunnel.nextFrame();

		// ��ֹ���ö�������
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		// ��ֹ������ɫ����
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		// ��ֹ����������������
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

	}


	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// TODO Auto-generated method stub

		float ratio = (float) width / height;

		// �����ӿ�(OpenGL�����Ĵ�С)
		gl.glViewport(0, 0, width, height);

		// ����ͶӰ����Ϊ͸��ͶӰ
		gl.glMatrixMode(GL10.GL_PROJECTION);

		// ����ͶӰ������Ϊ��λ����
		gl.glLoadIdentity();

		// ����һ���ԳƵ�͸��ͶӰ����
		GLU.gluPerspective(gl, 45.0f, ratio, 1f, 100f);
	}


	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub

		// ����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// ����������Ļ����ɫ
		gl.glClearColor(0, 0, 0, 1);

		// ������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);

		initApp(gl);
		
		setupLight(gl);
	}


	private void initApp(GL10 gl)
	{
		created = true;

		// ����2D������ͼ
		gl.glEnable(GL10.GL_TEXTURE_2D);

		loadTexture(gl, mBitmapTexture);
	}


	private void loadTexture(GL10 gl, Bitmap bmp)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer ib = bb.asIntBuffer();

		for (int y = 0; y < bmp.getHeight(); y++)
		{
			for (int x = 0; x < bmp.getWidth(); x++)
			{
				ib.put(bmp.getPixel(x, y));
			}
		}
		ib.position(0);
		bb.position(0);
		//��������	
		gl.glGenTextures(1, mTexture, 0);
		
		//������
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
		
		//��������
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bmp.getWidth(), bmp.getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
		/**
		 * int target,  //��������һ��2D����
		 * int level,    //����ͼ�����ϸ�̶�, Ĭ��Ϊ0���� 
		 * int internalformat, //��ɫ�ɷ�R(��ɫ����)��G(��ɫ����)��B(��ɫ����)�����֣���Ϊ4����R(��ɫ����)��G(��ɫ����)��B(��ɫ����)��Alpha
		 * int width, //����Ŀ��
		 * int height, //����ĸ߶�
		 * int border, //�߿��ֵ
		 * int format, //����OpenGLͼ�������ɺ졢�̡�����A��ɫ�������
		 * int type,   //���ͼ����������޷����ֽ�����
		 * Buffer pixels //����OpenGL�������ݵ���Դ
		 */
		//�������Բ�ֵ�㷨
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	}
	
	private void setupLight(GL10 gl)
	{
		//������Ч
		gl.glEnable(GL10.GL_LIGHTING);

		//����0�Ź�Դ
		gl.glEnable(GL10.GL_LIGHT0);
		
		//���������ɫ
		FloatBuffer light0Ambient = FloatBuffer.wrap(new float[]{0.4f, 0.4f, 0.4f, 1.0f});
		//���û�����
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, light0Ambient);
		
		//��������ɫ
		FloatBuffer light0Diffuse = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
		//���������
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, light0Diffuse);
		
		//�߹����ɫ
		FloatBuffer light0Position = FloatBuffer.wrap(new float[]{10.0f, 10.0f, 10.0f});
		//���ø߹�
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light0Position);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
		{
			centerX-=0.1f;
		}
		else if ( keyCode == KeyEvent.KEYCODE_DPAD_RIGHT )
		{
			centerX+=0.1f;
		}
		else if ( keyCode == KeyEvent.KEYCODE_DPAD_UP ) 
		{
			centerY+=0.1f;
		}
		else if ( keyCode == KeyEvent.KEYCODE_DPAD_DOWN ) 
		{
			centerY-=0.1f;
		}
		return false;
	}
}
