package de.hft_stuttgart.swp2.volume;

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
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;
import org.jocl.struct.Buffers;
import org.jocl.struct.SizeofStruct;

import de.hft_stuttgart.swp2.model.Building;
import de.hft_stuttgart.swp2.model.City;
import de.hft_stuttgart.swp2.model.Triangle;

public class VolumeCalculator {

	private static String programSource;

	private static cl_context context;
	private static cl_command_queue commandQueue;
	private static cl_kernel kernel;
	private static cl_program program;

	public static void calculateVolume() {

		init2();
	}

	private static void init() {
		try {
			programSource = new Scanner(new File("volumeCalculation.cl"))
					.useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// TODO: error handling
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		defaultInitialization();

		// Create input- and output data
		int n = City.getInstance().getBuildings().size();

		BuildingStruct buildingArray[] = new BuildingStruct[n];
		float volumeArray[] = new float[n];
		for (int i = 0; i < n; i++) {
			Building b = City.getInstance().getBuildings().get(i);
			buildingArray[i] = new BuildingStruct();
			buildingArray[i].triangleCount = b.getTriangles().size();
			float[] points = new float[b.getTriangles().size() * 9];
			for (int j = 0; j < b.getTriangles().size(); j++) {
				Triangle t = b.getTriangles().get(j);
				points[j * 9 + 0] = t.getVertices()[0].getX();
				points[j * 9 + 1] = t.getVertices()[0].getY();
				points[j * 9 + 2] = t.getVertices()[0].getZ();
				points[j * 9 + 3] = t.getVertices()[1].getX();
				points[j * 9 + 4] = t.getVertices()[1].getY();
				points[j * 9 + 5] = t.getVertices()[1].getZ();
				points[j * 9 + 6] = t.getVertices()[2].getX();
				points[j * 9 + 7] = t.getVertices()[2].getY();
				points[j * 9 + 8] = t.getVertices()[2].getZ();
			}
			buildingArray[i].vertices = points;
		}

		int structsize = SizeofStruct.sizeof(BuildingStruct.class);
		// Allocate a buffer that can store the building data
		ByteBuffer buildingBuffer = Buffers.allocateBuffer(buildingArray);

		// Write the buildings into the buffer
		Buffers.writeToBuffer(buildingBuffer, buildingArray);

		// Allocate the memory object for the buildings that
		// contains the data from the buildings buffer
		cl_mem buildingsMem = clCreateBuffer(context, CL.CL_MEM_READ_ONLY
				| CL.CL_MEM_USE_HOST_PTR, structsize * n,
				Pointer.to(buildingBuffer), null);

		Pointer volumePointer = Pointer.to(volumeArray);
		cl_mem volumeMem = clCreateBuffer(context, CL_MEM_READ_WRITE,
				Sizeof.cl_float * n, volumePointer, null);

		// Set the arguments for the kernel
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(buildingsMem));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(volumeMem));

		// Set the work-item dimensions
		long global_work_size[] = new long[] { n };

