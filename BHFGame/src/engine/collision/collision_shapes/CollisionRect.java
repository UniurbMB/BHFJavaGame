package engine.collision.collision_shapes;

import org.joml.Vector2f;

public class CollisionRect extends CollisionObject{
	public Vector2f size;
	
	public CollisionRect(float x, float y, float width, float height) {
		this.pos = new Vector2f(x, y);
		this.size = new Vector2f(width, height);
	}
	
	public CollisionRect(Vector2f pos, Vector2f size) {
		this(pos.x, pos.y, size.x, size.y);
	}
	
	@Override
	public <X extends CollisionObject> boolean testCollision(X obj) {
		if(obj instanceof CollisionPoint) {
			boolean collidesOnX = ((this.pos.x - this.size.x) <= obj.pos.x) &&
					((this.pos.x + this.size.x) >= obj.pos.x);
			boolean collidesOnY = ((this.pos.y - this.size.y) <= obj.pos.y) &&
					((this.pos.y + this.size.y) >= obj.pos.y);
			return collidesOnX && collidesOnY;
			
		}else if(obj instanceof CollisionRect) {
			
			CollisionRect r = (CollisionRect)obj;
			Vector2f ppos = new Vector2f(
					Math.clamp(this.pos.x, r.pos.x - r.size.x, r.pos.x + r.size.x),
					Math.clamp(this.pos.y, r.pos.y - r.size.y, r.pos.y + r.size.y)
					);
			CollisionPoint p = new CollisionPoint(ppos);
			return p.testCollision(this);
			
		}else if(obj instanceof CollisionCircle) {
			CollisionCircle c = (CollisionCircle)obj;
			Vector2f pos = new Vector2f(
					Math.clamp(c.pos.x, this.pos.x - this.size.x, this.pos.x + this.size.x),
					Math.clamp(c.pos.y, this.pos.y - this.size.y, this.pos.y + this.size.y));
			CollisionPoint p = new CollisionPoint(pos);
			return c.testCollision(p);
		}
		return obj.testCollision(this);
	}

}
