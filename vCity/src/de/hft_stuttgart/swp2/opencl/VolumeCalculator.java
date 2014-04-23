package de.hft_stuttgart.swp2.opencl;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_event;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;

/**
 * This is a utility class with only one method to initiate the calculation of
 * the volume of the whole city.
 * 
 * @author 12bema1bif, 12tost1bif, 12riju1bif
 * 
 */
public class VolumeCalculator {
	OpenClContext occ;
	String filename = "volumeCalculation.cl";

	public VolumeCalculator() throws OpenClException {
		// FIXME: Make Singleton
		occ = new OpenClContext();
	}

	/**
	 * This method calculates the volume of all buildings stored in the city
	 * singleton. If no buildings are stored this does nothing.
	 */
	public void calculateVolume() {
		cl_kernel kernel = occ.createKernelFromFile(filename);
		cl_context context = occ.getClContext();
		cl_command_queue commandQueue = occ.getClCommandQueue();

		// Create input- and output data
		int n = City.getInstance().getBuildings().size();

		int verticesSize = 0;
		for (Building b : City.getInstance().getBuildings()) {
			verticesSize += b.getTriangles().size() * 9;
		}
		float[] vertices = new float[verticesSize];
		int[] triangleCount = new int[n];
		float[] volumeArray = new float[n];
		int offset = 0;
		for (int i = 0; i < n; i++) {
			Building b = City.getInstance().getBuildings().get(i);
			triangleCount[i] = b.getTriangles().size();
			for (int j = 0; j < triangleCount[i]; j++) {
				Triangle t = b.getTriangles().get(j);
				vertices[offset + j * 9 + 0] = t.getVertices()[0].getX();
				vertices[offset + j * 9 + 1] = t.getVertices()[0].getY();
				vertices[offset + j * 9 + 2] = t.getVertices()[0].getZ();
				vertices[offset + j * 9 + 3] = t.getVertices()[1].getX();
				vertices[offset + j * 9 + 4] = t.getVertices()[1].getY();
				vertices[offset + j * 9 + 5] = t.getVertices()[1].getZ();
				vertices[offset + j * 9 + 6] = t.getVertices()[2].getX();
				vertices[offset + j * 9 + 7] = t.getVertices()[2].getY();
				vertices[offset + j * 9 + 8] = t.getVertices()[2].getZ();
			}
			offset = offset + triangleCount[i] * 9;
		}

		// System.out.println("Size of vertices array as float: " +
		// verticesSize);
		// System.out.println("vertices Array: " + Arrays.toString(vertices));
		// System.out.println("triangleCount Array: " +
		// Arrays.toString(triangleCount));

		// allocate memory on the gpu
		cl_mem verticesMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_float * verticesSize,
				Pointer.to(vertices), null);

		cl_mem triangleCountMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, Sizeof.cl_int * n,
				Pointer.to(triangleCount), null);

		Pointer volumePointer = Pointer.to(volumeArray);
		cl_mem volumeMem = clCreateBuffer(context, CL_MEM_READ_WRITE,
				Sizeof.cl_float * n, null, null);

		// Set the arguments for the kernel
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(verticesMem));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(triangleCountMem));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(volumeMem));

		// Set the work-item dimensions
		long global_work_size[] = new long[] { n };
		long local_work_size[] = new long[] { 1 };

		// Execute the kernel
		cl_event kernelEvent = new cl_event();
		clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size,
				local_work_size, 0, null, kernelEvent);

		// Read the output data
		clEnqueueReadBuffer(commandQueue, volumeMem, CL_TRUE, 0, n
				* Sizeof.cl_float, volumePointer, 0, null, null);

		// wait for the kernel to finish
		CL.clFinish(commandQueue);

		occ.profile(kernelEvent);

		for (int i = 0; i < n; i++) {
			City.getInstance().getBuildings().get(i).setVolume(volumeArray[i]);
		}

		// Release kernel, program, and memory objects
		clReleaseMemObject(verticesMem);
		clReleaseMemObject(triangleCountMem);
		clReleaseMemObject(volumeMem);

		occ.finalizeKernel(kernel);
	}
}
