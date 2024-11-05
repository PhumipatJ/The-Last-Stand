import java.awt.*;

public abstract class Zombie implements Downloadble,Runnable {
    protected float x;
    protected float y;
    protected float slope;
    protected float intercept;
    protected int speed;
    protected String word;
    protected int score;
    protected Image[] walkImages;
    protected int currentImageIndex = 0;
    boolean running;
    protected String textColor;


    public Zombie(float x, float y, int speed, String word, int score) {
        this.x = x;
        this.y = y;
        this.slope = 0;
        this.intercept = 0;
        this.speed = speed;
        this.word = word;
        this.score = score;
        this.textColor = "WHITE";
        this.running = true;
        loadImages();
    }

    public abstract void draw(Graphics g);


    public void startMoving() {
        Thread movementThread = new Thread(this);
        movementThread.start();
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            if(y <= 150){
                moveY();
            }
            else{
                if (slope == 0 && intercept == 0){
                    setLinearEquation();
                }
                moveY();
                moveX();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public int getScore() {
        return score;
    }

    public void moveY() {
        y += speed;
    }

    public void moveX() {
        if (slope != 0) {
            x = (int) ((y - intercept) / slope);
        }
    }

    public void setLinearEquation() {
        int soldierX = 260;
        int soldierY = 580;
        slope = (soldierY - y)/(soldierX - x);
        intercept = y-(slope*x);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String newWord) {
        this.word = newWord;
    }

    public void setOrangeText(){
        textColor = "ORANGE";
    }

    public Color getTextColor() {
        if(textColor.equals("ORANGE")){
            return Color.ORANGE;
        }
        return Color.WHITE;
    }

    public void increaseSpeed(){
        speed++;
    }

    /*
    public void moveToSoldier() {
        int soldierX = 260;
        int soldierY = 580;

        double deltaY = soldierY - y;
        double deltaX = soldierX - x;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0) {
            double speedRatio = speed / distance;
            x += deltaX * speedRatio;
            y += deltaY * speedRatio;

        }
    }
    */


}
