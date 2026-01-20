package engine;

import java.util.Optional;

import org.joml.Vector2f;

import engine.collision.collision_shapes.CollisionObject;
import engine.rendering.rendering_primitives.Renderable;

public abstract class Entity {
	
	protected boolean visible = true;
	protected Renderable renderObject;
	protected CollisionObject collider;
	public Vector2f pos;
	public Vector2f renderOffset;
	public Vector2f colliderOffset;
	
	public Entity(Renderable renderObject, CollisionObject collider,
			float x, float y,
			float renderOffsetX, float renderOffsetY,
			float colliderOffsetX, float colliderOffsetY) {
		this.renderObject = renderObject;
		this.collider = collider;
		this.pos = new Vector2f(x, y);
		this.renderOffset = new Vector2f(renderOffsetX, renderOffsetY);
		this.colliderOffset = new Vector2f(colliderOffsetX, colliderOffsetY);
	}
	
	public Entity(Renderable renderObject, CollisionObject collider,
			float x, float y) {
		this(renderObject, collider, x, y, 0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public void init() {}
	public abstract void update();

}
