package engine.scenes;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import org.joml.Matrix4f;

import engine.Scene;
import engine.Sound;
import engine.Window;
import engine.events.KeyListener;
import engine.rendering.rendering_primitives.*;

public class MenuScene extends Scene{
	
	private boolean debounce = false;
	Rect r = new Rect();
	Ellipse e = new Ellipse(0.5f, -0.5f);
	Sound s = Sound.newSound("src/assets/sounds/soundcheck.ogg", true);
	
	@Override
	public void init() {
		s.play();
	}
	
	@Override
	public void update(float delta) {
		
		glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		r.render();
		e.render();
		
		if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE) && debounce) {
			w.setShouldClose(true);
		}else if(!KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
			if(KeyListener.isKeyPressed(GLFW_KEY_P) && debounce) {
				debounce = false;
				if(s.isPlaying())s.pause();
				else s.play();
			}else if(!KeyListener.isKeyPressed(GLFW_KEY_P)) {
				debounce = true;
			}
		}
		
		if(KeyListener.isKeyPressed(GLFW_KEY_ENTER)) {
			w.setCurrentScene(new TestScene());
		}
	}
	
	@Override
	public void cleanUp() {
		s.stop();
	}

}
