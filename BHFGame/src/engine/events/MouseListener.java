package engine.events;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
	private static float posX, posY, lastX, lastY;
	private static float scrollX, scrollY;
	private static boolean mouseButtonPressed[] = new boolean[3];
	private static boolean dragging;
	
	public static void mousePosCallback(long window, double xpos, double ypos) {
		lastX = posX;
		lastY = posY;
		posX = (float) xpos;
		posY = (float)ypos;
		dragging = mouseButtonPressed[0] || mouseButtonPressed[1] || mouseButtonPressed[2];
	}
	
	public static void mouseButtonCallback(long window, int button, int action, int mod) {
		if(action == GLFW_PRESS) {
			if(button < mouseButtonPressed.length) {
				mouseButtonPressed[button] = true;
			}
		}else if(action == GLFW_RELEASE) {
			if(button < mouseButtonPressed.length) {
				mouseButtonPressed[button] = false;
				dragging = false;
			}
		}
	}
	
	public static void mouseScrollCallback(long window, double xoffset, double yoffset) {
		scrollX = (float)xoffset;
		scrollY = (float)yoffset;
	}
	
	public static void endFrame() {
		scrollX = 0.0f;
		scrollY = 0.0f;
		lastX = posX;
		lastY = posY;
	}
	
	public static float getX() {
		return posX;
	}
	
	public static float getY() {
		return posY;
	}
	
	public static float getdx() {
		return posX - lastX;
	}
	
	public static float getdy() {
		return posY - lastY;
	}
	
	public static float getScrollx() {
		return scrollX;
	}
	
	public static float getScrolly() {
		return scrollY;
	}
	
	public static boolean isDragging() {
		return dragging;
	}
	
	public static boolean mouseButtonDown(int button) {
		if(button < mouseButtonPressed.length) {
			return mouseButtonPressed[button];
		}else {
			return false;
		}
	}
}
