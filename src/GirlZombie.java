import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class GirlZombie extends Zombie {
    private int frameCounter = 0;

    public GirlZombie(float x, float y, String word) {
        super(x, y, 5, word, 25);
    }

    @Override
    public void loadImages() {
        walkImages = new Image[3];

        URL image1Url = getClass().getResource("/picture/girl_zombie/girl_zombie_1.png");
        URL image2Url = getClass().getResource("/picture/girl_zombie/girl_zombie_2.png");
        URL image3Url = getClass().getResource("/picture/girl_zombie/girl_zombie_3.png");

        walkImages[0] = new ImageIcon(image1Url).getImage();
        walkImages[1] = new ImageIcon(image2Url).getImage();
        walkImages[2] = new ImageIcon(image3Url).getImage();
    }

    @Override
    public void draw(Graphics g){
        g.drawImage(walkImages[currentImageIndex], (int) x, (int) y, 80, 120, null);
        g.setColor(Color.RED);
        //g.drawRect((int) x, (int) y, 80, 120);

        frameCounter++;
        if (frameCounter >= 0) { // smaller = faster
            currentImageIndex = (currentImageIndex + 1) % walkImages.length;
            frameCounter = 0;
        }

        FontMetrics metrics = g.getFontMetrics();
        int stringWidth = metrics.stringWidth(word);
        int stringHeight = metrics.getHeight();

        int rectX = (int) x + (80 - stringWidth) / 2;
        int rectY = (int) y - stringHeight - 5;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(rectX - 5 , rectY + 5 + 25, stringWidth + 10, stringHeight);


        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(getTextColor());
        //word = "(" + x +"," + y+ ")";
        g.drawString(word, rectX, rectY + stringHeight + 25);
    }
}
