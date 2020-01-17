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

		// 首先清理屏幕
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// 重置矩阵
		gl.glLoadIdentity();

		// 视点变换
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

		// 设置视口(OpenGL场景的大小)
		gl.glViewport(0, 0, width, height);

		// 设置投影矩阵为透视投影
		gl.glMatrixMode(GL10.GL_PROJECTION);

		// 重置投影矩阵（置为单位矩阵）
		gl.glLoadIdentity();

		// 创建一个透视投影矩阵（设置视口大小）
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub

		// 告诉系统需要对透视进行修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// 设置清理屏幕的颜色
		gl.glClearColor(0, 0, 0.5f, 1);

		// 启用深度缓存
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
		//打开2D贴图
	    gl.glEnable(GL10.GL_TEXTURE_2D);
	    
	    //打开混色功能
	    gl.glEnable(GL10.GL_BLEND);
	    
	    //指定混色方法
	    gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR); 
	    
	    {
	    	IntBuffer intBuffer = IntBuffer.allocate(1);
			// 创建纹理
			gl.glGenTextures(1, intBuffer);
			mTextureSky = intBuffer.get();
			
			//绑定纹理
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureSky);
			
			//当纹理需要被放大和缩小时都使用线性插值方法调整图像
			gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR); 
			gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR); 
		    
			//生成纹理（加载图像）
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapTextureSky, 0);	
	    }
		
		/****************/
		{
			IntBuffer intBuffer = IntBuffer.allocate(1);
			// 创建纹理
			gl.glGenTextures(1, intBuffer);
			mTextureFloor = intBuffer.get();
			
			//绑定纹理
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureFloor);
			
			//当纹理需要被放大和缩小时都使用线性插值方法调整图像
			gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR); 
			gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR); 
		    
			//生成纹理（加载图像）
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
