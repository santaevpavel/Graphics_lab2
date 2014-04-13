package ru.nsu.fit.santaev;

public class Bernulli {

	public static double getY(double x){
		return Math.sqrt(Math.sqrt(1 + 4 * x * x) - x * x - 1);
	}
	public static double getX(double y){
		return Math.sqrt(Math.sqrt(-4 * y * y + 1) - (y * y - 1));
	}
}
