package engine.collision.collision_shapes;

import org.joml.Vector2f;

public class CollisionRect extends CollisionObject{
	public Vector2f size;
	
	public CollisionRect(float x, float y, float height, float width) {
		this.pos = new Vector2f(x, y);
		this.size = new Vector2f(width, height);
	}
	
	public CollisionRect(Vector2f pos, Vector2f size) {
		this(pos.x, pos.y, size.x, size.y);
	}
	
	@Override
	public <X extends CollisionObject> boolean testCollision(X obj) {
		if(obj instanceof CollisionPoint) {
			boolean collidesOnX = ((this.pos.x - this.size.x / 2.0f) <= obj.pos.x) &&
					((this.pos.x + this.size.x / 2.0f) >= obj.pos.x);
			boolean collidesOnY = ((this.pos.y - this.size.y / 2.0f) <= obj.pos.y) &&
					((this.pos.y + this.size.y / 2.0f) >= obj.pos.y);
			return collidesOnX && collidesOnY;
		}else if(obj instanceof CollisionRect) {
			CollisionRect r = (CollisionRect)obj;
			boolean leftSideCollides = ((this.pos.x - this.size.x / 2.0f) <= r.pos.x - r.size.x / 2.0f ) &&
					((this.pos.x + this.size.x / 2.0f) >= r.pos.x - r.size.x / 2.0f );
			boolean rightSideCollides = ((this.pos.x - this.size.x / 2.0f) <= r.pos.x + r.size.x / 2.0f ) &&
					((this.pos.x + this.size.x / 2.0f) >= r.pos.x + r.size.x / 2.0f );
			boolean topSideCollides = ((this.pos.y - this.size.y / 2.0f) <= r.pos.y + r.size.y / 2.0f ) &&
					((this.pos.y + this.size.y / 2.0f) >= r.pos.y + r.size.y / 2.0f );
			boolean bottomSideCollides = ((this.pos.y - this.size.y / 2.0f) <= r.pos.y - r.size.y / 2.0f ) &&
					((this.pos.y + this.size.y / 2.0f) >= r.pos.y - r.size.y / 2.0f );
			return (leftSideCollides || rightSideCollides) && (topSideCollides || bottomSideCollides);
		}else if(obj instanceof CollisionCircle) {
			CollisionCircle c = (CollisionCircle)obj;
			Vector2f pos = new Vector2f(
					Math.clamp(c.pos.x, this.pos.x - this.size.x / 2.0f, this.pos.x - this.size.x / 2.0f),
					Math.clamp(c.pos.y, this.pos.y - this.size.y / 2.0f, this.pos.y - this.size.y / 2.0f));
			CollisionPoint p = new CollisionPoint(pos);
			return c.testCollision(p);
		}
		return obj.testCollision(this);
	}

}
