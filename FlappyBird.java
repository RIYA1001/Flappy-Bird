import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FlappyBird extends JPanel implements ActionListener {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final int BIRD_SIZE = 20;
    private static final int PIPE_WIDTH = 50;
    private static final int PIPE_HEIGHT = 200;
    private static final int PIPE_GAP = 150;
    private static final int PIPE_SPEED = 3;
    private static final int GRAVITY = 1;

    private Timer timer;
    private int birdY;
    private int velocity;
    private int pipeX;
    private int pipeY;
    private boolean gameover;
    private int score;

    private Clip flapSound;

    public FlappyBird() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);

        loadSound();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameover) {
                    velocity = -15; // Jump up
                    playSound();
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE && gameover) {
                    restartGame();
                }
            }
        });

        birdY = HEIGHT / 2;
        pipeX = WIDTH;
        pipeY = 0;
        gameover = false;
        score = 0;

        timer = new Timer(30, this);
        timer.start();
    }

    private void loadSound() {
        try {
            File file = new File("coin.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            flapSound = AudioSystem.getClip();
            flapSound.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void playSound() {
        if (flapSound.isRunning()) {
            flapSound.stop();
        }
        flapSound.setFramePosition(0);
        flapSound.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw bird
        g.setColor(Color.white);
        g.fillRect(WIDTH / 4, birdY - BIRD_SIZE / 2, BIRD_SIZE, BIRD_SIZE);

        // Draw pipe
        g.setColor(Color.green);
        g.fillRect(pipeX, pipeY, PIPE_WIDTH, PIPE_HEIGHT);
        g.fillRect(pipeX, pipeY + PIPE_HEIGHT + PIPE_GAP, PIPE_WIDTH, HEIGHT - pipeY - PIPE_HEIGHT - PIPE_GAP);

        // Display score
        g.setColor(Color.gray);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);

        if (gameover) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over! Press SPACE to restart", 50, HEIGHT / 2);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!gameover) {
            birdY += velocity;
            velocity += GRAVITY;

            pipeX -= PIPE_SPEED;

            if (pipeX + PIPE_WIDTH < WIDTH / 4 && pipeX + PIPE_WIDTH + PIPE_SPEED >= WIDTH / 4) {
                score++; // Increment score when bird passes through the gap
            }

            if (pipeX + PIPE_WIDTH < 0) {
                pipeX = WIDTH;
                pipeY = (int) (Math.random() * (HEIGHT - PIPE_HEIGHT - PIPE_GAP));
            }

            // Check for collision with pipes
            if ((birdY - BIRD_SIZE / 2 < pipeY + PIPE_HEIGHT || birdY + BIRD_SIZE / 2 > pipeY + PIPE_HEIGHT + PIPE_GAP) &&
                    (pipeX < WIDTH / 4 + BIRD_SIZE && pipeX + PIPE_WIDTH > WIDTH / 4)) {
                gameover = true;
            }

            // Check for out of bounds
            if (birdY - BIRD_SIZE / 2 <= 0 || birdY + BIRD_SIZE / 2 >= HEIGHT) {
                gameover = true;
            }
        }
        repaint();
    }

    private void restartGame() {
        birdY = HEIGHT / 2;
        velocity = 0;
        pipeX = WIDTH;
        pipeY = 0;
        gameover = false;
        score = 0;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().add(new FlappyBird(), BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
