import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class BoyZombie extends Zombie {
    private int frameCounter = 0;

    public BoyZombie(float x, float y, String word) {
        super(x, y, 4, word,20);
    }

    @Override
    public void loadImages() {
        walkImages = new Image[2];

        URL image1Url = getClass().getResource("/picture/boy_zombie/boy_zombie_1.png");
        URL image2Url = getClass().getResource("/picture/boy_zombie/boy_zombie_2.png");

        walkImages[0] = new ImageIcon(image1Url).getImage();
        walkImages[1] = new ImageIcon(image2Url).getImage();
    }

    @Override
    public void draw(Graphics g){
        g.drawImage(walkImages[currentImageIndex], (int) x, (int) y, 80, 120, null);
        g.setColor(Color.RED);
        //g.drawRect((int) x, (int) y, 80, 120);

        frameCounter++;
        if (frameCounter >= 3) { // smaller = faster
            currentImageIndex = (currentImageIndex + 1) % walkImages.length;
            frameCounter = 0;
        }

        FontMetrics metrics = g.getFontMetrics();
        int stringWidth = metrics.stringWidth(word);
        int stringHeight = metrics.getHeight();

        int rectX = (int) x + (80 - stringWidth) / 2;
        int rectY = (int) y - stringHeight - 5;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(rectX - 5 , rectY + 5 + 30, stringWidth + 10, stringHeight);


        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(getTextColor());
        //word = "(" + x +"," + y+ ")";
        g.drawString(word, rectX, rectY + stringHeight + 30);
    }
}
