package com.android.chapter29;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;

public class GLRender implements Renderer
{
	private Bitmap mBitmapTextureSky;
	
	private int mTextureSky = -1;
	
	private Bitmap mBitmapTextureFloor;
	
	private int mTextureFloor = -1;
	
	private SkyBox skybox = null;
	private Plane plane = null;
	
	private float centerX,centerY=35,centerZ;
	
	public GLRender(Context context)
	{
		mBitmapTextureSky = BitmapFactory.decodeResource(context.getResources(), R.drawable.clouds);
		mBitmapTextureFloor = BitmapFactory.decodeResource(context.getResources(), R.drawable.floor);
	}

	
	public void onDrawFrame(GL10 gl)
	{
		// TODO Auto-generated method stub

		// ����������Ļ
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// ����ģ����ͼ����
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// ���þ���
		gl.glLoadIdentity();

		// �ӵ�任
		GLU.gluLookAt(gl, 40, 60, 50, centerX, centerY, centerZ, 0, 1, 0);

		plane.renderModel(gl);
		
		// ...
		skybox.setRotate(-90.0f, 0.0f, 0.0f);
		skybox.renderModel(gl);
	}

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

		// ����һ��͸��ͶӰ���������ӿڴ�С��
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub

		// ����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// ����������Ļ����ɫ
		gl.glClearColor(0, 0, 0.5f, 1);

		// ������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		setupTexture(gl);
		
		init();
	}
	
	private void init()
	{
		skybox = new SkyBox(128, 30.0f, 10.0f);
		skybox.setTextureId(mTextureSky,0);
		
		plane = new Plane(4, 4, 64.0f);
		plane.setTextureId(mTextureFloor,0);
	    plane.setPosition(-128.0f, 0.0f, -128.0f);
	}
	
	private void setupTexture(GL10 gl)
	{
		//��2D��ͼ
	    gl.glEnable(GL10.GL_TEXTURE_2D);
	    
	    //�򿪻�ɫ����
	    gl.glEnable(GL10.GL_BLEND);
	    
	    //ָ����ɫ����
	    gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR); 
	    
	    {
	    	IntBuffer intBuffer = IntBuffer.allocate(1);
			// ��������
			gl.glGenTextures(1, intBuffer);
			mTextureSky = intBuffer.get();
			
			//������
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureSky);
			
			//��������Ҫ���Ŵ����Сʱ��ʹ�����Բ�ֵ��������ͼ��
			gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR); 
			gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR); 
		    
			//������������ͼ��
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapTextureSky, 0);	
	    }
		
		/****************/
		{
			IntBuffer intBuffer = IntBuffer.allocate(1);
			// ��������
			gl.glGenTextures(1, intBuffer);
			mTextureFloor = intBuffer.get();
			
			//������
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureFloor);
			
			//��������Ҫ���Ŵ����Сʱ��ʹ�����Բ�ֵ��������ͼ��
			gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR); 
			gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR); 
		    
			//������������ͼ��
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapTextureFloor, 0);	
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if (event.getAction() != KeyEvent.ACTION_UP) { return false; }
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
		{
			centerX-=10;
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
			centerX+=10;
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
		{
			centerY+=10;
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
		{
			centerY-=10;
		}
		else if (keyCode == KeyEvent.KEYCODE_SPACE)
		{
			
		}
		return false;
	}
}
