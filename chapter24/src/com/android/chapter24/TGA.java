package com.android.chapter24;

import java.nio.ByteBuffer;

public class TGA
{
	// TGA文件的前六个字节
    ByteBuffer header = BufferUtil.newByteBuffer(6);		
    // 每像素的字节数 (3 或 4)
    int bytesPerPixel;	
    // 控制存储图像所需的内存空间
    int imageSize;								
    int temp;	
    // 图像类型 GL_RGB 或 GL_RGBA
    int type;
    // 图像数据的宽度和高度
    int height;									
    int width;									
    // 每像素的比特数 (24 或 32)
    int bpp;									
}

