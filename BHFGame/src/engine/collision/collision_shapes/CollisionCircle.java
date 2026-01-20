package engine.collision.collision_shapes;

import org.joml.Vector2f;

public class CollisionCircle extends CollisionObject{
	public float radius;
	
	public CollisionCircle(float x, float y, float radius) {
		this.pos = new Vector2f(x, y);
		this.radius = radius;
	}
	
	public CollisionCircle(Vector2f pos, float radius) {
		this(pos.x, pos.y, radius);
	}
	
	@Override
	public <X extends CollisionObject> boolean testCollision(X obj) {
		if(obj instanceof CollisionCircle) {
			CollisionCircle c = (CollisionCircle)obj;
			Vector2f vectorDistance = new Vector2f(this.pos.x - c.pos.x, this.pos.y - c.pos.y);
			/*System.out.println("{" + this.pos.x + ", " + this.pos.y + "}");
			System.out.println("{" + c.pos.x + ", " + c.pos.y + "}");
			System.out.println("{" + vectorDistance.x + ", " + vectorDistance.y + "}");*/
			float distance = vectorDistance.length();
			System.out.println(distance);
			return distance <= this.radius + c.radius;
		}else if(obj instanceof CollisionPoint) {
			CollisionPoint p = (CollisionPoint)obj;
			Vector2f vectorDistance = this.pos.sub(p.pos);
			float distance = (float)Math.sqrt(Math.pow(vectorDistance.x, 2) + Math.pow(vectorDistance.y, 2));
			return distance <= this.radius;
		}
		return obj.testCollision(this);
	}

}
