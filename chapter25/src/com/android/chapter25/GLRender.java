package com.android.chapter25;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;

public class GLRender implements Renderer
{
	private Context	mContext;

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

	public void onDrawFrame(GL10 gl)
	{
		Draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		windowWidth = width;
		windowHeight = height;
		height = (height == 0) ? 1 : height;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU.gluPerspective(gl, 45, (float) width / height, 1, 1000);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		init(gl);
	}

	/*********************************************************************************/
	// 迷宫大小
	private static final int	MAZE_WIDTH		= 128;
	private static final int	MAZE_HEIGHT		= 128;

	//mx和my纪录了当前所在迷宫中的房间。每个房间都被墙隔开(因此房间都是2个单元大小的部分）。
	//with和height是用来建立纹理需要的。它也是迷宫的宽和高。让迷宫和贴图的大小一致的原因是使迷宫中的象素和纹理中的象素一一对应。
	/**
	 * 如果你的显卡能支持处理大型贴图。可以试着以2次幂增加这个值(256, 512, 1023）。确保这个值不至于太大。
	 * 如果这个主窗口的宽度有1024个象素，并且每个视口的大小都是主窗口的一半，相应的你应该设置你的贴图宽度也是窗口宽度的一半。
	 * 如果你使贴图宽度为1024象素，但你的视口大小只有512,空间不足于容纳贴图中所有得象素，这样每两个象素就会重叠在一起。
	 * 贴图的高度也作同样处理:高度是窗口高度的1/2. 当然你还必须四舍五入到2的幂。
	 */
	// 循环变量--用于迷宫绘制
	private int					mx, my;
	/* r[4]保存了4个随机的红色分量值，
	 * g[4]保存了4个随机的绿色分量值，
	 * b[4]保存了4个随机的兰色分量值。
	 * 这些值赋给各个视口不同的颜色。
	 * 第一个视口颜色为r[0],g[0],b[0]。请注意每一个颜色都是一个字节的值，而不是常用的浮点值。
	 * 我这里用字节是因为产生0-255的随机值比产生0.0f-1.0f的浮点值更容易。
	 * tex_data指向我们的贴图数据。
	 */
	// 保存纹理数据
	// 分配了足够的内存来保存我们的纹理(width*height*3).
	private ByteBuffer			tex_data		= BufferUtil
														.newByteBuffer(MAZE_WIDTH * MAZE_HEIGHT * 3);
	// 随机的颜色
	private byte[]				r				= new byte[4];
	private byte[]				g				= new byte[4];
	private byte[]				b				= new byte[4];
	// 球体
	private GlSphere			quadricGlSphere;
	// 圆柱体
	private GlCylinder			quadricGlCylinder;
	// 旋转物体(xrot,yrot和zrot是旋转3d物体用到的变量。)
	private float				xrot, yrot, zrot;

	private long				previousTime	= System.currentTimeMillis();
	// 迷宫是否被建完--是否需要重置
	private boolean				resetMaze		= false;

	int							windowWidth		= 0;
	int							windowHeight	= 0;

	// 重置
	public void resetMaze()
	{
		resetMaze = true;
	}

	// 更新纹理数据
	private void updateTex(int dmx, int dmy)
	{
		/*
		 * 设置纹理中位置dmx,dmy的颜色值为纯白色。
		 * tex_data是指向我们的纹理数据的。
		 * 每一个象素都由3字节组成(1字节红色分量，1字节绿色分量，一字节兰色分量). 
		 * 红色分量的偏移为0，我们要修改的象素的在纹理数据中的偏移为dmx(象素的x坐标）加上dmy(象素y坐标)与贴图宽度的乘积,最后的结果乘3(3字节每象素)。
		 * 下面第一行代码设置red(0)颜色分量为255, 
		 * 第二行设置green(1)颜色分量为255,
		 * 最后一行设置blue(2)颜色分量为255,最后的结果为在dmx,dmy处的象素颜色为白色。
		 */
		// 设置颜色为白色
		tex_data.put(((dmx + (MAZE_WIDTH * dmy)) * 3), (byte) 255);
		tex_data.put(1 + ((dmx + (MAZE_WIDTH * dmy)) * 3), (byte) 255);
		tex_data.put(2 + ((dmx + (MAZE_WIDTH * dmy)) * 3), (byte) 255);
	}

