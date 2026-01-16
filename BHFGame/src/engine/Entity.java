package engine;

import java.util.Optional;

import org.joml.Vector2f;

import engine.rendering.rendering_primitives.Renderable;

public abstract class Entity {
	
	protected boolean visible = true;
	protected Optional<? extends Renderable> renderObject;
	public Vector2f pos = new Vector2f();
	public Vector2f size = new Vector2f();
	
	public abstract void init();
	public abstract void update();

}
