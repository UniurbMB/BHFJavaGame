package engine.events;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
	private static MouseListener instance = new MouseListener();
	private float posX, posY, lastX, lastY;
	private float scrollX, scrollY;
	private boolean mouseButtonPressed[];
	private boolean dragging;
	
	private MouseListener() {
		this.posX = 0.0f;
		this.posY = 0.0f;
		this.lastX = 0.0f;
		this.lastY = 0.0f;
		this.scrollX = 0.0f;
		this.scrollY = 0.0f;
		this.mouseButtonPressed = new boolean[3];
	}
	
	public static void mousePosCallback(long window, double xpos, double ypos) {
		instance.lastX = instance.posX;
		instance.lastY = instance.posY;
		instance.posX = (float) xpos;
		instance.posY = (float)ypos;
		instance.dragging = instance.mouseButtonPressed[0] || instance.mouseButtonPressed[1] || instance.mouseButtonPressed[2];
	}
	
	public static void mouseButtonCallback(long window, int button, int action, int mod) {
		if(action == GLFW_PRESS) {
			if(button < instance.mouseButtonPressed.length) {
				instance.mouseButtonPressed[button] = true;
			}
		}else if(action == GLFW_RELEASE) {
			if(button < instance.mouseButtonPressed.length) {
				instance.mouseButtonPressed[button] = false;
				instance.dragging = false;
			}
		}
	}
	
	public static void mouseScrollCallback(long window, double xoffset, double yoffset) {
		instance.scrollX = (float)xoffset;
		instance.scrollY = (float)yoffset;
	}
	
	public static void endFrame() {
		instance.scrollX = 0.0f;
		instance.scrollY = 0.0f;
		instance.lastX = instance.posX;
		instance.lastY = instance.posY;
	}
	
	public static float getX() {
		return instance.posX;
	}
	
	public static float getY() {
		return instance.posY;
	}
	
	public static float getdx() {
		return instance.posX - instance.lastX;
	}
	
	public static float getdy() {
		return instance.posY - instance.lastY;
	}
	
	public static float getScrollx() {
		return instance.scrollX;
	}
	
	public static float getScrolly() {
		return instance.scrollY;
	}
	
	public static boolean isDragging() {
		return instance.dragging;
	}
	
	public static boolean mouseButtonDown(int button) {
		if(button < instance.mouseButtonPressed.length) {
			return instance.mouseButtonPressed[button];
		}else {
			return false;
		}
	}
}
