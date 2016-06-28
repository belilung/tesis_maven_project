package Application;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Андрей on 21.06.2016.
 */
public class ImageSevice {
    /**
     * Возвращает цвет пикселя
     *
     * @param bi изображение
     * @param x  координата x
     * @param y  координата y
     * @return
     */
    private Color getPixelColor(BufferedImage bi, int x, int y) {
        Object colorData = bi.getRaster().getDataElements(x, y, null);//данные о пикселе
        int argb = bi.getColorModel().getRGB(colorData);//преобразование данных в цветовое значение
        return new Color(argb, true);
    }

    String path = "";

    public ImageSevice(String path) throws IOException {
//
        this.path = path;

    }

    private void imageProcessing() throws IOException {
        File myFolder = new File(path);
        File[] files = myFolder.listFiles();

        String tmpTxtFile = files[0].toString() + ".txt";

        BufferedImage image = ImageIO.read(new File(files[0].toString()));

        createFile(tmpTxtFile);

        try (FileWriter writer = new FileWriter(tmpTxtFile, false)) {
            // запись всей строки
            String text = getPixelColor(image, 0, 0).toString();
            writer.write(text);
            // запись по символам
            writer.append('\n');
            writer.write("Dw: " + image.getHeight());
            writer.append('\n');
            writer.write("Dh: " + image.getWidth());
            writer.append('\n');
            writer.write("Rc: 3");

            writer.flush();
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
    }

    private void videoProcessing() throws IOException {
        File myFolder = new File(path);
        File[] files = myFolder.listFiles();

        for (int i = 0; i < files.length - 1; i++) {
            System.out.println(files[i].toString());
            String tmpTxtFile = files[i].toString() + ".txt";

            BufferedImage image = ImageIO.read(new File(files[0].toString()));

            System.out.println();
            createFile(tmpTxtFile);

            try (FileWriter writer = new FileWriter(tmpTxtFile, false)) {
                // запись всей строки
                String text = getPixelColor(image, 0, 0).toString();
                writer.write(text);
                // запись по символам
                writer.append('\n');
                writer.write("Dw: " + image.getHeight());
                writer.append('\n');
                writer.write("Dh: " + image.getWidth());
                writer.append('\n');
                writer.write("Rc: 3");

                writer.flush();
            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

    private void createFile(String filePath) throws IOException {
        File newFile = new File(filePath);
        if (newFile.createNewFile()) {
//            System.out.println("Новый файл создан");
        } else {
//            System.out.println("Файл уже существует");
        }
    }


}
