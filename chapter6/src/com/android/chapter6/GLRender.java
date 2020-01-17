package com.android.chapter6;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{
	private float rot = 0.0f;
	
	
	//��������
	private FloatBuffer vertices = FloatBuffer.wrap(new float[]{
			 0.f, 			-0.525731f,  0.850651f,
			 0.850651f, 		   0.f,  0.525731f,
			 0.850651f, 		   0.f, -0.525731f,
			-0.850651f, 		   0.f, -0.525731f,
			-0.850651f, 		   0.f,  0.525731f,
			-0.525731f, 	 0.850651f,  0.f,
			 0.525731f, 	 0.850651f,  0.f,
			 0.525731f, 	-0.850651f,  0.f,
			-0.525731f, 	-0.850651f,  0.f,
			 0.f, 			-0.525731f, -0.850651f,
			 0.f, 			 0.525731f, -0.850651f,
			 0.f, 			 0.525731f,  0.850651f}); 
	
	//��������
	private ByteBuffer icosahedronFaces = ByteBuffer.wrap(new byte[]{
			 1,  2,  6,
	         1,  7,  2,
	         3,  4,  5,
	         4,  3,  8,
	         6,  5, 11,
	         5,  6, 10,
	         9, 10,  2,
	        10,  9,  3,
	         7,  8,  9,
	         8,  7,  0,
	        11,  0,  1,
	         0, 11,  4,
	         6,  2, 10,
	         1,  6, 11,
	         3,  5, 10,
	         5,  4, 11,
	         2,  7,  9,
	         7,  1,  0,
	         3,  9,  8,
	         4,  8,  0});
	
	
	//��������
	private FloatBuffer normals = FloatBuffer.wrap(new float[]{
			 0.000000f, -0.417775f,  0.675974f,
			 0.675973f,  0.000000f,  0.417775f,
			 0.675973f, -0.000000f, -0.417775f,
			-0.675973f,  0.000000f, -0.417775f,
			-0.675973f, -0.000000f,  0.417775f,
			-0.417775f,  0.675974f,  0.000000f,
			 0.417775f,  0.675973f, -0.000000f,
			 0.417775f, -0.675974f,  0.000000f,
			-0.417775f, -0.675974f,  0.000000f,
			 0.000000f, -0.417775f, -0.675973f,
			 0.000000f,  0.417775f, -0.675974f,
			 0.000000f,  0.417775f,  0.675973f});
	
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		// TODO Auto-generated method stub
		
		// ����������Ļ
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// ����ģ����ͼ����
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//���þ���
		gl.glLoadIdentity();
		
		// �ӵ�任
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);
		
		//ƽ�Ʋ���
		gl.glTranslatef(0.0f,0.0f,-3.0f);
		
		//��ת����
		gl.glRotatef(rot,1.0f,1.0f,1.0f);
		
		//���Ų���
		gl.glScalef(3.0f, 3.0f, 3.0f);
		
		//�������ö�������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		//�������÷�������
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		//���÷�������
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normals);

		//gl.glShadeModel(GL10.GL_FLAT);
		
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		//���ö�������
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		
		//����
		gl.glDrawElements(GL10.GL_TRIANGLES, 60, GL10.GL_UNSIGNED_BYTE, icosahedronFaces);

		//ȡ���������������
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		//ȡ�����÷�������
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	    
	    rot+=0.5f;
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
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//����ϵͳ��Ҫ��͸�ӽ�������
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//����������Ļ����ɫ
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		//������Ȼ���
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		setupLight(gl);
		
		setupMaterial(gl);
	}
	
	
	private void setupMaterial(GL10 gl)
	{
//		//����Ԫ�غ�ɢ��Ԫ����ɫ
//		FloatBuffer ambientAndDiffuse = FloatBuffer.wrap(new float[]{0.0f, 0.1f, 0.9f, 1.0f});
//		//���û���Ԫ�غ�ɢ��Ԫ��
//		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, ambientAndDiffuse);
		
		//����Ԫ����ɫ
		FloatBuffer ambient = FloatBuffer.wrap(new float[]{0.0f, 0.1f, 0.9f, 1.0f});
		//���û���Ԫ��
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambient);
		
		//ɢ��Ԫ����ɫ
		FloatBuffer diffuse = FloatBuffer.wrap(new float[]{0.9f, 0.0f, 0.1f, 1.0f});
		//����ɢ��Ԫ��
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuse);
		
		//�߹�Ԫ����ɫ
		FloatBuffer specular = FloatBuffer.wrap(new float[]{0.9f, 0.9f, 0.0f, 1.0f});
		//���ø߹�Ԫ��
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specular);
		//���÷����
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25.0f);
		
		//�Է�����ɫ
		FloatBuffer emission = FloatBuffer.wrap(new float[]{0.0f, 0.4f, 0.0f, 1.0f});
		//�����Է���
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, emission);
	}
	
	private void setupLight(GL10 gl)
	{
		//����ƽ����Ӱģʽ
		gl.glShadeModel(GL10.GL_SMOOTH);

		//������Ч
		gl.glEnable(GL10.GL_LIGHTING);
		
		//����0�Ź�Դ
		gl.glEnable(GL10.GL_LIGHT0);
		
		//���������ɫ
		FloatBuffer light0Ambient = FloatBuffer.wrap(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
		//���û�����
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, light0Ambient);
		
		//��������ɫ
		FloatBuffer light0Diffuse = FloatBuffer.wrap(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
		//���������
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, light0Diffuse);
		
		//�߹����ɫ
		FloatBuffer light0Specular = FloatBuffer.wrap(new float[]{0.9f, 0.9f, 0.0f, 1.0f});
		//���ø߹�
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, light0Specular);
		
		//��Դ��λ��
		FloatBuffer light0Position = FloatBuffer.wrap(new float[]{0.0f, 10.0f, 10.0f, 0.0f});
		//���ù�Դ��λ��
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light0Position); 
		
		//���ߵķ���
		FloatBuffer light0Direction = FloatBuffer.wrap(new float[]{0.0f, 0.0f, -1.0f});
		//���ù��ߵķ���
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, light0Direction);

		//���ù�Դ�ĽǶ�
		gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_CUTOFF, 45.0f);
	}
}