	/**
	 * 重置有相当多的工作量。它清空纹理，给每一个视口设置随机颜色，删除迷宫中的墙并为迷宫的生成设置新的随机起点。
	 * 第一行代码清空tex_data指向的贴图数据。
	 * 我们需要清空width(贴图宽）*height(贴图高)*3(红，绿，兰)。 
     * 清空内存空间就是设置所有的字节为0。如果3个颜色分量都清零，那么整个贴图就完全变黑了！
	 */
	private void reset()
	{
		// 清空纹理数据
		tex_data.clear();
		while (tex_data.hasRemaining())
		{
			tex_data.put((byte) 0);
		}
		tex_data.flip();

		/**
		 * 我们有四个视口，因此我们需要从0-3的循环来处理。
		 * 我们给每一个颜色(red,green,blue)从128-255中间的随机值。要加128的目的是需要更亮的颜色。
		 * 最小值为0，最大值为255,而128则表示大约有50%的亮度
		 */
		// 循环随机生成颜色
		for (int loop = 0; loop < 4; loop++)
		{
			r[loop] = (byte) ((int) (Math.random() * Integer.MAX_VALUE) % 128);
			g[loop] = (byte) ((int) (Math.random() * Integer.MAX_VALUE) % 128);
			b[loop] = (byte) ((int) (Math.random() * Integer.MAX_VALUE) % 128);
		}

		setRandomMazePosition();
	}

	/**
	 * 我们设置一个随机的起点。我们的起点必须是一个房间。
	 * 在纹理中每两个象素就是一个房间。
	 * 为确保起点是房间而不是墙，我们在0至贴图宽度一半的范围内挑选一个数，并和2相乘。
	 * 通过这种方法我们只能得到如0,2,6,8之类的数，也就是说我们总是得到一个随机的房间，
	 * 决不会着陆到一堵墙上如1,3,5,7,9等等。
	 */
	// 设置一个随机的起点
	// 绘制迷宫的起点
	private void setRandomMazePosition()
	{
		mx = (int) Math.round(Math.random() * ((MAZE_WIDTH - 1) / 2)) * 2;
		my = (int) Math.round(Math.random() * ((MAZE_HEIGHT - 1) / 2)) * 2;
	}

	//初始化
	public void init(GL10 gl)
	{
		// 重置纹理贴图
		reset();

		/**
		 *  Reset会清空贴图，设置所需颜色，并为迷宫选取随机起点。
		 *  一旦所有的东西都设置好了。我们建立我们的初始纹理。
		 *  前两个纹理参数将纹理坐标截断在 [0,1]范围内，
		 *  为了看到CLAMP参数的重要性，可以移掉这两行代码看看。
		 *  如果没有Clamping,你会注意到在纹理的顶部和右边的细小线条。
		 *  这些线条的出现是因为线性过滤想使整个纹理平滑，包括纹理边界。
		 *  如果一个靠近边界的点被画了，在纹理的对边上就会出现一条线。
		 *  我们打算用线性过滤来使纹理变的更平滑一点。 
		 *  用什么类型的过滤是由你来决定的。如果它使程序跑起来很慢，那就换成过滤类型为GL_NEAREST
		 *  最后，我们利用tex_data数据（并没有利用alpha通道）建立了一个二维的RGB纹理。
		 */
		// 设置纹理参数
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D,
				0,
				GL10.GL_RGB,
				MAZE_WIDTH,
				MAZE_HEIGHT,
				0,
				GL10.GL_RGB,
				GL10.GL_UNSIGNED_BYTE,
				tex_data);

		gl.glClearColor(0.0f, 0.0f, 0.5f, 0.0f);
		gl.glClearDepthf(1.0f);

		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glEnable(GL10.GL_COLOR_MATERIAL);

		gl.glEnable(GL10.GL_TEXTURE_2D);

