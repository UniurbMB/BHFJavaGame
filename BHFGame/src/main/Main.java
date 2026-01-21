package main;

import engine.*;
import engine.scenes.*;
import engine.rendering.rendering_primitives.*;

public class Main {
	
	private static Window window;
	private static Scene scene;
	
	public static void main(String[] args) {
		
		init();
		
		update();
		
		cleanup();

	}
	
	private static void init() {
		window = new Window(1900, 967, "Bullet Heaven Monday", 60, false);
		scene = new QuadTreeCollisionScene();
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
