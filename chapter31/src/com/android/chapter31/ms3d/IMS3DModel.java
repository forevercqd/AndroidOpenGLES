package com.android.chapter31.ms3d;

import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.android.chapter31.GLRender;
import com.android.chapter31.utils.IBufferFactory;
import com.android.chapter31.utils.Matrix4f;
import com.android.chapter31.utils.Quat4f;
import com.android.chapter31.utils.TextureInfo;
import com.android.chapter31.utils.Vector3f;
// MS3Dģ��
public class IMS3DModel
{
	// ģ������
	public static final String	MODEL_TYPE_MS3D	= "ms3d";
	// ģ������
	public String				mName			= "";
	// ͷ�ļ�
	public MS3DHeader			mHeader;
	// ��������
	public MS3DVertex[]			mpVertices;
	// ����Σ������Σ�
	public MS3DTriangle[]		mpTriangles;
	// �����飩
	public MS3DGroup[]			mpGroups;
	// ����
	public MS3DMaterial[]		mpMaterials;
	// �ؽ�
	public Joint[]				mpJoints;
	// ��ʱ��
	public float				mTotalTime;
	// ��ǰʱ��
	public float				mCurrentTime;
	public float				mFps;
	// ֡��
	public int					mNumFrames;
	public int					mNumPrimitives;
	private String				mStrComment;
	// ���㻺����
	private FloatBuffer[]		mpBufVertices;
	//��ɫ������
	private FloatBuffer[]		mpBufColor;
	// ������ͼ������
	private FloatBuffer[]		mpBufTextureCoords;
	
	//ģ�͵İ󶨿�
	private Vector3f			mvMax			= new Vector3f();
	private Vector3f			mvMin			= new Vector3f();
	private Vector3f			mvCenter		= new Vector3f();
	private float				mfRadius;
	//�Ƿ��ʼ����Χ��
	private boolean				mbInitBoundingBox;

	// ��ͼ��Ϣ
	private TextureInfo[]		mpTexInfo;

	// ��־ģ���Ƿ����
	private boolean				mbDirtFlag		= false;

	//�ؽ����ߵ�λ�û���
	private FloatBuffer			mBufJointLinePosition;
	// �ؽڵ��λ�û���
	private FloatBuffer			mBufJointPointPosition;
	//�ؽڵ�������ؽ����߼���
	private int					mJointPointCount, mJointLineCount;

	public String getComment()
	{
		return mStrComment;
	}

	public void setComment(String comment)
	{
		this.mStrComment = comment;
	}

	public void setTexture(TextureInfo[] pTexInfo)
	{
		mpTexInfo = pTexInfo;
	}

	public boolean loadModel(InputStream is)
	{
		IMS3DLoader loader = new IMS3DLoader();
		
		//װ��ms3dģ��
		boolean resultOK = loader.Load(is, this);

		if (!resultOK) { return false; }

		mCurrentTime = 0.0f;
		mTotalTime = mNumFrames / mFps;

		mpBufVertices = new FloatBuffer[mpGroups.length];
		mpBufColor = new FloatBuffer[mpGroups.length];
		mpBufTextureCoords = new FloatBuffer[mpGroups.length];

		for (int i = 0; i < mpGroups.length; i++)
		{
			mpBufColor[i] = IBufferFactory.newFloatBuffer(mpGroups[i].getTriangleCount() * 3 * 4);
			mpBufVertices[i] = IBufferFactory
					.newFloatBuffer(mpGroups[i].getTriangleCount() * 3 * 3);
			mpBufTextureCoords[i] = IBufferFactory
					.newFloatBuffer(mpGroups[i].getTriangleCount() * 3 * 2);

			for (int j = 0; j < mpGroups[i].getTriangleCount(); j++)
			{
				MS3DTriangle triangle = mpTriangles[mpGroups[i].getTriangleIndicies()[j]];

				for (int k = 0; k < 3; k++)
				{
					mpBufTextureCoords[i].put(triangle.getS()[k]);
					mpBufTextureCoords[i].put(triangle.getT()[k]);
				}
			}
			mpBufTextureCoords[i].rewind();
		}

		mbDirtFlag = true;
		animate(0.0f);
		mbInitBoundingBox = true;
		fillRenderBuffer();
		mbInitBoundingBox = false;
		return true;
	}

