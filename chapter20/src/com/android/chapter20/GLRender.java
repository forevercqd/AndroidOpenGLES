package com.android.chapter20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{	
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
	
	//��ɫ����
	private FloatBuffer colors = FloatBuffer.wrap(new float[]{
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.5f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f,
			0.5f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.5f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 0.5f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.5f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f, 0.5f, 1.0f});
	
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

		gl.glScalef(2.0f, 2.0f, 2.0f);

		/************************/
		IntBuffer bufs = IntBuffer.allocate(1), samples = IntBuffer.allocate(1);
		// ����Ƿ�֧�ֶ��ز���
		gl.glGetIntegerv(GL11.GL_SAMPLE_BUFFERS, bufs);
		// �õ���������
		gl.glGetIntegerv(GL11.GL_SAMPLES, samples);
		
		if ( bufs.get(0) == 1 && samples.get(0)>1)
		{
			// ͨ��alphaֵȷ��������������
			gl.glEnable(GL10.GL_MULTISAMPLE);
			
			gl.glEnable(GL11.GL_SAMPLE_ALPHA_TO_COVERAGE);
			gl.glEnable(GL11.GL_SAMPLE_ALPHA_TO_ONE);
			
			// ֱ��ʹ������������
			gl.glEnable(GL10.GL_SAMPLE_COVERAGE);
			gl.glSampleCoverage(0.6f, true);
			//DRAW
			gl.glSampleCoverage(0.6f, false);
			//DRAW
			
		}
		
		// �򿪵��ֱ�ߵķ�����
//		gl.glEnable(GL10.GL_BLEND);
//		gl.glEnable(GL10.GL_POINT_SMOOTH);
//		gl.glEnable(GL10.GL_LINE_SMOOTH);
//		gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
		gl.glTranslatef(0.0f,1.0f,-1.0f);
		draw3D(gl);
		
		if ( bufs.get(0) == 1  && samples.get(0)>1)
		{
			gl.glDisable(GL10.GL_MULTISAMPLE);
			gl.glDisable(GL11.GL_SAMPLE_ALPHA_TO_COVERAGE);
			gl.glDisable(GL11.GL_SAMPLE_ALPHA_TO_ONE);
			
			gl.glDisable(GL10.GL_SAMPLE_COVERAGE);
		}
		
		// ��ֹ���ֱ�ߵķ�����
//		gl.glDisable(GL10.GL_BLEND);
//		gl.glDisable(GL10.GL_POINT_SMOOTH);
//		gl.glDisable(GL10.GL_LINE_SMOOTH);
		gl.glTranslatef(0.0f,-2.0f,0.0f);
		draw3D(gl);
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
	}

	private void draw3D(GL10 gl)
	{
		gl.glFrontFace(GL10.GL_CCW);
		
		//�������ö�������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		//����������ɫ����
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		//���ö�������
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		
		//������ɫ����
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);
		
		//����
		gl.glDrawElements(GL10.GL_TRIANGLES, 60, GL10.GL_UNSIGNED_BYTE, icosahedronFaces);
		
		// ʹ���ߵķ�ʽ����
		//gl.glDrawElements(GL10.GL_LINES, 60, GL10.GL_UNSIGNED_BYTE, icosahedronFaces);

		//ȡ�������������ɫ���������
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}
	
}