		// 构建二次几何体
		quadricGlSphere = new GlSphere(1.3f, 32, 12, true, true);
		quadricGlCylinder = new GlCylinder(0.7f, 0.7f, 3.0f, 32, 4, true, true);
		/**
		 * Light0被激活，但是如果我们不激活光照，它不会起任何作用。
		 * Light0是预定义的灯光，方向指向屏幕内。你也可以自己定义
		 */
		gl.glEnable(GL10.GL_LIGHT0);
	}

	/**
	 * 我们需要设置一个变量dir, 用它来表示记录随机的向上，向右，向下或向左值。
	 */
	// 更新各个参数
	private void update(float milliseconds)
	{
		// 保存当前的方向
		int dir;
		/**
		 * xrot,yrot和zrot通过和一些小浮点数相乘而随着时间的消逝而增加。
		 * 这样我们可以让物体绕x轴，y轴和z轴旋转。每个变量都增加不同的值使旋转好看一点
		 */
		// 旋转变量
		xrot += milliseconds * 0.02f;
		yrot += milliseconds * 0.03f;
		zrot += milliseconds * 0.015f;

		/**
		 * 检查当前房间的右边房间是否被访问过或者是否当前位置的右边是迷宫的右边界（当前房间右边的房间就不存在），
		 * 同样检查左边的房间是否访问过或者是否达到左边界。其它方向也作如此检查。
		 * 如果房间颜色的红色分量的值为255,就表示这个房间已经被访问过了(因为它已经被函数UpdateTex更新过)。
		 * 如果mx(当前x坐标）小于2, 就表示我们已经到了迷宫最左边不能再左了。
		 * 如果往四个方向都不能移动了或以已经到了边界，就给mx和my一个随机值，然后检查这个值对应点是否被访问，
		 * 如果没有，我们就重新寻找一个新的随机变量，直到该变量对应的单元早已经被访问。
		 * 因为需要从旧的路径中分叉出新的路径，所以我们必须保持搜素知道发觉有一老的路径可以从那里开始新的路径。
		 * 为了使代码尽量简短，我没有打算去检查mx-2是否小于0。
		 * 如果你想有100%的错误检测，你可以修改这段代码阻止访问不属于当前贴图的内存。
		 */
		// 检测是否走过这里
		if ((mx > (MAZE_WIDTH - 4) || (tex_data.get((((mx + 2) + (MAZE_WIDTH * my)) * 3)) != 0)) && (mx < 2 || (tex_data
				.get((((mx - 2) + (MAZE_WIDTH * my)) * 3)) != 0)) && (my > (MAZE_HEIGHT - 4) || (tex_data
				.get(((mx + (MAZE_WIDTH * (my + 2))) * 3)) != 0)) && (my < 2 || (tex_data
				.get(((mx + (MAZE_WIDTH * (my - 2))) * 3)) != 0)))
		{
			do
			{
				setRandomMazePosition();
			} while (tex_data.get(((mx + (MAZE_WIDTH * my)) * 3)) == 0);
		}

		/**
		 * 赋给dir变量0-3之间的随机值，这个值告诉我们该往右，往上，往左还是往下画迷宫。
		 * 在得到随机的方向之后，我们检查dir的值是否为0(往右移)，
		 * 如果是并且我们不在迷宫的右边界，然后检查当前房间的右边房间，
		 * 如果没被访问，我们就调用UpdateTex(mx+1,my)在两个房间之间建立一堵墙，
		 * 然后mx增加2移到新的房间.
		 */
		// 随机一个走向
		dir = (int) Math.round(Math.random() * 3f);

		// 向右走，更新数据
		if ((dir == 0) && (mx <= (MAZE_WIDTH - 4)))
		{
			if (tex_data.get((((mx + 2) + (MAZE_WIDTH * my)) * 3)) == 0)
			{
				updateTex(mx + 1, my);
				mx += 2;
			}
		}
		/**
		 * 如果dir的值为1(往下)，并且我们不在迷宫底部，我们检查当前房间的下面房间是否被访问过。
		 * 如果没被访问过，我们就在两个房间(当前房间和当前房间下面的房间)建立一堵墙。
		 * 然后my增加2移到新的房间.
		 */
		//  向下走，更新数据
		if ((dir == 1) && (my <= (MAZE_HEIGHT - 4)))
		{
			if (tex_data.get(((mx + (MAZE_WIDTH * (my + 2))) * 3)) == 0)
			{
				updateTex(mx, my + 1);
				my += 2;
			}
		}
		/**
		 * 如果dir的值为2(向左)并且我们不在左边界，我们就检查左边的房间是否被访问，
		 * 如果没被访问，我们也在两个房间(当前房间和左边的房间)之间建立一堵墙，
		 * 然后mx减2移到新的房间.
		 */
		// 向左走，更新数据
		if ((dir == 2) && (mx >= 2))
		{
			if (tex_data.get((((mx - 2) + (MAZE_WIDTH * my)) * 3)) == 0)
			{
				updateTex(mx - 1, my);
				mx -= 2;
			}
		}
		/**
		 * 如果dir的值为3并且不在迷宫的最顶部，
		 * 当前房间和当前房间上面个房间之间建立一堵墙，
		 * 然后my增加2移到新的房间。
		 */
		// 向上走，更新数据
		if ((dir == 3) && (my >= 2))
		{
			if (tex_data.get(((mx + (MAZE_WIDTH * (my - 2))) * 3)) == 0)
			{
				updateTex(mx, my - 1);
				my -= 2;
			}
		}
		/**
		 * 移到新的房间后，我们必须标志当前房间为正在访问状态。
		 * 我们通过调用以当前位置mx, my为参数的UpdateTex()函数来达到这个目的。
		 */
		// 更新纹理
		updateTex(mx, my);
	}

	private void Draw(GL10 gl)
	{
		if (resetMaze)
		{
			reset();
			resetMaze = false;
		}

		long currentTime = System.currentTimeMillis();
		update(currentTime - previousTime);
		previousTime = currentTime;

		/**
		 * 在每一帧都需要更新纹理并且要在映射纹理之前更新。更新纹理最快的方法是用命令glTexSubImage2D(). 
		 * 它能把内存中的纹理的全部或部分和屏幕中的物体建立映射。
		 * 纹理细节级别为0，没有x方向(0)或y方向(0)的偏移，
		 * 我们需要利用整张纹理的每一部分，图像为GL_RGB类型，对应的数据类型为GL_UNSIGNED_BYTE. 
		 * tex_data是我们需要映射的具体数据。
		 * 这是一个非非常快的不用重建纹理而更新纹理的方法。
		 * 同样需要注意的是这个命令不会为你建立一个纹理。你必须在更新纹理前把纹理建立好。
		 */
		// 设置更新的纹理
		gl.glTexSubImage2D(GL10.GL_TEXTURE_2D,
				0,
				0,
				0,
				MAZE_WIDTH,
				MAZE_HEIGHT,
				GL10.GL_RGB,
				GL10.GL_UNSIGNED_BYTE,
				tex_data);

		/**
		 * 这行代码非常重要，它将清空整个屏幕。
		 * 一次性清空整个屏幕，然后在画每一个视口前清空它们的深度存非常重要。
		 */
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		/**
		 * 我们要画四个视口，所以建立了一个0到3的循环。
		 * 首先要做的事是设置用glColor3ub(r,g,b)设置当前视口的颜色。
		 * 它跟glColor3f(r,g,b)几乎一样但是用无符号字节代替浮点数为参数。
		 * 记住早些时候我说过参省一个0-255的随机颜色值会更容易。好在已经有了该命令设置正确颜色所需要的值。
		 * glColor3f(0.5f,0.5f,0.5f)是指颜色中红，绿，蓝具有50%的亮度值。
		 * glColor3ub(127,127,127)同样也表示同样的意思。
		 * 如果loop的值为0,我们将选择r[0],b[0],b[[0]，如果loop指为1, 
		 * 我们选用r[1],g[1],b[1]. 这样，每个场景都有自个的随机颜色。
		 */
		// 循环绘制4个视口
		for (int loop = 0; loop < 4; loop++)
		{
			gl.glColor4f(Math.abs(((float) r[loop]) / 255.0f),
					Math.abs(((float) g[loop]) / 255.0f),
					Math.abs(((float) b[loop]) / 255.0f),
					1.0f);
			// 绘制左上角的视口
			if (loop == 0)
			{
				// 设置视口区域
				gl.glViewport(0, windowHeight / 2, windowWidth / 2, windowHeight / 2);

				gl.glMatrixMode(GL10.GL_PROJECTION);

				gl.glLoadIdentity();

				GLU.gluOrtho2D(gl, 0, windowWidth / 2, windowHeight / 2, 0);

			}
			// 绘制右上角视口
			if (loop == 1)
			{
				gl.glViewport(windowWidth / 2, windowHeight / 2, windowWidth / 2, windowHeight / 2);
				gl.glMatrixMode(GL10.GL_PROJECTION);
				gl.glLoadIdentity();
				GLU.gluPerspective(gl,
						45.0f,
						(float) (MAZE_WIDTH) / (float) (MAZE_HEIGHT),
						0.1f,
						500.0f);
			}
			// 绘制右下角视口
			if (loop == 2)
			{

				gl.glViewport(windowWidth / 2, 0, windowWidth / 2, windowHeight / 2);
				gl.glMatrixMode(GL10.GL_PROJECTION);
				gl.glLoadIdentity();
				GLU.gluPerspective(gl,
						45.0f,
						(float) (MAZE_WIDTH) / (float) (MAZE_HEIGHT),
						0.1f,
						500.0f);
			}
			// 绘制左下角视口
			if (loop == 3)
			{
				gl.glViewport(0, 0, windowWidth / 2, windowHeight / 2);
				gl.glMatrixMode(GL10.GL_PROJECTION);
				gl.glLoadIdentity();
				GLU.gluPerspective(gl,
						45.0f,
						(float) (MAZE_WIDTH) / (float) (MAZE_HEIGHT),
						0.1f,
						500.0f);
			}

			/**
			 * 选择模型视图矩阵为当前矩阵真，并重置它。然后清空深度缓存。
			 * 我们在每个视口画之前清空深度缓存。注意到我们没有清除屏幕颜色，只是深度缓存！
			 * 如果你没有清除深度缓存，你将看到物体的部分消失了，等等，很明显不美观！
			 */
			gl.glMatrixMode(GL10.GL_MODELVIEW);

			gl.glLoadIdentity();

			gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
			// 绘制左上角的视图
			if (loop == 0)
			{
				float vertices[][] = new float[4][2];
				float texcoords[][] = new float[4][2];

				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

				texcoords[0][0] = 1.0f;
				texcoords[0][1] = 0.0f;
				vertices[0][0] = windowWidth / 2;
				vertices[0][1] = 0.0f;
				texcoords[1][0] = 0.0f;
				texcoords[1][1] = 0.0f;
				vertices[1][0] = 0.0f;
				vertices[1][1] = 0.0f;
				texcoords[2][0] = 1.0f;
				texcoords[2][1] = 1.0f;
				vertices[2][0] = windowWidth / 2;
				vertices[2][1] = windowHeight / 2;
				texcoords[3][0] = 0.0f;
				texcoords[3][1] = 1.0f;
				vertices[3][0] = 0.0f;
				vertices[3][1] = windowHeight / 2;

				gl.glVertexPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(vertices));
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));

				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			}
			// 绘制右上角的视图
			if (loop == 1)
			{
				gl.glTranslatef(0.0f, 0.0f, -4.0f);
				gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
				gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
				gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);

				gl.glEnable(GL10.GL_LIGHTING);
				quadricGlSphere.draw(gl);
				gl.glDisable(GL10.GL_LIGHTING);
			}
			// 绘制右下角的视图
			if (loop == 2)
			{
				float vertices[][] = new float[4][3];
				float texcoords[][] = new float[4][2];

				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

				gl.glTranslatef(0.0f, 0.0f, -1.7f);
				gl.glRotatef(-45.0f, 1.0f, 0.0f, 0.0f);
				gl.glRotatef(zrot / 1.5f, 0.0f, 0.0f, 1.0f);

				texcoords[0][0] = 1.0f;
				texcoords[0][1] = 1.0f;
				texcoords[1][0] = 0.0f;
				texcoords[1][1] = 1.0f;
				texcoords[2][0] = 1.0f;
				texcoords[2][1] = 0.0f;
				texcoords[3][0] = 0.0f;
				texcoords[3][1] = 0.0f;
				vertices[0][0] = 0.5f;
				vertices[0][1] = 0.5f;
				vertices[0][2] = 0.0f;
				vertices[1][0] = -0.5f;
				vertices[1][1] = 0.5f;
				vertices[1][2] = 0.0f;
				vertices[2][0] = 0.5f;
				vertices[2][1] = -0.5f;
				vertices[2][2] = 0.0f;
				vertices[3][0] = -0.5f;
				vertices[3][1] = -0.5f;
				vertices[3][2] = 0.0f;

				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(vertices));
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));

				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			}
			// 绘制左下角的视图
			if (loop == 3)
			{
				gl.glTranslatef(0.0f, 0.0f, -4.0f);
				gl.glRotatef(-xrot / 2, 1.0f, 0.0f, 0.0f);
				gl.glRotatef(-yrot / 2, 0.0f, 1.0f, 0.0f);
				gl.glRotatef(-zrot / 2, 0.0f, 0.0f, 1.0f);

				gl.glEnable(GL10.GL_LIGHTING);

				gl.glTranslatef(0.0f, 0.0f, -2.0f);

				quadricGlCylinder.draw(gl);

				gl.glDisable(GL10.GL_LIGHTING);
			}
		}
	}
	
	// 数组转换
	protected FloatBuffer makeFloatBuffer(float[][] arr)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * arr[0].length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			fb.put(arr[i]);
		}
		fb.position(0);
		return fb;
	}
	
	// 按键处理
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
		{
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
		{
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
		{
		}
		else if (keyCode == KeyEvent.KEYCODE_SPACE)
		{
			resetMaze();
		}
		return false;
	}

}
