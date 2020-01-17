package com.android.chapter13;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;

public class GLRender implements Renderer
{
	private Bitmap mBitmap;
	
	// 最大的粒子数量
	public final static int MAX_PARTICLES =1000;
	
	// 切换效果
	boolean rainbow=true;  
	// 随机种子
	Random random = new Random();
	// 减慢粒子速度
	float slowdown=0.5f;  
	
	// 速度
	float xspeed=1;        
	float yspeed=3; 
	// 缩放
	float zoom=-30.0f;    

	int loop;           
	int col=0;          
	int delay;         
	// 纹理
	int mTtexture[];     
	
	// 颜色数组
	static float colors[][]=
	{
	   {1.0f,  0.5f,  0.5f},
	   {1.0f,  0.75f, 0.5f},
	   {1.0f,  1.0f,  0.5f},
	   {0.75f, 1.0f,  0.5f},
	   {0.5f,  1.0f,  0.5f},
	   {0.5f,  1.0f,  0.75f},
	   {0.5f,  1.0f,  1.0f},
	   {0.5f,  0.75f, 1.0f},
	   {0.5f,  0.5f,  1.0f},
	   {0.75f, 0.5f,  1.0f},
	   {1.0f,  0.5f,  1.0f},
	   {1.0f,  0.5f,  0.75f}
	};
	
