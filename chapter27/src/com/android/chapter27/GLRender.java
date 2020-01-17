package com.android.chapter27;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class GLRender implements Renderer
{

	final float[]		projM	= new float[16];
	final float[]		rotM	= new float[16];
	final float[]		modelM	= new float[16];
	final ShortBuffer	tri		= shortBuf(new short[]{0, 1, 2});
	final FloatBuffer	v0		= floatBuf(new float[]{-1f, -1f, 0f, 1f, 1f, -1f, 0f, 1f, 1f, 1f, 0f, 1f});
	final FloatBuffer	v1		= floatBuf(new float[]{1f, 1f, 0f, 1f, -1f, 1f, 0f, 1f, -1f, -1f, 0f, 1f});
	int[]				view;
	VelocityTracker		vel;

	public GLRender()
	{
		vel = VelocityTracker.obtain();
	}

	static FloatBuffer floatBuf(float[] xs)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(4 * xs.length);
		buf.order(ByteOrder.nativeOrder());
		FloatBuffer b = buf.asFloatBuffer();
		b.put(xs);
		b.position(0);
		return b;
	}

	static ShortBuffer shortBuf(short[] xs)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(2 * xs.length);
		buf.order(ByteOrder.nativeOrder());
		ShortBuffer b = buf.asShortBuffer();
		b.put(xs);
		b.position(0);
		return b;
	}

	// øΩ±¥æÿ’Û
	static void copyM(float[] dst, float[] src)
	{
		for (int i = 0; i < 16; i++)
			dst[i] = src[i];
	}

	public void onDrawFrame(GL10 gl)
	{
		gl.glClearColor(0.0f, 0.0f, 0.5f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glMultMatrixf(modelM, 0);

		gl.glColor4f(1f, 0f, 0f, 1f);
		gl.glVertexPointer(4, GL10.GL_FLOAT, 0, v0);
		gl.glDrawElements(GL10.GL_TRIANGLES, 3, GL10.GL_UNSIGNED_SHORT, tri);
		gl.glVertexPointer(4, GL10.GL_FLOAT, 0, v1);
		gl.glDrawElements(GL10.GL_TRIANGLES, 3, GL10.GL_UNSIGNED_SHORT, tri);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		view = new int[]{0, 0, width, height};
		float ratio = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-1f, 1f, -1f / ratio, 1f / ratio, 1f, 10f);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		// æÿ’Ûµ•ŒªªØ
		Matrix.setIdentityM(projM, 0);
		Matrix.frustumM(projM, 0, -1f, 1f, -1f / ratio, 1f / ratio, 1f, 10f);
		// æÿ’Ûµ•ŒªªØ
		Matrix.setIdentityM(rotM, 0);
		Matrix.setIdentityM(modelM, 0);
		rotModel(0f, 0f);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	}

	public synchronized void rotModel(float x, float y)
	{
		float[] old = new float[16];
		float[] r = new float[16];
		copyM(old, rotM);
		Matrix.setIdentityM(r, 0);
		// –˝◊™æÿ’Û
		Matrix.rotateM(r, 0, 5f * x, 1f, 0f, 0f);
		Matrix.rotateM(r, 0, 5f * y, 0f, 1f, 0f);
		// æÿ’Ûœ‡≥À
		Matrix.multiplyMM(rotM, 0, r, 0, old, 0);

		Matrix.setIdentityM(modelM, 0);
		// Àı∑≈æÿ’Û
		Matrix.scaleM(modelM, 0, 2f, 2f, 1f);
		// ∆Ω“∆æÿ’Û
		Matrix.translateM(modelM, 0, 0f, 0f, -4f);
		// æÿ’Ûœ‡≥À
		Matrix.multiplyMM(modelM, 0, modelM, 0, rotM, 0);
	}

	public synchronized void showPos(float x, float y)
	{
		// »°µ√…‰œﬂ
		y = view[3] - 1 - y;
		float[] r1 = new float[4];
		float[] r2 = new float[4];
		GLU.gluUnProject(x, y, 0f, modelM, 0, projM, 0, view, 0, r1, 0);
		GLU.gluUnProject(x, y, 1f, modelM, 0, projM, 0, view, 0, r2, 0);

		// solve (x,y) for z=0
		float t = -r1[2] / r2[2];
		float mx = r1[0] + t * r2[0];
		float my = r1[1] + t * r2[1];

		Log.v("Unproject", " Point: " + mx + ", " + my + ", " + 0);

		// 3D->2D
		//GLU.gluProject(objX, objY, objZ, model, modelOffset, project, projectOffset, view, viewOffset, win, winOffset)
	}
	
	private boolean f(int x,int y,int z)
	{
		//......
		return true;
	}

	public boolean onTouchEvent(final MotionEvent ev)
	{
		if (ev.getAction() != MotionEvent.ACTION_DOWN) { return false; }
		vel.addMovement(ev);
		vel.computeCurrentVelocity(1);
		showPos(ev.getX(), ev.getY());
		rotModel(vel.getYVelocity(), vel.getXVelocity());
		return true;
	}
}