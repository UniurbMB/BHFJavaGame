package engine.collision;

import engine.collision.collision_shapes.CollisionRect;
import engine.rendering.rendering_primitives.Rect;
import engine.Entity;

import java.util.Optional;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.ArrayList;

public class QuadTree {
	
	private class QuadTreeNode {
		private QuadTree tree;
		private int objectCount;
		private int depth;
		private CollisionRect bounds;
		private List<Entity> objects;
		private Optional<QuadTreeNode[]> children;
		
		private QuadTreeNode(CollisionRect bounds, QuadTree tree, int depth) {
			this.objectCount = 0;
			this.tree = tree;
			this.depth = depth;
			this.bounds = bounds;
			this.objects = new ArrayList<>();
			this.children = Optional.empty();
		}
		
		public QuadTreeNode(CollisionRect bounds, QuadTree tree) {
			this(bounds, tree, 0);
		}
		
		public boolean addObject(Entity obj) {
			boolean out = false;
			if(!this.objectBelongsToNode(obj))return out;
			this.objectCount++;
			out = true;
			if(this.isLeaf()) {
				
				if(this.objectCount > this.tree.getMaxNodeSize()) {
					this.extend();
					this.objectCount--;
					this.addObject(obj);
				}else {
					this.objects.add(obj);
				}
				
			}else {
				QuadTreeNode[] c = this.getChildrenToOperateOn(obj);
				for(QuadTreeNode v: c)v.addObject(obj);
			}
			return true;
		}
		
		public boolean testCollision(Entity obj) {
			if(this.isLeaf()) {
				for(Entity v: this.objects) if(v.testCollision(obj)) return true;
				return false;
			}else {
				QuadTreeNode[] c = this.getChildrenToOperateOn(obj);
				for(QuadTreeNode v: c) if(v.testCollision(obj)) return true;
				return false;
			}
		}

		public boolean testCollisionAgainstTarget(Entity obj, Entity target) {
			if(this.isLeaf()) {
				for(Entity v: this.objects) if(v == target && v.testCollision(obj)) return true;
				return false;
			}else {
				QuadTreeNode[] c = this.getChildrenToOperateOn(obj);
				for(QuadTreeNode v: c) if(v.testCollisionAgainstTarget(obj, target)) return true;
				return false;
			}
		}
		
		public void removeObject(Entity obj) {
			if(!this.objectBelongsToNode(obj))return;
			this.objectCount--;
			if(this.isLeaf()) {
				this.objects.remove(obj);
			}else {
				QuadTreeNode[] c = this.getChildrenToOperateOn(obj);
				for(QuadTreeNode v: c)v.removeObject(obj);
			}
			this.shake();
		}
		
		public void clear() {
			if(!this.isLeaf()) for(QuadTreeNode v: this.children.get()) v.clear();
			this.children = Optional.empty();
			if(!this.objects.isEmpty()) this.objects.clear();
		}
		
		private void extend() {
			List<Entity> l = new ArrayList<>();
			for(Entity v: this.objects)l.add(v);
			this.objects.clear();
			this.children = Optional.of(new QuadTreeNode[4]);
			for(int i = 0; i < 4; i++) {
				Vector2f pos = new Vector2f();
				Vector2f size = new Vector2f(this.bounds.size.x / 2.0f, this.bounds.size.y / 2.0f);
				pos.x = this.bounds.pos.x + (this.bounds.size.x * ((i%2==0)?-1.0f:1.0f)) / 2.0f;
				pos.y = this.bounds.pos.y + (this.bounds.size.y * ((i/2==0)?1.0f:-1.0f)) / 2.0f;
				this.children.get()[i] = new QuadTreeNode(new CollisionRect(pos, size), this.tree, this.depth + 1);
			}
			for(Entity v: l) {
				QuadTreeNode[] c = this.getChildrenToOperateOn(v);
				for(QuadTreeNode n: c)n.addObject(v);
			}
			
		}
		
