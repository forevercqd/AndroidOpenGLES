package com.android.chapter18;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{	
	float xrot = 0.0f;
	float yrot = 0.0f;
	
	float lightAmbient[] = new float[] { 0.2f, 0.3f, 0.6f, 1.0f };
	float lightDiffuse[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float[] lightPos = new float[] {0,0,3,1};
	
	float matAmbient[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float matDiffuse[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	
	float white[] = new float[] { 1f, 1f, 1f, 1.0f};
	float trans[] = new float[] { 1f, 1f, 1f, 0.3f};
	
	//��������
	private FloatBuffer vertices = FloatBuffer.wrap(new float[] {
			// FRONT
			 0.5f, -0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			-0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			// BACK
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// LEFT
			-0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			// RIGHT
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			// TOP
			-0.5f,  0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			 -0.5f,  0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// BOTTOM
			-0.5f, -0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f, -0.5f, -0.5f,
		}); 
	
	private FloatBuffer floorVertices = FloatBuffer.wrap(new float[] {
			-3.0f, 0.0f, 3.0f,
			 3.0f, 0.0f, 3.0f,
			-3.0f, 0.0f,-3.0f,
			 3.0f, 0.0f,-3.0f
		});
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		// TODO Auto-generated method stub
		
		// ����������Ļ
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_STENCIL_BUFFER_BIT);
		
		// ����ģ����ͼ����
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//���þ���
		gl.glLoadIdentity();
		
		// �ӵ�任
		GLU.gluLookAt(gl, 0, 0.5f, 3, 0, 0, 0, 0, 1, 0);
		

		/* ���Ƶ�ģ�滺���,ָ���ɰ����� */
		// ��ֹ��Ȳ���
		gl.glDisable(GL10.GL_DEPTH_TEST);
		// ���ò�����дɫ�����֡����
		gl.glColorMask(false, false, false, false);
		// ������д����Ȼ�����
		gl.glDepthMask(false);
		
		// ����ģ�����
		gl.glEnable(GL10.GL_STENCIL_TEST);
		gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
		// �����ɰ��������ͨ�����ο�ֵ��Ϊ1������ֵҲ��Ϊ0xffffffff
		gl.glStencilFunc(GL10.GL_ALWAYS, 1, 0xffffffff);
	
		// ���Ƶ���
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floorVertices);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, white, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, white, 0);
		gl.glNormal3f(0,1,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		/* ���Ʒ��� */
		gl.glColorMask(true, true, true, true);
		gl.glDepthMask(true);
		/* �����ɰ建�� */
		// ������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glStencilFunc(GL10.GL_EQUAL, 1, 0xffffffff);
		gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);
		
		/* ���Ʒ������������ */
		// ���浱ǰ�ľ���
		gl.glPushMatrix();
		gl.glScalef(1.0f, -1f, 1f);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		gl.glCullFace(GL10.GL_FRONT);
		
		setupCube(gl);
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f,2.0f,-1.0f);
		gl.glRotatef(xrot, 1, 0, 0);
		gl.glRotatef(yrot, 0, 1, 0);
		// ���Ƶ���
		drawCube(gl);
		gl.glPopMatrix();
		
		gl.glCullFace(GL10.GL_BACK);
		gl.glPopMatrix();
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		
		// ��ֹ�ɰ����
		gl.glDisable(GL10.GL_STENCIL_TEST);
		
		/* ��ϵ����뷴������ */
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floorVertices);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, trans, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, trans, 0);
		gl.glNormal3f(0,1,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisable(GL10.GL_BLEND);
		
		setupCube(gl);
		gl.glPushMatrix();
		gl.glTranslatef(0.0f,2.0f,-1.0f);
		gl.glRotatef(xrot, 1, 0, 0);
		gl.glRotatef(yrot, 0, 1, 0);
		
		/* ���������� */
		drawCube(gl);
		gl.glPopMatrix();
		
		xrot+=1.0f;
		yrot+=0.5f;
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
		
		//����һ��͸��ͶӰ���������ӿڴ�С��
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//����������Ļ����ɫ
		gl.glClearColor(0, 0, 0, 1);
		
		//������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		// �򿪹�Ч
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		
		// ���ò���
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		
		// ���ù�Ч
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		// �������ö�������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// ����������Ȼ���
		gl.glClearDepthf(1.0f);						
		// �޳�����
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Ϊģ�建����ָ�����������ֵ..
		gl.glClearStencil(0);
	}

	public void setupCube(GL10 gl)
	{
		// ���ö�������
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	}

	// ����������
	public void drawCube(GL10 gl)
	{
		gl.glColor4f(1.0f, 1, 1, 1.0f);
		gl.glNormal3f(0, 0, 1);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glNormal3f(0, 0, -1);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);

		gl.glColor4f(1, 1.0f, 1, 1.0f);
		gl.glNormal3f(-1, 0, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
		gl.glNormal3f(1, 0, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);

		gl.glColor4f(1, 1, 1.0f, 1.0f);
		gl.glNormal3f(0, 1, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
		gl.glNormal3f(0, -1, 0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
	}
}
