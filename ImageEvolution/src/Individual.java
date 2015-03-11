import java.awt.Color;
import java.awt.image.BufferedImage;

public abstract class Individual {
    BufferedImage image;
    
    protected static int mutateValue(int current, int range, int min, int max) {
        current += (int)(Math.random() * range * 2) - range;
        current = Math.max(min, current);
        current = Math.min(max, current);
        return current;
    }
    
    public double calcFitness(BufferedImage target) {
        long difference = 0;
        
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getWidth(); y++) {
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