	private void updateJointsHelper()
	{
		if (!GLRender.gbShowJoints) { return; }
		if (!containsJoint()) { return; }
		if (mBufJointPointPosition == null)
		{
			mJointPointCount = mpJoints.length;
			mBufJointPointPosition = IBufferFactory.newFloatBuffer(mpJoints.length * 3);
		}
		mBufJointPointPosition.position(0);

		for (int i = 0, n = mpJoints.length; i < n; i++)
		{
			Joint joint = mpJoints[i];

			float x = joint.mMatGlobal.m03;
			float y = joint.mMatGlobal.m13;
			float z = joint.mMatGlobal.m23;

			mBufJointPointPosition.put(x);
			mBufJointPointPosition.put(y);
			mBufJointPointPosition.put(z);
		}

		mBufJointPointPosition.position(0);

		if (mBufJointLinePosition == null)
		{
			mJointLineCount = mpJoints.length * 2;
			mBufJointLinePosition = IBufferFactory.newFloatBuffer(mpJoints.length * 2 * 3);
		}
		mBufJointLinePosition.position(0);

		for (int i = 0, n = mpJoints.length; i < n; i++)
		{
			Joint joint = mpJoints[i];

			float x0, y0, z0;
			float x1, y1, z1;
			x0 = joint.mMatGlobal.m03;
			y0 = joint.mMatGlobal.m13;
			z0 = joint.mMatGlobal.m23;
			if (joint.mParentId == -1)
			{
				x1 = x0;
				y1 = y0;
				z1 = z0;
			}
			else
			{
				joint = mpJoints[joint.mParentId];
				x1 = joint.mMatGlobal.m03;
				y1 = joint.mMatGlobal.m13;
				z1 = joint.mMatGlobal.m23;
			}

			mBufJointLinePosition.put(x0);
			mBufJointLinePosition.put(y0);
			mBufJointLinePosition.put(z0);

			mBufJointLinePosition.put(x1);
			mBufJointLinePosition.put(y1);
			mBufJointLinePosition.put(z1);
		}

		mBufJointLinePosition.position(0);
	}

	/**
	 * ����ʱ��������ģ�Ͷ���
	 * 
	 * @param timedelta
	 *            - ����tickʱ��
	 */
	public void animate(float timedelta)
	{
		// �ۼ�ʱ��
		mCurrentTime += timedelta;

		if (mCurrentTime > mTotalTime)
		{
			mCurrentTime = 0.0f;
		}
		// ����Ҫ����ÿ�������ڵ�ĵ�ǰλ����Ϣ
		for (int i = 0; i < mpJoints.length; i++)
		{
			Joint joint = mpJoints[i];
			// ���������������Ϣ�Ǿ��������
			if (joint.mNumTranslationKeyframes == 0 && joint.mNumRotationKeyframes == 0)
			{
				joint.mMatGlobal.set(joint.mMatJointAbsolute);
				continue;
			}

			// ��ʼ���в�ֵ����
			// ���Ƚ�����ת��ֵ
			Matrix4f matKeyframe = getJointRotation(i, mCurrentTime);
			// ����ƫ�Ƶ����Բ�ֵ
			matKeyframe.setTranslation(getJointTranslation(i, mCurrentTime));
			// ���Խڵ㱾�����Ծ���
			matKeyframe.mul(joint.mMatJointRelative, matKeyframe);

			// ���Ը����󣬵õ����վ���
			if (joint.mParentId == -1)
			{
				joint.mMatGlobal.set(matKeyframe);
			}
			else
			{
				matKeyframe.mul(mpJoints[joint.mParentId].mMatGlobal, matKeyframe);
				joint.mMatGlobal.set(matKeyframe);
			}
		}
		// ���µ�����Ⱦ�Ĺ���������Ϣ
		updateJointsHelper();

		// ��ʼ����ÿ������
		for (int i = 0, n = mpVertices.length; i < n; i++)
		{
			MS3DVertex vertex = mpVertices[i];

			if (vertex.getBoneID() == -1)
			{
				// ����ö��㲻�ܹ���Ӱ�죬��ô���������
				vertex.mvTransformedLocation.set(vertex.getLocation());
			}
			else
			{
				// ͨ���������㣬�õ�����ĵ�ǰλ��
				transformVertex(vertex);
			}
		}

		mbDirtFlag = true;
	}

