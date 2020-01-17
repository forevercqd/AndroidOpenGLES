package com.android.chapter26;

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
	private Context	mContext;

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
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) (width) / (float) (height), 1.0f, 100.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		//更新鼠标的移动范围
		arcBall.setBounds((float) width, (float) height);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		init(gl);
	}

	/********************************************************/
	private GlSphere			quadricGlSphere;
	private GlCylinder			quadricGlCylinder;
	//上一次鼠标拖动得到的旋转矩阵
    private Matrix4f LastRot = new Matrix4f();
    //这次鼠标拖动得到的旋转矩阵。
    private Matrix4f ThisRot = new Matrix4f();
    private final Object matrixLock = new Object();
    private float[] matrix = new float[16];
    // 构造轨迹球
    private ArcBall arcBall = new ArcBall(320.0f, 480.0f);
    
    public void init(GL10 gl)
	{

		LastRot.setIdentity();
		ThisRot.setIdentity();
		ThisRot.get(matrix);

		gl.glClearColor(0.0f, 0.0f, 0.5f, 0.5f);
		gl.glClearDepthf(1.0f);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glShadeModel(GL10.GL_FLAT);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		quadricGlSphere = new GlSphere(1.3f, 32, 12, true, true);
		quadricGlCylinder = new GlCylinder(0.7f, 0.7f, 2.0f, 32, 4, true, true);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glEnable(GL10.GL_LIGHTING);

		gl.glEnable(GL10.GL_COLOR_MATERIAL);
	}

    //重置所有的变量
    void reset()
	{
		synchronized (matrixLock)
		{
			LastRot.setIdentity();
			ThisRot.setIdentity();
		}
	}


	void startDrag(Point MousePt)
	{
		synchronized (matrixLock)
		{
			LastRot.set(ThisRot);
		}
		arcBall.click(MousePt);
	}

	// 更新轨迹球的变量
	void drag(Point MousePt)
	{
		Quat4f ThisQuat = new Quat4f();

		arcBall.drag(MousePt, ThisQuat);
		synchronized (matrixLock)
		{
			// 计算旋转量
			ThisRot.setRotation(ThisQuat);
			ThisRot.mul(ThisRot, LastRot);
		}
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
	
    public void Draw(GL10 gl)
	{
		synchronized (matrixLock)
		{
			ThisRot.get(matrix);
		}

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(-1.5f, 0.0f, -10.0f);
		
		gl.glPushMatrix();
		gl.glMultMatrixf(matrix, 0);
		gl.glColor4f(0.75f, 0.75f, 1.0f, 1.0f);
		quadricGlCylinder.draw(gl);
		gl.glPopMatrix();

		gl.glLoadIdentity();
		gl.glTranslatef(1.5f, 0.0f, -10.0f);

		gl.glPushMatrix();
		gl.glMultMatrixf(matrix, 0);
		gl.glColor4f(1.0f, 0.75f, 0.75f, 1.0f);
		quadricGlSphere.draw(gl);
		gl.glPopMatrix();
	}


}