	// 粒子数组
	Particle particles[] = new Particle[MAX_PARTICLES];
	
	
	public GLRender(Context context)
	{
		// 装载图片
		mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.particle);
		mTtexture = new int[1];
	}
	
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
		
		draw(gl);
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
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 200);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// TODO Auto-generated method stub
		
		//告诉系统需要对透视进行修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//设置清理屏幕的颜色
		gl.glClearColor(0, 0, 0, 1);
		
		/* 不能使用深度测试 */
		//启用深度缓存
		//gl.glEnable(GL10.GL_DEPTH_TEST);
		
		// 打开混合 
		gl.glEnable(GL10.GL_BLEND);

		// 设置混色的方式
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

		// 对采样点进行修正（按质量最好的方式）
		gl.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_NICEST);		
		
		LoadTextures(gl);
		
		initData();
	}
	
	public void LoadTextures(GL10 gl)
	{
		// 允许2D纹理贴图
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		IntBuffer textureBuffer = IntBuffer.allocate(1);
		// 创建纹理
		mTtexture[0] = textureBuffer.get();

        // 绑定纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTtexture[0]);
		
        // 线性插值算法
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        
        // 装载纹理
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
	}

	
	private int rand()
	{
		return Math.abs(random.nextInt(1000));
	}
	
	private void initData()
	{
		for (loop = 0; loop < MAX_PARTICLES; loop++)
		{
			int color = loop*(12/MAX_PARTICLES);
			float xi, yi, zi;

			xi = (float) ((rand() % 50) - 26.0f) * 10.0f;
			yi = zi = (float) ((rand() % 50) - 25.0f) * 10.0f;

			resetParticle(loop, color, xi, yi, zi);
		}
	}
	
	private void resetParticle(int num, int color, float xDir, float yDir, float zDir)
	{
		Particle tmp = new Particle();
	    /* Make the particels active */
		tmp.active=true;
	    /* Give the particles life */
		tmp.life=1.0f;
	    /* Random Fade Speed */
		tmp.fade=(float)(rand()%100)/1000.0f+0.003f;
	    /* Select Red Rainbow Color */
		tmp.r=colors[color][0];
	    /* Select Green Rainbow Color */
		tmp.g=colors[color][1];
	    /* Select Blue Rainbow Color */
		tmp.b=colors[color][2];
	    /* Set the position on the X axis */
		tmp.x=0.0f;
	    /* Set the position on the Y axis */
		tmp.y=0.0f;
	    /* Set the position on the Z axis */
		tmp.z=0.0f;
	    /* Random Speed On X Axis */
		tmp.xi=xDir;
	    /* Random Speed On Y Axi */
		tmp.yi=yDir;
	    /* Random Speed On Z Axis */
		tmp.zi=zDir;
	    /* Set Horizontal Pull To Zero */
	    tmp.xg=0.0f;
	    /* Set Vertical Pull Downward */
	    tmp.yg=-0.5f;
	    /* Set Pull On Z Axis To Zero */
	    tmp.zg=0.0f;

	    particles[num] = tmp;
	    return;
	}
	
	
	private void draw(GL10 gl)
	{
		FloatBuffer vertices = FloatBuffer.wrap(new float[12]);
		FloatBuffer texcoords = FloatBuffer.wrap(new float[8]);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoords);

		gl.glLoadIdentity();

		// 修改每一颗粒子
		for (loop = 0; loop < MAX_PARTICLES; loop++)
		{
			// 粒子是否被激活
			if (particles[loop].active)
			{
				float x = particles[loop].x;
				float y = particles[loop].y;

				// z轴方向可能放大
				float z = particles[loop].z + zoom;

				// 设置颜色，根据生命值设置透明度
				gl.glColor4f(particles[loop].r, particles[loop].g, particles[loop].b, particles[loop].life);

				texcoords.clear();
				vertices.clear();
				/* Top Right */
				texcoords.put(1.0f);
				texcoords.put(1.0f);
				vertices.put(x + 0.5f);
				vertices.put(y + 0.5f);
				vertices.put(z);

				/* Top Left */
				texcoords.put(0.0f);
				texcoords.put(1.0f);
				vertices.put(x - 0.5f);
				vertices.put(y + 0.5f);
				vertices.put(z);

				/* Bottom Right */
				texcoords.put(1.0f);
				texcoords.put(0.0f);
				vertices.put(x + 0.5f);
				vertices.put(y - 0.5f);
				vertices.put(z);

				/* Bottom Left */
				texcoords.put(0.0f);
				texcoords.put(0.0f);
				vertices.put(x - 0.5f);
				vertices.put(y - 0.5f);
				vertices.put(z);

				// 绘制四边形
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

				// 通过x,y,z3个方向的速度分别改变粒子的位置
				particles[loop].x += particles[loop].xi / (slowdown * 100);
				particles[loop].y += particles[loop].yi / (slowdown * 100);
				particles[loop].z += particles[loop].zi / (slowdown * 100);

				// x,y,z方向上的重力加速度
				particles[loop].xi += particles[loop].xg;
				particles[loop].yi += particles[loop].yg;
				particles[loop].zi += particles[loop].zg;

				// 控制粒子的生命逐渐衰减
				particles[loop].life -= particles[loop].fade;

				// 如果这个粒子死亡之后就重新生成一个
				if (particles[loop].life < 0.0f)
				{
					float xi, yi, zi;

					xi = xspeed + (float) ((rand() % 60) - 32.0f);
					yi = yspeed + (float) ((rand() % 60) - 30.0f);
					zi = (float) ((rand() % 60) - 30.0f);
					resetParticle(loop, col, xi, yi, zi);
				}
			}
		}
		
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_8:
				if (particles[loop].yg < 1.5f)
				{
					particles[loop].yg += 0.1f;
				}
				break;
			case KeyEvent.KEYCODE_2:
				if (particles[loop].yg > -1.5f)
				{
					particles[loop].yg -= 0.1f;
				}
				break;
			case KeyEvent.KEYCODE_6:
				if ( particles[loop].xg<1.5f )
				{
					particles[loop].xg+=0.1f;
				}
				break;
			case KeyEvent.KEYCODE_4:
				if ( particles[loop].xg>-1.5f )
				{
					 particles[loop].xg-=0.1f;
				}
				break;
			case KeyEvent.KEYCODE_SPACE:// 按Tab键，使粒子回到原点
				particles[loop].x=0.0f;					
				particles[loop].y=0.0f;					
				particles[loop].z=0.0f;					
				particles[loop].xi=(float)((rand()%50)-26.0f)*10.0f;	// 随机生成速度
				particles[loop].yi=(float)((rand()%50)-25.0f)*10.0f;	
				particles[loop].zi=(float)((rand()%50)-25.0f)*10.0f;
				break;
			case KeyEvent.KEYCODE_D:
				if ( slowdown>1.0f )
				{
					slowdown-=0.1f;		// 按D号，加速粒子
				}
				break;
			case KeyEvent.KEYCODE_A:
				if ( slowdown<4.0f )
				{
					slowdown+=0.1f;	// 按a号，减速粒子
				}
				break;
			case KeyEvent.KEYCODE_W:
				zoom+=0.1f;		// 让粒子靠近视点
				break;
			case KeyEvent.KEYCODE_S:
				zoom-=0.1f;		// 让粒子远离视点
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				if ( yspeed<200 )
				{
					// 按上增加粒子Y轴正方向的速度
					yspeed+=1.0f;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if ( yspeed>-200 )
				{
					// 按下减少粒子Y轴正方向的速度
					yspeed-=1.0f;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if ( xspeed<200 )
				{
					// 按右增加粒子X轴正方向的速度
					xspeed+=1.0f;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if ( xspeed>-200 )
				{
					// 按左减少粒子X轴正方向的速度
					xspeed-=1.0f;
				}
				break;
			
		}
		return false;
	}
}