	static Matrix4f	tmpMatrixJointRotation	= new Matrix4f();

	private Matrix4f getJointRotation(int jointIndex, float time)
	{
		Quat4f quat = lerpKeyframeRotate(mpJoints[jointIndex].mpRotationKeyframes, time);

		Matrix4f matRot = tmpMatrixJointRotation;
		matRot.set(quat);
		return matRot;
	}

	static Quat4f	tmpQuatLerp	= new Quat4f();
	static Quat4f	tmpQuatLerpLeft	= new Quat4f(), tmpQuatLerpRight = new Quat4f();

	/**
	 * ���ݴ����ʱ�䣬�����ֵ�����ת��
	 * 
	 * @param frames
	 *            ��ת���ؼ�֡����
	 * @param time
	 *            Ŀ��ʱ��
	 * @return ��ֵ�����ת������
	 */
	private Quat4f lerpKeyframeRotate(Keyframe[] frames, float time)
	{
		Quat4f quat = tmpQuatLerp;
		int frameIndex = 0;
		int numFrames = frames.length;

		// �������ʹ�ö��ֲ��ҽ����Ż�
		while (frameIndex < numFrames && frames[frameIndex].mfTime < time)
		{
			++frameIndex;
		}
		// ���ȴ���߽����
		if (frameIndex == 0)
		{
			quat.set(frames[0].mvParam);
		}
		else if (frameIndex == numFrames)
		{
			quat.set(frames[numFrames - 1].mvParam);
		}
		else
		{
			int prevFrameIndex = frameIndex - 1;
			// �ҵ����ڽ�����֡
			Keyframe right = frames[frameIndex];
			Keyframe left = frames[prevFrameIndex];
			// ����ò�ֵ����
			float timeDelta = right.mfTime - left.mfTime;
			float interpolator = (time - left.mfTime) / timeDelta;
			// ������Ԫ����ֵ
			Quat4f quatRight = tmpQuatLerpRight;
			Quat4f quatLeft = tmpQuatLerpLeft;

			quatRight.set(right.mvParam);
			quatLeft.set(left.mvParam);
			quat.interpolate(quatLeft, quatRight, interpolator);
		}

		return quat;
	}

	private Vector3f getJointTranslation(int jointIndex, float time)
	{
		Vector3f translation = lerpKeyframeLinear(mpJoints[jointIndex].mpTranslationKeyframes, time);

		return translation;
	}

	static Vector3f	tmpVectorLerp	= new Vector3f();

	/**
	 * ���ݴ����ʱ�䣬���ز�ֵ���λ����Ϣ
	 * 
	 * @param frames
	 *            ƫ�����ؼ�֡����
	 * @param time
	 *            Ŀ��ʱ��
	 * @return ��ֵ���λ����Ϣ
	 */
	private Vector3f lerpKeyframeLinear(Keyframe[] frames, float time)
	{
		int frameIndex = 0;
		int numFrames = frames.length;

		// �������ʹ�ö��ֲ��ҽ����Ż�
		while (frameIndex < numFrames && frames[frameIndex].mfTime < time)
		{
			++frameIndex;
		}

		// ���ȴ���߽����
		Vector3f parameter = tmpVectorLerp;
		if (frameIndex == 0)
		{
			parameter.set(frames[0].mvParam.x, frames[0].mvParam.y, frames[0].mvParam.z);
		}
		else if (frameIndex == numFrames)
		{
			parameter.set(frames[numFrames - 1].mvParam.x,
					frames[numFrames - 1].mvParam.y,
					frames[numFrames - 1].mvParam.z);
		}
		else
		{
			int prevFrameIndex = frameIndex - 1;
			// �õ��ٽ���֡
			Keyframe right = frames[frameIndex];
			Keyframe left = frames[prevFrameIndex];
			// �����ֵ����
			float timeDelta = right.mfTime - left.mfTime;
			float interpolator = (time - left.mfTime) / timeDelta;
			// ���м򵥵����Բ�ֵ
			parameter.interpolate(left.mvParam, right.mvParam, interpolator);
		}

		return parameter;
	}

