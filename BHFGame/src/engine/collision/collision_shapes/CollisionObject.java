package engine.collision.collision_shapes;

import org.joml.Vector2f;

public abstract class CollisionObject {
	public Vector2f pos;
	
	public abstract<X extends CollisionObject> boolean testCollision(X obj);
	
}
