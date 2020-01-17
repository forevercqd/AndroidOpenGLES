package com.android.chapter26;

public class ArcBall
{
	private static final float	Epsilon	= 1.0e-5f;

	// ����������
	Vector3f					StVec;
	// �����϶�����
	Vector3f					EnVec;
	// �����ƶ���Χ
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
		//���浽��ʱ����
		Point2f tempPoint = new Point2f(point.x, point.y);

		// ������ӳ�䵽[-1��1]֮��
		tempPoint.x = (tempPoint.x * this.adjustWidth) - 1.0f;
		tempPoint.y = 1.0f - (tempPoint.y * this.adjustHeight);

		// ���������������ĵ�ľ���
		float length = (tempPoint.x * tempPoint.x) + (tempPoint.y * tempPoint.y);

		// ����õ�ӳ�䵽�켣��ı߽�
		if (length > 1.0f)
		{
			float norm = (float) (1.0 / Math.sqrt(length));

			vector.x = tempPoint.x * norm;
			vector.y = tempPoint.y * norm;
			//��z����Ϊ0
			vector.z = 0.0f;
		}
		else
		{
			vector.x = tempPoint.x;
			vector.y = tempPoint.y;
			//��z������Ϊ�����ά��ӳ�䵽�����϶�Ӧ��zֵ��
			vector.z = (float) Math.sqrt(1.0f - length);
		}

	}

	//������ڴ�С�ı䣬��������ƶ��ķ�Χ
	public void setBounds(float NewWidth, float NewHeight)
	{
		assert ((NewWidth > 1.0f) && (NewHeight > 1.0f));

		adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
		adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
	}

	//��������ʱ����¼�����λ�� 
	public void click(Point NewPt)
	{
		mapToSphere(NewPt, this.StVec);

	}
	//���϶����ʱ����¼��ǰ����λ�ã����������ת������
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
