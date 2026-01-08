package engine.events;

import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;

public class WindowResizeListener {
	
	private static int width;
	private static int height;
	private static ArrayList<AbstractEvent> events = new ArrayList<>();
	
	public static void init(int width, int height) {
		WindowResizeListener.width = width;
		WindowResizeListener.height = height;
	}

	public static void windowResizeCallback(long window, int width, int height) {
		glViewport(0, 0, width, height);
		WindowResizeListener.width = width;
		WindowResizeListener.height = height;
		for(AbstractEvent e: events) {
			e.invoke();
		}
	}
	
	public static void addEvent(AbstractEvent e) {
		events.add(e);
	}
	
	public static void removeEvent(AbstractEvent e) {
		events.remove(e);
	}
	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
	
}
