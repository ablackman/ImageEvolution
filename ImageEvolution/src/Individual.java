import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class Individual {
    BufferedImage image;
    
private ArrayList<Circle> DNA;
    
    /**
     * GENOTYPE LAYOUT:
     * X
     * Y
     * Radius
     * Red
     * Green
     * Blue
     * Alpha
     */
    
    private int circles;
    
    public Individual(int width, int height, int numCircles) {
        circles = numCircles;
        DNA = new ArrayList();
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int i = 0; i < circles; i++) {
        	DNA.add(new Circle((int)(Math.random() * width), (int)(Math.random() * height), Math.random(),
        	                   (int)(Math.random() * 50), (int)(Math.random() * 256), (int)(Math.random() * 256),
        	                   (int)(Math.random() * 256), 0));
        }
        
        Collections.sort(DNA);
        drawImage();
    }
    
    public Individual(Individual parent, double mutationRate, int circleCount) {
    	ArrayList<Circle> parentDNA = parent.getDNA();
    	
        circles = circleCount;
    	DNA = new ArrayList();
        
        image = new BufferedImage(parent.getImage().getWidth(), parent.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
        
        int i = 0;
        
        for(; i < circles && i < parentDNA.size(); i++) {
        	Circle pc = parentDNA.get(i);
        	DNA.add(new Circle(pc.x, pc.y, pc.z, pc.radius, pc.r, pc.g, pc.b, pc.a));
        	Circle c = DNA.get(i);
        	if(Math.random() < mutationRate) {
        		double r = Math.random();
            	
            	if(r < 0.2){
                    c.x = (int)mutateValue(c.x, 50, 0, image.getWidth());
                    c.y = (int)mutateValue(c.y, 50, 0, image.getHeight());    
                } else if(r < 0.3) {
                	c.z = mutateValue(c.z, 0.5, 0, 1);
                } else if(r < 0.5) {
                	c.radius = (int)mutateValue(c.radius, 10, 1, 50);
                } else if(r < 0.75) {
                	c.r = (int)mutateValue(c.r, 100, 0, 255);
                  	c.g = (int)mutateValue(c.g, 100, 0, 255);
                    c.b = (int)mutateValue(c.b, 100, 0, 255);
                } else {
                    c.a = (int)mutateValue(c.a, 30, 0, 255);
                }
        	}
        }
        
        for(; i < circles; i++) {
            DNA.add(new Circle((int)(Math.random() * image.getWidth()), (int)(Math.random() * image.getHeight()), Math.random(),
                    (int)(Math.random() * 50), (int)(Math.random() * 256), (int)(Math.random() * 256),
                    (int)(Math.random() * 256), 0));
        }
        
        Collections.sort(DNA);
        drawImage();
    }
    
    private void drawImage() {
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        
        for(int i = 0; i < circles; i++) {
        	Circle c = DNA.get(i);
            g2d.setColor(new Color(c.r, c.g, c.b, c.a));
            g2d.fillOval(c.x - c.radius, c.y - c.radius, c.radius * 2, c.radius * 2);
        }
    }
    
    public ArrayList getDNA() {
        return DNA;
    }
    
    protected static double mutateValue(double current, double range, double min, double max) {
        current += (int)(Math.random() * range * 2) - range;
        current = Math.max(min, current);
        current = Math.min(max, current);
        return current;
    }
    
    public double calcFitness(BufferedImage target) {
        long difference = 0;
        
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                Color c1 = new Color(image.getRGB(x, y));
                Color c2 = new Color(target.getRGB(x, y));
                int dR = Math.abs(c1.getRed() - c2.getRed());
                int dG = Math.abs(c1.getGreen() - c2.getGreen());
                int dB = Math.abs(c1.getBlue() - c2.getBlue());
                difference += dR + dG + dB;
            }
        }
        double diffPercent = (double)difference / (image.getWidth() * image.getHeight());
        diffPercent /= (255 * 3);
        double fitness = 100 - diffPercent * 100;
        return fitness;
    }
    
    public BufferedImage getImage() {
        return image;
    }
}
