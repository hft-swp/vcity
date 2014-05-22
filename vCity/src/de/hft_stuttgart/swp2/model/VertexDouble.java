package de.hft_stuttgart.swp2.model;
  
/**
 * 
 * simple container class for a vertex in a 3D-space with double values
 * 
 * @author 12alsi1bif, 12bema1bif, 12tost1bif, 12riju1bif
 * 
 */
public class VertexDouble {
  
    private final double[] coords = new double[3];
      
    private boolean visited = false;
  
    /**
     * creates a new vertex from a double array.
     * 
     * @param coordinates
     *            double array of 3 doubles 
     */
    public VertexDouble(final double[] coordinates) {
        if (coordinates.length != 3) {
            throw new IllegalArgumentException(
                    "A 3D vertex should have exactly 3 doubles, you provided: "
                            + coordinates.length);
        }
        coords[0] = coordinates[0];
        coords[1] = coordinates[1];
        coords[2] = coordinates[2];
    }
  
    /**
     * creates a new vertex out of 3 coordinates.
     * 
     * @param x
     *            coordinate
     * @param y
     *            coordinate
     * @param z
     *            coordinate
     */
    public VertexDouble(double x, double y, double z) {
        coords[0] = x;
        coords[1] = y;
        coords[2] = z;
    }
  
    /**
     * 
     * @return all coordinates in one array. No copy is made.
     */
    public double[] getCoordinates() {
        return coords;
    }
  
    /**
     * 
     * @return the X value of the vertex (same as getCoordinates()[0])
     */
    public double getX() {
        return coords[0];
    }
  
    /**
     * 
     * @return the Y value of the vertex (same as getCoordinates()[1])
     */
    public double getY() {
        return coords[1];
    }
  
    /**
     * 
     * @return the Z value of the vertex (same as getCoordinates()[2])
     */
    public double getZ() {
        return coords[2];
    }
  
    /**
     * moves the point according to the given values
     * @param x
     * @param y
     * @param z
     */
    public void translate(double x, double y, double z) {
        coords[0] += x;
        coords[1] += y;
        coords[2] += z;
    }
      
    public void scale(double x, double y, double z) {
        coords[0] *= x;
        coords[1] *= y;
        coords[2] *= z;
    }
      
    public boolean wasVisited() {
        return visited;
    }
      
    public void visit() {
        visited = true;
    }
      
    public void resetVisit() {
        visited = false;
    }
  
} 