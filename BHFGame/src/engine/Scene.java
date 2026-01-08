package engine;

public abstract class Scene {
	
	protected Window w;
	
	public Scene() {}
	
	public void init() {}
	
	public abstract void update(float delta);
	
	public final void attachWindow(Window w) {
		this.w = w;
	}
	
	public void cleanUp() {}
	
}
