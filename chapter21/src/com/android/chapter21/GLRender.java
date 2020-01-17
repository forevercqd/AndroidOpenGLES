package com.android.chapter21;

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
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
		
		// ����ģ����ͼ����
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//���þ���
		gl.glLoadIdentity();
		
		// �ӵ�任
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);
		
		//...
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
		
		//����һ��͸��ͶӰ���������ӿڴ�С��
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//����������Ļ����ɫ
		gl.glClearColor(0, 0, 0, 1);
		
		//������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}
	
	/* ��ջ��� */
	private void ClearBuffer(GL10 gl)
	{
		// ���������ɫ�����ֵ
		gl.glClearColor(0.0f,0.0f,0.0f,0.0f);
		// ���������Ȼ����ֵ
		gl.glClearDepthf(1.0f);
		gl.glClearDepthx(1);
		// �������ģ�滺���ֵ
		gl.glClearStencil(0);
		
		// �����ɫ���桢��Ȼ��桢ģ�滺��
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_STENCIL_BUFFER_BIT);		
		
	}
	
	/* ��ȡ��ɫ���� */
	private void ReadColorBuffer(GL10 gl)
	{
		//gl.glReadPixels(10, 10, 50, 50, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);
		/**
		 * x:x
		 * y:y
		 * width��width
		 * height��height
		 * format����ɫ��ʽ
		 * type:��������
		 * pixels�������ȡ����ɫ���浽������
		 *
		 */
		
	}

	/* ���λ��� */
	private void MaskBuffer(GL10 gl)
	{
		// ����д����Ӧ����ɫ����
		gl.glColorMask(true, true, true, true);
		// ������Ȼ���
		gl.glDepthMask(false);
		// ������1��Ӧ������λ��д����ɫ����
		gl.glStencilMask(1);
		
		
	}
	
	/* ���ò��� */
	private void ScissorTest(GL10 gl)
	{
		// �򿪼��ò���
		gl.glEnable(GL10.GL_SCISSOR_TEST);
		// ���ü�������
		gl.glScissor(10, 10, 50, 50);
		
		// �رռ��ò���
		gl.glDisable(GL10.GL_SCISSOR_TEST);
	}
	
	// Alpha����
	private void AlphaTest(GL10 gl)
	{
		// ��Alpha����
		gl.glEnable(GL10.GL_ALPHA_TEST);
		// ����alpha���ԵĲο�ֵ
		gl.glAlphaFunc(GL10.GL_NOTEQUAL, 0.0f);
		
		// �ر�Alpha����
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}
	
	// ģ�����
	private void StencilTest(GL10 gl)
	{
		gl.glEnable(GL10.GL_STENCIL_TEST);
		// ���ò��Խ������
		gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
		// �����ɰ��������ͨ�����ο�ֵ��Ϊ1������ֵҲ��Ϊ0xffffffff
		gl.glStencilFunc(GL10.GL_ALWAYS, 1, 0xffffffff);
		
		// �ر�ģ�����
		gl.glDisable(GL10.GL_STENCIL_TEST);
	}
	
	// ��Ȳ���
	private void DepthTest(GL10 gl)
	{
		// ������Ȳ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// ������Ȳ��Ե�����
		gl.glDepthFunc(GL10.GL_LESS);
		
		// ��ֹ��Ȳ���
		gl.glDisable(GL10.GL_DEPTH_TEST);
	}
	
}
