package de.hft_stuttgart.swp2.render.city3d;

import javax.media.opengl.glu.GLU;

public class Camera {

	public double speed = 1f;
	public double horizontalAngle = 0.70 * Math.PI;
	public double verticalAngle = -0.10d;

	private GLU glu;

	private double directionX;
	private double directionY;
	private double directionZ;

	public double positionX = -45;
	public double positionY = 75;
	public double positionZ = 29;

	private double rightX;
	private double rightY;
	private double rightZ;

	private double upX;
	private double upY = 1;
	private double upZ;

	public Camera(GLU glu) {
		this.glu = glu;
		calcDirectionVector();
		calcRightVector();
		calcUpVector();
	}

	public void lookAt() {
		glu.gluLookAt(positionX, positionY, positionZ, positionX + directionX,
				positionY + directionY, positionZ + directionZ, upX, upY, upZ);
	}

	/**
	 * sets the perspective the camera is locking at
	 * 
	 * @param width
	 * @param height
	 */
	public void setPerspective(int width, int height) {
		glu.gluPerspective(60, (double) width / height, 0.1, 20000);
	}

	/**
	 * Turns the camera to the left for an given angle.
	 * 
	 * @param rad
	 */
	public void turnLeft(double rad) {
		horizontalAngle -= speed * rad;
		calcDirectionVector();
		calcRightVector();
		calcUpVector();
	}

	/**
	 * Turns the camera to the right for an given angle.
	 * 
	 * @param rad
	 */
	public void turnRight(double rad) {
		horizontalAngle += speed * rad;
		calcDirectionVector();
		calcRightVector();
		calcUpVector();
	}

	/**
	 * Moves the Camera nearer to the object focused by the screen-mid. Distance
	 * change given by an double-value.
	 * 
	 * @param rad
	 */
	public void moveForward(double delta) {
		positionX += directionX * delta * speed;
		double newY = positionY + directionY * delta * speed;
		if (newY >= 0) {
			positionY = newY;
		}
		// positionY += directionY * delta * speed;
		positionZ += directionZ * delta * speed;
	}

	/**
	 * Moves the Camera away from to the object focused by the screen-mid.
	 * Distance change given by an double-value.
	 * 
	 * @param rad
	 */
	public void moveBackwards(double delta) {
		positionX -= directionX * delta * speed;
		double newY = positionY - directionY * delta * speed;
		if (newY >= 0) {
			positionY = newY;
		}
		// positionY -= directionY * delta * speed;
		positionZ -= directionZ * delta * speed;
	}

	/**
	 * Moves the camera forward on the x and z axis while ignoring the y axis.
	 * Doesn't change the height of the camera.
	 * 
	 * @param delta
	 */
	public void strafeForward(double delta) {
		positionX += directionX * delta * speed;
		positionZ += directionZ * delta * speed;
	}

	/**
	 * Moves the camera backwards on the x and z axis while ignoring the y axis.
	 * Doesn't change the height of the camera.
	 * 
	 * @param delta
	 */
	public void strafeBackwards(double delta) {
		positionX -= directionX * delta * speed;
		positionZ -= directionZ * delta * speed;
	}

	/**
	 * Moves the camera to the right on the x and z axis while ignoring the y
	 * axis. Doesn't change the height of the camera.
	 * 
	 * @param delta
	 */
	public void strafeRight(double delta) {
		positionX += rightX * delta * speed;
		// positionY += rightY * delta * speed;
		positionZ += rightZ * delta * speed;
	}

	/**
	 * Moves the camera to the left on the x and z axis while ignoring the y
	 * axis. Doesn't change the height of the camera.
	 * 
	 * @param delta
	 */
	public void strafeLeft(double delta) {
		positionX -= rightX * delta * speed;
		// positionY -= rightY * delta * speed;
		positionZ -= rightZ * delta * speed;
	}

	public void slider(double delta) {
		this.positionY = delta;
	}

	/**
	 * Turns the camera up for an given angle.
	 * 
	 * @param rad
	 */
	public void turnUp(double rad) {
		verticalAngle += speed * rad;
		if (verticalAngle > Math.PI) {
			verticalAngle = Math.PI;
		} else if (verticalAngle < -Math.PI) {
			verticalAngle = -Math.PI;
		}
		calcDirectionVector();
		calcUpVector();
	}

	/**
	 * Turns the camera down for an given angle.
	 * 
	 * @param rad
	 */
	public void turnDown(double rad) {
		verticalAngle -= speed * rad;
		if (verticalAngle > Math.PI / 2) {
			verticalAngle = Math.PI / 2;
		} else if (verticalAngle < -Math.PI / 2) {
			verticalAngle = -Math.PI / 2;
		}
		calcDirectionVector();
		calcUpVector();
	}

	/**
	 * calculates the direction vector for x,y,z
	 */
	private void calcDirectionVector() {
		directionX = Math.cos(verticalAngle) * Math.sin(horizontalAngle);
		directionY = Math.sin(verticalAngle);
		directionZ = Math.cos(verticalAngle) * Math.cos(horizontalAngle);
	}

	/**
	 * calculates the right vector for x,y,z
	 */
	private void calcRightVector() {
		rightX = Math.sin(horizontalAngle - Math.PI / 2.0d);
		rightY = 0;
		rightZ = Math.cos(horizontalAngle - Math.PI / 2.0d);
	}

	/**
	 * calculates the up vector for x,y,z
	 */
	private void calcUpVector() {
		// right x direction
		upX = rightY * directionZ - rightZ * directionY;
		upY = rightZ * directionX - rightX * directionZ;
		upZ = rightX * directionY - rightY * directionX;
	}
}
