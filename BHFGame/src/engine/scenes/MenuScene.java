package engine.scenes;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import engine.Scene;
import engine.events.KeyListener;
import engine.rendering_primitives.Rect;

public class MenuScene extends Scene{
	
	private boolean debounce = false;
	Rect r = new Rect();
	@Override
	public void update(float delta) {
		
		glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		r.render();
		
		if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE) && debounce) {
			w.setShouldClose(true);
		}else if(!KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
			debounce = true;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_ENTER)) {
			w.setCurrentScene(new TestScene());
		}
	}

}
