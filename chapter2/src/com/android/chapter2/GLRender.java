package com.android.chapter2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{
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
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);

		// ...
		
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
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub

		// 告诉系统需要对透视进行修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// 设置清理屏幕的颜色
		gl.glClearColor(0, 0, 0, 1);

		// 启用深度缓存
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

}
