package com.android.chapter14;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;

public class GLRender implements Renderer
{
	private Context mContext;
	
	// 随机种子
	private Random random = new Random();
	
	// 顶点数组、颜色数据
	private FloatBuffer vertices = FloatBuffer.wrap(new float[512*3*3]);
	private FloatBuffer colors = FloatBuffer.wrap(new float[512*3*4]);
	
	// 旋转(x,y,z)
	private float xrot, yrot, zrot;         
	// 旋转的速度
	private float xspeed, yspeed, zspeed;
	// x,y,z坐标值
	private float cx, cy, cz=-5;          
	// 是否变形的标志
	private boolean morph=false;
	// 计数器和最大步数
	private int step=0, steps=200;
	// 最大的顶点数量
	private int maxver; 
	
	// 要被变形的物体
	OBJECT morph1 = new OBJECT();
	OBJECT morph2= new OBJECT();
	OBJECT morph3= new OBJECT();
	OBJECT morph4= new OBJECT();
	// 辅助对象、源对象、目标对象
	OBJECT helper= new OBJECT(), sour, dest; 
	
	public GLRender(Context context)
	{
		mContext = context;
	}
	
	private InputStream getFile(String name)
	{
		AssetManager am = mContext.getResources().getAssets();
		try
		{
			return am.open(name);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
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
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000);
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
		
		// 设置混合的方式为半透明
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		
		// 设置清理深度缓存的深度
		gl.glClearDepthf(1.0f);

		// 设置深度测试的类型
		gl.glDepthFunc(GL10.GL_LESS);
		
		// 启用平滑阴影
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		
		initData();
	}
	
	// 初始化数据
	private void initData()
	{
		// 设置最大顶点数为0
		maxver = 0;
		// 装载模型
		objload("sphere.txt", morph1);
		objload("torus.txt", morph2);
		objload("tube.txt", morph3);

		for (int i = 0; i < 486; i++)
		{
			// 随机创建一个相同定点数的物体顶点作为变形时使用
			float xx = ((float) (rand() % 14000) / 1000) - 7;
			float yy = ((float) (rand() % 14000) / 1000) - 7;
			float zz = ((float) (rand() % 14000) / 1000) - 7;

			morph4.points.add(new VERTEX(xx, yy, zz));
		}

		// 装载辅助模型
		objload("sphere.txt", helper);
		sour = dest = morph1;
	}

	// 取得随机数
	public int rand()
	{
		return Math.abs(random.nextInt());
	}
	
	// 读取一行字符串
	public String readstr(BufferedReader br)
	{
		String str = "";
		try
		{
			do
			{
				str = br.readLine();
			}
			while ((str.charAt(0) == '/') || (str.charAt(0) == '\n'));
		}
		catch (Exception e){}
		return str;
	}
	
	// 从文件中装载物体(文件名，物体对象)
	void objload(String name, OBJECT k)
	{
		// 顶点数
	    int   ver = 0;           
	    String  oneline;
	    int   i;
	    
		BufferedReader br = new BufferedReader(new InputStreamReader(getFile(name)));	
		//读出顶点数
		oneline = readstr(br);
		ver=Integer.valueOf(oneline).intValue();
		
		k.verts = ver;
		
		// 读取每一个顶点的数据
		for (i=0; i<ver; i++)
		{
			oneline=readstr(br);
			String part[] = oneline.trim().split("\\s+");
			float x = Float.valueOf(part[0]);
			float y = Float.valueOf(part[1]);
			float z = Float.valueOf(part[2]);
			VERTEX vertex = new VERTEX(x, y, z);
			k.points.add(vertex);
		}
		
		// 如果顶点数大于最大顶点数
	    if (ver>maxver)
	    {
	        maxver=ver;
	    }
	}
	
