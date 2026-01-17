package main;

import engine.Scene;
import engine.Window;
import engine.rendering.font.BFont;
import engine.rendering.rendering_primitives.*;
import engine.scenes.*;

public class Main {
	
	private static Window window;
	private static Scene testScene;
	
	public static void main(String[] args) {
		
		init();
		
		update();
		
		cleanup();

	}
	
	private static void init() {
		window = new Window(1967, 967, "Font test すごい чангус", 60);
		testScene = new FontTestScene();
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
