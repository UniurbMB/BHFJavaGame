package engine.events;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
	private static boolean[] keyPressed = new boolean[350];
	
	private KeyListener() {
		keyPressed = new boolean[350];
	}
	
	public static void keyCallback(long window, int key, int scancode, int action, int mods) {
		if(action == GLFW_PRESS) {
			keyPressed[key] = true;
		}else if(action == GLFW_RELEASE) {
			keyPressed[key] = false;
		}
	}
	
	public static boolean isKeyPressed(int key) {
		if(key >= keyPressed.length) {
			throw new RuntimeException("The requested keycode does not exist!");
		}
		return keyPressed[key];

	}
}
