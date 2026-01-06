package engine.events;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
	private static KeyListener instance = new KeyListener();
	private boolean[] keyPressed;
	
	private KeyListener() {
		keyPressed = new boolean[350];
	}
	
	public static void keyCallback(long window, int key, int scancode, int action, int mods) {
		if(action == GLFW_PRESS) {
			instance.keyPressed[key] = true;
		}else if(action == GLFW_RELEASE) {
			instance.keyPressed[key] = false;
		}
	}
	
	public static boolean isKeyPressed(int key) {
		if(key >= instance.keyPressed.length) {
			throw new RuntimeException("The requested keycode does not exist!");
		}
		return instance.keyPressed[key];

	}
}
