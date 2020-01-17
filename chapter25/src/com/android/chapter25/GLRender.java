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
	// �Թ���С
	private static final int	MAZE_WIDTH		= 128;
	private static final int	MAZE_HEIGHT		= 128;

	//mx��my��¼�˵�ǰ�����Թ��еķ��䡣ÿ�����䶼��ǽ����(��˷��䶼��2����Ԫ��С�Ĳ��֣���
	//with��height����������������Ҫ�ġ���Ҳ���Թ��Ŀ�͸ߡ����Թ�����ͼ�Ĵ�Сһ�µ�ԭ����ʹ�Թ��е����غ������е�����һһ��Ӧ��
	/**
	 * �������Կ���֧�ִ��������ͼ������������2�����������ֵ(256, 512, 1023����ȷ�����ֵ������̫��
	 * �����������ڵĿ����1024�����أ�����ÿ���ӿڵĴ�С���������ڵ�һ�룬��Ӧ����Ӧ�����������ͼ���Ҳ�Ǵ��ڿ�ȵ�һ�롣
	 * �����ʹ��ͼ���Ϊ1024���أ�������ӿڴ�Сֻ��512,�ռ䲻����������ͼ�����е����أ�����ÿ�������ؾͻ��ص���һ��
	 * ��ͼ�ĸ߶�Ҳ��ͬ������:�߶��Ǵ��ڸ߶ȵ�1/2. ��Ȼ�㻹�����������뵽2���ݡ�
	 */
	// ѭ������--�����Թ�����
	private int					mx, my;
	/* r[4]������4������ĺ�ɫ����ֵ��
	 * g[4]������4���������ɫ����ֵ��
	 * b[4]������4���������ɫ����ֵ��
	 * ��Щֵ���������ӿڲ�ͬ����ɫ��
	 * ��һ���ӿ���ɫΪr[0],g[0],b[0]����ע��ÿһ����ɫ����һ���ֽڵ�ֵ�������ǳ��õĸ���ֵ��
	 * ���������ֽ�����Ϊ����0-255�����ֵ�Ȳ���0.0f-1.0f�ĸ���ֵ�����ס�
	 * tex_dataָ�����ǵ���ͼ���ݡ�
	 */
	// ������������
	// �������㹻���ڴ����������ǵ�����(width*height*3).
	private ByteBuffer			tex_data		= BufferUtil
														.newByteBuffer(MAZE_WIDTH * MAZE_HEIGHT * 3);
	// �������ɫ
	private byte[]				r				= new byte[4];
	private byte[]				g				= new byte[4];
	private byte[]				b				= new byte[4];
	// ����
	private GlSphere			quadricGlSphere;
	// Բ����
	private GlCylinder			quadricGlCylinder;
	// ��ת����(xrot,yrot��zrot����ת3d�����õ��ı�����)
	private float				xrot, yrot, zrot;

	private long				previousTime	= System.currentTimeMillis();
	// �Թ��Ƿ񱻽���--�Ƿ���Ҫ����
	private boolean				resetMaze		= false;

	int							windowWidth		= 0;
	int							windowHeight	= 0;

	// ����
	public void resetMaze()
	{
		resetMaze = true;
	}

	// ������������
	private void updateTex(int dmx, int dmy)
	{
		/*
		 * ����������λ��dmx,dmy����ɫֵΪ����ɫ��
		 * tex_data��ָ�����ǵ��������ݵġ�
		 * ÿһ�����ض���3�ֽ����(1�ֽں�ɫ������1�ֽ���ɫ������һ�ֽ���ɫ����). 
		 * ��ɫ������ƫ��Ϊ0������Ҫ�޸ĵ����ص������������е�ƫ��Ϊdmx(���ص�x���꣩����dmy(����y����)����ͼ��ȵĳ˻�,���Ľ����3(3�ֽ�ÿ����)��
		 * �����һ�д�������red(0)��ɫ����Ϊ255, 
		 * �ڶ�������green(1)��ɫ����Ϊ255,
		 * ���һ������blue(2)��ɫ����Ϊ255,���Ľ��Ϊ��dmx,dmy����������ɫΪ��ɫ��
		 */
		// ������ɫΪ��ɫ
		tex_data.put(((dmx + (MAZE_WIDTH * dmy)) * 3), (byte) 255);
		tex_data.put(1 + ((dmx + (MAZE_WIDTH * dmy)) * 3), (byte) 255);
		tex_data.put(2 + ((dmx + (MAZE_WIDTH * dmy)) * 3), (byte) 255);
	}

	/**
	 * �������൱��Ĺ������������������ÿһ���ӿ����������ɫ��ɾ���Թ��е�ǽ��Ϊ�Թ������������µ������㡣
	 * ��һ�д������tex_dataָ�����ͼ���ݡ�
	 * ������Ҫ���width(��ͼ��*height(��ͼ��)*3(�죬�̣���)�� 
     * ����ڴ�ռ�����������е��ֽ�Ϊ0�����3����ɫ���������㣬��ô������ͼ����ȫ����ˣ�
	 */
	private void reset()
	{
		// �����������
		tex_data.clear();
		while (tex_data.hasRemaining())
		{
			tex_data.put((byte) 0);
		}
		tex_data.flip();

		/**
		 * �������ĸ��ӿڣ����������Ҫ��0-3��ѭ��������
		 * ���Ǹ�ÿһ����ɫ(red,green,blue)��128-255�м�����ֵ��Ҫ��128��Ŀ������Ҫ��������ɫ��
		 * ��СֵΪ0�����ֵΪ255,��128���ʾ��Լ��50%������
		 */
		// ѭ�����������ɫ
		for (int loop = 0; loop < 4; loop++)
		{
			r[loop] = (byte) ((int) (Math.random() * Integer.MAX_VALUE) % 128);
			g[loop] = (byte) ((int) (Math.random() * Integer.MAX_VALUE) % 128);
			b[loop] = (byte) ((int) (Math.random() * Integer.MAX_VALUE) % 128);
		}

		setRandomMazePosition();
	}

	/**
	 * ��������һ���������㡣���ǵ���������һ�����䡣
	 * ��������ÿ�������ؾ���һ�����䡣
	 * Ϊȷ������Ƿ��������ǽ��������0����ͼ���һ��ķ�Χ����ѡһ����������2��ˡ�
	 * ͨ�����ַ�������ֻ�ܵõ���0,2,6,8֮�������Ҳ����˵�������ǵõ�һ������ķ��䣬
	 * ��������½��һ��ǽ����1,3,5,7,9�ȵȡ�
	 */
	// ����һ����������
	// �����Թ������
	private void setRandomMazePosition()
	{
		mx = (int) Math.round(Math.random() * ((MAZE_WIDTH - 1) / 2)) * 2;
		my = (int) Math.round(Math.random() * ((MAZE_HEIGHT - 1) / 2)) * 2;
	}

	//��ʼ��
	public void init(GL10 gl)
	{
		// ����������ͼ
		reset();

		/**
		 *  Reset�������ͼ������������ɫ����Ϊ�Թ�ѡȡ�����㡣
		 *  һ�����еĶ��������ú��ˡ����ǽ������ǵĳ�ʼ����
		 *  ǰ���������������������ض��� [0,1]��Χ�ڣ�
		 *  Ϊ�˿���CLAMP��������Ҫ�ԣ������Ƶ������д��뿴����
		 *  ���û��Clamping,���ע�⵽������Ķ������ұߵ�ϸС������
		 *  ��Щ�����ĳ�������Ϊ���Թ�����ʹ��������ƽ������������߽硣
		 *  ���һ�������߽�ĵ㱻���ˣ�������ĶԱ��Ͼͻ����һ���ߡ�
		 *  ���Ǵ��������Թ�����ʹ�����ĸ�ƽ��һ�㡣 
		 *  ��ʲô���͵Ĺ����������������ġ������ʹ�����������������Ǿͻ��ɹ�������ΪGL_NEAREST
		 *  �����������tex_data���ݣ���û������alphaͨ����������һ����ά��RGB����
		 */
		// �����������
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

		// �������μ�����
		quadricGlSphere = new GlSphere(1.3f, 32, 12, true, true);
		quadricGlCylinder = new GlCylinder(0.7f, 0.7f, 3.0f, 32, 4, true, true);
		/**
		 * Light0���������������ǲ�������գ����������κ����á�
		 * Light0��Ԥ����ĵƹ⣬����ָ����Ļ�ڡ���Ҳ�����Լ�����
		 */
		gl.glEnable(GL10.GL_LIGHT0);
	}

	/**
	 * ������Ҫ����һ������dir, ��������ʾ��¼��������ϣ����ң����»�����ֵ��
	 */
	// ���¸�������
	private void update(float milliseconds)
	{
		// ���浱ǰ�ķ���
		int dir;
		/**
		 * xrot,yrot��zrotͨ����һЩС��������˶�����ʱ������Ŷ����ӡ�
		 * �������ǿ�����������x�ᣬy���z����ת��ÿ�����������Ӳ�ͬ��ֵʹ��ת�ÿ�һ��
		 */
		// ��ת����
		xrot += milliseconds * 0.02f;
		yrot += milliseconds * 0.03f;
		zrot += milliseconds * 0.015f;

		/**
		 * ��鵱ǰ������ұ߷����Ƿ񱻷��ʹ������Ƿ�ǰλ�õ��ұ����Թ����ұ߽磨��ǰ�����ұߵķ���Ͳ����ڣ���
		 * ͬ�������ߵķ����Ƿ���ʹ������Ƿ�ﵽ��߽硣��������Ҳ����˼�顣
		 * ���������ɫ�ĺ�ɫ������ֵΪ255,�ͱ�ʾ��������Ѿ������ʹ���(��Ϊ���Ѿ�������UpdateTex���¹�)��
		 * ���mx(��ǰx���꣩С��2, �ͱ�ʾ�����Ѿ������Թ�����߲��������ˡ�
		 * ������ĸ����򶼲����ƶ��˻����Ѿ����˱߽磬�͸�mx��myһ�����ֵ��Ȼ�������ֵ��Ӧ���Ƿ񱻷��ʣ�
		 * ���û�У����Ǿ�����Ѱ��һ���µ����������ֱ���ñ�����Ӧ�ĵ�Ԫ���Ѿ������ʡ�
		 * ��Ϊ��Ҫ�Ӿɵ�·���зֲ���µ�·�����������Ǳ��뱣������֪��������һ�ϵ�·�����Դ����￪ʼ�µ�·����
		 * Ϊ��ʹ���뾡����̣���û�д���ȥ���mx-2�Ƿ�С��0��
		 * ���������100%�Ĵ����⣬������޸���δ�����ֹ���ʲ����ڵ�ǰ��ͼ���ڴ档
		 */
		// ����Ƿ��߹�����
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
		 * ����dir����0-3֮������ֵ�����ֵ�������Ǹ����ң����ϣ����������»��Թ���
		 * �ڵõ�����ķ���֮�����Ǽ��dir��ֵ�Ƿ�Ϊ0(������)��
		 * ����ǲ������ǲ����Թ����ұ߽磬Ȼ���鵱ǰ������ұ߷��䣬
		 * ���û�����ʣ����Ǿ͵���UpdateTex(mx+1,my)����������֮�佨��һ��ǽ��
		 * Ȼ��mx����2�Ƶ��µķ���.
		 */
		// ���һ������
		dir = (int) Math.round(Math.random() * 3f);

		// �����ߣ���������
		if ((dir == 0) && (mx <= (MAZE_WIDTH - 4)))
		{
			if (tex_data.get((((mx + 2) + (MAZE_WIDTH * my)) * 3)) == 0)
			{
				updateTex(mx + 1, my);
				mx += 2;
			}
		}
		/**
		 * ���dir��ֵΪ1(����)���������ǲ����Թ��ײ������Ǽ�鵱ǰ��������淿���Ƿ񱻷��ʹ���
		 * ���û�����ʹ������Ǿ�����������(��ǰ����͵�ǰ��������ķ���)����һ��ǽ��
		 * Ȼ��my����2�Ƶ��µķ���.
		 */
		//  �����ߣ���������
		if ((dir == 1) && (my <= (MAZE_HEIGHT - 4)))
		{
			if (tex_data.get(((mx + (MAZE_WIDTH * (my + 2))) * 3)) == 0)
			{
				updateTex(mx, my + 1);
				my += 2;
			}
		}
		/**
		 * ���dir��ֵΪ2(����)�������ǲ�����߽磬���Ǿͼ����ߵķ����Ƿ񱻷��ʣ�
		 * ���û�����ʣ�����Ҳ����������(��ǰ�������ߵķ���)֮�佨��һ��ǽ��
		 * Ȼ��mx��2�Ƶ��µķ���.
		 */
		// �����ߣ���������
		if ((dir == 2) && (mx >= 2))
		{
			if (tex_data.get((((mx - 2) + (MAZE_WIDTH * my)) * 3)) == 0)
			{
				updateTex(mx - 1, my);
				mx -= 2;
			}
		}
		/**
		 * ���dir��ֵΪ3���Ҳ����Թ��������
		 * ��ǰ����͵�ǰ�������������֮�佨��һ��ǽ��
		 * Ȼ��my����2�Ƶ��µķ��䡣
		 */
		// �����ߣ���������
		if ((dir == 3) && (my >= 2))
		{
			if (tex_data.get(((mx + (MAZE_WIDTH * (my - 2))) * 3)) == 0)
			{
				updateTex(mx, my - 1);
				my -= 2;
			}
		}
		/**
		 * �Ƶ��µķ�������Ǳ����־��ǰ����Ϊ���ڷ���״̬��
		 * ����ͨ�������Ե�ǰλ��mx, myΪ������UpdateTex()�������ﵽ���Ŀ�ġ�
		 */
		// ��������
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
		 * ��ÿһ֡����Ҫ����������Ҫ��ӳ������֮ǰ���¡������������ķ�����������glTexSubImage2D(). 
		 * ���ܰ��ڴ��е������ȫ���򲿷ֺ���Ļ�е����彨��ӳ�䡣
		 * ����ϸ�ڼ���Ϊ0��û��x����(0)��y����(0)��ƫ�ƣ�
		 * ������Ҫ�������������ÿһ���֣�ͼ��ΪGL_RGB���ͣ���Ӧ����������ΪGL_UNSIGNED_BYTE. 
		 * tex_data��������Ҫӳ��ľ������ݡ�
		 * ����һ���Ƿǳ���Ĳ����ؽ��������������ķ�����
		 * ͬ����Ҫע�������������Ϊ�㽨��һ������������ڸ�������ǰ���������á�
		 */
		// ���ø��µ�����
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
		 * ���д���ǳ���Ҫ���������������Ļ��
		 * һ�������������Ļ��Ȼ���ڻ�ÿһ���ӿ�ǰ������ǵ���ȴ�ǳ���Ҫ��
		 */
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		/**
		 * ����Ҫ���ĸ��ӿڣ����Խ�����һ��0��3��ѭ����
		 * ����Ҫ��������������glColor3ub(r,g,b)���õ�ǰ�ӿڵ���ɫ��
		 * ����glColor3f(r,g,b)����һ���������޷����ֽڴ��渡����Ϊ������
		 * ��ס��Щʱ����˵����ʡһ��0-255�������ɫֵ������ס������Ѿ����˸�����������ȷ��ɫ����Ҫ��ֵ��
		 * glColor3f(0.5f,0.5f,0.5f)��ָ��ɫ�к죬�̣�������50%������ֵ��
		 * glColor3ub(127,127,127)ͬ��Ҳ��ʾͬ������˼��
		 * ���loop��ֵΪ0,���ǽ�ѡ��r[0],b[0],b[[0]�����loopָΪ1, 
		 * ����ѡ��r[1],g[1],b[1]. ������ÿ�����������Ը��������ɫ��
		 */
		// ѭ������4���ӿ�
		for (int loop = 0; loop < 4; loop++)
		{
			gl.glColor4f(Math.abs(((float) r[loop]) / 255.0f),
					Math.abs(((float) g[loop]) / 255.0f),
					Math.abs(((float) b[loop]) / 255.0f),
					1.0f);
			// �������Ͻǵ��ӿ�
			if (loop == 0)
			{
				// �����ӿ�����
				gl.glViewport(0, windowHeight / 2, windowWidth / 2, windowHeight / 2);

				gl.glMatrixMode(GL10.GL_PROJECTION);

				gl.glLoadIdentity();

				GLU.gluOrtho2D(gl, 0, windowWidth / 2, windowHeight / 2, 0);

			}
			// �������Ͻ��ӿ�
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
			// �������½��ӿ�
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
			// �������½��ӿ�
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
			 * ѡ��ģ����ͼ����Ϊ��ǰ�����棬����������Ȼ�������Ȼ��档
			 * ������ÿ���ӿڻ�֮ǰ�����Ȼ��档ע�⵽����û�������Ļ��ɫ��ֻ����Ȼ��棡
			 * �����û�������Ȼ��棬�㽫��������Ĳ�����ʧ�ˣ��ȵȣ������Բ����ۣ�
			 */
			gl.glMatrixMode(GL10.GL_MODELVIEW);

			gl.glLoadIdentity();

			gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
			// �������Ͻǵ���ͼ
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
			// �������Ͻǵ���ͼ
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
			// �������½ǵ���ͼ
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
			// �������½ǵ���ͼ
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
	
	// ����ת��
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
	
	// ��������
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
