import java.awt.*;

public class Bullet implements Runnable{
    private int x;
    private int y;
    private Zombie target;
    protected float slope;
    protected float intercept;
    private boolean running = true;

    public Bullet(int x, int y, Zombie target) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.slope = 0;
        this.intercept = 0;
    }

    public void setLinearEquation() {
        float targetX = target.getX()+40;
        float targetY = target.getY()+100;
        slope = (targetY - y)/(targetX - x);
        intercept = y-(slope*x);
    }

    public void move() {
        y -= 3;

        if (slope != 0) {
            x = (int) ((y - intercept) / slope);
        }
    }

    public void startMoving() {
        Thread movementThread = new Thread(this);
        movementThread.start();
    }

    public void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        if (slope == 0) {
            setLinearEquation();
        }

        while (running) {
            move();

            if (y <= getTargetY()) {
                running = false;
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, 5, 5);
    }

    public Zombie getTarget() {
        return target;
    }


    public float getY() {
        return y;
    }

    public float getTargetY() {
        return target.getY()+60;
    }


}