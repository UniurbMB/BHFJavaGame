package engine.scenes;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import engine.Scene;
import engine.events.KeyListener;
import engine.rendering.rendering_primitives.*;

public class PauseScene extends Scene{
	
	private boolean debounce = false;
	private TestScene pausedScene;
	Rect r = new Rect(0.0f, -0.5f, 0.25f, 0.25f);
	Rect r2 = new Rect(0.0f, 0.5f, 0.25f, 0.25f);
	
	@Override
	public void update(float delta) {
		
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		r.render();
		r2.render();
		
		if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE) && debounce) {
			pausedScene.cleanUp();
			w.setCurrentScene(new MenuScene());
		}else if(!KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
			debounce = true;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_ENTER)) {
			w.setCurrentScene(pausedScene);
		}
		
	}
	
	public void setTestScene(TestScene pausedScene) {
		this.pausedScene = pausedScene;
	}

}