	/**
	 * �����Ⱦ��������
	 */
	public void fillRenderBuffer()
	{
		if (!mbDirtFlag)
		{
			// ���ģ������û�и��£���ô�������������
			return;
		}
		Vector3f position = null;
		// ��������Group
		for (int i = 0; i < mpGroups.length; i++)
		{
			// ��ø�Group�����е�����������
			int[] indexes = mpGroups[i].getTriangleIndicies();
			mpBufVertices[i].position(0);
			int vertexIndex = 0;
			// ����ÿһ��������
			for (int j = 0; j < indexes.length; j++)
			{
				// �������γ����ҵ���Ӧ������
				MS3DTriangle triangle = mpTriangles[indexes[j]];
				// ���������ε�ÿ������
				for (int k = 0; k < 3; k++)
				{
					// �Ӷ�������ҵ���Ӧ����
					MS3DVertex vertex = mpVertices[triangle.getVertexIndicies()[k]];
					// ������µ�λ��
					// ���ģ�ʹ���������ô���ǵ�ǰ�ı任���λ��
					// ������ǳ�ʼλ��
					// ����ı任������ο�animate(float timedelta)����
					position = vertex.mvTransformedLocation;
					// ��䶥��λ����Ϣ��������
					mpBufVertices[i].put(position.x);
					mpBufVertices[i].put(position.y);
					mpBufVertices[i].put(position.z);

					if (mbInitBoundingBox)
					{
						// ����ģ�Ͱ󶨿򣬽���ģ������ʱ����
						mvMin.x = Math.min(mvMin.x, position.x);
						mvMin.y = Math.min(mvMin.y, position.y);
						mvMin.z = Math.min(mvMin.z, position.z);

						mvMax.x = Math.max(mvMax.x, position.x);
						mvMax.y = Math.max(mvMax.y, position.y);
						mvMax.z = Math.max(mvMax.z, position.z);
					}
				}
			}

			mpBufVertices[i].position(0);
		}

		if (mbInitBoundingBox)
		{
			// ���㶯̬����
			float distance = Vector3f.distance(mvMin, mvMax);
			mfRadius = distance * 0.5f;

			mvCenter.set(mvMin);
			mvCenter.add(mvMax);
			mvCenter.scale(0.5f);

			mbInitBoundingBox = false;
		}

		mbDirtFlag = false;
	}

	private static final int[]		JOINT_INDEXES	= new int[4], JOINT_WEIGHTS = new int[4];
	private static final float[]	WEIGHTS			= new float[4];

	static Vector3f					tmp				= new Vector3f(), tmpResult = new Vector3f(),
			tmpPos = new Vector3f();

	// ���ݹؽڵľ���任����
	private Vector3f transformVertex(MS3DVertex vertex)
	{
		Vector3f position = vertex.mvTransformedLocation;
		fillJointIndexesAndWeights(vertex, JOINT_INDEXES, JOINT_WEIGHTS);

		if (JOINT_INDEXES[0] < 0 || JOINT_INDEXES[0] >= mpJoints.length || mCurrentTime < 0.0f)
		{
			position.set(vertex.getLocation());
		}
		else
		{
			int numWeight = 0;
			for (int i = 0; i < 4; i++)
			{
				if (JOINT_WEIGHTS[i] > 0 && JOINT_INDEXES[i] >= 0 && JOINT_INDEXES[i] < mpJoints.length)
				{
					++numWeight;
				}
				else
				{
					break;
				}
			}

			position.zero();
			for (int i = 0; i < 4; i++)
			{
				WEIGHTS[i] = (float) JOINT_WEIGHTS[i] * 0.01f; // /100.0f
			}
			if (numWeight == 0)
			{
				numWeight = 1;
				WEIGHTS[0] = 1.0f;
			}

			for (int i = 0; i < numWeight; i++)
			{
				Joint joint = mpJoints[JOINT_INDEXES[i]];

				Matrix4f mat = joint.mMatJointAbsolute;

				Vector3f result = tmpResult;
				Vector3f pos = tmpPos;
				pos.set(vertex.getLocation());
				mat.invTransform(pos, tmp);
				joint.mMatGlobal.transform(tmp, result);

				position.x += result.x * WEIGHTS[i];
				position.y += result.y * WEIGHTS[i];
				position.z += result.z * WEIGHTS[i];
			}
		}

		return position;
	}

