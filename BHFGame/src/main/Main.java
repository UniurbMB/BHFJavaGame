package main;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import engine.Entity;
import engine.Scene;
import engine.ShaderProgram;
import engine.Window;
import engine.scenes.*;
import engine.events.AbstractEvent;
import engine.events.KeyListener;
import engine.events.MouseListener;
import engine.events.WindowResizeListener;
import engine.rendering.font.BFont;
import engine.rendering.rendering_primitives.*;

import java.nio.*;
import java.util.Optional;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Main {
	
	private static Window window;
	private static Scene testScene;
	
	public static void main(String[] args) {
		
		init();
		
		update();
		
		cleanup();

	}
	
	private static void init() {
		window = new Window(1967, 600, "Cool test", 60);
		testScene = new FontTestScene();
		//String filepath = "src/assets/fonts/yuji-syuku-japanese-400-normal.ttf";
		//stbtt_InitFont();
		//BFont font = new BFont(filepath, 24);
		
		try (MemoryStack s = stackPush()) {
            FloatBuffer px = s.mallocFloat(1);
            FloatBuffer py = s.mallocFloat(1);
            long monitor = glfwGetPrimaryMonitor();
            glfwGetMonitorContentScale(monitor, px, py);

            float contentScaleX = px.get(0);
            float contentScaleY = py.get(0);
            System.out.println(contentScaleX);
		}
		
		
		window.setCurrentScene(testScene);
	}
	
	private static void update() {
		while(!window.shouldClose()) {
			window.update();
		}
	}

	private static void cleanup() {
		Sprite.cleanUp();
		Canvas.cleanUp();
		window.destroy();
	}
	
}
