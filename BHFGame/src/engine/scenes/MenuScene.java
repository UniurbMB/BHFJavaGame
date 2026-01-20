package engine.scenes;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import org.joml.Matrix4f;

import engine.Scene;
import engine.Sound;
import engine.Window;
import engine.events.AbstractEvent;
import engine.events.KeyListener;
import engine.events.WindowResizeListener;
import engine.rendering.font.BFont;
import engine.rendering.font.BText;
import engine.rendering.rendering_primitives.*;

public class MenuScene extends Scene{
	
	private boolean debounce = false;
	Rect r = new Rect();
	Canvas c = new Canvas(0, 0, 1.0f, 1.0f, 1024, 1024, 0.25f, 0.25f, 0.25f);
	BFont font = new BFont("src/assets/fonts/wdxl-lubrifont-jp-n-japanese-400-normal.ttf", 185.0f);
	BText title = new BText("BULLET HEAVEN", font, true, -0.9f, 0.75f, 0.125f, 0.15f, 0.005f, 0.7f, 0.7f, 1.0f);
	BText title2 = new BText("FRIDAY", font, true, -0.9f, 0.55f, 0.125f, 0.15f, 0.005f, 0.7f, 0.7f, 1.0f);
	Sound s = Sound.newSound("src/assets/sounds/soundcheck.ogg", true);
	
	AbstractEvent resizeEvent = new AbstractEvent() {
		Canvas can = c;
		public void invoke() {
			if(WindowResizeListener.getWidth() > WindowResizeListener.getHeight()) {
				float ratio = ((float)WindowResizeListener.getHeight())/((float)WindowResizeListener.getWidth());
				can.size.x = ratio;
				can.size.y = 1.0f;
			}else {
				float ratio = ((float)WindowResizeListener.getWidth())/((float)WindowResizeListener.getHeight());
				can.size.y = ratio;
				can.size.x = 1.0f;
			}
		}
	};
	
	@Override
	public void init() {
		s.play();
		
		WindowResizeListener.addEvent(resizeEvent);
		resizeEvent.invoke();
	}
	
	@Override
	public void update(float delta) {
		
		glClearColor(0.05f, 0.05f, 0.05f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		c.beginDrawing(WindowResizeListener.getWidth(), WindowResizeListener.getHeight());
		
		r.render();
		title.render();
		title2.render();
		
		c.stopDrawing();
		
		c.render();
		
		
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
		WindowResizeListener.removeEvent(this.resizeEvent);
	}

}
