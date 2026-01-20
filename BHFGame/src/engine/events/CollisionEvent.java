package engine.events;

import engine.collision.collision_shapes.CollisionObject;

public abstract class CollisionEvent extends AbstractEvent {

	private CollisionObject obj;
	
	@Override
	public abstract void invoke();
	
	public void setCollisionObject(CollisionObject obj) {
		this.obj = obj;
	}
	
}
