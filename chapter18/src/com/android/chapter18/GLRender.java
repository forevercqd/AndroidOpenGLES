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
	
	//顶点数组
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
		
		// 首先清理屏幕
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_STENCIL_BUFFER_BIT);
		
		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//重置矩阵
		gl.glLoadIdentity();
		
		// 视点变换
		GLU.gluLookAt(gl, 0, 0.5f, 3, 0, 0, 0, 0, 1, 0);
		

		/* 绘制到模版缓冲层,指定蒙板区域 */
		// 禁止深度测试
		gl.glDisable(GL10.GL_DEPTH_TEST);
		// 设置不允许写色彩组件帧缓冲
		gl.glColorMask(false, false, false, false);
		// 不允许写入深度缓冲区
		gl.glDepthMask(false);
		
		// 启动模版测试
		gl.glEnable(GL10.GL_STENCIL_TEST);
		gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
		// 设置蒙板测试总是通过，参考值设为1，掩码值也设为0xffffffff
		gl.glStencilFunc(GL10.GL_ALWAYS, 1, 0xffffffff);
	
		// 绘制地面
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floorVertices);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, white, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, white, 0);
		gl.glNormal3f(0,1,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		/* 绘制反射 */
		gl.glColorMask(true, true, true, true);
		gl.glDepthMask(true);
		/* 启用蒙板缓存 */
		// 启用深度缓存
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glStencilFunc(GL10.GL_EQUAL, 1, 0xffffffff);
		gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);
		
		/* 绘制反射出的立方体 */
		// 保存当前的矩阵
		gl.glPushMatrix();
		gl.glScalef(1.0f, -1f, 1f);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		gl.glCullFace(GL10.GL_FRONT);
		
		setupCube(gl);
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f,2.0f,-1.0f);
		gl.glRotatef(xrot, 1, 0, 0);
		gl.glRotatef(yrot, 0, 1, 0);
		// 绘制地面
		drawCube(gl);
		gl.glPopMatrix();
		
		gl.glCullFace(GL10.GL_BACK);
		gl.glPopMatrix();
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		
		// 禁止蒙板测试
		gl.glDisable(GL10.GL_STENCIL_TEST);
		
		/* 混合地面与反射物体 */
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
		
		/* 绘制立方体 */
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
		
		// 设置视口(OpenGL场景的大小)
		gl.glViewport(0, 0, width, height);

		// 设置投影矩阵为透视投影
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		// 重置投影矩阵（置为单位矩阵）
		gl.glLoadIdentity();
		
		//创建一个透视投影矩阵（设置视口大小）
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);
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
		
		// 打开光效
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		
		// 设置材质
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		
		// 设置光效
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		// 允许设置顶点数组
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// 设置清理深度缓存
		gl.glClearDepthf(1.0f);						
		// 剔除背面
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		// 为模板缓冲区指定用于清除的值..
		gl.glClearStencil(0);
	}

	public void setupCube(GL10 gl)
	{
		// 设置顶点数组
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	}

	// 绘制立方体
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
