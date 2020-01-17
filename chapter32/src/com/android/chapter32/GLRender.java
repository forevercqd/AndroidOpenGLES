package com.android.chapter32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;

import com.android.chapter32.math.Tuple3d;

public class GLRender implements Renderer
{
	private Context				mContext				= null;

	private static final float	EPSILON					= 1e-8f;

	private Tuple3d[]			arrayVel				= new Tuple3d[10];
	private Tuple3d[]			arrayPos				= new Tuple3d[10];
	private Tuple3d[]			oldPos					= new Tuple3d[10];
	private Tuple3d				veloc					= new Tuple3d(.5, -.1, .5);
	private Tuple3d				accel					= new Tuple3d(0, -.05, 0);
	private Tuple3d				dir						= new Tuple3d(0, 0, -10);
	private Tuple3d				cameraPosition			= new Tuple3d(0, -50, 1000);
	private boolean				increaseCameraZ;
	private boolean				decreaseCameraZ;

	private double				timeStep				= .6;
	private boolean				increaseTimeStep;
	private boolean				decreaseTimeStep;

	private float				cameraRotation			= 0;							
	private boolean				increaseCameraRotation;
	private boolean				decreaseCameraRotation;

	private float[]				spec					= {1.0f, 1.0f, 1.0f, 1.0f};	
	private float[]				posl					= {0.0f, 400f, 0.0f, 1.0f};	
	private float[]				amb2					= {0.3f, 0.3f, 0.3f, 1.0f};	
	private float[]				amb						= {0.2f, 0.2f, 0.2f, 1.0f};	

	private int					dlist;													
	private int[][]				texture					= new int[2][2];				
	private int					nrOfBalls;												
	private boolean				cameraAttachedToBall	= false;						
	private boolean				soundsEnabled			= true;						
	private Plane				pl1, pl2, pl3, pl4, pl5;								
	private Cylinder			cyl1, cyl2, cyl3;										
	private Explosion[]			explosions				= new Explosion[20];			
	private GlSphere			cyshare;												
	private GlCylinder			mGlCylinder;

	public void zoomOut(boolean increase)
	{
		increaseCameraZ = increase;
	}

	public void zoomIn(boolean decrease)
	{
		decreaseCameraZ = decrease;
	}

	public void increaseTimeStep(boolean increase)
	{
		increaseTimeStep = increase;
	}

	public void decreaseTimeStep(boolean decrease)
	{
		decreaseTimeStep = decrease;
	}

	public boolean isSoundsEnabled()
	{
		return soundsEnabled;
	}

	public void toggleSounds()
	{
		this.soundsEnabled = !soundsEnabled;
	}

	public boolean isCameraAttachedToBall()
	{
		return cameraAttachedToBall;
	}

	public void toggleCameraAttachedToBall()
	{
		this.cameraAttachedToBall = !cameraAttachedToBall;
		cameraRotation = 0;
	}

	public void increaseCameraRotation(boolean increase)
	{
		increaseCameraRotation = increase;
	}

