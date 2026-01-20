package engine.collision.collision_shapes;

import org.joml.Vector2f;
import engine.rendering.rendering_primitives.*;

public class CollisionPoint extends CollisionObject{

	public CollisionPoint(float x, float y) {
		this.pos = new Vector2f(x, y);
	}
	
	public CollisionPoint(Vector2f pos) {
		this(pos.x, pos.y);
	}
	
	public CollisionPoint() {
		this(0.0f, 0.0f);
	}
	
	@Override
	public <X extends CollisionObject> boolean testCollision(X obj) {
		if(obj instanceof CollisionPoint) {
			return (this.pos.x == obj.pos.x && this.pos.y == obj.pos.y);
		}
		return obj.testCollision(this);
	}

}
