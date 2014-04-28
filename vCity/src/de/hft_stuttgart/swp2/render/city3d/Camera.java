package de.hft_stuttgart.swp2.render.city3d;

import javax.media.opengl.glu.GLU;

public class Camera {

	private double speed = 1f;
	private double horizontalAngle = Math.PI;
	private double verticalAngle = 0.0d;

	private GLU glu;

	private double directionX;
	private double directionY;
	private double directionZ;

	private double positionX = -10;
	private double positionY = 8;
	private double positionZ = 10;

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

	public void setPerspective(int width, int height) {
		glu.gluPerspective(60, (double) width / height, 0.1, 10000);
	}

	public void turnLeft(double rad) {
		horizontalAngle -= speed * rad;
		calcDirectionVector();
		calcRightVector();
		calcUpVector();
	}

	public void turnRight(double rad) {
		horizontalAngle += speed * rad;
		calcDirectionVector();
		calcRightVector();
		calcUpVector();
	}

	public void moveForward(double delta) {
		positionX += directionX * delta * speed;
		positionY += directionY * delta * speed;
		positionZ += directionZ * delta * speed;
	}

	public void moveBackwards(double delta) {
		positionX -= directionX * delta * speed;
		positionY -= directionY * delta * speed;
		positionZ -= directionZ * delta * speed;
	}

	public void strafeRight(double delta) {
		positionX += rightX * delta * speed;
		positionY += rightY * delta * speed;
		positionZ += rightZ * delta * speed;
	}

	public void strafeLeft(double delta) {
		positionX -= rightX * delta * speed;
		positionY -= rightY * delta * speed;
		positionZ -= rightZ * delta * speed;
	}

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

	private void calcDirectionVector() {
		directionX = Math.cos(verticalAngle) * Math.sin(horizontalAngle);
		directionY = Math.sin(verticalAngle);
		directionZ = Math.cos(verticalAngle) * Math.cos(horizontalAngle);
	}

	private void calcRightVector() {
		rightX = Math.sin(horizontalAngle - Math.PI / 2.0d);
		rightY = 0;
		rightZ = Math.cos(horizontalAngle - Math.PI / 2.0d);
	}

	private void calcUpVector() {
		// right x direction
		upX = rightY * directionZ - rightZ * directionY;
		upY = rightZ * directionX - rightX * directionZ;
		upZ = rightX * directionY - rightY * directionX;
	}
}
