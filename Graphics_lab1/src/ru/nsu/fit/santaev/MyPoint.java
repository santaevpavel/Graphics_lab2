package ru.nsu.fit.santaev;

import java.awt.Color;
import java.awt.Point;

public class MyPoint extends Point{
	
	private static final long serialVersionUID = 1L;
	
	private Color color = Color.BLACK;
	
	public MyPoint(int x, int y, Color color){
		super(x, y);
		setColor(color);
	}
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
}
