package engine.events;

import static org.lwjgl.opengl.GL33.*;

public class WindowResizeListener {
	
	private static int width;
	private static int height;
	
	public static void init(int width, int height) {
		WindowResizeListener.width = width;
		WindowResizeListener.height = height;
	}

	public static void windowResizeCallback(long window, int width, int height) {
		glViewport(0, 0, width, height);
		WindowResizeListener.width = width;
		WindowResizeListener.height = height;
	}
	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
	
}
