package com.android.chapter24;

import java.nio.ByteBuffer;

public class Texture
{
	// ͼ������
    ByteBuffer imageData;
    // ͼ����ÿ�����ص���ɫ���
    int bpp;	
    // ��Ⱥ͸߶�
    int width;											
    int height;		
    // ��ͼID
    int[] texID = new int[1];		
    // ͼ�����ݵ����ͣ�rgb ���� argb��
    int type;
}

