package de.hft_stuttgart.swp2.parser;

import java.util.Vector;

import de.hft_stuttgart.swp2.model.Vertex;


public class TestPolygon {
    
    private String id;
    private Vector<Vertex> points = new Vector<>();
    
    public TestPolygon(String id, Vector<Vertex> points){
	this.id = id;
	this.points = points;
    }
    
    public String getId(){
	return id;
    }
    
    public Vector<Vertex> getPoints (){
	return points;
    }

}
