import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CircleIndividual extends Individual{
    private ArrayList<Integer> DNA;
    
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
    
    public CircleIndividual(int width, int height, int numCircles) {
        circles = numCircles;
        DNA = new ArrayList();
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getWidth(); y++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        
        for(int i = 0; i < circles; i++) {
            DNA.add(width / 2);
            DNA.add(height / 2);
            DNA.add(10);
            DNA.add(128);
            DNA.add(128);
            DNA.add(128);
            DNA.add(0);
        }
        
        drawImage();
    }
    
    public CircleIndividual(CircleIndividual parent, double mutationRate) {
        ArrayList<Integer> parentDNA = parent.getDNA();
        
        circles = parentDNA.size() / 7;
        DNA = (ArrayList<Integer>)parentDNA.clone();
        
        image = new BufferedImage(parent.getImage().getWidth(), parent.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
        
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getWidth(); y++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        
        for(int i = 0; i < circles * 7; i += 7) {
            if(Math.random() < mutationRate){
                DNA.set(i, mutateValue(DNA.get(i), 50, 0, image.getWidth()));
                DNA.set(i + 1, mutateValue(DNA.get(i + 1), 50, 0, image.getHeight()));
            }
            if(Math.random() < mutationRate) DNA.set(i + 2, mutateValue(DNA.get(i + 2), 10, 1, 50));
            if(Math.random() < mutationRate) DNA.set(i + 3, mutateValue(DNA.get(i + 3), 50, 0, 255));
            if(Math.random() < mutationRate) DNA.set(i + 4, mutateValue(DNA.get(i + 4), 50, 0, 255));
            if(Math.random() < mutationRate) DNA.set(i + 5, mutateValue(DNA.get(i + 5), 50, 0, 255));
            if(Math.random() < mutationRate) DNA.set(i + 6, mutateValue(DNA.get(i + 6), 50, 0, 255));
        }
        
        drawImage();
    }
    
    private void drawImage() {
        Graphics2D g2d = image.createGraphics();
        
        for(int i = 0; i < circles * 7; i += 7) {
            int radius = DNA.get(i + 2);
            int x = DNA.get(i) - radius;
            int y = DNA.get(i + 1) - radius;
            int r = DNA.get(i + 3);
            int g = DNA.get(i + 4);
            int b = DNA.get(i + 5);
            int a = DNA.get(i + 6);
            g2d.setColor(new Color(r, g, b, a));
            g2d.fillOval(x, y, radius * 2, radius * 2);
        }
    }
    
    public ArrayList getDNA() {
        return DNA;
    }
}
