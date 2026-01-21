package engine.scenes;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import engine.Entity;
import engine.Scene;
import engine.collision.QuadTree;
import engine.collision.collision_shapes.*;
import engine.events.KeyListener;
import engine.rendering.rendering_primitives.*;

public class QuadTreeCollisionScene extends Scene {
	
	boolean debounce;
	final int entityCount = 100;
	final int rows = 10;
	final int columns = 10;
	QuadTree tree = new QuadTree(30, 2);
	
	Entity[] entities = new Entity[entityCount];
	Entity e = new Entity(new Ellipse(0, 0, 0.03125f, 0.03125f),
			new CollisionCircle(0, 0, 0.03125f),
			0, 0) {
		float speed = 0.5f;
		
		@Override
		public void update(float delta) {
			if(KeyListener.isKeyPressed(GLFW_KEY_Z)) {
				this.speed = 0.125f;
			}else this.speed = 0.5f;
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
		}
	};
	
	@Override
	public void init() {
		for(int i = 0; i < entityCount; i++) {
			int row = i / rows;
			int column = i % columns;
			
			float posx = ((float)(row - (rows / 2))) / ((float)(rows/2));
			float posy = ((float)(column -(columns / 2))) / ((float)(columns/2));
			
			RenderingPrimitive r;
			CollisionObject c;
			if(i % 2 == 0) {
				r = new Rect(0.0f ,0.0f, 0.03125f, 0.03125f);
				c = new CollisionRect(0, 0, 0.03125f, 0.03125f);
			}else {
				r = new Ellipse(0.0f ,0.0f, 0.03125f, 0.03125f);
				c = new CollisionCircle(0, 0, 0.03125f);
			}
			
			entities[i] = new Entity(
					r,
					c,
					posx, posy) {
				
				float initx = posx;
				float inity = posy;
				
				@Override
				public void init() {
					this.align();
				}
				@Override
				public void update(float delta) {
					tree.removeObject(this);
					double time = glfwGetTime();
					this.pos.x = this.initx;
					this.pos.x = this.initx + (float)Math.sin(time * 0.5f) * this.renderObject.size.x * 19.25f;
					this.pos.y = this.inity + (float)Math.sin(time * 1f) * this.renderObject.size.y * 30.5f;
					//this.pos.x = this.initx + (float)Math.sin(time * 1.5f);
					//this.pos.y = this.inity = (float)Math.sin(time * 0.125f);
					tree.addObject(this);
					this.render();
				}
			};
			tree.addObject(entities[i]);
			entities[i].init();
		}
	}

	@Override
	public void update(float delta) {
		glClearColor(0.25f, 0.25f, 0.25f, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		tree.render();
		
		if(KeyListener.isKeyPressed(GLFW_KEY_0) && debounce) {
			debounce = false;
			tree.printObjectCount();
		}else if(!KeyListener.isKeyPressed(GLFW_KEY_0)){
			debounce = true;
		}
		
		for(Entity v: entities)v.update(delta);
		if(tree.testCollision(e)) {
			((Ellipse)e.getRenderObject()).color.x = 0.0f;
		}else {
			((Ellipse)e.getRenderObject()).color.x = 1.0f;
		}
		e.update(delta);
	//	System.out.println(tree.getObjectCount());
	}

}
