package com.android.chapter29;

public class SkyBox extends Model
{
	public SkyBox(int radius, float dtheta, float dphi)
	{
		super();
        int vt_idx = 0, uv_idx = 0;
        float vx, vy, vz, mag;

        //calc triangle counts
        int num_vertices = (int)((360 / dtheta) * ((int)(90 / dphi) + 1) * 2);

        setMeshCount(1);

        float[] vertices0 = new float[num_vertices * 9];
        float[] uvs0 = new float[num_vertices * 6];

        setTriangleNums(num_vertices,0);

        for (float phi = 0.0f; phi <= 90.0f; phi += dphi) {
            for (float theta = -180.0f; theta < 180.0f; theta += dtheta) {
                float vertices[]=new float[12];
                float uvs[]=new float[8];
                float phi0 = phi + dphi;
                float theta0 = theta + dtheta;

                //point 0
                vertices[0] = (float) (radius * Math.sin(DTOR * theta) * Math.cos(DTOR * phi));
                vertices[1] = (float) (radius * Math.cos(DTOR * theta) * Math.cos(DTOR * phi));
                vertices[2] = (float) (radius * Math.sin(DTOR * phi));

                vx = vertices[0]; 
                vy = vertices[1]; 
                vz = vertices[2]; 
                mag = (float)Math.sqrt(SQR(vx)+SQR(vy)+SQR(vz)); 
                vx /= mag;  
                vy /= mag;  
                vz /= mag;  
                uvs[0] = (float)(Math.atan2(vx, vz)/(Math.PI * 2)) + 0.5f;    
                uvs[1] = (float)(Math.asin(vy) / Math.PI) + 0.5f;
                

                //point 1
                vertices[3] = (float) (radius * Math.sin(DTOR * theta) * Math.cos(DTOR * (phi + dphi)));
                vertices[4] = (float) (radius * Math.cos(DTOR * theta) * Math.cos(DTOR * (phi + dphi)));
                vertices[5] = (float) (radius * Math.sin(DTOR * (phi + dphi)));

                vx = vertices[3]; 
                vy = vertices[4]; 
                vz = vertices[5]; 
                mag = (float)Math.sqrt(SQR(vx)+SQR(vy)+SQR(vz)); 
                vx /= mag;  
                vy /= mag;  
                vz /= mag;  
                uvs[2] = (float)(Math.atan2(vx, vz)/(Math.PI * 2)) + 0.5f;    
                uvs[3] = (float)(Math.asin(vy) / Math.PI) + 0.5f;

                //point 2
                vertices[6] = (float) (radius * Math.sin(DTOR * theta0) * Math.cos(DTOR * phi0));
                vertices[7] = (float) (radius * Math.cos(DTOR * theta0) * Math.cos(DTOR * phi0));
                vertices[8] = (float) (radius * Math.sin(DTOR * phi0));

                vx = vertices[6]; 
                vy = vertices[7]; 
                vz = vertices[8]; 
                mag = (float)Math.sqrt(SQR(vx)+SQR(vy)+SQR(vz)); 
                vx /= mag;  
                vy /= mag;  
                vz /= mag;  
                uvs[4] = (float)(Math.atan2(vx, vz)/(Math.PI * 2)) + 0.5f;    
                uvs[5] = (float)(Math.asin(vy) / Math.PI) + 0.5f;

                //point 3
                vertices[9] = (float) (radius * Math.sin(DTOR * theta0) * Math.cos(DTOR * (phi0 + dphi)));
                vertices[10] = (float) (radius * Math.cos(DTOR * theta0) * Math.cos(DTOR * (phi0 + dphi)));
                vertices[11] = (float) (radius * Math.sin(DTOR * (phi0 + dphi)));

                vx = vertices[9]; 
                vy = vertices[10]; 
                vz = vertices[11]; 
                mag = (float)Math.sqrt(SQR(vx)+SQR(vy)+SQR(vz)); 
                vx /= mag;  
                vy /= mag;  
                vz /= mag;  
                uvs[6] = (float)(Math.atan2(vx, vz)/(Math.PI * 2)) + 0.5f;    
                uvs[7] = (float)(Math.asin(vy) / Math.PI) + 0.5f;
                
                
                vertices0[vt_idx++] = vertices[0];
                vertices0[vt_idx++] = vertices[1];
                vertices0[vt_idx++] = vertices[2];

                uvs0[uv_idx++] = uvs[0];
                uvs0[uv_idx++] = uvs[1];

                vertices0[vt_idx++] = vertices[3];
                vertices0[vt_idx++] = vertices[4];
                vertices0[vt_idx++] = vertices[5];

                uvs0[uv_idx++] = uvs[2];
                uvs0[uv_idx++] = uvs[3];

                vertices0[vt_idx++] = vertices[6];
                vertices0[vt_idx++] = vertices[7];
                vertices0[vt_idx++] = vertices[8];

                uvs0[uv_idx++] = uvs[4];
                uvs0[uv_idx++] = uvs[5];

                //////
                vertices0[vt_idx++] = vertices[3];
                vertices0[vt_idx++] = vertices[4];
                vertices0[vt_idx++] = vertices[5];

                uvs0[uv_idx++] = uvs[2];
                uvs0[uv_idx++] = uvs[3];

                vertices0[vt_idx++] = vertices[6];
                vertices0[vt_idx++] = vertices[7];
                vertices0[vt_idx++] = vertices[8];

                uvs0[uv_idx++] = uvs[4];
                uvs0[uv_idx++] = uvs[5];

                vertices0[vt_idx++] = vertices[9];
                vertices0[vt_idx++] = vertices[10];
                vertices0[vt_idx++] = vertices[11];

                uvs0[uv_idx++] = uvs[6];
                uvs0[uv_idx++] = uvs[7];
            }
        }

        setVertices(vertices0, num_vertices * 9,0);
        setUvs(uvs0, num_vertices * 6,0);

        if ( vertices0 != null )
		{
        	vertices0 = null;
		}
        if ( uvs0 != null )
		{
        	uvs0 = null;
		}

		setPosition(0.0f, (float)(-radius * Math.sin(DTOR * dphi)), 0.0f);
        setRotate(-90.0f, 0.0f, 0.0f);	
	}
	
	
	public float SQR(float x)
	{
		return x*x;
	}

}
