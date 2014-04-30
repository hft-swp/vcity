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
	private static final String filename = "shadowCalculation.cl";
	private OpenClContext occ;
	public ShadowCalculatorOpenClBackend() throws OpenClException {
		occ = OpenClContext.getInstance();
	}
	
	@Override
	public void calculateShadow(ShadowPrecision precision) {
		recalculateShadowTriangles(precision);
				
		cl_kernel kernel = occ.createKernelFromFile(filename);
		cl_command_queue commandQueue = occ.getClCommandQueue();
		cl_context context = occ.getClContext();
		
		int verticesSize = 0;
		for (Building b : City.getInstance().getBuildings()) {
			verticesSize += b.getTriangles().size() * 9;
		}
		float[] cityVertices = new float[verticesSize];
		int offset = 0;
		for (Building b : City.getInstance().getBuildings()) {
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

		int[] cityVerticesCount = {cityVertices.length};
		
		ArrayList<ShadowTriangle> sts = new ArrayList<ShadowTriangle>();
		for (Building b : City.getInstance().getBuildings()) {
			sts.addAll(b.getShadowTriangles());
		}
//		sts.addAll(City.getInstance().getBuildings().get(0).getShadowTriangles());
		float[] shadowVerticeCenters = new float[sts.size() * 3];
		int count = 0;
		for(ShadowTriangle st : sts) {
			for(float p : st.getCenter().getCoordinates()) {
				shadowVerticeCenters[count] = p;
				count++;
			}
		}
		
		int[] shadowVerticeCentersCount = {shadowVerticeCenters.length / 3};
		
		count = 0;
		float[] sunDirections = new float[144*3];
		for(Vertex v : calcDirections()) {
			for(float p : v.getCoordinates()) {
				sunDirections[count] = p;
				count++;
			}
		}
		
		byte[] hasShadow = new byte[18*shadowVerticeCentersCount[0]];

//		__kernel void calc(__global float* cityVertices,
//				   __global int* cityVerticesCount,
//				   __global float* shadowVerticeCenters,
//				   __global int* shadowVerticeCentersCount,
//				   __global float* sunDirections,
//				   __global char* hasShadow) // 18*shadowTrianglesCount char
		
		// allocate memory on gpu
		cl_mem cityVerticesMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_float * cityVertices.length,
				Pointer.to(cityVertices), null);

		cl_mem cityTriangleCountMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_int,
				Pointer.to(cityVerticesCount), null);
		
		cl_mem shadowVerticesMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_float * shadowVerticeCenters.length,
				Pointer.to(shadowVerticeCenters), null);

		cl_mem shadowTriangleCountMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_int,
				Pointer.to(shadowVerticeCentersCount), null);
		
		cl_mem sunDirectionsMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_float * sunDirections.length,
				Pointer.to(sunDirections), null);
		

		Pointer hasShadowPointer = Pointer.to(hasShadow);
		cl_mem hasShadowMem = clCreateBuffer(context, CL_MEM_READ_WRITE,
				Sizeof.cl_char * hasShadow.length, null, null);
		
		// Set the arguments for the kernel
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(cityVerticesMem));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(cityTriangleCountMem));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(shadowVerticesMem));
		clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(shadowTriangleCountMem));
		clSetKernelArg(kernel, 4, Sizeof.cl_mem, Pointer.to(sunDirectionsMem));
		clSetKernelArg(kernel, 5, Sizeof.cl_mem, Pointer.to(hasShadowMem));
		clSetKernelArg(kernel, 6, Sizeof.cl_int, Pointer.to(new int[]{shadowVerticeCenters.length / 3}));

		cl_device_id device = occ.getDevice();
		long[] kernelWorkSize = new long[1];
		
 		CL.clGetKernelWorkGroupInfo(kernel, device, CL.CL_KERNEL_WORK_GROUP_SIZE, Sizeof.size_t,
				Pointer.to(kernelWorkSize), null);
		int localWorkSize = (int) kernelWorkSize[0];
		
		int workSize = ((shadowVerticeCenters.length / 3) / localWorkSize  + 1) * localWorkSize;
		long global_work_size[] = new long[] { workSize};
		long local_work_size[] = new long[] { localWorkSize};
		
		// Execute the kernel
		cl_event kernelEvent = new cl_event();
		
		clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size,
				local_work_size, 0, null, kernelEvent);

		// Read the output data
		clEnqueueReadBuffer(commandQueue, hasShadowMem, CL_TRUE, 0, hasShadow.length
				* Sizeof.cl_char, hasShadowPointer, 0, null, null);

		// wait for the kernel to finish
		CL.clFinish(commandQueue);

		occ.profile(kernelEvent);

		count = 0;
//		BitSet bs = BitSet.valueOf(hasShadow);
		for (ShadowTriangle st : sts) {
//			BitSet new_bs = bs.get(count*144, (count+1)*144);
			BitSet new_bs = new BitSet(144);
			for (int i = 0; i < 144; i++) {
				if ((hasShadow[count * 18 + i / 8] & (1 << 7-i%8)) > 0) {
					new_bs.set(i, true);
				} else {
					new_bs.set(i, false);
				}
			}
			st.setShadowSet(new_bs);
			count++;
		}

		// Release kernel, program, and memory objects
		clReleaseMemObject(cityVerticesMem);
		clReleaseMemObject(cityTriangleCountMem);
		clReleaseMemObject(shadowVerticesMem);
		clReleaseMemObject(shadowTriangleCountMem);
		clReleaseMemObject(sunDirectionsMem);
		clReleaseMemObject(hasShadowMem);
		
		occ.finalizeKernel(kernel);
	}
	
}
