package de.hft_stuttgart.swp2.opencl;

import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clSetKernelArg;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_event;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;

import de.hft_stuttgart.swp2.model.BoundarySurface;
import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Polygon;
import de.hft_stuttgart.swp2.model.ShadowTriangle;
import de.hft_stuttgart.swp2.model.Triangle;
import de.hft_stuttgart.swp2.model.Vertex;

public class ShadowCalculatorOpenClBackend extends ShadowCalculatorInterface {

	/**
	 * This is the minimum count of triangles which are calculated at once on
	 * the gpu
	 */
	private static final int TRIANGLE_COUNT = 1000;

	/**
	 * This is the maximum distance in which neighbours are found
	 */
	private static final float MAX_DISTANCE = 80;

	private static final String filename = "shadowCalculation.cl";
	private OpenClContext occ;

	/**
	 * This creates the backend for calculating the shadow on the gpu.
	 * 
	 * @throws OpenClException
	 *             if the context creation fails
	 */
	public ShadowCalculatorOpenClBackend() throws OpenClException {
		occ = OpenClContext.getInstance();
	}
	
	/**
	 * @deprecated Please use {@link ShadowCalculatorOpenClBackend#calculateShadow(ShadowPrecision, int, int)}
	 */
	public void calculateShadow(ShadowPrecision precision) {
		calculateShadow(precision, 12, 12);
	}
	
	/**
	 * 
	 * This method calculates the shadow of each triangle on the gpu.
	 * 
	 * @param precicion
	 *            the precicsion which determins the maximum area a shadow
	 *            triangle may have
	 * @param splitAzimuth split the Azimuth angle in given parts
	 * @param splitHeight split the Height angle in given parts
	 */
	@Override
	public void calculateShadow(ShadowPrecision precision, int splitAzimuth, int splitHeight) {
		City city = City.getInstance();
		// if no buildings are available, abort
		if (city.getBuildings().size() == 0) {
			return;
		}
		calculateCenterOfBuildingsAndCity();
		recalculateShadowTriangles(precision);

		cl_kernel kernel = occ.createKernelFromFile(filename);
		cl_command_queue commandQueue = occ.getClCommandQueue();
		cl_context context = occ.getClContext();

		// float[] cityVertices = getCityVerticesArray();
		// int[] cityVerticesCount = getCityVerticesCountArray();
		//
		// cl_mem cityVerticesMem = storeOnGPUAsReadOnly(context, cityVertices);
		// cl_mem cityVerticesCountMem = storeOnGPUAsReadOnly(context,
		// cityVerticesCount);

		float[] sunDirections = getSunDirections(splitAzimuth, splitHeight);
		cl_mem sunDirectionsMem = storeOnGPUAsReadOnly(context, sunDirections);

		int currentBuilding = 0;
		while (currentBuilding < city.getBuildings().size()) {
			// suche geb�ude zusammen solange schatten dreiecke weniger als 1
			// mio
			int triangleCount = 0;
			ArrayList<Building> calcBuildings = new ArrayList<Building>();
			while (triangleCount < TRIANGLE_COUNT
					&& currentBuilding < city.getBuildings().size()) {
				Building b = city.getBuildings().get(currentBuilding);
				for (BoundarySurface surface : b.getBoundarySurfaces()) {
					for (Polygon p : surface.getPolygons()) {
						triangleCount += p.getShadowTriangles().size();
					}
				}
				calcBuildings.add(b);
				currentBuilding++;
			}

			ArrayList<Integer> neighbours = new ArrayList<Integer>();
			int[] numNeighbours = new int[calcBuildings.size()];

			float[] shadowVerticeCenters = new float[triangleCount * 3];
			float[] shadowTriangleNormals = new float[triangleCount * 3];
			int[] shadowVerticeCentersCount = new int[calcBuildings.size()];

			int shadowVerticeCenterIdx = 0;
			HashMap<Integer, Building> cityBuildings = new HashMap<Integer, Building>();
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
						cityBuildings.put(i, neighbourBuilding);
					}
				}
				int numNeighbour = neighbours.size();
				for (int i = 0; i < calculateBuildingIdx; ++i) {
					numNeighbour -= numNeighbours[i];
				}
				numNeighbours[calculateBuildingIdx] = numNeighbour;

