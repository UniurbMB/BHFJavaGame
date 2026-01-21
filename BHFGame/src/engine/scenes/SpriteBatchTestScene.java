package engine.scenes;

import java.util.ArrayList;

import org.joml.Vector2f;

import engine.Scene;
import engine.rendering.SpriteBatchRenderer;
import engine.rendering.rendering_primitives.Sprite;

import static org.lwjgl.opengl.GL33.*;

public class SpriteBatchTestScene extends Scene{

	int spriteCount = 1000000;
	int rows = 1000;
	int columns = 1000;
	Sprite s = new Sprite("src/assets/images/ship.png");
	SpriteBatchRenderer b = new SpriteBatchRenderer(s);
	ArrayList<Sprite> sprites = new ArrayList<>();
	
	@Override
	public void init() {
		for(int i = 0; i < spriteCount; i++) {
			int rowPos = (i / rows) - (rows / 2);
			int colPos = (i % columns) - (columns / 2);
			Vector2f pos = new Vector2f(	((float)colPos) / (float)(columns/2),
											((float)rowPos) / (float)(rows/2));
			//Vector2f pos = new Vector2f(0.25f, 0);
			Vector2f size= new Vector2f(0.005f, 0.005f);
			sprites.add(new Sprite("src/assets/images/ship.png", pos, size));
		}
		b.constructBuffer(sprites, 64);
	}
	
	@Override
	public void update(float delta) {
		glClearColor(0.25f, 0.25f, 0.25f, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		
		b.render();
		//for(Sprite v: sprites)v.render();
		System.out.println(w.getFps());
		
	}

}
