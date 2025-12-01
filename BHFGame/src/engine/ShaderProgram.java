package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL33.*;

public class ShaderProgram {
	
	private int vertShader, fragShader, programid;
	
	public ShaderProgram(String vertShaderSource, String fragShaderSource) {
		this.vertShader = loadShader(vertShaderSource, GL_VERTEX_SHADER);
		this.fragShader = loadShader(fragShaderSource, GL_FRAGMENT_SHADER);
		this.programid = glCreateProgram();
		glAttachShader(programid, vertShader);
		glAttachShader(programid, fragShader);
		glLinkProgram(programid);
		glValidateProgram(programid);
	}
	
	public void start() {
		glUseProgram(programid);
		
	}
	
	public void stop() {
		glUseProgram(0);
	}
	
	public void cleanup() {
		stop();
		glDetachShader(programid, vertShader);
		glDetachShader(programid, fragShader);
		glDeleteShader(vertShader);
		glDeleteShader(fragShader);
		glDeleteProgram(programid);
	}
	
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		}catch(IOException e) {
			System.err.println("Could not read " + file);
			e.printStackTrace();
			System.exit(-1);
		}
		int shader = glCreateShader(type);
		glShaderSource(shader, shaderSource);
		glCompileShader(shader);
		if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Could not compile shader " + file);
			System.err.println(glGetShaderInfoLog(shader, 1024));
			System.exit(-1);
		}
		return shader;
	}
}