				int shadowTriangleCount = 0;
				for (BoundarySurface surface : b.getBoundarySurfaces()) {
					for (Polygon p : surface.getPolygons()) {
						for (ShadowTriangle st : p.getShadowTriangles()) {
							shadowTriangleNormals[shadowVerticeCenterIdx + 0] = st
									.getNormalVector().getX();
							shadowTriangleNormals[shadowVerticeCenterIdx + 1] = st
									.getNormalVector().getY();
							shadowTriangleNormals[shadowVerticeCenterIdx + 2] = st
									.getNormalVector().getZ();
							for (float coordinate : st.getCenter()
									.getCoordinates()) {
								shadowVerticeCenters[shadowVerticeCenterIdx] = coordinate;
								shadowVerticeCenterIdx++;
							}
							shadowTriangleCount++;
						}
					}
				}
				shadowVerticeCentersCount[calculateBuildingIdx] = shadowTriangleCount;
			}
			HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
			ArrayList<Building> shadowCaster = new ArrayList<Building>();

			for (Entry<Integer, Building> e : cityBuildings.entrySet()) {
				int toIndex = shadowCaster.size();
				shadowCaster.add(e.getValue());
				indexMap.put(e.getKey(), toIndex);
			}

			int[] cityVerticesCount = getCityVerticesCountArray(shadowCaster);
			float[] cityVertices = getCityVerticesArray(shadowCaster, cityVerticesCount);

			cl_mem cityVerticesMem = storeOnGPUAsReadOnly(context, cityVertices);
			cl_mem cityVerticesCountMem = storeOnGPUAsReadOnly(context,
					cityVerticesCount);

			// tritt nur ein f�r MAX_DISTANCE = 0
			if (neighbours.size() == 0) {
				continue;
			}
			int[] neigh = new int[neighbours.size()];
			for (int i = 0; i < neigh.length; ++i) {
				neigh[i] = indexMap.get(neighbours.get(i));
			}
			// System.out.println("Umgebungsgeb�ude: " +
			// Arrays.toString(neigh));
			System.out.println("Anzahl umgebungsgeb�ude: " + neigh.length);

			cl_mem buildingNeighboursMem = storeOnGPUAsReadOnly(context, neigh);
			cl_mem buildingNeighboursCountMem = storeOnGPUAsReadOnly(context,
					numNeighbours);
			cl_mem shadowVerticesMem = storeOnGPUAsReadOnly(context,
					shadowVerticeCenters);
			cl_mem shadowTriangleCountMem = storeOnGPUAsReadOnly(context,
					shadowVerticeCentersCount);
			cl_mem shadowTriangleNormalsMem = storeOnGPUAsReadOnly(context,
					shadowTriangleNormals);

			byte[] hasShadow = new byte[(int) (Math.ceil(splitAzimuth * splitHeight / 8.0) * triangleCount)];

			Pointer hasShadowPointer = Pointer.to(hasShadow);
			cl_mem hasShadowMem = clCreateBuffer(context, CL_MEM_READ_WRITE,
					Sizeof.cl_char * hasShadow.length, null, null);

			// Stadt in gro�en dreiecken
			clSetKernelArg(kernel, 0, Sizeof.cl_mem,
					Pointer.to(cityVerticesMem));
			// Anzahl an Dreiecken pro Geb�ude
			clSetKernelArg(kernel, 1, Sizeof.cl_mem,
					Pointer.to(cityVerticesCountMem));

			// Indizes der Nachbarn der zu rechnenden Geb�uden
			clSetKernelArg(kernel, 2, Sizeof.cl_mem,
					Pointer.to(buildingNeighboursMem));

			// Anzahl der Nachbarn pro Geb�ude
			clSetKernelArg(kernel, 3, Sizeof.cl_mem,
					Pointer.to(buildingNeighboursCountMem));

			// Alle Schattendreiecksmitten der zu rechnenden Geb�uden
			clSetKernelArg(kernel, 4, Sizeof.cl_mem,
					Pointer.to(shadowVerticesMem));
			// Schattendreiecksmittenanzahl pro Geb�ude
			clSetKernelArg(kernel, 5, Sizeof.cl_mem,
					Pointer.to(shadowTriangleCountMem));
			// Anzahl der der zu rechneden Geb�ude
			clSetKernelArg(kernel, 6, Sizeof.cl_int,
					Pointer.to(new int[] { calcBuildings.size() }));

			// Normalenvektoren der dreiecke
			clSetKernelArg(kernel, 7, Sizeof.cl_mem,
					Pointer.to(shadowTriangleNormalsMem));

			// Sonnenrichtungs vektoren
			clSetKernelArg(kernel, 8, Sizeof.cl_mem,
					Pointer.to(sunDirectionsMem));

			// Ergebnis array
			clSetKernelArg(kernel, 9, Sizeof.cl_mem, Pointer.to(hasShadowMem));

			// actual work size
			clSetKernelArg(kernel, 10, Sizeof.cl_int,
					Pointer.to(new int[] { shadowVerticeCenters.length / 3 }));

			// Azimuthwinkel * H�henwinkel
			clSetKernelArg(kernel, 11, Sizeof.cl_int,
					Pointer.to(new int[] { splitAzimuth * splitHeight }));

			cl_device_id device = occ.getDevice();
			long[] kernelWorkSize = new long[1];
			CL.clGetKernelWorkGroupInfo(kernel, device,
					CL.CL_KERNEL_WORK_GROUP_SIZE, Sizeof.size_t,
					Pointer.to(kernelWorkSize), null);
			int localWorkSize = (int) kernelWorkSize[0];
			int workSize = ((shadowVerticeCenters.length / 3) / localWorkSize + 1)
					* localWorkSize;
			System.out.println("Worksize = " + workSize);
			System.out.println("Actual Worksize = "
					+ shadowVerticeCenters.length / 3);
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

			writeShadowDataIntoTriangles(calcBuildings, hasShadow, splitAzimuth, splitHeight);
			// System.out.println(Arrays.toString(cityVerticesCount));

			clReleaseMemObject(shadowVerticesMem);
			clReleaseMemObject(shadowTriangleCountMem);
			clReleaseMemObject(hasShadowMem);
			clReleaseMemObject(buildingNeighboursMem);
			clReleaseMemObject(buildingNeighboursCountMem);
			clReleaseMemObject(cityVerticesCountMem);
			clReleaseMemObject(cityVerticesMem);
			clReleaseMemObject(shadowTriangleNormalsMem);
		}

		// Release kernel, program, and memory objects
		clReleaseMemObject(sunDirectionsMem);

		occ.finalizeKernel(kernel);

	}

	private void writeShadowDataIntoTriangles(
			ArrayList<Building> calcBuildings, byte[] hasShadow, int splitAzimuth, int splitHeight) {
		int count = 0;
		int skymodel = splitAzimuth * splitHeight;
		// BitSet bs = BitSet.valueOf(hasShadow);
		for (Building b : calcBuildings) {
			for (BoundarySurface surface : b.getBoundarySurfaces()) {
				for (Polygon p : surface.getPolygons()) {
					double [] percentageShadow = new double[skymodel];
					double shadow = 1.0/p.getShadowTriangles().size();
					for (ShadowTriangle st : p.getShadowTriangles()) {
						// BitSet new_bs = bs.get(count*144, (count+1)*144);
						BitSet new_bs = new BitSet(skymodel);
						for (int i = 0; i < skymodel; i++) {
							if ((hasShadow[count * (int)Math.ceil(skymodel / 8) + i / 8] & (1 << 7 - i % 8)) > 0) {
								new_bs.set(i, true);
								percentageShadow[i]+= shadow;
							} else {
								new_bs.set(i, false);
							}
						}
						st.setShadowSet(new_bs);
						count++;
					}
					p.setPercentageShadow(percentageShadow);
				}
			}
		}
	}

	private cl_mem storeOnGPUAsReadOnly(cl_context context, int[] array) {
		return clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_int * array.length,
				Pointer.to(array), null);
	}

	private cl_mem storeOnGPUAsReadOnly(cl_context context, float[] array) {
		return clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_float * array.length,
				Pointer.to(array), null);
	}

	private int[] getCityVerticesCountArray(ArrayList<Building> buildings) {
		int[] cityVerticesCount = new int[buildings.size()];
		for (int i = 0; i < cityVerticesCount.length; ++i) {
			int triangleCount = 0;
			Building b = buildings.get(i);
			for (BoundarySurface surface : b.getBoundarySurfaces()) {
				for (Polygon p : surface.getPolygons()) {
					triangleCount += p.getTriangles().size();
				}
			}
			cityVerticesCount[i] = triangleCount * 9;
		}
		return cityVerticesCount;
	}

	private float[] getCityVerticesArray(ArrayList<Building> buildings, int[] cityVerticesCount) {
		int verticesSize = 0;
		for (int i = 0; i < cityVerticesCount.length; ++i) {
			verticesSize += cityVerticesCount[i];
		}
		float[] cityVertices = new float[verticesSize];
		int offset = 0;
		for (Building b : buildings) {
			for (BoundarySurface surface : b.getBoundarySurfaces()) {
				for (Polygon p : surface.getPolygons()) {
					for (int j = 0; j < p.getTriangles().size(); j++) {
						Triangle t = p.getTriangles().get(j);
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
					offset += p.getTriangles().size() * 9;
				}
			}
		}
		return cityVertices;
	}

	private float[] getSunDirections(int splitAzimuth, int splitHeight) {
		float[] sunDirections = new float[(splitAzimuth* splitHeight) * 3];
		int sunDirectionsCount = 0;
		Vertex[] sunDirectionsV = calcDirections(splitAzimuth, splitHeight);
		for (int sunDirectionIdx = 0; sunDirectionIdx < sunDirectionsV.length; ++sunDirectionIdx) {
			Vertex v = sunDirectionsV[sunDirectionIdx];
			for (float p : v.getCoordinates()) {
				sunDirections[sunDirectionsCount] = p;
				sunDirectionsCount++;
			}
		}
		return sunDirections;
	}

	private float distance(Vertex v) {
		return (float) Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY()
				+ v.getZ() * v.getZ());
	}

	private void calculateCenterOfBuildingsAndCity() {
		float xCity = 0, zCity = 0;
		for (Building b : City.getInstance().getBuildings()) {
			float x = 0, z = 0;
			int count = 0;
			for (BoundarySurface surface : b.getBoundarySurfaces()) {
				for (Polygon p : surface.getPolygons()) {
					for (Triangle t : p.getTriangles()) {
						for (Vertex v : t.getVertices()) {
							x += v.getX();
							z += v.getZ();
							count++;
						}
					}
				}
			}
			xCity += x / count;
			zCity += z / count;
			b.setCenter(new Vertex(x / count, 0, z / count));
		}
		int buildingCount = City.getInstance().getBuildings().size();
		City.getInstance().setCenter(
				new Vertex(xCity / buildingCount, 0, zCity / buildingCount));
	}

}