	// 变形时，点的运动
	VERTEX calculate(int i)
	{
	    VERTEX a = new VERTEX(0,0,0);

	    a.x=(sour.points.get(i).x-dest.points.get(i).x)/steps;
	    a.y=(sour.points.get(i).y-dest.points.get(i).y)/steps;
	    a.z=(sour.points.get(i).z-dest.points.get(i).z)/steps;

	    return a;
	}
	
	
	private void draw(GL10 gl)
	{
		int i;
		float tx, ty, tz;
		VERTEX q = new VERTEX(0,0,0);
		
	    // 平移
	    gl.glTranslatef(cx, cy, cz);
	    // 旋转
	    gl.glRotatef(xrot, 1, 0, 0);
	    gl.glRotatef(yrot, 0, 1, 0);
	    gl.glRotatef(zrot, 0, 0, 1);

	    // 改变旋转的角度
	    xrot+=xspeed; yrot+=yspeed; zrot+=zspeed;

	    // 允许设置顶点数组和纹理坐标数组
	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	    
	    // 设置顶点数组和颜色数组
	    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
	    gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);
	    
	    colors.clear();
	    vertices.clear();

	    for(i=0; i<morph1.verts; i++)
	    {
	        if (morph)
	        {
	            q=calculate(i);
	        }
	        else
	        {
	            q.x=q.y=q.z=0;
	        }
	        
	        // 移动一步
	        helper.points.get(i).x-=q.x;
	        helper.points.get(i).y-=q.y;
	        helper.points.get(i).z-=q.z;
	        
	        
	        tx=helper.points.get(i).x;
	        ty=helper.points.get(i).y;
	        tz=helper.points.get(i).z;
	        
	        // 颜色数据
	        colors.put(0.0f); colors.put(1.0f); colors.put(1.0f); colors.put(1.0f);
	        // 顶点数据
	        vertices.put(tx); vertices.put(ty); vertices.put(tz);

	        colors.put(0.0f); colors.put(0.5f); colors.put(1.0f); colors.put(1.0f);
	        tx-=2*q.x; ty-=2*q.y; ty-=2*q.y;
	        vertices.put(tx); vertices.put(ty); vertices.put(tz);

	        colors.put(1.0f); colors.put(0.0f); colors.put(0.0f); colors.put(1.0f);
	        tx-=2*q.x; ty-=2*q.y; ty-=2*q.y;
	        vertices.put(tx); vertices.put(ty); vertices.put(tz);	        
	    }
	    
	    // 绘制模型
	    gl.glDrawArrays(GL10.GL_POINTS, 0, morph1.verts*3);

	    // 设置顶点数组和颜色数组
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	    
	    if (morph && step<=steps)
	    {
	        step++;
	    }
	    else
	    {
	        morph=false;
	        sour=dest;
	        step=0;
	    }
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_1:
				if (!morph)
				{
					morph = true;
					dest = morph1;
				}
				break;
			case KeyEvent.KEYCODE_2:
				if (!morph)
				{
					morph = true;
					dest = morph2;
				}
				break;
			case KeyEvent.KEYCODE_3:
				if (!morph)
				{
					morph = true;
					dest = morph3;
				}
				break;
			case KeyEvent.KEYCODE_4:
				if (!morph)
				{
					morph = true;
					dest = morph4;
				}
				break;
			case KeyEvent.KEYCODE_N:
				zspeed+=0.1f;//增加绕z轴旋转的速度
				break;
			case KeyEvent.KEYCODE_M:
				zspeed-=0.1f;//减少绕z轴旋转的速度
				break;
			case KeyEvent.KEYCODE_Q:
				cz-=0.1f;// 向屏幕里移动
				break;
			case KeyEvent.KEYCODE_Z:
				cz+=0.1f;// 向屏幕外移动
				break;
			case KeyEvent.KEYCODE_W:
				cy+=0.1f;// 向上移动
				break;
			case KeyEvent.KEYCODE_S:
				cy-=0.1f;// 向下移动
				break;
			case KeyEvent.KEYCODE_D:
				cx+=0.1f;// 向右移动
				break;
			case KeyEvent.KEYCODE_A:
				cx-=0.1f;// 向左移动
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				xspeed-=0.1f;// 减少绕x轴旋转的速度
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				xspeed+=0.1f;// 增加绕x轴旋转的速度
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				yspeed-=0.1f;// 减少沿y轴旋转的速度
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				break;
		}
		return false;
	}
	
}

class VERTEX
{
	// 顶点(x,y,z)
	float x, y, z;        
	public VERTEX(float x,float y,float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
}

class OBJECT
{
	// 顶点数目
    int     verts;       
    // 顶点数组
    List<VERTEX>	points	= new ArrayList<VERTEX>();
}

