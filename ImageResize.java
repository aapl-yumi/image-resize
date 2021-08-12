import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageResize {
    public static void main(String[] args) throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose the folder your photos are in");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        double[][] hwset = { { 16, 9 }, { 19.5, 9 }, { 13, 6 }, { 4, 3 } }; // height, width
        String[] hwsetstr = {};
        for (double[] s : hwset) {
            String str = s[0] + "x" + s[1];
            ArrayList<String> arr = new ArrayList<String>(Arrays.asList(hwsetstr));
            arr.add(str);
            hwsetstr = arr.toArray(hwsetstr);
        }
        int choice = JOptionPane.showOptionDialog(null, "Select a size", "Select a size", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, hwsetstr, hwsetstr[0]);
        if (choice < 0) {
            choice = 0;
        }
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String d = chooser.getSelectedFile().toString();
            if (d.substring(d.length() - 1) != "/") {
                d = d + "/";
            }
            System.out.println("Chosen directory: " + d);
            File dir = new File(d);
            String[] children = dir.list();
            Path path = Paths.get(d + "result");
            Files.createDirectories(path);
            if (children == null) {
                System.out.println("Either dir does not exist or is not a directory");
            } else {
                for (int i = 0; i < children.length; i++) {
                    String filename = children[i];
                    File folderInput = new File(d + filename);
                    String extension = "";
                    int j = filename.lastIndexOf('.');
                    if (j >= 0) {
                        extension = filename.substring(j + 1).toLowerCase();
                    }
                    if (extension.equals("png") || extension.equals("jpeg") || extension.equals("jpg")) {
                        BufferedImage a = ImageIO.read(folderInput);
                        int newWidth = a.getWidth();
                        int newHeight = a.getHeight();
                        double height = hwset[choice][0];
                        double width = hwset[choice][1];
                        if (a.getWidth() * height > a.getHeight() * width) {
                            newHeight = (int) Math.floor(a.getWidth() * height / width);
                        } else {
                            newWidth = (int) Math.floor(a.getHeight() * width / height);
                        }
                        BufferedImage background = new BufferedImage(newWidth, newHeight, a.getType());
                        Graphics g = background.getGraphics();
                        g.drawImage(a, (newWidth - a.getWidth()) / 2, (newHeight - a.getHeight()) / 2, a.getWidth(),
                                a.getHeight(), null);
                        ImageIO.write(background, "jpg", new File(d + "result/" + i + ".jpg"));
                        char[] animationChars = new char[] { '|', '/', '-', '\\' };
                        System.out.print("Processing: " + (100 * (i + 1) / children.length) + "% "
                                + animationChars[i % 4] + "\r");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println("Processing: Done!          ");
            }
        } else {
            System.out.println("No Selection ");
        }
    }
}