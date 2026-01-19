package engine.scenes;

import engine.Scene;
import engine.events.KeyListener;
import engine.rendering.font.BFont;
import engine.rendering.font.BText;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;

public class FontTestScene extends Scene{
	
	BFont font = new BFont("src/assets/fonts/yuji-syuku-japanese-400-normal.ttf", 228.0f);
	BFont font2 = new BFont("src/assets/fonts/wdxl-lubrifont-jp-n-japanese-400-normal.ttf", 185.0f);
	BText text = new BText("TEST", font2, true, -1.0f, -0.5f, 0.15f, 0.15f, 0.005f, 1.0f, 1.0f, 1.0f);
	BText number = new BText("none", font, false, 0.85f, 0.5f, 0.15f, 0.15f, 0.005f, 1.0f, 1.0f, 1.0f);
	boolean debounce = true;
	boolean current = false;
	int currentNumber = 0;
	
	
	@Override
	public void init() {}
	@Override
	public void update(float delta) {
		glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		//font.render();
		number.setText(Integer.toString(currentNumber));
		text.render();
		number.render();
		if(KeyListener.isKeyPressed(GLFW_KEY_T) && debounce) {
			debounce = false;
			if(current) {
				text.setText("HELLO 123");
				current = false;
			}else {
				text.setText("NICE PRINTING");
				current = true;
			}
		}else if(!KeyListener.isKeyPressed(GLFW_KEY_T)) {
			debounce = true;
		}
		currentNumber++;
	}

}