	public void decreaseCameraRotation(boolean decrease)
	{
		decreaseCameraRotation = decrease;
	}
	//初始化
	public void init(GL10 gl)
	{

		float df[] = {100f};

		gl.glClearDepthf(1.0f); 
		gl.glEnable(GL10.GL_DEPTH_TEST); 
		gl.glDepthFunc(GL10.GL_LEQUAL); 
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 

		gl.glClearColor(0, 0, 0, 0);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, spec, 0);
		gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SHININESS, df, 0);

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, posl, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, amb2, 0);
		gl.glEnable(GL10.GL_LIGHT0);

		gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, amb, 0);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		loadGLTextures(gl);

		initVars();
	}

	// 装载纹理
	private void loadGLTextures(GL10 gl)
	{

		Bitmap image1 = Utils.getTextureFromBitmapResource(mContext, R.drawable.marble);
		Bitmap image2 = Utils.getTextureFromBitmapResource(mContext, R.drawable.spark);
		Bitmap image3 = Utils.getTextureFromBitmapResource(mContext, R.drawable.boden);
		Bitmap image4 = Utils.getTextureFromBitmapResource(mContext, R.drawable.wand);

		/* Create Texture **************************************** */
		gl.glGenTextures(2, texture[0], 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0][0]);

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image1, 0);

		/* Create Texture ***************************************** */
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0][1]);

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image2, 0);

		/* Create Texture ******************************************* */
		gl.glGenTextures(2, texture[1], 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1][0]);

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image3, 0);

		/* Create Texture ******************************************** */
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1][1]);

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image4, 0);
	}
	//初始化数据
	private void initVars()
	{
		// create palnes
		pl1 = new Plane();
		pl1.position = new Tuple3d(0, -300, 0);
		pl1.normal = new Tuple3d(0, 1, 0);

		pl2 = new Plane();
		pl2.position = new Tuple3d(300, 0, 0);
		pl2.normal = new Tuple3d(-1, 0, 0);

		pl3 = new Plane();
		pl3.position = new Tuple3d(-300, 0, 0);
		pl3.normal = new Tuple3d(1, 0, 0);

		pl4 = new Plane();
		pl4.position = new Tuple3d(0, 0, 300);
		pl4.normal = new Tuple3d(0, 0, -1);

		pl5 = new Plane();
		pl5.position = new Tuple3d(0, 0, -300);
		pl5.normal = new Tuple3d(0, 0, 1);

		cyl1 = new Cylinder();
		cyl1.position = new Tuple3d(0, 0, 0);
		cyl1.axis = new Tuple3d(0, 1, 0);
		cyl1.radius = 60 + 20;

		cyl2 = new Cylinder();
		cyl2.position = new Tuple3d(200, -300, 0);
		cyl2.axis = new Tuple3d(0, 0, 1);
		cyl2.radius = 60 + 20;

		cyl3 = new Cylinder();
		cyl3.position = new Tuple3d(-200, 0, 0);
		cyl3.axis = new Tuple3d(0, 1, 1);
		cyl3.axis.normalize();
		cyl3.radius = 30 + 20;

		cyshare = new GlSphere(15.0f, 32, 12, true, true);
		mGlCylinder = new GlCylinder(30.0f, 30.0f, 1000.0f, 32, 16, true, true);

		nrOfBalls = 10;
		arrayVel[0] = new Tuple3d(veloc);
		arrayPos[0] = new Tuple3d(199, 180, 10);

		explosions[0] = new Explosion();
		explosions[0].alpha = 0;
		explosions[0].scale = 1;
		arrayVel[1] = new Tuple3d(veloc);
		arrayPos[1] = new Tuple3d(0, 150, 100);

		explosions[1] = new Explosion();
		explosions[1].alpha = 0;
		explosions[1].scale = 1;
		arrayVel[2] = new Tuple3d(veloc);
		arrayPos[2] = new Tuple3d(-100, 180, -100);

		explosions[2] = new Explosion();
		explosions[2].alpha = 0;
		explosions[2].scale = 1;

		for (int i = 3; i < 10; i++)
		{
			arrayVel[i] = new Tuple3d(veloc);
			arrayPos[i] = new Tuple3d(-500 + i * 75, 300, -500 + i * 50);

			explosions[i] = new Explosion();
			explosions[i].alpha = 0;
			explosions[i].scale = 1;
		}

		for (int i = 10; i < 20; i++)
		{
			explosions[i] = new Explosion();
			explosions[i].alpha = 0;
			explosions[i].scale = 1;
		}
	}

	//更新摄像机的位置旋转
	private void update()
	{
		if (decreaseCameraZ) cameraPosition.z -= 10;
		if (increaseCameraZ) cameraPosition.z += 10;
		if (increaseCameraRotation) cameraRotation += 10;
		if (decreaseCameraRotation) cameraRotation -= 10;
		if (increaseTimeStep) timeStep += 0.1;
		if (decreaseTimeStep) timeStep -= 0.1;
	}
	//模拟函数，计算碰撞检测和物理模拟
	private void idle()
	{
		Tuple3d uveloc = new Tuple3d(), normal = new Tuple3d(), point = new Tuple3d(), norm = new Tuple3d(), Pos2 = new Tuple3d(), Nc = new Tuple3d();

		double rt2, rt4, rt[] = {0}, lamda[] = {10000}, RestTime[] = {0}, BallTime[] = {0};

		int BallNr = 0, BallColNr1[] = {0}, BallColNr2[] = {0};

		//如果没有锁定到球上，旋转摄像机
		if (!cameraAttachedToBall)
		{
			cameraRotation += 0.1f;
			if (cameraRotation > 360) cameraRotation = 0;
		}

		RestTime[0] = this.timeStep;
		lamda[0] = 1000;

		//计算重力加速度
		for (int j = 0; j < nrOfBalls; j++)
			arrayVel[j].scaleAdd(RestTime[0], accel, arrayVel[j]);

		//如果在一步的模拟时间内(如果来不及计算，则跳过几步)
		while (RestTime[0] > EPSILON)
		{

			lamda[0] = 10000; 
			//对于每个球，找到它们最近的碰撞点
			for (int i = 0; i < nrOfBalls; i++)
			{
				//计算新的位置和移动的距离
				oldPos[i] = new Tuple3d();
				oldPos[i].set(arrayPos[i]);
				uveloc.set(arrayVel[i]);
				uveloc.normalize();
				arrayPos[i].scaleAdd(RestTime[0], arrayVel[i], arrayPos[i]);
				rt2 = oldPos[i].distance(arrayPos[i]);
				//测试是否和墙面碰撞
				if (TestIntersionPlane(pl1, oldPos[i], uveloc, rt, norm))
				{
					//计算碰撞的时间
					rt4 = rt[0] * RestTime[0] / rt2;
					//如果小于当前保存的碰撞时间，则更新它
					if (rt4 <= lamda[0])
					{
						if (rt4 <= RestTime[0] + EPSILON) if (!((rt[0] <= EPSILON) && (uveloc
								.dot(norm) > EPSILON)))
						{
							normal.set(norm);
							point.scaleAdd(rt[0], uveloc, oldPos[i]);
							lamda[0] = rt4;
							BallNr = i;
						}
					}
				}

				if (TestIntersionPlane(pl2, oldPos[i], uveloc, rt, norm))
				{
					rt4 = rt[0] * RestTime[0] / rt2;
					if (rt4 <= lamda[0])
					{
						if (rt4 <= RestTime[0] + EPSILON) if (!((rt[0] <= EPSILON) && (uveloc
								.dot(norm) > EPSILON)))
						{
							normal.set(norm);
							point.scaleAdd(rt[0], uveloc, oldPos[i]);
							lamda[0] = rt4;
							BallNr = i;
						}
					}
				}

				if (TestIntersionPlane(pl3, oldPos[i], uveloc, rt, norm))
				{
					rt4 = rt[0] * RestTime[0] / rt2;
					if (rt4 <= lamda[0])
					{
						if (rt4 <= RestTime[0] + EPSILON) if (!((rt[0] <= EPSILON) && (uveloc
								.dot(norm) > EPSILON)))
						{
							normal.set(norm);
							point.scaleAdd(rt[0], uveloc, oldPos[i]);
							lamda[0] = rt4;
							BallNr = i;
						}
					}
				}

				if (TestIntersionPlane(pl4, oldPos[i], uveloc, rt, norm))
				{
					rt4 = rt[0] * RestTime[0] / rt2;
					if (rt4 <= lamda[0])
					{
						if (rt4 <= RestTime[0] + EPSILON) if (!((rt[0] <= EPSILON) && (uveloc
								.dot(norm) > EPSILON)))
						{
							normal.set(norm);
							point.scaleAdd(rt[0], uveloc, oldPos[i]);
							lamda[0] = rt4;
							BallNr = i;
						}
					}
				}

				if (TestIntersionPlane(pl5, oldPos[i], uveloc, rt, norm))
				{
					rt4 = rt[0] * RestTime[0] / rt2;
					if (rt4 <= lamda[0])
					{
						if (rt4 <= RestTime[0] + EPSILON) if (!((rt[0] <= EPSILON) && (uveloc
								.dot(norm) > EPSILON)))
						{
							normal.set(norm);
							point.scaleAdd(rt[0], uveloc, oldPos[i]);
							lamda[0] = rt4;
							BallNr = i;
						}
					}
				}

				//测试是否与三个圆柱相碰
				if (TestIntersionCylinder(cyl1, oldPos[i], uveloc, rt, norm, Nc))
				{
					rt4 = rt[0] * RestTime[0] / rt2;
					if (rt4 <= lamda[0])
					{
						if (rt4 <= RestTime[0] + EPSILON) if (!((rt[0] <= EPSILON) && (uveloc
								.dot(norm) > EPSILON)))
						{
							normal.set(norm);
							point.set(Nc);
							lamda[0] = rt4;
							BallNr = i;
						}
					}
				}

				if (TestIntersionCylinder(cyl2, oldPos[i], uveloc, rt, norm, Nc))
				{
					rt4 = rt[0] * RestTime[0] / rt2;
					if (rt4 <= lamda[0])
					{
						if (rt4 <= RestTime[0] + EPSILON) if (!((rt[0] <= EPSILON) && (uveloc
								.dot(norm) > EPSILON)))
						{
							normal.set(norm);
							point.set(Nc);
							lamda[0] = rt4;
							BallNr = i;
						}
					}
				}

				if (TestIntersionCylinder(cyl3, oldPos[i], uveloc, rt, norm, Nc))
				{
					rt4 = rt[0] * RestTime[0] / rt2;
					if (rt4 <= lamda[0])
					{
						if (rt4 <= RestTime[0] + EPSILON) if (!((rt[0] <= EPSILON) && (uveloc
								.dot(norm) > EPSILON)))
						{
							normal.set(norm);
							point.set(Nc);
							lamda[0] = rt4;
							BallNr = i;
						}
					}
				}
			}

			//计算每个球之间的碰撞，如果碰撞时间小于与上面的碰撞，则替换它们
			if (findBallCol(Pos2, BallTime, RestTime, BallColNr1, BallColNr2) == 1)
			{

				if ((lamda[0] == 10000) || (lamda[0] > BallTime[0]))
				{
					RestTime[0] = RestTime[0] - BallTime[0];
					Tuple3d pb1 = new Tuple3d(), pb2 = new Tuple3d(), xaxis = new Tuple3d(), U1x = new Tuple3d(), U1y = new Tuple3d(), U2x = new Tuple3d(), U2y = new Tuple3d(), V1x = new Tuple3d(), V1y = new Tuple3d(), V2x = new Tuple3d(), V2y = new Tuple3d();
					double a, b;

					pb1.scaleAdd(BallTime[0], arrayVel[BallColNr1[0]], oldPos[BallColNr1[0]]);
					pb2.scaleAdd(BallTime[0], arrayVel[BallColNr2[0]], oldPos[BallColNr2[0]]);
					xaxis.sub(pb2, pb1);
					xaxis.normalize();

					a = xaxis.dot(arrayVel[BallColNr1[0]]);
					U1x.scaleAdd(a, xaxis);
					U1y.sub(arrayVel[BallColNr1[0]], U1x);

					xaxis.sub(pb1, pb2);
					xaxis.normalize();

					b = xaxis.dot(arrayVel[BallColNr2[0]]);
					U2x.scaleAdd(b, xaxis);
					U2y.sub(arrayVel[BallColNr2[0]], U2x);

					V1x.add(U1x, U2x);
					V1x.sub(new Tuple3d(U1x.x - U2x.x, U1x.y - U2x.y, U1x.z - U2x.z));
					V1x.scale(.5);

					V2x.add(U1x, U2x);
					V2x.sub(new Tuple3d(U2x.x - U1x.x, U2x.y - U1x.y, U2x.z - U1x.z));
					V2x.scale(.5);

					V1y.set(U1y);
					V2y.set(U2y);

					for (int j = 0; j < nrOfBalls; j++)
						arrayPos[j].scaleAdd(BallTime[0], arrayVel[j], oldPos[j]);

					arrayVel[BallColNr1[0]].add(V1x, V1y);
					arrayVel[BallColNr2[0]].add(V2x, V2y);

					// 更新爆炸数组
					for (int j = 0; j < 20; j++)
					{
						if (explosions[j].alpha <= 0)
						{
							explosions[j].alpha = 1;
							explosions[j].position = arrayPos[BallColNr1[0]];
							explosions[j].scale = 1;
							break;
						}
					}
					continue;
				}
			}
			//最后的测试，替换下次碰撞的时间，并更新爆炸效果的数组
			if (lamda[0] != 10000)
			{
				RestTime[0] -= lamda[0];

				for (int j = 0; j < nrOfBalls; j++)
					arrayPos[j].scaleAdd(lamda[0], arrayVel[j], oldPos[j]);

				rt2 = arrayVel[BallNr].length();
				arrayVel[BallNr].normalize();

				normal.scale(-2 * normal.dot(arrayVel[BallNr]));
				arrayVel[BallNr].add(normal, arrayVel[BallNr]);
				arrayVel[BallNr].normalize();
				arrayVel[BallNr].scale(rt2);

				// 更新爆炸数组
				for (int j = 0; j < 20; j++)
				{
					if (explosions[j].alpha <= 0)
					{
						explosions[j].alpha = 1;
						explosions[j].position = point;
						explosions[j].scale = 1;
						break;
					}
				}
			}
			else
				RestTime[0] = 0;
		}
	}
	//判断是否和平面相交，是则返回true，否则返回false
	private boolean TestIntersionPlane(Plane plane, Tuple3d position, Tuple3d direction, double[] lamda, Tuple3d pNormal)
	{

		double dotProduct = direction.dot(plane.normal);
		double l2;

		//判断是否平行于平面
		if ((dotProduct < EPSILON) && (dotProduct > -EPSILON)) return false;

		Tuple3d substract = new Tuple3d(plane.position);
		substract.sub(position);
		l2 = (plane.normal.dot(substract)) / dotProduct;

		if (l2 < -EPSILON) return false;

		pNormal.set(plane.normal);
		lamda[0] = l2;
		return true;
	}

	//计算射线和圆柱方程组得解。
	private boolean TestIntersionCylinder(Cylinder cylinder, Tuple3d position, Tuple3d direction, double[] lamda, Tuple3d pNormal, Tuple3d newposition)
	{
		Tuple3d RC = new Tuple3d(), HB = new Tuple3d(), n = new Tuple3d(), O = new Tuple3d();
		double d, t, s, ln, in, out;

		RC.sub(position, cylinder.position);
		n.cross(direction, cylinder.axis);

		ln = n.length();

		if ((ln < EPSILON) && (ln > -EPSILON)) return false;

		n.normalize();
		d = Math.abs(RC.dot(n));

		if (d <= cylinder.radius)
		{

			O.cross(RC, cylinder.axis);
			t = -O.dot(n) / ln;
			O.cross(n, cylinder.axis);
			O.normalize();
			s = Math.abs(Math.sqrt(cylinder.radius * cylinder.radius - d * d) / direction.dot(O));

			in = t - s;
			out = t + s;

			if (in < -EPSILON)
			{
				if (out < -EPSILON)
					return false;
				else
					lamda[0] = out;
			}
			else if (out < -EPSILON)
			{
				lamda[0] = in;
			}
			else if (in < out)
				lamda[0] = in;
			else
				lamda[0] = out;

			newposition.scaleAdd(lamda[0], direction, position);
			HB.sub(newposition, cylinder.position);
			pNormal.scaleAdd(-HB.dot(cylinder.axis), cylinder.axis, HB);
			pNormal.normalize();
			return true;
		}
		return false;
	}

	//判断球和球是否相交，是则返回true，否则返回false
	private int findBallCol(Tuple3d point, double[] TimePoint, double[] Time2, int[] BallNr1, int[] BallNr2)
	{

		Tuple3d RelativeVClone = new Tuple3d(), RelativeV = new Tuple3d(), posi = new Tuple3d();

		double Timedummy = 10000, MyTime = 0, Add = Time2[0] / 150;
		TRay rays;

		//判断球和球是否相交
		for (int i = 0; i < nrOfBalls - 1; i++)
		{
			for (int j = i + 1; j < nrOfBalls; j++)
			{

				RelativeV.sub(arrayVel[i], arrayVel[j]);
				RelativeVClone.set(RelativeV);
				RelativeVClone.normalize();
				rays = new TRay(oldPos[i], RelativeVClone);
				MyTime = 0;

				if ((rays.dist(oldPos[j])) > 40) continue;

				while (MyTime < Time2[0])
				{

					MyTime += Add;
					posi.scaleAdd(MyTime, RelativeV, oldPos[i]);
					if (posi.distance(oldPos[j]) <= 40)
					{
						point.set(posi);
						if (Timedummy > (MyTime - Add)) Timedummy = MyTime - Add;

						BallNr1[0] = i;
						BallNr2[0] = j;
						break;
					}
				}
			}
		}

		if (Timedummy != 10000)
		{
			TimePoint[0] = Timedummy;
			return 1;
		}
		return 0;
	}

	public GLRender(Context context)
	{
		mContext = context;
	}

	public void onDrawFrame(GL10 gl)
	{
		Draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		height = (height == 0) ? 1 : height;

		gl.glViewport(0, 0, width, height); // Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 50.0f, (float) width / height, 10.f, 1700.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity();

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		init(gl);
	}

	//渲染
	void Draw(GL10 gl)
	{
		update();

		float texcoords[][] = new float[4][2];
		short vertices[][] = new short[4][3];
		byte indices[] = {0, 1, 3, 2}; /* QUAD to TRIANGLE_STRIP conversion; */
		int i;

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		// set camera in hookmode
		if (cameraAttachedToBall)
		{
			GLU.gluLookAt(gl,
					(float) (arrayPos[0].x + 250),
					(float) (arrayPos[0].y + 250),
					(float) (arrayPos[0].z),
					(float) (arrayPos[0].x + arrayVel[0].x),
					(float) (arrayPos[0].y + arrayVel[0].y),
					(float) (arrayPos[0].z + arrayVel[0].z),
					0,
					1,
					0);
		}
		else
		{
			GLU.gluLookAt(gl,
					(float) cameraPosition.x,
					(float) cameraPosition.y,
					(float) cameraPosition.z,
					(float) (cameraPosition.x + dir.x),
					(float) (cameraPosition.y + dir.y),
					(float) (cameraPosition.z + dir.z),
					0,
					1,
					0);

		}

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glRotatef(cameraRotation, 0, 1, 0);

		// render balls
		for (i = 0; i < nrOfBalls; i++)
		{
			switch (i)
			{
			case 1:
				gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				break;
			case 2:
				gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
				break;
			case 3:
				gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
				break;
			case 4:
				gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
				break;
			case 5:
				gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
				break;
			case 6:
				gl.glColor4f(0.65f, 0.2f, 0.3f, 1.0f);
				break;
			case 7:
				gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
				break;
			case 8:
				gl.glColor4f(0.0f, 0.7f, 0.4f, 1.0f);
				break;
			default:
				gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			}
			gl.glPushMatrix();
			gl.glTranslatef((float) arrayPos[i].x, (float) arrayPos[i].y, (float) arrayPos[i].z);
			cyshare.draw(gl);
			gl.glPopMatrix();

		}

		gl.glEnable(GL10.GL_TEXTURE_2D);

		// render walls(planes) with texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1][1]);
		gl.glColor4f(1, 1, 1, 1);

		/* Enable vertex and texcoord arrays */
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		/* Fill texture coordinates, one set for all planes */
		texcoords[0][0] = 1.0f;
		texcoords[0][1] = 0.0f;
		texcoords[1][0] = 1.0f;
		texcoords[1][1] = 1.0f;
		texcoords[2][0] = 0.0f;
		texcoords[2][1] = 1.0f;
		texcoords[3][0] = 0.0f;
		texcoords[3][1] = 0.0f;

		vertices[0][0] = 320;
		vertices[0][1] = 320;
		vertices[0][2] = 320;
		vertices[1][0] = 320;
		vertices[1][1] = -320;
		vertices[1][2] = 320;
		vertices[2][0] = -320;
		vertices[2][1] = -320;
		vertices[2][2] = 320;
		vertices[3][0] = -320;
		vertices[3][1] = 320;
		vertices[3][2] = 320;

		gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
				.wrap(indices));

		vertices[0][0] = -320;
		vertices[0][1] = 320;
		vertices[0][2] = -320;
		vertices[1][0] = -320;
		vertices[1][1] = -320;
		vertices[1][2] = -320;
		vertices[2][0] = 320;
		vertices[2][1] = -320;
		vertices[2][2] = -320;
		vertices[3][0] = 320;
		vertices[3][1] = 320;
		vertices[3][2] = -320;

		gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
				.wrap(indices));

		vertices[0][0] = 320;
		vertices[0][1] = 320;
		vertices[0][2] = -320;
		vertices[1][0] = 320;
		vertices[1][1] = -320;
		vertices[1][2] = -320;
		vertices[2][0] = 320;
		vertices[2][1] = -320;
		vertices[2][2] = 320;
		vertices[3][0] = 320;
		vertices[3][1] = 320;
		vertices[3][2] = 320;

		gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
				.wrap(indices));

		vertices[0][0] = -320;
		vertices[0][1] = 320;
		vertices[0][2] = 320;
		vertices[1][0] = -320;
		vertices[1][1] = -320;
		vertices[1][2] = 320;
		vertices[2][0] = -320;
		vertices[2][1] = -320;
		vertices[2][2] = -320;
		vertices[3][0] = -320;
		vertices[3][1] = 320;
		vertices[3][2] = -320;

		gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
				.wrap(indices));

		// render floor (plane) with colours
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1][0]);

		texcoords[0][0] = 1.0f;
		texcoords[0][1] = 0.0f;
		vertices[0][0] = -320;
		vertices[0][1] = -320;
		vertices[0][2] = 320;
		texcoords[1][0] = 1.0f;
		texcoords[1][1] = 1.0f;
		vertices[1][0] = 320;
		vertices[1][1] = -320;
		vertices[1][2] = 320;
		texcoords[2][0] = 0.0f;
		texcoords[2][1] = 1.0f;
		vertices[2][0] = 320;
		vertices[2][1] = -320;
		vertices[2][2] = -320;
		texcoords[3][0] = 0.0f;
		texcoords[3][1] = 0.0f;
		vertices[3][0] = -320;
		vertices[3][1] = -320;
		vertices[3][2] = -320;

		gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
				.wrap(indices));

		/* Disable vertex and texcoord arrays */
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// render columns(cylinders)
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0][0]);
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
		gl.glPushMatrix();
		gl.glRotatef(90, 1, 0, 0);
		gl.glTranslatef(0, 0, -100);
		mGlCylinder.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(200, -300, -100);
		mGlCylinder.draw(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(-200, 0, 0);
		gl.glRotatef(135, 1, 0, 0);
		gl.glTranslatef(0, 0, -100);
		mGlCylinder.draw(gl);
		gl.glPopMatrix();

		//爆炸效果
		// 使用混合
		gl.glEnable(GL10.GL_BLEND);
		// 禁用深度缓存
		gl.glDepthMask(false);
		// 设置纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0][1]);
		
		for (i = 0; i < 20; i++)
		{

			if (explosions[i].alpha >= 0)
			{
				gl.glPushMatrix();
				// 设置alpha
				explosions[i].alpha -= 0.01f;
				// 设置缩放
				explosions[i].scale += 0.03f;
				// 设置颜色
				gl.glColor4f(1, 1, 0, explosions[i].alpha);
				gl.glScalef(explosions[i].scale, explosions[i].scale, explosions[i].scale);
				// 设置位置
				gl.glTranslatef((float) explosions[i].position.x / explosions[i].scale,
						(float) explosions[i].position.y / explosions[i].scale,
						(float) explosions[i].position.z / explosions[i].scale);

				/* Enable vertex and texcoord arrays */
				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

				gl.glRotatef(-45, 0, 1, 0);

				texcoords[0][0] = 0.0f;
				texcoords[0][1] = 0.0f;
				texcoords[1][0] = 0.0f;
				texcoords[1][1] = 1.0f;
				texcoords[2][0] = 1.0f;
				texcoords[2][1] = 1.0f;
				texcoords[3][0] = 1.0f;
				texcoords[3][1] = 0.0f;

				gl.glNormal3f(0, 0, 1);
				vertices[0][0] = -50;
				vertices[0][1] = -40;
				vertices[0][2] = 0;
				vertices[1][0] = 50;
				vertices[1][1] = -40;
				vertices[1][2] = 0;
				vertices[2][0] = 50;
				vertices[2][1] = 40;
				vertices[2][2] = 0;
				vertices[3][0] = -50;
				vertices[3][1] = 40;
				vertices[3][2] = 0;

				gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
						.wrap(indices));

				gl.glNormal3f(0, 0, -1);
				vertices[0][0] = -50;
				vertices[0][1] = 40;
				vertices[0][2] = 0;
				vertices[1][0] = 50;
				vertices[1][1] = 40;
				vertices[1][2] = 0;
				vertices[2][0] = 50;
				vertices[2][1] = -40;
				vertices[2][2] = 0;
				vertices[3][0] = -50;
				vertices[3][1] = -40;
				vertices[3][2] = 0;

				gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
						.wrap(indices));

				gl.glNormal3f(1, 0, 0);
				vertices[0][0] = 0;
				vertices[0][1] = -40;
				vertices[0][2] = 50;
				vertices[1][0] = 0;
				vertices[1][1] = -40;
				vertices[1][2] = -50;
				vertices[2][0] = 0;
				vertices[2][1] = 40;
				vertices[2][2] = -50;
				vertices[3][0] = 0;
				vertices[3][1] = 40;
				vertices[3][2] = 50;

				gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
						.wrap(indices));

				gl.glNormal3f(-1, 0, 0);
				vertices[0][0] = 0;
				vertices[0][1] = 40;
				vertices[0][2] = 50;
				vertices[1][0] = 0;
				vertices[1][1] = 40;
				vertices[1][2] = -50;
				vertices[2][0] = 0;
				vertices[2][1] = -40;
				vertices[2][2] = -50;
				vertices[3][0] = 0;
				vertices[3][1] = -40;
				vertices[3][2] = 50;

				gl.glVertexPointer(3, GL10.GL_SHORT, 0, makeShortBuffer(vertices));
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(texcoords));
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, ByteBuffer
						.wrap(indices));

				/* Disable vertex and texcoord arrays */
				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

				gl.glPopMatrix();
			}
		}

		gl.glDepthMask(true);
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		//
		idle();

		gl.glFinish();
	}

	//数组转换为缓冲区
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

	protected ShortBuffer makeShortBuffer(short[][] arr)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * arr[0].length * 4);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer sb = bb.asShortBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			sb.put(arr[i]);
		}
		sb.position(0);
		return sb;
	}

	/*************************************/
	//爆炸效果
	private static class Explosion
	{
		public Tuple3d	position	= new Tuple3d();
		public float	alpha;
		public float	scale;
	}
	private static class Cylinder
	{
		public Tuple3d	position;
		public Tuple3d	axis;
		public double	radius;
	}

	private static class Plane
	{
		public Tuple3d	position;
		public Tuple3d	normal;
	}
	//射线
	private static class TRay
	{
		public Tuple3d	p;
		public Tuple3d	v;

		public TRay(Tuple3d p, Tuple3d v)
		{
			this.p = new Tuple3d(p);
			this.v = new Tuple3d(v);
		}

		public double dist(Tuple3d point)
		{
			double lambda = v.dot(new Tuple3d(point.x - p.x, point.y - p.y, point.z - p.z));
			Tuple3d point2 = new Tuple3d();
			point2.scaleAdd(lambda, v, p);
			return point.distance(point2);
		}
	}

}
