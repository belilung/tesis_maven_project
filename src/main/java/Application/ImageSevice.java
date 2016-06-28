package Application;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Андрей on 21.06.2016.
 */
public class ImageSevice {
    /**
     Возвращает цвет пикселя

     @param bi изображение
     @param x координата x
     @param y координата y
     @return
     */
    private Color getPixelColor(BufferedImage bi, int x, int y) {
        Object colorData = bi.getRaster().getDataElements(x, y, null);//данные о пикселе
        int argb = bi.getColorModel().getRGB(colorData);//преобразование данных в цветовое значение
        return new Color(argb, true);
    }

    public ImageSevice() throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\img.png"));
    }
}
