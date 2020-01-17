package com.android.chapter3;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GLRender implements Renderer
{
	int one = 0x10000;
	
	// 用于控制三角形和正方形旋转的角度
	float rotateTri, rotateQuad;
	
	//三角形三个顶点
	 private IntBuffer triggerBuffer = IntBuffer.wrap(new int[]{
				   0, one,0,	//上顶点
				-one,-one,0,    //左下点
				 one,-one,0,}); //右下点
	 
	 //正方形的4个顶点
	 private IntBuffer quaterBuffer = IntBuffer.wrap(new int[]{
				 one, one,0,
				-one, one,0,
				 one,-one,0,
				-one,-one,0});
	 
	 //三角形的顶点颜色值(r,g,b,a)
	 private IntBuffer colorBuffer = IntBuffer.wrap(new int[]{
			 	one,  0,  0,one,
			 	  0,one,  0,one,
			 	  0,  0,one,one,
	 });
	
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
		
		// 设置模型位置
		gl.glTranslatef(-3.0f, 0.0f, -4.0f);
		
		//设置旋转(y轴)
		gl.glRotatef(rotateTri, 0.0f, 1.0f, 0.0f);
		
		// 允许设置顶点
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		// 允许设置颜色数组
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		//设置颜色数组
		gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);
		
		// 设置三角形的顶点数据
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, triggerBuffer);
		
		//放大三角形
		gl.glScalef(2.0f, 2.0f, 2.0f);
		
		//绘制三角形
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		
		//关闭颜色数组的设置
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
		/*****正方形*****/
		
		//设置正方形的颜色
		gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f);
		
		// 重置当前的模型观察矩阵
	    gl.glLoadIdentity();
	    
	    // 设置模型位置
	    gl.glTranslatef(1.0f, 0.0f, -4.0f);
	    
	    //设置旋转(x轴)
	    gl.glRotatef(rotateQuad, 1.0f, 0.0f, 0.0f);
	    
	    //设置正方形顶点数组
	    gl.glVertexPointer(3, GL10.GL_FIXED, 0, quaterBuffer);
	    
	    //绘制正方形
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	    
	    /* 绘制出来则是线框 */
	    //gl.glDrawArrays(GL10.GL_LINES, 0, 4);

	    // 取消顶点设置
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	    
	    //改变旋转的角度
	    rotateTri += 0.5f;
	    rotateQuad -= 0.5f;
		
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
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		
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
	}

}

