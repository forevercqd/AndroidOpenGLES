package com.android.chapter24;

import java.nio.ByteBuffer;

public class TGA
{
	// TGA�ļ���ǰ�����ֽ�
    ByteBuffer header = BufferUtil.newByteBuffer(6);		
    // ÿ���ص��ֽ��� (3 �� 4)
    int bytesPerPixel;	
    // ���ƴ洢ͼ��������ڴ�ռ�
    int imageSize;								
    int temp;	
    // ͼ������ GL_RGB �� GL_RGBA
    int type;
    // ͼ�����ݵĿ�Ⱥ͸߶�
    int height;									
    int width;									
    // ÿ���صı����� (24 �� 32)
    int bpp;									
}

