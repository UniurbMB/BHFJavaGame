package engine.scenes;

import engine.Scene;
import engine.rendering.font.BFont;
import engine.rendering.font.FontMapSprite;
import engine.rendering.font.RenderCharacter;
import engine.rendering.rendering_primitives.Rect;
import engine.rendering.rendering_primitives.Sprite;

import static org.lwjgl.opengl.GL33.*;

public class FontTestScene extends Scene{

	Rect r = new Rect(0.0f, 0.0f, 0.5f, 0.5f, 0.25f, 0.25f, 0.25f);
	Sprite s = new Sprite("src/assets/images/test.png", -0.5f, -0.5f, 0.25f, 0.25f);
	BFont font = new BFont("/src/assets/fonts/yuji-syuku-japanese-400-normal.ttf", 60);
	RenderCharacter c = new RenderCharacter(font, 'A', 0.0f, 0.0f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f);
	FontMapSprite fm = new FontMapSprite(font,
			0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
	
	@Override
	public void init() {
		System.out.println("waht " + s.getTextureid());
		System.out.println("hiahf " + fm.getTextureid());
	}
	@Override
	public void update(float delta) {
		glClearColor(0.125f, 0.125f, 0.125f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		r.render();
		c.render();
		//fm.render();
		//s.render();
		//System.out.println("waht " + s.getTextureid());
	}
	
	@Override
	public void cleanUp() {
		this.c.destroy();
		this.fm.destroy();
	}

}
