import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.Timer;
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
    private JButton pauseButton;
    private JButton saveImageButton;
    
    private JSpinner mutationSpinner;
    private JSpinner circleSpinner;
    private JLabel mutationSpinnerTitle;
    private JLabel circleSpinnerTitle;
    private JLabel percentLabel;
    
    private double mutationRate= 0.02;
    private int circleCount = 200;
    
    private JFileChooser fc;
    private BufferedImage targetImage;
    
    private Individual bestImage;
    private Individual evolvingImage;
    
    private int generation = 0;
    private int improvements = 0;
    private double bestFitness = 0;
    
    private Font titleFont = new Font("Sans Serif", Font.BOLD, 28);
    private Font statFont = new Font("Sans Serif", Font.BOLD, 24);
    private Font optionFont = new Font("Sans Serif", Font.PLAIN, 18);
    
    private boolean pause = true;
    
    private SpringLayout spring = new SpringLayout();
    
    @Override
    public void init() {
        getContentPane().setBackground(new Color(220, 220, 230));
        
        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg"));
        
        try {
            targetImage = ImageIO.read(new URL("http://i.imgur.com/8xQ0G9v.jpg"));
        } catch (IOException ex) {
            //This shouldn't happen
        }
        
        for(int x = 0; x < targetImage.getWidth(); x++) {
            for(int y = 0; y < targetImage.getHeight(); y++) {
            	if((targetImage.getRGB(x, y) >> 24 & 0xFF) < 0xFF) {
            		targetImage.setRGB(x,  y, 0xFFFFFFFF);
            	}
            }
        }
        
        evolvingImage = new Individual(targetImage.getWidth(), targetImage.getHeight(), circleCount);
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
        
        //create stat labels
        fitnessLabel = new JLabel();
        fitnessLabel.setText("Fitness: 0%");
        fitnessLabel.setFont(statFont);
        add(fitnessLabel);
        spring.putConstraint(SpringLayout.NORTH, fitnessLabel, 0,
                             SpringLayout.NORTH, evolvingDisplayLabel);
        spring.putConstraint(SpringLayout.WEST, fitnessLabel, 5,
                             SpringLayout.EAST, evolvingDisplayLabel);
        
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
        pauseButton = new JButton();
        pauseButton.setText("Start");
        pauseButton.setFont(new Font("Sans Serif", Font.BOLD, 42));
        pauseButton.setPreferredSize(new Dimension(200, 75));
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause = !pause;
                pauseButton.setText((pause) ? "Resume" : "Pause");
            }
        });
        add(pauseButton);
        spring.putConstraint(SpringLayout.SOUTH, pauseButton, 0,
                             SpringLayout.SOUTH, evolvingDisplayLabel);
        spring.putConstraint(SpringLayout.WEST, pauseButton, 5,
                             SpringLayout.EAST, evolvingDisplayLabel);

      //create get image button
        getImageButton = new JButton();
        getImageButton.setText("Select a new image");
        getImageButton.setFont(optionFont);
        getImageButton.setPreferredSize(new Dimension(200, 25));
        getImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause = true;
                pauseButton.setText("Resume");
                
                int returnVal = fc.showOpenDialog(EvolutionApplet.this);
                
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        targetImage = ImageIO.read(fc.getSelectedFile());
                        targetDisplayLabel.setIcon(new ImageIcon(targetImage));
                    } catch (IOException ex) {
                        //This still shouldn't happen
                    }
                    
                    for(int x = 0; x < targetImage.getWidth(); x++) {
                        for(int y = 0; y < targetImage.getHeight(); y++) {
                            if((targetImage.getRGB(x, y) >> 24 & 0xFF) < 0xFF) {
                                    targetImage.setRGB(x,  y, 0xFFFFFFFF);
                            }
                        }
                    }
                    
                    pauseButton.setText("Start");
                    evolvingImage = new Individual(targetImage.getWidth(), targetImage.getHeight(), 512);
                    evolvingDisplayLabel.setIcon(new ImageIcon(evolvingImage.getImage()));
                    bestImage = evolvingImage;
                    bestDisplayLabel.setIcon(new ImageIcon(bestImage.getImage()));
                    bestFitness = bestImage.calcFitness(targetImage);
                }
            }
        });
        add(getImageButton);
        spring.putConstraint(SpringLayout.NORTH, getImageButton, 5,
                             SpringLayout.SOUTH, targetDisplayLabel);
        spring.putConstraint(SpringLayout.WEST, getImageButton, 0,
                             SpringLayout.WEST, targetDisplayLabel);
        spring.putConstraint(SpringLayout.EAST, getImageButton, 0,
                             SpringLayout.EAST, targetDisplayLabel);
        
        //create save image button
        saveImageButton = new JButton();
        saveImageButton.setText("Save this image");
        saveImageButton.setFont(optionFont);
        saveImageButton.setPreferredSize(new Dimension(200, 25));
        saveImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause = true;
                pauseButton.setText("Resume");
                
                int returnVal = fc.showSaveDialog(EvolutionApplet.this);
                
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    try {
                    	String path = fc.getSelectedFile().getAbsolutePath();
                    	if(!path.endsWith(".jpg")) {
                    		path += ".jpg";
                    	}
                        ImageIO.write(bestImage.getImage(), "PNG", new File(path));
                    } catch (IOException ex) {
                        //Still not happening
                    }
                }
            }
        });
        add(saveImageButton);
        
        spring.putConstraint(SpringLayout.NORTH, saveImageButton, 5,
                             SpringLayout.SOUTH, bestDisplayLabel);
        spring.putConstraint(SpringLayout.WEST, saveImageButton, 0,
                             SpringLayout.WEST, bestDisplayLabel);
        spring.putConstraint(SpringLayout.EAST, saveImageButton, 0,
                             SpringLayout.EAST, bestDisplayLabel);
        
        //create mutation spinner title
        mutationSpinnerTitle = new JLabel();
        mutationSpinnerTitle.setText("Mutation Rate:");
        mutationSpinnerTitle.setFont(statFont);
        add(mutationSpinnerTitle);
        
        spring.putConstraint(SpringLayout.NORTH, mutationSpinnerTitle, 5,
                             SpringLayout.SOUTH, getImageButton);
        spring.putConstraint(SpringLayout.WEST, mutationSpinnerTitle, 0,
                             SpringLayout.WEST, getImageButton);
        
        //create mutation spinner
        mutationSpinner = new JSpinner(new SpinnerNumberModel(2, 0.1, 100, 0.1));
        mutationSpinner.setFont(statFont);
        add(mutationSpinner);
        
        spring.putConstraint(SpringLayout.NORTH, mutationSpinner, 0,
                             SpringLayout.NORTH, mutationSpinnerTitle);
        spring.putConstraint(SpringLayout.WEST, mutationSpinner, 2,
                             SpringLayout.EAST, mutationSpinnerTitle);
        
        //create % sign
        percentLabel = new JLabel();
        percentLabel.setText("%");
        percentLabel.setFont(statFont);
        add(percentLabel);
        
        spring.putConstraint(SpringLayout.NORTH, percentLabel, 0,
        		             SpringLayout.NORTH, mutationSpinner);
        spring.putConstraint(SpringLayout.WEST, percentLabel, 5,
        		             SpringLayout.EAST, mutationSpinner);
        
        circleSpinnerTitle = new JLabel();
        circleSpinnerTitle.setText("Circles:");
        circleSpinnerTitle.setFont(statFont);
        add(circleSpinnerTitle);
        
        spring.putConstraint(SpringLayout.NORTH, circleSpinnerTitle, 5,
                             SpringLayout.SOUTH, mutationSpinnerTitle);
        spring.putConstraint(SpringLayout.WEST, circleSpinnerTitle, 0,
                             SpringLayout.WEST, mutationSpinnerTitle);
        
        circleSpinner = new JSpinner(new SpinnerNumberModel(200, 1, 1000, 1));
        circleSpinner.setFont(statFont);
        add(circleSpinner);
        
        spring.putConstraint(SpringLayout.NORTH, circleSpinner, 0,
                             SpringLayout.NORTH, circleSpinnerTitle);
        spring.putConstraint(SpringLayout.WEST, circleSpinner, 0,
                             SpringLayout.WEST, mutationSpinner);
        
        
        Timer t = new Timer(0, new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if(!pause) {
        			update();
        		}
        	}
        });
        t.setRepeats(true);
        t.setDelay(50);
        t.start();       
    }
    
    public void update() {
    	generation++;
    	generationLabel.setText("Generation: " + generation);
    	
    	mutationRate = (double)(mutationSpinner.getValue()) / 100;
    	circleCount = (int) (circleSpinner.getValue());
        evolvingImage = new Individual(bestImage, mutationRate, circleCount);
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
