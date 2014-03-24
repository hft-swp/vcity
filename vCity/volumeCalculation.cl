typedef struct building_struct
{
    long int triangleCount;
    float* vertices;
} building;

float det(const float ax, const float ay, const float az, const float bx,
 		  const float by, const float bz, const float cx, const float cy,
  		  const float cz);

__kernel void calc(__global building* buildings,
					__global float* volumes)
{
	int gid = get_global_id(0);
	int triangleCount = buildings[gid].triangleCount;
	float* v = buildings[gid].vertices;
	float sum = 0;
	for (int i = 0; i < triangleCount * 9; i += 9) {
		float d = det(v[i + 0], v[i + 1], v[i + 2],
		     		  v[i + 3], v[i + 4], v[i + 5],
		              v[i + 6], v[i + 7], v[i + 8]);
		sum += d;
	}
	volumes[gid] = sum;
}

float det(const float ax, const float ay, const float az, const float bx,
 		  const float by, const float bz, const float cx, const float cy,
  		  const float cz) {
  return
    ax * by * cz +
    ay * bz * cx +
    az * bx * cz -
    az * by * cx -
    ay * bx * cz -
    ax * bz * cy;
}