		private void shake() {
			if(this.objectCount <= this.tree.getMaxNodeSize()) {
				if(!this.isLeaf()) {
					List<Entity> arr = new ArrayList<>();
					QuadTreeNode[] c = this.children.get();
					for(int i = 0; i < 4; i++) for(Entity v: c[i].objects) if(!arr.contains(v)) arr.add(v);
					this.objects = arr;
					this.children = Optional.empty();
				}
			}
		}
		
		private boolean objectBelongsToNode(Entity obj) {
			//obj.align();
			//return this.bounds.testCollision(obj.getCollider());
			return obj.testCollision(this.bounds);
		}
		
		private QuadTreeNode[] getChildrenToOperateOn(Entity obj) {
			int childrenCount = 0;
			obj.align();
			for(QuadTreeNode v: this.children.get()) if(v.objectBelongsToNode(obj)) childrenCount++;
			QuadTreeNode[] out = new QuadTreeNode[childrenCount];
			//System.out.println(childrenCount);
			childrenCount = 0;
			for(QuadTreeNode v: this.children.get()) if(v.objectBelongsToNode(obj)) {
				//System.out.println(childrenCount + " huh?");
				out[childrenCount] = v;
				childrenCount++;
			}
			return out;
		}
		
		private boolean isLeaf() {
			return this.children.isEmpty();
		}
		
		public void render(int index) {
			if(this.isLeaf()) {
				Vector3f col;
				if(index == 0)col = new Vector3f(1, 0, 0);
				else if(index == 1)col = new Vector3f(0, 1, 0);
				else if(index == 2)col = new Vector3f(0, 0, 1);
				else col = new Vector3f(1, 1, 0);
				Rect r = new Rect(this.bounds.pos, this.bounds.size, col);
				r.render();
			}else {
				QuadTreeNode[] arr = this.children.get();
				for(int i = 0; i < arr.length; i++) {
					arr[i].render(i);
				}
			}
		}
		
		public void printObjectCount() {
			System.out.println("<" + this.depth + "> {" + this.objectCount + "}");
			if(!this.isLeaf()) {
				QuadTreeNode[] arr = this.children.get();
				for(QuadTreeNode v: arr)v.printObjectCount();
			}
		}
		
	};
	
	public static final int DEFAULT_MAX_TREE_DEPTH = 16;
	public static final int DEFAULT_MAX_NODE_SIZE = 32;
	
	private final int maxTreeDepth;
	private final int maxNodeSize;
	
	private QuadTreeNode root;
	
	public QuadTree(int maxTreeDepth, int maxNodeSize, CollisionRect bounds) {
		this.maxTreeDepth = maxTreeDepth;
		this.maxNodeSize = maxNodeSize;
		this.root = new QuadTreeNode(bounds, this);
	}
	
	public QuadTree(CollisionRect bounds) {
		this(QuadTree.DEFAULT_MAX_TREE_DEPTH, QuadTree.DEFAULT_MAX_NODE_SIZE, bounds);
	}
	
	public QuadTree() {
		this(new CollisionRect(0.0f, 0.0f, 1.0f, 1.0f));
	}
	
	public QuadTree(int maxTreeDepth, int maxNodeSize) {
		this(maxTreeDepth, maxNodeSize, new CollisionRect(0, 0, 1, 1));
	}
	
	public void addObject(Entity obj) {
		this.root.addObject(obj);
	}
	
	public void removeObject(Entity obj) {
		this.root.removeObject(obj);
	}
	
	public boolean testCollision(Entity obj) {
		return this.root.testCollision(obj);
	}
	
	public boolean testCollisionAgainstTarget(Entity obj, Entity target) {
		return this.root.testCollisionAgainstTarget(obj, target);
	}
	
	public void clear() {
		this.root.clear();
	}
	
	public int getMaxTreeDepth() {
		return this.maxTreeDepth;
	}
	
	public int getMaxNodeSize() {
		return this.maxNodeSize;
	}
	
	public int getObjectCount() {
		return this.root.objectCount;
	}
	
	public void printObjectCount() {
		this.root.printObjectCount();
	}
	
	public void render() {
		this.root.render(0);
	}
	
}
