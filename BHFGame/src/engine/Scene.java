package engine;

public abstract class Scene {
	
	public Scene() {}
	
	public abstract void update(float delta);
	
	public void cleanUp() {}
	
}
