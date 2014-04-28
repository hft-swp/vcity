#define LOCAL_GROUP_XDIM 1024
/* typedef struct tag_vertex {
	float x;
	float y;
	float z;
} Vertex;

Vertex vertexDiff(Vertex v1, Vertex v0) {
	Vertex v2;
	v2.x = v1.x - v0.x;
	v2.y = v1.y - v0.y;
	v2.z = v1.z - v0.z;
	return v2;
}

Vertex crossproduct(Vertex v0, Vertex v1) {
	Vertex v2;
	v2.x = v0.y * v1.z - v0.z * v1.y;
	v2.y = v0.z * v1.x - v0.x * v1.z;
	v2.z = v0.x * v1.y - v0.y * v1.x;
	return v2;
}

float dot(Vertex v0, Vertex v1) {
	return v0.x * v1.x + v0.y * v1.y + v0.z * v1.z;
}*/

char rayIntersectsTriangle(float3 p, float3 sunDirection, float3 v0, float3 v1, float3 v2) {
	float3 e1 = v1 - v0;
	float3 e2 = v2 - v0;
			
	float3 h = cross(sunDirection, e2);
			
	float a = dot(e1, h);
			
	if ((a > -0.00001) && (a < 0.00001)) {
		return 0;
	}
			
	float f = 1 / a;
	
	float3 s2 = p - v0;
	
	float u = f * dot(s2, h);
	
	if ((u < 0.0) || (u > 1.0)) {
		return 0;
	}
	
	float3 q = cross(s2, e1);
	
	float v = f * dot(sunDirection, q);
	if (!((v < 0.0) || ((u + v) > 1.0))) {
		float3 uv = v0 + e1 * u + e2 * v;
		float3 s = uv - p;
			
		if (dot(s, sunDirection) > 0.01) {
			return 1;
		}
	}
	return 0;
}
// __attribute__((reqd_work_group_size(LOCAL_GROUP_XDIM, 1, 1)))
__kernel  
void calc(__global float* cityVertices,
				   __global int* cityVerticesCount,
				   __global float* shadowVerticeCenters,
				   __global int* shadowVerticeCentersCount,
				   __global float* sunDirections,
				   __global char* hasShadow, // (144/8)*shadowTrianglesCount char
				   __const int workSize) 
{
	int gid = get_global_id(0);
	if (gid >= workSize) {
		gid = 0;
	}
	
	float3 v0, v1, v2, sunDirection, p;
	
	p = (float3)(shadowVerticeCenters[gid*3], shadowVerticeCenters[gid*3+1], shadowVerticeCenters[gid*3+2]);
	for (int i = 0; i < 144; i++) {
		sunDirection = (float3)(sunDirections[i*3], sunDirections[i*3+1], sunDirections[i*3+2]);
		// skalarprodukt < 0 rausschmeißen
		for (int cityIdx = 0; cityIdx < cityVerticesCount[0]; cityIdx += 9) {
			v0 = (float3)(cityVertices[cityIdx], cityVertices[cityIdx+1], cityVertices[cityIdx+2]);
			v1 = (float3)(cityVertices[cityIdx+3], cityVertices[cityIdx+4], cityVertices[cityIdx+5]);
			v2 = (float3)(cityVertices[cityIdx+6], cityVertices[cityIdx+7], cityVertices[cityIdx+8]);
			char res = rayIntersectsTriangle(p, sunDirection, v0, v1, v2);
			if (res == 1) {
				hasShadow[gid*18+i/8] |= (1 << (7-i%8));
//				hasShadow[gid*18+i/8] |= (1 << i%8);
//				hasShadow[gid*18+i/8] = 0;
				break;
			} else {
				int mask = 255 - (1 << (7-i%8));
				hasShadow[gid*18+i/8] &= mask;
			}
		}
	}  



	/*
	for(int cityIdx = 0; cityIdx < cityVerticesCount[0]; cityIdx += 9) {
		v0 = (float3)(cityVertices[cityIdx], cityVertices[cityIdx+1], cityVertices[cityIdx+2]);
		v1 = (float3)(cityVertices[cityIdx+3], cityVertices[cityIdx+4], cityVertices[cityIdx+5]);
		v2 = (float3)(cityVertices[cityIdx+6], cityVertices[cityIdx+7], cityVertices[cityIdx+8]);
		float3 e1 = v1 - v0;
		float3 e2 = v2 - v0;
		
		for(int i = 0; i < 144; i++) {
			sunDirection = (float3)(sunDirections[i*3], sunDirections[i*3+1], sunDirections[i*3+2]);
			
			float3 h = cross(sunDirection, e2);
			
			float a = dot(e1, h);
			
			if ((a > -0.00001) && (a < 0.00001)) {
				continue;
			}
			
			float f = 1 / a;
	
			float3 s2 = p - v0;
	
			float u = f * dot(s2, h);
	
			if ((u < 0.0) || (u > 1.0)) {
				continue;
			}
	
			float3 q = cross(s2, e1);
	
			float v = f * dot(sunDirection, q);
	
			if (!((v < 0.0) || ((u + v) > 1.0))) {
				float3 uv = v0+e1*u+e2*v;
				float3 s = uv - p;
				
				if (dot(s, sunDirection) > 0.01) {
					hasShadow[gid*18+i/8] |= (1 << (7-i%8));
				}
			}
		}
	} */
}