	/**
	 * ��䶥��Ĺ�����Ȩ����Ϣ���Ա�ͳһ���㡣
	 * 
	 * @param vertex
	 * @param jointIndexes
	 * @param jointWeights
	 */
	private void fillJointIndexesAndWeights(MS3DVertex vertex, int[] jointIndexes, int[] jointWeights)
	{
		jointIndexes[0] = vertex.getBoneID();
		if (vertex.mpBoneIndexes == null)
		{
			for (int i = 0; i < 3; i++)
			{
				jointIndexes[i + 1] = 0;
			}
		}
		else
		{
			for (int i = 0; i < 3; i++)
			{
				jointIndexes[i + 1] = vertex.mpBoneIndexes[i] & 0xff;
			}
		}

		jointWeights[0] = 100;
		for (int i = 0; i < 3; i++)
		{
			jointWeights[i + 1] = 0;
		}

		if (vertex.mpWeights != null && vertex.mpWeights[0] != 0 && vertex.mpWeights[1] != 0 && vertex.mpWeights[2] != 0)
		{
			int sum = 0;
			for (int i = 0; i < 3; i++)
			{
				jointWeights[i] = vertex.mpWeights[i] & 0xff;
				sum += jointWeights[i];
			}

			jointWeights[3] = 100 - sum;
		}
	}

	/**
	 * ��Ⱦʵ��ģ��
	 * 
	 * @param gl
	 */
	public void render(GL10 gl)
	{
		gl.glPushMatrix();
		{
			// ����Ĭ����ɫ
			gl.glColor4f(1.0f, 0.5f, 0.5f, 1.0f);

			// ���ÿͻ���״̬
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

			// �������е�MS3D Group����Ⱦÿһ��Group
			for (int i = 0; i < mpGroups.length; i++)
			{
				if (mpGroups[i].getTriangleCount() == 0)
				{
					// �����Group�����������θ���Ϊ�㣬��ֱ������
					continue;
				}
				// �õ���Ӧ����
				TextureInfo tex = mpTexInfo[i % mpTexInfo.length];

				if (tex != null)
				{
					// �������Ϊ�գ������Ӧ����
					gl.glBindTexture(GL10.GL_TEXTURE_2D, tex.mTexID);
					// ����������ͼ
					gl.glEnable(GL10.GL_TEXTURE_2D);
					// ��������������
					gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mpBufTextureCoords[i]);

				}
				else
				{
					// �������Ϊ�գ�����������ͼ
					// ��������ͻ���״̬
					gl.glDisable(GL10.GL_TEXTURE_2D);
					gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				}
				// �󶨶�������
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mpBufVertices[i]);
				// �ύ��Ⱦ
				gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mpGroups[i].getTriangleCount() * 3);
			}
			// ��Ⱦ��ϣ����ÿͻ���״̬
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
		gl.glPopMatrix();
	}

	/**
	 * ��Ⱦ����������Ϣ
	 * 
	 * @param gl
	 */
	public void renderJoints(GL10 gl)
	{
		if (!containsJoint()) { return; }
		// Ϊ��֤����ʼ�տɼ�����ʱ������Ȳ���
		gl.glDisable(GL10.GL_DEPTH_TEST);
		// ���õ���ߵĿ��
		gl.glPointSize(4.0f);
		gl.glLineWidth(2.0f);
		// �������ö�������
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// ��Ⱦ��������
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);// ������ɫ
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufJointLinePosition);
		// ����ͼ��
		gl.glDrawArrays(GL10.GL_LINES, 0, mJointLineCount);

		// ��Ⱦ�ؽڵ�
		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);// ������ɫ
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBufJointPointPosition);
		// ����ͼ��
		gl.glDrawArrays(GL10.GL_POINTS, 0, mJointPointCount);

		// ����
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPointSize(1.0f);
		gl.glLineWidth(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

	public Vector3f getSphereCenter()
	{
		return mvCenter;
	}

	public float getSphereRadius()
	{
		return mfRadius;
	}

	public boolean containsAnimation()
	{
		return mNumFrames > 0 && mpJoints != null && mpJoints.length > 0;
	}

	public boolean containsJoint()
	{
		return mpJoints != null && mpJoints.length > 0;
	}
}
