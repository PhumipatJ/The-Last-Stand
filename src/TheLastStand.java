import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;


public class TheLastStand extends JPanel {

    private Soldier soldier;
    private ArrayList<Zombie> zombies = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private int score;
    private int wave;
    private int highestScore;
    private int highestWave;
    private int zombieAmount;
    private int zombieRemain;
    private int spawnDelay;
    private Zombie currentTargetedZombie = null;
    private boolean gameOver = false;
    private boolean inHome = true;
    URL backgroundImageUrl = getClass().getResource("/picture/background.png");
    private Image backgroundImage = new ImageIcon(backgroundImageUrl).getImage();

    public TheLastStand() {
        this.score = 0;
        this.wave = 0;
        this.highestScore = 0;
        this.highestWave = 0;
        this.zombieAmount = 5;
        this.spawnDelay = 3000;
        this.zombieRemain = 5;
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(600, 700));
        this.addKeyListener(new KeyboardListener());

        playSound("sound/backgroundSong.wav");

        soldier = new Soldier(260, 580);

        Thread gameThread = new Thread(new GameStart());
        gameThread.start();
    }

    public void playSound(String soundFile) {
        try {
            URL soundUrl = getClass().getResource(soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            if(soundFile.equals("sound/backgroundSong.wav")){
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public class GameStart implements Runnable {
        @Override
        public void run() {
            spawnZombie(zombieAmount,spawnDelay);

            while (!gameOver) {
                if (zombieRemain == 0) {
                    zombieAmount++;
                    zombieRemain = zombieAmount;
                    spawnDelay = Math.max(2000, spawnDelay - 100);
                    spawnZombie(zombieAmount,spawnDelay);
                }

                for (int i = 0; i < zombies.size(); i++) {
                    Zombie zombie = zombies.get(i);
                    if (checkCollision(zombie)) {
                        playSound("/sound/gameOver.wav");
                        gameOver = true;
                        break;
                    }
                }

                for (int i = 0; i < bullets.size(); i++) {
                    Bullet bullet = bullets.get(i);
                    bullet.startMoving();
                    if (bullet.getY() <= bullet.getTargetY()) {
                        bullet.stopThread();
                        bullets.remove(i);
                        i++;
                    }
                }

                repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void spawnZombie(int zombieAmounts, int spawnDelay) {
        wave++;
        Random rand = new Random();
        String[] words = {
                "head", "brain", "skull", "eyes", "jaw", "teeth", "neck", "spine",
                "heart", "lungs", "chest", "hand", "foot", "bone", "nail", "arm", "eye",
                "rib", "leg", "toe", "kidneys", "spleen", "liver", "elbow", "ulna",
                "pleura", "ileum", "muscle", "tendon", "artery", "throat", "pulmonary",
                "hemoglobin", "epiglottis", "intestines", "shoulder", "knuckles", "forehead",
                "collarbone", "pancreas", "gallbladder", "diaphragm", "humerus", "radius",
                "femur", "patella", "tibia", "fibula", "scapula", "sternum", "thorax", "ventricle",
                "atrium", "aorta", "capillary", "lymph", "cartilage", "ligament", "thymus",
                "urethra", "rectum"
        };

        ArrayList<Character> repeatedInitial = new ArrayList<>();

        Timer spawnTimer = new Timer(spawnDelay, new ActionListener() {
            int zombiesSpawned = zombieAmounts;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (zombiesSpawned <= 0) {
                    ((Timer) e.getSource()).stop(); // Stop the timer when all zombies are spawned
                    return;
                }

                int x, y;
                boolean validPosition;
                do {
                    validPosition = true;
                    x = 100 + rand.nextInt(325);
                    y = -50 - rand.nextInt(100);

                    // Check if the new position overlaps with existing zombies
                    for (int i = 0; i < zombies.size(); i++) {
                        Zombie existingZombie = zombies.get(i);
                        if (isOverlapping(x, y, existingZombie)) {
                            validPosition = false;
                            break; // Exit the loop and try again
                        }
                    }
                } while (!validPosition);

                String word;
                char initial;

                do {
                    word = words[rand.nextInt(words.length)];
                    initial = word.charAt(0);
                } while (repeatedInitial.contains(initial));

                repeatedInitial.add(initial);

                Zombie zombie;
                switch (rand.nextInt(4)) {
                    case 0:
                        zombie = new FatZombie(x, y, word);
                        break;
                    case 1:
                        zombie = new GirlZombie(x, y, word);
                        break;
                    case 2:
                        zombie = new WomenZombie(x, y, word);
                        break;
                    default:
                        zombie = new BoyZombie(x, y, word);
                        break;
                }

                if(wave%5==0){
                    zombie.increaseSpeed();
                }
                zombies.add(zombie);
                zombie.startMoving();

                zombiesSpawned--;
            }

            private boolean isOverlapping(int x, int y, Zombie existingZombie) {
                int zombieWidth = 80;
                int zombieHeight = 120;

                float existingX = existingZombie.getX();
                float existingY = existingZombie.getY();

                return (x < existingX + zombieWidth && x + zombieWidth > existingX &&
                        y < existingY + zombieHeight && y + zombieHeight > existingY);
            }

        });

        spawnTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        if (inHome) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("The Last Stand", 125, getHeight() / 2 - 100);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 36));
            g.drawString("Highest Wave: " + highestWave, getWidth() / 2 - 140, 300 );
            g.drawString("Highest Score: " + highestScore, getWidth() / 2 - 140, 350);

            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press Space to Start", getWidth() / 2 - 100, getHeight() / 2 +100);
        }
        else if (!gameOver)
        {
            soldier.draw(g);

            for (int i = 0; i < zombies.size(); i++) {
                Zombie zombie = zombies.get(i);
                zombie.draw(g);
            }

            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                bullet.draw(g);
                if (bullet.getY() <= bullet.getTargetY()) {
                    bullets.remove(i);
                    i--;
                }
            }

            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(500, 0, 100, 75);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Score: " + score, 505, 25);
            g.drawString("Wave: " + wave, 505, 50);

            //g.setColor(Color.BLUE);
            //g.drawLine(0,150,800,150);
        }
        else if(gameOver)
        {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Game Over", getWidth() / 2 - 130, getHeight() / 2 - 50);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 36));
            g.drawString("Wave: " + wave, getWidth() / 2 - 80, getHeight() / 2 + 70);
            g.drawString("Score: " + score, getWidth() / 2 - 80, getHeight() / 2 + 20);


            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press Space to Continue", 160, getHeight() / 2 + 300);
        }

    }

    public class KeyboardListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            char typedChar = e.getKeyChar();

            if (currentTargetedZombie == null) {
                for (int i = 0; i < zombies.size(); i++) {
                    Zombie zombie = zombies.get(i);
                    String word = zombie.getWord();

                    if (word.charAt(0) == typedChar) {
                        playSound("/sound/hitShot.wav");
                        soldier.setFaceDirection(zombie.getX()+40);
                        zombie.setOrangeText();
                        currentTargetedZombie = zombie;
                        zombie.setWord(word.substring(1));
                        bullets.add(new Bullet(soldier.getX() + 40, soldier.getY(), zombie));
                    }
                    else{
                        playSound("/sound/missShot.wav");
                    }
                }
            }
            else {
                String word = currentTargetedZombie.getWord();

                if (!word.isEmpty() && word.charAt(0) == typedChar) {
                    playSound("/sound/hitShot.wav");
                    currentTargetedZombie.setWord(word.substring(1));
                    soldier.setFaceDirection(currentTargetedZombie.getX()+40);
                    bullets.add(new Bullet(soldier.getX() + 40, soldier.getY(), currentTargetedZombie));

                    if (currentTargetedZombie.getWord().isEmpty()) {
                        Timer timer = new Timer(600, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (zombies.contains(currentTargetedZombie)) {
                                    currentTargetedZombie.stopThread();
                                    zombies.remove(currentTargetedZombie);
                                    if(currentTargetedZombie instanceof FatZombie || currentTargetedZombie instanceof WomenZombie){
                                        playSound("/sound/zombieDie2.wav");
                                    }
                                    else if(currentTargetedZombie instanceof GirlZombie || currentTargetedZombie instanceof BoyZombie){
                                        playSound("/sound/zombieDie1.wav");
                                    }

                                }
                                bullets.clear();
                                zombieRemain--;
                                score += currentTargetedZombie.getScore();
                                currentTargetedZombie = null;
                                ((Timer)e.getSource()).stop();
                            }
                        });
                        timer.setRepeats(false); // execute once
                        timer.start();
                    }
                }
                else{
                    playSound("/sound/missShot.wav");
                }
            }
        }


        @Override
        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (inHome) {
                    inHome = false;
                }
                else if (!inHome && gameOver) {
                    gameOver = false;
                    inHome = true;
                    if(score >= highestScore){
                        highestScore = score;
                    }
                    if(wave >= highestWave){
                        highestWave = wave;
                    }
                    score = 0;
                    wave = 0;
                    zombieAmount = 5;
                    spawnDelay = 3000;
                    zombieRemain = 5;
                    zombies.clear();
                    currentTargetedZombie = null;

                    Thread gameThread = new Thread(new GameStart());
                    gameThread.start();
                }
            }


        }

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    private boolean checkCollision(Zombie zombie) {
        int soldierX = soldier.getX();
        int soldierY = soldier.getY()+50;
        int soldierWidth = 80;
        int soldierHeight = 120;

        int zombieX = (int) zombie.getX();
        int zombieY = (int) zombie.getY();
        int zombieWidth = 80;
        int zombieHeight = 120;

        return (soldierX < zombieX + zombieWidth &&
                soldierX + soldierWidth > zombieX &&
                soldierY < zombieY + zombieHeight &&
                soldierY + soldierHeight > zombieY);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("The Last Stand");
        TheLastStand game = new TheLastStand();
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

