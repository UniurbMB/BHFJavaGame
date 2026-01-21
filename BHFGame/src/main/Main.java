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
	private static Scene scene;
	
	public static void main(String[] args) {
		
		init();
		
		update();
		
		cleanup();

	}
	
	private static void init() {
		window = new Window(1967, 967, "Cool test", 60);
		scene = new SpriteBatchTestScene();
		window.setCurrentScene(scene);
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
