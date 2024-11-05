import javax.swing.*;
import java.awt.*;
import java.net.URL;

class Soldier implements Downloadble {
    private int x;
    private int y;
    protected Image[] directionImages;
    protected Image faceDirection;

    public Soldier(int x, int y) {
        this.x = x;
        this.y = y;
        loadImages();
        faceDirection = directionImages[1];
    }


    @Override
    public void loadImages() {
        directionImages = new Image[3];

        URL image1Url = getClass().getResource("/picture/soldier/soldier_1.png");
        URL image2Url = getClass().getResource("/picture/soldier/soldier_2.png");
        URL image3Url = getClass().getResource("/picture/soldier/soldier_3.png");

        directionImages[0] = new ImageIcon(image1Url).getImage();
        directionImages[1] = new ImageIcon(image2Url).getImage();
        directionImages[2] = new ImageIcon(image3Url).getImage();
    }

    public void draw(Graphics g) {
        g.drawImage(faceDirection, x, y, 80, 120, null);
        g.setColor(Color.green);
        //g.drawRect(x,y+60,80,120);
    }

    public void setFaceDirection(float zombieX) {
        if(zombieX <= 250){
            faceDirection = directionImages[0];
        }
        else if(zombieX <= 350){
            faceDirection = directionImages[1];
        }
        else{
            faceDirection = directionImages[2];
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}