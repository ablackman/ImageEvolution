import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EvolutionApplet extends JApplet {
    private JLabel targetTitle;
    private JLabel bestTitle;
    private JLabel evolvingTitle;
	
    private JLabel targetDisplayLabel;
    private JLabel bestDisplayLabel;
    private JLabel evolvingDisplayLabel;
    
    private JLabel fitnessLabel;
    private JLabel generationLabel;
    private JLabel improvementLabel;
   
    private JButton getImageButton;
    private JButton toggleStartButton;
    
    private JFileChooser fc;
    private BufferedImage targetImage;
    
    private CircleIndividual bestImage;
    private CircleIndividual evolvingImage;
    
    private int generation = 0;
    private int improvements = 0;
    private double bestFitness = 0;
    
    Font titleFont = new Font("Sans Serif", Font.BOLD, 28);
    Font statFont = new Font("Sans Serif", Font.BOLD, 24);
    
    private boolean isStart = false;
    
    SpringLayout spring = new SpringLayout();
    
    private Thread t;
    
    @Override
    public void init() {
        getContentPane().setBackground(new Color(220, 220, 230));
        
        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg"));
        
        try {
            targetImage = ImageIO.read(new URL(getCodeBase(), "eclipse-logo.png"));
        } catch (IOException ex) {
            //This shouldn't happen
        }
        
        for(int x = 0; x < targetImage.getWidth(); x++) {
            for(int y = 0; y < targetImage.getWidth(); y++) {
            	if((targetImage.getRGB(x, y) >> 24 & 0xFF) < 0xFF) {
            		targetImage.setRGB(x,  y, 0xFFFFFFFF);
            	}
            }
        }
        
        evolvingImage = new CircleIndividual(targetImage.getWidth(), targetImage.getHeight(), 256);
        bestImage = evolvingImage;
        
        setLayout(spring);
        
        //Create target image title
        targetTitle = new JLabel();
        targetTitle.setText("Target");
        targetTitle.setFont(titleFont);
        add(targetTitle);
        spring.putConstraint(SpringLayout.NORTH, targetTitle, 5,
                             SpringLayout.NORTH, this);
        spring.putConstraint(SpringLayout.WEST, targetTitle, 5,
                             SpringLayout.WEST, this);
        
        //Create target image display label
        targetDisplayLabel = new JLabel();
        targetDisplayLabel.setIcon(new ImageIcon(targetImage));
        targetDisplayLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(targetDisplayLabel);
        spring.putConstraint(SpringLayout.NORTH, targetDisplayLabel, 5,
                             SpringLayout.SOUTH, targetTitle);
        spring.putConstraint(SpringLayout.WEST, targetDisplayLabel, 0,
                             SpringLayout.WEST, targetTitle);
        
        //Create best image title
        bestTitle = new JLabel();
        bestTitle.setText("Best");
        bestTitle.setFont(titleFont);
        add(bestTitle);
        spring.putConstraint(SpringLayout.NORTH, bestTitle, 0,
                             SpringLayout.NORTH, targetTitle);
        spring.putConstraint(SpringLayout.WEST, bestTitle, 5,
                             SpringLayout.EAST, targetDisplayLabel);
        
        //Create best individual display label
        bestDisplayLabel = new JLabel();
        bestDisplayLabel.setIcon(new ImageIcon(bestImage.getImage()));
        bestDisplayLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(bestDisplayLabel);
        spring.putConstraint(SpringLayout.NORTH, bestDisplayLabel, 5,
                             SpringLayout.SOUTH, bestTitle);
        spring.putConstraint(SpringLayout.WEST, bestDisplayLabel, 0,
                             SpringLayout.WEST, bestTitle);
        
        //Create evolving image title
        evolvingTitle = new JLabel();
        evolvingTitle.setText("Evolving");
        evolvingTitle.setFont(titleFont);
        add(evolvingTitle);
        spring.putConstraint(SpringLayout.NORTH, evolvingTitle, 0,
                             SpringLayout.NORTH, targetTitle);
        spring.putConstraint(SpringLayout.WEST, evolvingTitle, 5,
                             SpringLayout.EAST, bestDisplayLabel);
        
        //create evolving individual display label
        evolvingDisplayLabel = new JLabel();
        evolvingDisplayLabel.setIcon(new ImageIcon(evolvingImage.getImage()));
        evolvingDisplayLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(evolvingDisplayLabel);
        spring.putConstraint(SpringLayout.NORTH, evolvingDisplayLabel, 5,
                             SpringLayout.SOUTH, evolvingTitle);
        spring.putConstraint(SpringLayout.WEST, evolvingDisplayLabel, 0,
                             SpringLayout.WEST, evolvingTitle);
        
        //create stats
        fitnessLabel = new JLabel();
        fitnessLabel.setText("Fitness: 0%");
        fitnessLabel.setFont(statFont);
        add(fitnessLabel);
        spring.putConstraint(SpringLayout.WEST, fitnessLabel, 5,
                             SpringLayout.WEST, this);
        spring.putConstraint(SpringLayout.NORTH, fitnessLabel, 5,
                             SpringLayout.SOUTH, targetDisplayLabel);
        
        generationLabel = new JLabel();
        generationLabel.setText("Generations: 0");
        generationLabel.setFont(statFont);
        add(generationLabel);
        spring.putConstraint(SpringLayout.NORTH, generationLabel, 5,
                             SpringLayout.SOUTH, fitnessLabel);
        spring.putConstraint(SpringLayout.WEST, generationLabel, 0,
                             SpringLayout.WEST, fitnessLabel);
        
        improvementLabel = new JLabel();
        improvementLabel.setText("Improvements: 0");
        improvementLabel.setFont(statFont);
        add(improvementLabel);
        spring.putConstraint(SpringLayout.NORTH, improvementLabel, 5,
                             SpringLayout.SOUTH, generationLabel);
        spring.putConstraint(SpringLayout.WEST, improvementLabel, 0,
                             SpringLayout.WEST, generationLabel);
        
        //create start/pause button
        toggleStartButton = new JButton();
        toggleStartButton.setText("Start");
        toggleStartButton.setFont(titleFont);
        toggleStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isStart = !isStart;
                toggleStartButton.setText((isStart) ? "Pause" : "Resume");
            }
        });
        add(toggleStartButton);
        spring.putConstraint(SpringLayout.NORTH, toggleStartButton, 5,
                             SpringLayout.SOUTH, targetDisplayLabel);
        spring.putConstraint(SpringLayout.WEST, toggleStartButton, 0,
                             SpringLayout.WEST, bestDisplayLabel);

      //create get image button
        getImageButton = new JButton();
        getImageButton.setText("Select a new image");
        getImageButton.setFont(titleFont);
        getImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isStart = false;
                toggleStartButton.setText("Resume");
                
                int returnVal = fc.showOpenDialog(EvolutionApplet.this);
                
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        targetImage = ImageIO.read(fc.getSelectedFile());
                        targetDisplayLabel.setIcon(new ImageIcon(targetImage));
                    } catch (IOException ex) {
                        //This still shouldn't happen
                    }
                    toggleStartButton.setText("Start");
                    evolvingImage = new CircleIndividual(targetImage.getWidth(), targetImage.getHeight(), 512);
                    evolvingDisplayLabel.setIcon(new ImageIcon(evolvingImage.getImage()));
                    bestImage = evolvingImage;
                    bestDisplayLabel.setIcon(new ImageIcon(bestImage.getImage()));
                    bestFitness = bestImage.calcFitness(targetImage);
                }
            }
        });
        add(getImageButton);
        spring.putConstraint(SpringLayout.NORTH, getImageButton, 5,
                             SpringLayout.SOUTH, toggleStartButton);
        spring.putConstraint(SpringLayout.WEST, getImageButton, 0,
                             SpringLayout.WEST, toggleStartButton);
        
        //run 4ever
        t = new Thread(new Runnable() {
            @Override
            public void run() {
               for (;;) {
                   if (isStart) {
                        update();
                    }
                   try {
                       Thread.sleep(0);
                   } catch (InterruptedException ex) {
                       
                   }
                }
            }
        });
        t.start();
    }
    
    public void update() {
    	generation++;
    	generationLabel.setText("Generation: " + generation);
        evolvingImage = new CircleIndividual(bestImage, 0.02);
        evolvingDisplayLabel.setIcon(new ImageIcon(evolvingImage.getImage()));
        double thisFitness = evolvingImage.calcFitness(targetImage);
        if(thisFitness > bestFitness) {
            improvements++;
            improvementLabel.setText("Improvements:" + improvements);
            bestImage = evolvingImage;
            bestFitness = thisFitness;
            fitnessLabel.setText("Fitness: " + String.format("%.2f", bestFitness) + "%");
            bestDisplayLabel.setIcon(new ImageIcon(bestImage.getImage()));
        }
    }
}
