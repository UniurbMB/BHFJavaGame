package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL33.*;

public class ShaderProgram {
	
	private ArrayList<Integer> shaderSources;
	private HashMap<String, Integer> attributes;
	private int programid;
	
	public ShaderProgram(String vertShaderSource, String fragShaderSource) {
		this.attributes = new HashMap<>();
		this.shaderSources = new ArrayList<>();
		this.shaderSources.add(loadShader(vertShaderSource, GL_VERTEX_SHADER));
		this.shaderSources.add(loadShader(fragShaderSource, GL_FRAGMENT_SHADER));
		this.programid = glCreateProgram();
		for(Integer v: this.shaderSources) {
			glAttachShader(programid, v);
		}
		glLinkProgram(programid);
		glValidateProgram(programid);
	}
	
	public ShaderProgram() {
		this.attributes = new HashMap<>();
		this.shaderSources = new ArrayList<>();
		this.programid = 0;
	}
	
	public void addSource(String source, int type) {
		this.shaderSources.add(loadShader(source, type));
	}
	
	public void compile() {
		if(this.programid == 0) {
			this.programid = glCreateProgram();
			for(Integer v: this.shaderSources) {
				glAttachShader(programid, v);
			}
			glLinkProgram(programid);
			glValidateProgram(programid);
		}else {
			throw new RuntimeException(this.toString() + " has already compiled its shaders!");
		}
	}
	
	public void start() {
		glUseProgram(programid);
	}
	
	public void stop() {
		glUseProgram(0);
	}
	
	public void cleanup() {
		stop();
		for(Integer v: this.shaderSources) {
			glDetachShader(programid, v);
			glDeleteShader(v);
		}
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
	
	public int getAttribLocation(String attribName) {
		int result;
		if(this.attributes.get(attribName) == null) {
			this.attributes.put(attribName, glGetUniformLocation(this.programid, attribName));
		}
		result = this.attributes.get(attribName);
		return result;
	}
	
	public int getProgramid() {
		return this.programid;
	}
}
