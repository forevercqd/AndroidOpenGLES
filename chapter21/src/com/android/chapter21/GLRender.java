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
		
		// 首先清理屏幕
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
		
		// 设置模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//重置矩阵
		gl.glLoadIdentity();
		
		// 视点变换
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);
		
		//...
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
		
		//创建一个透视投影矩阵（设置视口大小）
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	}

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
	
	/* 清空缓存 */
	private void ClearBuffer(GL10 gl)
	{
		// 设置清空颜色缓存的值
		gl.glClearColor(0.0f,0.0f,0.0f,0.0f);
		// 设置清空深度缓存的值
		gl.glClearDepthf(1.0f);
		gl.glClearDepthx(1);
		// 设置清空模版缓存的值
		gl.glClearStencil(0);
		
		// 清除颜色缓存、深度缓存、模版缓存
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_STENCIL_BUFFER_BIT);		
		
	}
	
	/* 读取颜色缓存 */
	private void ReadColorBuffer(GL10 gl)
	{
		//gl.glReadPixels(10, 10, 50, 50, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);
		/**
		 * x:x
		 * y:y
		 * width：width
		 * height：height
		 * format：颜色格式
		 * type:数据类型
		 * pixels：保存读取的颜色缓存到缓冲区
		 *
		 */
		
	}

	/* 屏蔽缓存 */
	private void MaskBuffer(GL10 gl)
	{
		// 允许写入相应的颜色分量
		gl.glColorMask(true, true, true, true);
		// 禁用深度缓存
		gl.glDepthMask(false);
		// 掩码中1对应的数据位被写入颜色缓存
		gl.glStencilMask(1);
		
		
	}
	
	/* 剪裁测试 */
	private void ScissorTest(GL10 gl)
	{
		// 打开剪裁测试
		gl.glEnable(GL10.GL_SCISSOR_TEST);
		// 设置剪裁区域
		gl.glScissor(10, 10, 50, 50);
		
		// 关闭剪裁测试
		gl.glDisable(GL10.GL_SCISSOR_TEST);
	}
	
	// Alpha测试
	private void AlphaTest(GL10 gl)
	{
		// 打开Alpha测试
		gl.glEnable(GL10.GL_ALPHA_TEST);
		// 设置alpha测试的参考值
		gl.glAlphaFunc(GL10.GL_NOTEQUAL, 0.0f);
		
		// 关闭Alpha测试
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}
	
	// 模版测试
	private void StencilTest(GL10 gl)
	{
		gl.glEnable(GL10.GL_STENCIL_TEST);
		// 设置测试结果处理
		gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
		// 设置蒙板测试总是通过，参考值设为1，掩码值也设为0xffffffff
		gl.glStencilFunc(GL10.GL_ALWAYS, 1, 0xffffffff);
		
		// 关闭模版测试
		gl.glDisable(GL10.GL_STENCIL_TEST);
	}
	
	// 深度测试
	private void DepthTest(GL10 gl)
	{
		// 启用深度测试
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// 设置深度测试的类型
		gl.glDepthFunc(GL10.GL_LESS);
		
		// 禁止深度测试
		gl.glDisable(GL10.GL_DEPTH_TEST);
	}
	
}
