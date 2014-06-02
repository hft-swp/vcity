package de.hft_stuttgart.swp2.opencl;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_event;
import org.jocl.cl_kernel;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

/**
 * @author 12riju1bif
 *
 */
public class OpenClContext {
	
	private cl_context context;
	private cl_command_queue commandQueue;
	private cl_device_id device;
	private static OpenClContext instance;

	/**
	 * Singleton instance of the OpenCL context
	 * @return the only OpenCLContext instance
	 * @throws OpenClException
	 */
	public static OpenClContext getInstance() throws OpenClException {
		if(instance == null)
			instance = new OpenClContext();
		return instance;
	}
	
	private OpenClContext() throws OpenClException {
		reinit();
	}
	
	public void reinit() throws OpenClException {
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
				throw new OpenClException("Unable to create a context");
			}
		}

		// Enable exceptions and subsequently omit error checks in this sample
		CL.setExceptionsEnabled(true);

		// Get the list of GPU devices associated with the context
		long numBytes[] = new long[1];
		CL.clGetContextInfo(context, CL.CL_CONTEXT_DEVICES, 0, null, numBytes);

		// Obtain the cl_device_id for the first device
		int numDevices = (int) numBytes[0] / Sizeof.cl_device_id;
		cl_device_id[] devices = new cl_device_id[numDevices];
		CL.clGetContextInfo(context, CL.CL_CONTEXT_DEVICES, numBytes[0],
				Pointer.to(devices), null);
		device = devices[0];

		// Create a command-queue
		commandQueue = clCreateCommandQueue(context, devices[0],
				CL.CL_QUEUE_PROFILING_ENABLE, null);
		
	}
	

	@Override
	protected void finalize() throws Throwable {
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);
		super.finalize();
	}

	/**
	 * @param filename Path to file to read the source from
	 * @return A cl_kernel with the compiled source
	 */
	public cl_kernel createKernelFromFile(String filename) {
		String programSource = loadProgramText(filename);
		if (programSource.length() == 0) {
			throw new IllegalArgumentException("Program source length is 0");
		}
		// Create the program from the source code
		cl_program program = clCreateProgramWithSource(context, 1,
				new String[] { programSource }, null, null);

		// Build the program
		String options = "-Werror -cl-fast-relaxed-math";
		clBuildProgram(program, 0, null, options, null, null);

		// Create the kernel
		cl_kernel kernel = clCreateKernel(program, "calc", null);

		return kernel;
	}

	/**
	 * this destroys the given kernel
	 * @param kernel
	 */
	public void finalizeKernel(cl_kernel kernel) {
		clReleaseKernel(kernel);
	}

	/**
	 * 
	 * @return the first graphic device capable of OpenCL
	 */
	public cl_device_id getDevice() {
		return device;
	}
	
	/**
	 * use with caution
	 * @return the opencl context
	 */
	public cl_context getClContext() {
		return context;
	}

	/**
	 * use with caution
	 * @return the command queue
	 */
	public cl_command_queue getClCommandQueue() {
		return commandQueue;
	}

	/**
	 * prints some timing values of the kernel
	 * @param kernelEvent
	 */
	public void profile(cl_event kernelEvent) {
		long submitTime[] = new long[1];
		long queuedTime[] = new long[1];
		long startTime[] = new long[1];
		long endTime[] = new long[1];
		CL.clGetEventProfilingInfo(kernelEvent, CL.CL_PROFILING_COMMAND_QUEUED,
				Sizeof.cl_ulong, Pointer.to(queuedTime), null);
		CL.clGetEventProfilingInfo(kernelEvent, CL.CL_PROFILING_COMMAND_SUBMIT,
				Sizeof.cl_ulong, Pointer.to(submitTime), null);
		CL.clGetEventProfilingInfo(kernelEvent, CL.CL_PROFILING_COMMAND_START,
				Sizeof.cl_ulong, Pointer.to(startTime), null);
		CL.clGetEventProfilingInfo(kernelEvent, CL.CL_PROFILING_COMMAND_END,
				Sizeof.cl_ulong, Pointer.to(endTime), null);

		submitTime[0] -= queuedTime[0];
		startTime[0] -= queuedTime[0];
		endTime[0] -= queuedTime[0];

//		System.out.println("Queued : " + String.format("%8.3f", 0.0) + " ms");
//		System.out.println("Submit : "
//				+ String.format("%8.3f", submitTime[0] / 1e6) + " ms");
//		System.out.println("Start  : "
//				+ String.format("%8.3f", startTime[0] / 1e6) + " ms");
//		System.out.println("End    : "
//				+ String.format("%8.3f", endTime[0] / 1e6) + " ms");

//		long duration = endTime[0] - startTime[0];
//		System.out.println("Time : " + String.format("%8.3f", duration / 1e6)
//				+ " ms");
		System.out.printf(" | took : %8.3f milliseconds\n", endTime[0] / 1e6);

	}

	private static String loadProgramText(String fileName) {
		Scanner sc = null;
		String source = "";
		try {
			sc = new Scanner(new File(fileName)).useDelimiter("\\Z");
			source = sc.next();
		} catch (FileNotFoundException e) {
			return "";
		} finally {
			if (sc != null) {
				sc.close();
			}
		}
		return source;
	}
}