		// Execute the kernel
		clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size,
				null, 0, null, null);

		// Read the output data
		clEnqueueReadBuffer(commandQueue, volumeMem, CL_TRUE, 0, n
				* Sizeof.cl_float, volumePointer, 0, null, null);

		// Release kernel, program, and memory objects
		clReleaseMemObject(buildingsMem);
		clReleaseMemObject(volumeMem);
		clReleaseKernel(kernel);
		clReleaseProgram(program);
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);
	}
	
	private static void init2() {
		
		try {
			programSource = new Scanner(new File("volumeCalculation2.cl"))
					.useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// TODO: error handling
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		defaultInitialization();

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
		
		System.out.println("Size of vertices array as float: " + verticesSize);
		System.out.println("vertices Array: " + Arrays.toString(vertices));
		System.out.println("triangleCount Array: " + Arrays.toString(triangleCount));
		
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
		long global_work_size[] = new long[] {n};
		long local_work_size[] = new long[] {1};

		// Execute the kernel
		clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, global_work_size,
				local_work_size, 0, null, null);

		// Read the output data
		clEnqueueReadBuffer(commandQueue, volumeMem, CL_TRUE, 0, n
				* Sizeof.cl_float, volumePointer, 0, null, null);
		
		System.out.println("Ergebnis: " + Arrays.toString(volumeArray));
		
		// Release kernel, program, and memory objects
		clReleaseMemObject(verticesMem);
		clReleaseMemObject(triangleCountMem);
		clReleaseMemObject(volumeMem);
		clReleaseKernel(kernel);
		clReleaseProgram(program);
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);
	}

	/**
	 * Default initialization of the context, command queue, kernel and program
	 */
	private static void defaultInitialization() {
		// Obtain the platform IDs and initialize the context properties
		cl_platform_id platforms[] = new cl_platform_id[1];
		clGetPlatformIDs(platforms.length, platforms, null);
		cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM, platforms[0]);

		// Create an OpenCL context on a GPU device
		context = CL.clCreateContextFromType(contextProperties,
				CL.CL_DEVICE_TYPE_GPU, null, null, null);
		if (context == null) {
			// If no context for a GPU device could be created,
			// try to create one for a CPU device.
			context = CL.clCreateContextFromType(contextProperties,
					CL.CL_DEVICE_TYPE_CPU, null, null, null);

			if (context == null) {
				// TODO: error handling
				System.out.println("Unable to create a context");
				return;
			}
		}

		// Enable exceptions and subsequently omit error checks in this sample
		CL.setExceptionsEnabled(true);

		// Get the list of GPU devices associated with the context
		long numBytes[] = new long[1];
		CL.clGetContextInfo(context, CL.CL_CONTEXT_DEVICES, 0, null, numBytes);

		// Obtain the cl_device_id for the first device
		int numDevices = (int) numBytes[0] / Sizeof.cl_device_id;
		cl_device_id devices[] = new cl_device_id[numDevices];
		CL.clGetContextInfo(context, CL.CL_CONTEXT_DEVICES, numBytes[0],
				Pointer.to(devices), null);

		// Create a command-queue
		commandQueue = clCreateCommandQueue(context, devices[0], 0, null);

		// Create the program from the source code
		program = clCreateProgramWithSource(context, 1,
				new String[] { programSource }, null, null);

		// Build the program
		clBuildProgram(program, 0, null, null, null, null);
		
		// Create the kernel
		kernel = clCreateKernel(program, "calc", null);
	}
	
	private void debugProgram(cl_device_id[] devices) {
		long[] logSize = new long[1];
        CL.clGetProgramBuildInfo(program, devices[0], CL.CL_PROGRAM_BUILD_STATUS, 0, null, logSize);
        System.out.println("build status: "+logSize[0] +"");
        byte[] logData = new byte[(int)logSize[0]];
        CL.clGetProgramBuildInfo(program, devices[0], CL.CL_PROGRAM_BUILD_STATUS, logSize[0], Pointer.to(logData), null);
        System.out.println("Obtained status data:");
        System.out.println(">"+new String(logData, 0, logData.length-1)+"<");
        logSize = new long[1];
        CL.clGetProgramBuildInfo(program, devices[0], CL.CL_PROGRAM_BUILD_OPTIONS, 0, null, logSize);
        System.out.println("build options: "+logSize[0] +"");
        logData = new byte[(int)logSize[0]];
        CL.clGetProgramBuildInfo(program, devices[0], CL.CL_PROGRAM_BUILD_OPTIONS, logSize[0], Pointer.to(logData), null);
        System.out.println("Obtained build options data:");
        System.out.println(">"+new String(logData, 0, logData.length-1)+"<");

        logSize = new long[1];
        CL.clGetProgramBuildInfo(program, devices[0], CL.CL_PROGRAM_BUILD_LOG, 0, null, logSize);

	}

}
