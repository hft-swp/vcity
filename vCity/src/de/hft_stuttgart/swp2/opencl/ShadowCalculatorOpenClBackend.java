package de.hft_stuttgart.swp2.opencl;

import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clSetKernelArg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_event;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public class ShadowCalculatorOpenClBackend extends ShadowCalculatorInterface {

	private static final int TRIANGLE_COUNT = 1000000;

	private static final float MAX_DISTANCE = 100;

	private static final String filename = "shadowCalculation.cl";
	private OpenClContext occ;

	public ShadowCalculatorOpenClBackend() throws OpenClException {
		occ = OpenClContext.getInstance();
	}

	@Override
	public void calculateShadow(ShadowPrecision precision) {
		calculateCenterOfBuildingsAndCity();
		recalculateShadowTriangles(precision);

		cl_kernel kernel = occ.createKernelFromFile(filename);
		cl_command_queue commandQueue = occ.getClCommandQueue();
		cl_context context = occ.getClContext();

		City city = City.getInstance();

		int verticesSize = 0;
		for (Building b : city.getBuildings()) {
			verticesSize += b.getTriangles().size() * 9;
		}
		if (verticesSize == 0) {
			return;
		}
		float[] cityVertices = new float[verticesSize];
		int offset = 0;
		for (Building b : city.getBuildings()) {
			for (int j = 0; j < b.getTriangles().size(); j++) {
				Triangle t = b.getTriangles().get(j);
				cityVertices[offset + j * 9 + 0] = t.getVertices()[0].getX();
				cityVertices[offset + j * 9 + 1] = t.getVertices()[0].getY();
				cityVertices[offset + j * 9 + 2] = t.getVertices()[0].getZ();
				cityVertices[offset + j * 9 + 3] = t.getVertices()[1].getX();
				cityVertices[offset + j * 9 + 4] = t.getVertices()[1].getY();
				cityVertices[offset + j * 9 + 5] = t.getVertices()[1].getZ();
				cityVertices[offset + j * 9 + 6] = t.getVertices()[2].getX();
				cityVertices[offset + j * 9 + 7] = t.getVertices()[2].getY();
				cityVertices[offset + j * 9 + 8] = t.getVertices()[2].getZ();
			}
			offset = offset + b.getTriangles().size() * 9;
		}

		int[] cityVerticesCount = new int[city.getBuildings().size()];
		for (int i = 0; i < cityVerticesCount.length; ++i) {
			cityVerticesCount[i] = city.getBuildings().get(i).getTriangles()
					.size() * 9;
		}

		cl_mem cityVerticesMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR,
				Sizeof.cl_float * cityVertices.length,
				Pointer.to(cityVertices), null);

		cl_mem cityVerticesCountMem = clCreateBuffer(context,
				CL.CL_MEM_READ_ONLY | CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_int
						* cityVerticesCount.length,
				Pointer.to(cityVerticesCount), null);

		float[] sunDirections = getSunDirections();
		cl_mem sunDirectionsMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_float
				* sunDirections.length, Pointer.to(sunDirections), null);

		int currentBuilding = 0;
		while (currentBuilding < city.getBuildings().size()) {
			// suche gebäude zusammen solange schatten dreiecke weniger als 1 mio
			int triangleCount = 0;
			ArrayList<Building> calcBuildings = new ArrayList<Building>();
			while (triangleCount < TRIANGLE_COUNT
					&& currentBuilding < city.getBuildings().size()) {
				Building b = city.getBuildings().get(currentBuilding);
				calcBuildings.add(b);
				triangleCount += b.getShadowTriangles().size();
				currentBuilding++;
			}

			ArrayList<Integer> neighbours = new ArrayList<Integer>();
			int[] numNeighbours = new int[calcBuildings.size()];

			float[] shadowVerticeCenters = new float[triangleCount * 3];
			int[] shadowVerticeCentersCount = new int[calcBuildings.size()];

			int shadowVerticeCenterIdx = 0;
			for (int calculateBuildingIdx = 0; calculateBuildingIdx < calcBuildings
					.size(); ++calculateBuildingIdx) {
				Building b = calcBuildings.get(calculateBuildingIdx);
				// calculate neighbours
				for (int i = 0; i < city.getBuildings().size(); ++i) {
					Building neighbourBuilding = city.getBuildings().get(i);
					Vertex v = vertexDiff(b.getCenter(),
							neighbourBuilding.getCenter());
					float distance = distance(v);
					if (distance < MAX_DISTANCE) {
						neighbours.add(i);
					}
				}
				int numNeighbour = neighbours.size();
				for (int i = 0; i < calculateBuildingIdx; ++i) {
					numNeighbour -= numNeighbours[i];
				}
				numNeighbours[calculateBuildingIdx] = numNeighbour;

				for (ShadowTriangle st : b.getShadowTriangles()) {
					for (float p : st.getCenter().getCoordinates()) {
						shadowVerticeCenters[shadowVerticeCenterIdx] = p;
						shadowVerticeCenterIdx++;
					}
				}
				shadowVerticeCentersCount[calculateBuildingIdx] = b
						.getShadowTriangles().size();
			}

			// tritt nur ein für MAX_DISTANCE = 0
			if (neighbours.size() == 0) {
				continue;
			}
			int[] neigh = new int[neighbours.size()];
			for (int i = 0; i < neigh.length; ++i) {
				neigh[i] = neighbours.get(i);
			}
			System.out.println("Anzahl umgebungsgebäude: " + neigh.length);

			cl_mem buildingNeighboursMem = clCreateBuffer(context,
					CL.CL_MEM_READ_ONLY | CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_int
							* neigh.length, Pointer.to(neigh), null);
			cl_mem buildingNeighboursCountMem = clCreateBuffer(context,
					CL.CL_MEM_READ_ONLY | CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_int
							* numNeighbours.length, Pointer.to(numNeighbours),
					null);

			cl_mem shadowVerticesMem = clCreateBuffer(context,
					CL.CL_MEM_READ_ONLY | CL.CL_MEM_USE_HOST_PTR,
					Sizeof.cl_float * shadowVerticeCenters.length,
					Pointer.to(shadowVerticeCenters), null);
			cl_mem shadowTriangleCountMem = clCreateBuffer(context,
					CL.CL_MEM_READ_ONLY | CL.CL_MEM_USE_HOST_PTR,
					Sizeof.cl_int, Pointer.to(shadowVerticeCentersCount), null);

			byte[] hasShadow = new byte[18 * triangleCount];

			Pointer hasShadowPointer = Pointer.to(hasShadow);
			cl_mem hasShadowMem = clCreateBuffer(context, CL_MEM_READ_WRITE,
					Sizeof.cl_char * hasShadow.length, null, null);
			// Stadt in großen dreiecken
			clSetKernelArg(kernel, 0, Sizeof.cl_mem,
					Pointer.to(cityVerticesMem));
			// Anzahl an Dreiecken pro Gebäude
			clSetKernelArg(kernel, 1, Sizeof.cl_mem,
					Pointer.to(cityVerticesCountMem));

			// Indizes der Nachbarn der zu rechnenden Gebäuden
			clSetKernelArg(kernel, 2, Sizeof.cl_mem,
					Pointer.to(buildingNeighboursMem));

			// Anzahl der Nachbarn pro Gebäude
			clSetKernelArg(kernel, 3, Sizeof.cl_mem,
					Pointer.to(buildingNeighboursCountMem));

			// Alle Schattendreiecksmitten
			clSetKernelArg(kernel, 4, Sizeof.cl_mem,
					Pointer.to(shadowVerticesMem));
			// Schattendreiecksmittenanzahl pro Gebäude
			clSetKernelArg(kernel, 5, Sizeof.cl_mem,
					Pointer.to(shadowTriangleCountMem));
			// Anzahl der Schattendreiecken
			clSetKernelArg(kernel, 6, Sizeof.cl_int, Pointer.to(new int[] {calcBuildings.size()}));

			// Sonnenrichtungs vektoren
			clSetKernelArg(kernel, 7, Sizeof.cl_mem,
					Pointer.to(sunDirectionsMem));

			// Ergebnis array
			clSetKernelArg(kernel, 8, Sizeof.cl_mem, Pointer.to(hasShadowMem));

			// actual work size
			clSetKernelArg(kernel, 9, Sizeof.cl_int,
					Pointer.to(new int[] { shadowVerticeCenters.length / 3 }));

			cl_device_id device = occ.getDevice();
			long[] kernelWorkSize = new long[1];

			CL.clGetKernelWorkGroupInfo(kernel, device,
					CL.CL_KERNEL_WORK_GROUP_SIZE, Sizeof.size_t,
					Pointer.to(kernelWorkSize), null);
			int localWorkSize = (int) kernelWorkSize[0];

			int workSize = ((shadowVerticeCenters.length / 3) / localWorkSize + 1)
					* localWorkSize;
			System.out.println(workSize);
			long global_work_size[] = new long[] { workSize };
			long local_work_size[] = new long[] { localWorkSize };

			// Execute the kernel
			cl_event kernelEvent = new cl_event();

			clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
					global_work_size, local_work_size, 0, null, kernelEvent);

			// Read the output data
			clEnqueueReadBuffer(commandQueue, hasShadowMem, CL_TRUE, 0,
					hasShadow.length * Sizeof.cl_char, hasShadowPointer, 0,
					null, null);

			// wait for the kernel to finish
			CL.clFinish(commandQueue);

			occ.profile(kernelEvent);

			int count = 0;
			// BitSet bs = BitSet.valueOf(hasShadow);
			for (Building b : calcBuildings) {
				for (ShadowTriangle st : b.getShadowTriangles()) {
					// BitSet new_bs = bs.get(count*144, (count+1)*144);
					BitSet new_bs = new BitSet(144);
					for (int i = 0; i < 144; i++) {
						if ((hasShadow[count * 18 + i / 8] & (1 << 7 - i % 8)) > 0) {
							new_bs.set(i, true);
						} else {
							new_bs.set(i, false);
						}
					}
					st.setShadowSet(new_bs);
					count++;
				}
			}
			System.out.println(Arrays.toString(cityVerticesCount));
			System.out.println("Count = "+ count);

			clReleaseMemObject(shadowVerticesMem);
			clReleaseMemObject(shadowTriangleCountMem);
			clReleaseMemObject(hasShadowMem);
			clReleaseMemObject(buildingNeighboursMem);
			clReleaseMemObject(buildingNeighboursCountMem);
		}

		// Release kernel, program, and memory objects
		clReleaseMemObject(cityVerticesMem);
		clReleaseMemObject(sunDirectionsMem);
		clReleaseMemObject(cityVerticesCountMem);

		occ.finalizeKernel(kernel);

	}

	private float[] getSunDirections() {
		float[] sunDirections = new float[144 * 3];
		int sunDirectionsCount = 0;
		Vertex[] sunDirectionsV = calcDirections();
		for (int sunDirectionIdx = 0; sunDirectionIdx < sunDirectionsV.length; ++sunDirectionIdx) {
			Vertex v = sunDirectionsV[sunDirectionIdx];
			for (float p : v.getCoordinates()) {
				sunDirections[sunDirectionsCount] = p;
				sunDirectionsCount++;
			}
		}
		return sunDirections;
	}

	private int[] getNeighbourhood(Building b) {
		City city = City.getInstance();
		ArrayList<Integer> neighbourhood = new ArrayList<>();
		for (int i = 0; i < city.getBuildings().size(); ++i) {
			Building neighbourBuilding = city.getBuildings().get(i);
			if (i == 4000) {
				System.out.println();
			}
			Vertex v = vertexDiff(b.getCenter(), neighbourBuilding.getCenter());
			float distance = distance(v);
			if (distance < MAX_DISTANCE) {
				neighbourhood.add(i);
			}
		}
		int[] neigh = new int[neighbourhood.size()];
		for (int i = 0; i < neigh.length; ++i) {
			neigh[i] = neighbourhood.get(i);
		}
		return neigh;
	}

	private float distance(Vertex v) {
		return (float) Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY()
				+ v.getZ() * v.getZ());
	}

	private void calculateCenterOfBuildingsAndCity() {
		float xCity = 0, yCity = 0, zCity = 0;
		for (Building b : City.getInstance().getBuildings()) {
			float x = 0, y = 0, z = 0;
			int count = 0;
			for (Triangle t : b.getTriangles()) {
				for (Vertex v : t.getVertices()) {
					x += v.getX();
					y += v.getY();
					z += v.getZ();
					count++;
				}
			}
			xCity += x / count;
			yCity += y / count;
			zCity += z / count;
			b.setCenter(new Vertex(x / count, y / count, z / count));
		}
		int buildingCount = City.getInstance().getBuildings().size();
		City.getInstance().setCenter(
				new Vertex(xCity / buildingCount, yCity / buildingCount, zCity
						/ buildingCount));
	}

}
