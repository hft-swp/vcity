package de.hft_stuttgart.swp2.model;

import java.util.ArrayList;
import java.util.List;

public class Building extends MeshInterface {
	
    private String id;
	private double volume;
	private List<Vertex> polygon;
	private ArrayList<ShadowTriangle> shadowTriangles = new ArrayList<>();
	
	public Building(){
	}
	
	public Building(String id, List<Vertex> polygon){
	    this.id = id;
	    this.polygon = polygon;
	}
	
	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public double getVolume() {
		return volume;
	}
	
	public String getId(){
	    return id;
	}
	
	public List<Vertex> getPolygon(){
	    return polygon;
	}
	
	public void addShadowTriangle(ShadowTriangle t ) {
		shadowTriangles.add(t);
	}
	
	public ArrayList<ShadowTriangle> getShadowTriangles() {
		return shadowTriangles;
	}
	
}
