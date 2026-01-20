package engine.scenes;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import engine.Entity;
import engine.Scene;
import engine.collision.collision_shapes.*;
import engine.events.KeyListener;
import engine.rendering.rendering_primitives.*;

public class CollisionTestScene extends Scene {

	Entity e1 = new Entity(new Rect(0.0f, 0.0f, 0.25f, 0.25f),
			new CollisionRect(0.0f, 0.0f, 0.25f, 0.25f), 0.0f, 0.0f){

		float speed = 2.0f;
		@Override
		public void update(float delta) {
			if(KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
				this.pos.x += speed * delta;
			}
			if(KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
				this.pos.x -= speed * delta;
			}
			if(KeyListener.isKeyPressed(GLFW_KEY_UP)) {
				this.pos.y += speed * delta;
			}
			if(KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
				this.pos.y -= speed * delta;
			}
			
			this.align();
			this.render();
		}};
		
	Entity e2 = new Entity(new Rect(0.0f, 0.5f, 0.125f, 0.125f),
			new CollisionRect(0.0f, 0.5f, 0.125f, 0.125f),
			0.0f, 0.5f) {

		@Override
		public void update(float delta) {
			this.align();
			
			if(e2.testCollision(e1)) {
				((Rect)this.renderObject).color.x = 0.0f;
			}else {
				((Rect)this.renderObject).color.x = 1.0f;
			}
			
			if(e2.testCollision(e4)) {
				((Rect)this.renderObject).color.x = 0.0f;
			}else {
				((Rect)this.renderObject).color.x = 1.0f;
			}
			
			
			this.render();
		}
		
	};
	
	Entity e3 = new Entity(new Ellipse(0.0f, 0.0f, 0.125f, 0.125f),
			new CollisionCircle(0, 0, 0.125f),
			-0.75f, -0.75f) {
		
		@Override
		public void update(float delta) {
			this.align();
			System.out.println("{" + e4.pos.x + ", " + e4.pos.y + "}");
			//System.out.println("{" + e4.collider.pos.x + ", " + e4.collider.pos.y + "}");
			
			if(this.testCollision(e4)) {
				((Rect)this.renderObject).color.z = 0.0f;
			}
			else if(this.testCollision(e4)) {
				((Rect)this.renderObject).color.z = 0.0f;
			}else {
				((Rect)this.renderObject).color.z = 1.0f;
			}
			
			this.render();
		}
		
	};
	
	Entity e4 = new Entity(
			new Ellipse(0.0f, 0.0f, 0.125f, 0.125f),
			new CollisionCircle(0.0f, 0.0f, 0.125f),
			0.75f, -0.75f) {
		
		float speed = 2.0f;
		
		@Override
		public void update(float delta) {
			
			if(KeyListener.isKeyPressed(GLFW_KEY_D)) {
				this.pos.x += speed * delta;
			}
			if(KeyListener.isKeyPressed(GLFW_KEY_A)) {
				this.pos.x -= speed * delta;
			}
			if(KeyListener.isKeyPressed(GLFW_KEY_W)) {
				this.pos.y += speed * delta;
			}
			if(KeyListener.isKeyPressed(GLFW_KEY_S)) {
				this.pos.y -= speed * delta;
			}
			
			this.align();
			//System.out.println("{" + this.pos.x + ", " + this.pos.y + "}");
			//System.out.println("{" + this.collider.pos.x + ", " + this.collider.pos.y + "}");
			this.render();
		}
		
	};
	
	@Override
	public void update(float delta) {
		glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		e4.update(delta);
		e1.update(delta);
		e2.update(delta);
		e3.update(delta);

	}

}
