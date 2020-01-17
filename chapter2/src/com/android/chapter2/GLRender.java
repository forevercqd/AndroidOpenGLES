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

		// ����������Ļ
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// ����ģ����ͼ����
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// ���þ���
		gl.glLoadIdentity();

		// �ӵ�任
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);

		// ...
		
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
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub

		// ����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// ����������Ļ����ɫ
		gl.glClearColor(0, 0, 0, 1);

		// ������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

}
