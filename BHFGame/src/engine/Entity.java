package engine;

import java.util.Optional;

import org.joml.Vector2f;

import engine.collision.collision_shapes.CollisionObject;
import engine.events.AbstractEvent;
import engine.rendering.rendering_primitives.Renderable;
import engine.rendering.rendering_primitives.RenderingPrimitive;


public abstract class Entity {
	
	protected boolean visible = true;
	protected RenderingPrimitive renderObject;
	protected CollisionObject collider;
	public Vector2f pos;
	public Vector2f renderOffset;
	public Vector2f colliderOffset;
	private Optional<AbstractEvent> collisionEvent;
	
	public Entity(RenderingPrimitive renderObject, CollisionObject collider,
			float x, float y,
			float renderOffsetX, float renderOffsetY,
			float colliderOffsetX, float colliderOffsetY) {
		this.renderObject = renderObject;
		this.collider = collider;
		this.pos = new Vector2f(x, y);
		this.renderOffset = new Vector2f(renderOffsetX, renderOffsetY);
		this.colliderOffset = new Vector2f(colliderOffsetX, colliderOffsetY);
	}
	
	public Entity(RenderingPrimitive renderObject, CollisionObject collider,
			float x, float y) {
		this(renderObject, collider, x, y, 0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public void init() {}
	public abstract void update(float delta);
	
	public boolean testCollision(Entity e) {
		this.align();
		e.align();
		return this.collider.testCollision(e.collider);
	}
	
	public boolean testCollision(CollisionObject c) {
		this.align();
		return this.collider.testCollision(c);
	}
	
	public final void align() {
		this.collider.pos.x = this.pos.x + this.colliderOffset.x;
		this.collider.pos.y = this.pos.y + this.colliderOffset.y;
		this.renderObject.pos.x = this.pos.x + this.renderOffset.x;
		this.renderObject.pos.y = this.pos.y + this.renderOffset.y;
	}
	
	public final void render() {
		if(this.visible) {
			this.align();
			this.renderObject.render();
		}
	}
	
	public final CollisionObject getCollider() {
		return this.collider;
	}
	
	public final RenderingPrimitive getRenderObject() {
		return this.renderObject;
	}

}
