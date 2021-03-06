inline char rayIntersectsTriangle(float3 p, float3 sunDirection, float3 v0, float3 v1, float3 v2) {
	float3 e1 = v1 - v0;
	float3 e2 = v2 - v0;
			
	float3 h = cross(sunDirection, e2);
			
	float a = dot(e1, h);
			
	if ((a > -0.00001f) && (a < 0.00001f)) {
		return 0;
	}
			
	float f = 1 / a;
	
	float3 s2 = p - v0;
	
	float u = f * dot(s2, h);
	
	if ((u < 0.0f) || (u > 1.0f)) {
		return 0;
	}
	
	float3 q = cross(s2, e1);
	
	float v = f * dot(sunDirection, q);
	if (!((v < 0.0f) || ((u + v) > 1.0f))) {
		float3 uv = v0 + e1 * u + e2 * v;
		float3 s = uv - p;
			
		if (dot(s, sunDirection) > 0.01f) {
			return 1;
		}
	}
	return 0;
}

__kernel  
void calc(__global const float* cityVertices,
				   __global const int* cityVerticesCount,
				   __global const int* neighbours,
				   __global const int* neighboursCount,
				   __global const float* shadowVerticeCenters,
				   __global const int* shadowVerticeCentersCount,
				   __const int shadowBuildingsCount,
				   __global const float* shadowTriangleNormals,
				   __global const float* sunDirections,
				   __global char* hasShadow, // (144/8)*shadowTrianglesCount char
				   __const int workSize,
				   __const int skymodel) 
{
	int gid = get_global_id(0);
	if (gid >= workSize) {
		return;
	}
	
	float3 v0, v1, v2, sunDirection, p;

	int gid3 = gid*3; 
	p = (float3)(shadowVerticeCenters[gid3], shadowVerticeCenters[gid3+1], shadowVerticeCenters[gid3+2]);
	int buildingIdx = 0;
	int buildingTriangle = gid;
	for (int i = 0; i < shadowBuildingsCount; ++i) {
		int tempBuildingTriangle = buildingTriangle - shadowVerticeCentersCount[i];
		if (tempBuildingTriangle < 0) {
			break;
		}
		buildingTriangle = tempBuildingTriangle;
		++buildingIdx;
	}
	
	int neighbourOffset = 0;
	for (int i = 0; i < buildingIdx; ++i) {
		neighbourOffset += neighboursCount[i];
	}
	 
	for (int i = 0; i < skymodel; i++) {
		sunDirection = (float3)(sunDirections[i*3], sunDirections[i*3+1], sunDirections[i*3+2]);
		float3 n = (float3)(shadowTriangleNormals[gid3], shadowTriangleNormals[gid3+1], shadowTriangleNormals[gid3+2]);
		float l0 = fast_length(sunDirection);
		float l1 = fast_length(n);
		float c = dot(sunDirection, n) / (l0 * l1);
		if (c < 0) {
			hasShadow[gid*(skymodel/8)+i/8] |= (1 << (7-i%8));
			continue;
		}
		
		int breakInt = 0;
		for (int neighbourIdx = neighbourOffset; neighbourIdx < neighbourOffset + neighboursCount[buildingIdx]; ++neighbourIdx) {
			int offset = 0;
			for (int j = 0; j < neighbours[neighbourIdx]; ++j) {
				offset += cityVerticesCount[j];
			}
			
			
			for (int cityIdx = offset; cityIdx < offset + cityVerticesCount[neighbours[neighbourIdx]]; cityIdx += 9) {
				v0 = (float3)(cityVertices[cityIdx], cityVertices[cityIdx+1], cityVertices[cityIdx+2]);
				v1 = (float3)(cityVertices[cityIdx+3], cityVertices[cityIdx+4], cityVertices[cityIdx+5]);
				v2 = (float3)(cityVertices[cityIdx+6], cityVertices[cityIdx+7], cityVertices[cityIdx+8]);
				char res = rayIntersectsTriangle(p, sunDirection, v0, v1, v2);
				if (res == 1) {
					hasShadow[gid*(skymodel/8)+i/8] |= (1 << (7-i%8));
					breakInt = 1;
					break;
				} else {
					int mask = 255 - (1 << (7-i%8));
					hasShadow[gid*(skymodel/8)+i/8] &= mask;
				}
			}
			
			if (breakInt == 1) {
				break;
			}
		}		
	}  
}