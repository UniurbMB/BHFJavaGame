package engine.scenes;

import engine.Scene;
import engine.rendering.font.BFont;
import engine.rendering.rendering_primitives.Sprite;

import static org.lwjgl.opengl.GL33.*;

public class FontTestScene extends Scene{
	
	Sprite s = new Sprite(0, 0, 0, 0, 1, 1, 1);
	//BFont font = new BFont("src/assets/fonts/yuji-syuku-japanese-400-normal.ttf", 228.0f);
	BFont font = new BFont("src/assets/fonts/wdxl-lubrifont-jp-n-japanese-400-normal.ttf", 224.0f);
	
	
	@Override
	public void init() {
		this.s.setTextureid(font.getTextureid());
		s.size.x = 1.0f;
		s.size.y = -1.0f;
		font.getGlyph('!');
	}
	@Override
	public void update(float delta) {
		glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		s.render();
	}

}
