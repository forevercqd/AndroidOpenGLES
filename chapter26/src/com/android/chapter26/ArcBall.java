package com.android.chapter26;

public class ArcBall
{
	private static final float	Epsilon	= 1.0e-5f;

	// 保存点击向量
	Vector3f					StVec;
	// 保存拖动向量
	Vector3f					EnVec;
	// 鼠标可移动范围
	float						adjustWidth;
	float						adjustHeight;

	public ArcBall(float NewWidth, float NewHeight)
	{
		StVec = new Vector3f();
		EnVec = new Vector3f();
		setBounds(NewWidth, NewHeight);
	}

	public void mapToSphere(Point point, Vector3f vector)
	{
		//保存到临时点中
		Point2f tempPoint = new Point2f(point.x, point.y);

		// 将坐标映射到[-1，1]之间
		tempPoint.x = (tempPoint.x * this.adjustWidth) - 1.0f;
		tempPoint.y = 1.0f - (tempPoint.y * this.adjustHeight);

		// 计算正方形离中心点的距离
		float length = (tempPoint.x * tempPoint.x) + (tempPoint.y * tempPoint.y);

		// 如果该点映射到轨迹球的边界
		if (length > 1.0f)
		{
			float norm = (float) (1.0 / Math.sqrt(length));

			vector.x = tempPoint.x * norm;
			vector.y = tempPoint.y * norm;
			//把z轴设为0
			vector.z = 0.0f;
		}
		else
		{
			vector.x = tempPoint.x;
			vector.y = tempPoint.y;
			//把z轴设置为这个二维点映射到球面上对应的z值。
			vector.z = (float) Math.sqrt(1.0f - length);
		}

	}

	//如果窗口大小改变，设置鼠标移动的范围
	public void setBounds(float NewWidth, float NewHeight)
	{
		assert ((NewWidth > 1.0f) && (NewHeight > 1.0f));

		adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
		adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
	}

	//当点击鼠标时，记录点击的位置 
	public void click(Point NewPt)
	{
		mapToSphere(NewPt, this.StVec);

	}
	//当拖动鼠标时，记录当前鼠标的位置，并计算出旋转的量。
	public void drag(Point NewPt, Quat4f NewRot)
	{
		//Map the point to the sphere
		this.mapToSphere(NewPt, EnVec);

		//Return the quaternion equivalent to the rotation
		if (NewRot != null)
		{
			Vector3f Perp = new Vector3f();
			//Compute the vector perpendicular to the begin and end vectors
			Vector3f.cross(Perp, StVec, EnVec);

			//Compute the length of the perpendicular vector
			if (Perp.length() > Epsilon)
			{
				NewRot.x = Perp.x;
				NewRot.y = Perp.y;
				NewRot.z = Perp.z;
				NewRot.w = Vector3f.dot(StVec, EnVec);
			}
			else
			{
				//The begin and end vectors coincide, so return an identity transform
				NewRot.x = NewRot.y = NewRot.z = NewRot.w = 0.0f;
			}
		}
	}

}
