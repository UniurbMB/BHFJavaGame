package engine.scenes;

import java.util.Optional;

import engine.*;
import engine.events.*;
import engine.rendering_primitives.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class TestScene extends Scene{
	
	Rect r = new Rect(0.0f, 0.0f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
	Rect r2 = new Rect(-0.5f, -0.65f, 0.125f, 0.45f, 0.0f, 1.0f, 0.0f);
	Rect r3 = new Rect(0.53125f, 0.0f, 0.46875f, 1.0f, 0.98f, 0.15f, 0.65f);
	Sprite s = new Sprite("src/assets/images/test.png", 0.5f, 0.5f, 0.25f, 0.6f, 1.0f, 1.0f, 0.0f);
	Sprite s2 = new Sprite("src/assets/images/test.png", -0.5f, 0.5f, 1.0f, 0.25f, 1.0f, 1.0f, 1.0f);
	Canvas c = new Canvas(-0.46875f, 0.0f, 0.53125f, 1.0f, 544, 1024, 0.25f, 0.25f, 0.25f);
	Canvas c2 = new Canvas(0.0f, 0.0f, 1.0f, 1.0f, 600, 600);
	ShaderRect shad = new ShaderRect(0.0f, 0.0f, 1.0f, 1.0f, "src/shaders/vertexShader.glsl", "src/shaders/specialFragmentShader.glsl");
	Entity en = new Entity() {
		public void init() {
			this.renderObject = Optional.of(new Rect());
			this.visible = true;
		}
		public void update() {
			this.renderObject.get().render();
		}
	};
	
	AbstractEvent windowResizeEvent = new AbstractEvent() {
		private Canvas canv = c2;
		
		public void invoke() {
			if(WindowResizeListener.getWidth() > WindowResizeListener.getHeight()) {
				float ratio = ((float)WindowResizeListener.getHeight())/((float)WindowResizeListener.getWidth());
				canv.size.x = ratio;
				canv.size.y = 1.0f;
			}else {
				float ratio = ((float)WindowResizeListener.getWidth())/((float)WindowResizeListener.getHeight());
				canv.size.y = ratio;
				canv.size.x = 1.0f;
			}
		}
	};
	
	public void init() {
		WindowResizeListener.addEvent(windowResizeEvent);
		windowResizeEvent.invoke();
		en.init();
	}
	
	@Override
	public void update(float delta) {
		//glBindFramebuffer(GL_FRAMEBUFFER, 0);
		c.beginDrawing(WindowResizeListener.getWidth(), WindowResizeListener.getHeight());
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		r.render();
		r2.render();
		s.render();
		s2.render();
		en.update();
		
		c.stopDrawing();
		
		c2.beginDrawing(WindowResizeListener.getWidth(), WindowResizeListener.getHeight());
		c.render();
		r3.render();
		c2.stopDrawing();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		shad.render();
		c2.render();
		
		
		if(KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
			s2.pos.x += 2.5f * delta;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
			s2.pos.x -= 2.5f * delta;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_UP)) {
			s2.pos.y += 2.5f * delta;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
			s2.pos.y -= 2.5f * delta;
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		c.beginDrawing(WindowResizeListener.getWidth(), WindowResizeListener.getHeight());
		glClear(GL_COLOR_BUFFER_BIT);
		
		r.render();
		r2.render();
		s.render();
		s2.render();
		en.update();
		
		c.stopDrawing();
		
		c2.beginDrawing(WindowResizeListener.getWidth(), WindowResizeListener.getHeight());
		c.render();
		r3.render();
		c2.stopDrawing();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		shad.render();
		c2.render();
		
		if(w == null) {
			System.err.println("this shouldn't print");
		}
		
		if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
			PauseScene p = new PauseScene();
			p.setTestScene(this);
			w.setCurrentScene(p, false);
		}
		
	}
	
	@Override
	public void cleanUp() {
		WindowResizeListener.removeEvent(windowResizeEvent);
	}

}
