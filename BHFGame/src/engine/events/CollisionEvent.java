package engine.events;

public abstract class CollisionEvent extends AbstractEvent {

	@SuppressWarnings("unused")
	private Object collider;
	
	public void setCollider(Object c) {
		this.collider = c;
	}
	
	@Override
	public abstract void invoke();
	
}
