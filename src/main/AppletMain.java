package main;

import java.io.File;
import processing.core.PApplet;
import processing.data.JSONObject;

public class AppletMain extends PApplet {
	
	public static void main(String[] args) {
		JSONObject json = loadJSONObject(new File("config.json"));
		width = json.getInt("width");
		height = json.getInt("height");
		orange = (int)Long.parseLong(json.getString("orange").substring(2), 16)|0xff000000;
		System.out.println(json.getString("msg", ""));
		PApplet.main(AppletMain.class, args);
	}
	
	private static int width=300, height=300, orange=255;
	
	
	@Override
	public void settings() {
		size(width, height);
	}
	
	@Override
	public void setup() {
		ellipseMode(CENTER);
	}
	
	@Override
	public void draw() {
		background(0);
		fill(orange);
		circle(mouseX, mouseY, 50);
	}
	
}
