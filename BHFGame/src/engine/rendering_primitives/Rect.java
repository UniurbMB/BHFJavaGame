package engine.rendering_primitives;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Rect extends Sprite{

	public Rect(float posX, float posY, 
			float width, float height,
			float red, float green, float blue) {
		super(null, posX, posY, width, height, red, green, blue);
	}
	
	public Rect(float posX, float posY, 
			float width, float height) {
		super(null, posX, posY, width, height, 1.0f, 1.0f, 1.0f);
	}
	
	public Rect(float posX, float posY) {
		super(null, posX, posY, 0.5f, 0.5f);
	}
	
	public Rect() {
		super(null, 0.0f, 0.0f);
	}
	
	public Rect(Vector2f pos, Vector2f size, Vector3f col) {
		super(null, pos.x, pos.y, size.x, size.y, col.x, col.y, col.z);
	}
	
	public Rect(Vector2f pos, Vector2f size) {
		super(null, pos, size, new Vector3f(1.0f, 1.0f, 1.0f));
	}
	
	public Rect(Vector2f pos) {
		super(null, pos, new Vector2f(0.5f, 0.5f));
	}
}
