float det(const float ax, const float ay, const float az, const float bx,
 		  const float by, const float bz, const float cx, const float cy,
  		  const float cz);

__kernel void calc(__global float* vertices,
				   __global int* trianglesCount,
				   __global float* volumes)
{
	int gid = get_global_id(0);
	int offset = 0;
	for (int i = 0; i < gid; i++) {
		offset = offset + trianglesCount[i] * 9;
	}
	float sum = 0;
	for (int i = offset; i < offset + trianglesCount[gid] * 9; i += 9) {
		float d = det(vertices[i + 0], vertices[i + 1], vertices[i + 2],
		     		  vertices[i + 3], vertices[i + 4], vertices[i + 5],
		              vertices[i + 6], vertices[i + 7], vertices[i + 8]);
		sum += d;
	}
	volumes[gid] = sum / 6;
	if (volumes[gid] < 0) {
		volumes[gid] = volumes[gid] * (-1);
	}
}

float det(const float ax, const float ay, const float az, const float bx,
 		  const float by, const float bz, const float cx, const float cy,
  		  const float cz) {
  return
  	(- cx * by * az
  	 + bx * cy * az
  	 + cx * ay * bz
  	 - ax * cy * bz
  	 - bx * ay * cz
  	 + ax * by * cz);
}