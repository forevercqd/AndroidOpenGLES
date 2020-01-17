package com.android.chapter5;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{
	private float rot = 0.0f;
	
	//顶点数组
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
	
	//颜色数组
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
	
	//索引数组
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
	
	
	//法线数组
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
		
		// 首先清理屏幕
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//重置矩阵
		gl.glLoadIdentity();
		
		// 视点变换
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);
		
		//平移操作
		gl.glTranslatef(0.0f,0.0f,-3.0f);
		
		//旋转操作
		gl.glRotatef(rot,1.0f,1.0f,1.0f);
		
		//缩放操作
		gl.glScalef(3.0f, 3.0f, 3.0f);
		
		//允许设置顶点数组
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		//允许设置颜色数组
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		
		
		//允许设置法线数组
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		//设置法线数组
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normals);

		//gl.glShadeModel(GL10.GL_FLAT);
		
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		//设置顶点数组
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		
		//设置颜色数组
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);
		
		//绘制
		gl.glDrawElements(GL10.GL_TRIANGLES, 60, GL10.GL_UNSIGNED_BYTE, icosahedronFaces);

		//取消顶点数组和颜色数组的设置
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
		//取消设置法线数组
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);

		//更改旋转角度
		rot+=0.5;
	}
	
	@Override
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
		
		//创建一个透视投影矩阵（设置视口大小）
		gl.glFrustumf(-ratio, ratio, -1, 1, 1.0f, 1000.0f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//告诉系统需要对透视进行修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//设置清理屏幕的颜色
		gl.glClearColor(0, 0, 0, 1);
		
		//启用深度缓存
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		setupLight(gl);
	}
	
	/* 设置光效 */
	public void setupLight(GL10 gl)
	{
		// 开启颜色材质
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		
		//开启光效
		gl.glEnable(GL10.GL_LIGHTING);
		
		//开启0号光源
		gl.glEnable(GL10.GL_LIGHT0);
		
		//环境光的颜色
		FloatBuffer light0Ambient = FloatBuffer.wrap(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
		//设置环境光
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, light0Ambient);
		
		//漫射光的颜色
		FloatBuffer light0Diffuse = FloatBuffer.wrap(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
		//设置漫射光
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, light0Diffuse);
		
		//高光的颜色
		FloatBuffer light0Specular = FloatBuffer.wrap(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
		//设置高光
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, light0Specular);
		
		//光源的位置
		FloatBuffer light0Position = FloatBuffer.wrap(new float[]{0.0f, 10.0f, 10.0f, 0.0f});
		//设置光源的位置
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light0Position); 
		
		//光线的方向
		FloatBuffer light0Direction = FloatBuffer.wrap(new float[]{0.0f, 0.0f, -1.0f});
		//设置光线的方向
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, light0Direction);

		//设置光源的角度
		gl.glLightf(GL10.GL_LIGHT0, GL10.GL_SPOT_CUTOFF, 45.0f);
		
	}
}

