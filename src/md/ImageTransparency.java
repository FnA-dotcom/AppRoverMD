package md;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;

import static java.lang.System.out;

/**
 * Class containing code for converting an image's white background to be
 * transparent, adapted with minor changes from StackOverflow thread "How to
 * make a color transparent in a BufferedImage and save as PNG"
 * (http://stackoverflow.com/questions/665406/how-to-make-a-color-transparen...).
 */
public class ImageTransparency {
    /**
     * Main function for converting image at provided path/file name to have
     * transparent background.
     *
     * @param arguments Command-line arguments: only one argument is required
     *                  with the first (required) argument being the path/name of the source
     *                  image and the second (optional) argument being the path/name of the
     *                  destination file.
     */
    public static void main(final String[] arguments) throws Exception {
//        if (arguments.length < 1)
//        {
//            out.println("A source image file must be provided.");
//            System.exit(-1);
//        }

        String inputFileName = "C:\\Users\\abid_\\Desktop\\New folder\\logo-light-text.png";
        int decimalPosition = inputFileName.lastIndexOf(".");
        String outputFileName =
                arguments.length > 1
                        ? arguments[1]
                        : inputFileName.substring(0, decimalPosition) + ".copy.png";

        out.println("Copying file " + inputFileName + " to " + outputFileName);

        final File in = new File(inputFileName);
        final BufferedImage source = ImageIO.read(in);

        final int color = source.getRGB(0, 0);

        final Image imageWithTransparency = makeColorTransparent(source, new Color(color));

        final BufferedImage transparentImage = imageToBufferedImage(imageWithTransparency);

        final File out = new File(outputFileName);
        ImageIO.write(transparentImage, "PNG", out);
    }

    /**
     * Convert Image to BufferedImage.
     *
     * @param image Image to be converted to BufferedImage.
     * @return BufferedImage corresponding to provided Image.
     */
    private static BufferedImage imageToBufferedImage(final Image image) {
        final BufferedImage bufferedImage =
                new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return bufferedImage;
    }

    /**
     * Make provided image transparent wherever color matches the provided color.
     *
     * @param im    BufferedImage whose color will be made transparent.
     * @param color Color in provided image which will be made transparent.
     * @return Image with transparency applied.
     */
    public static Image makeColorTransparent(final BufferedImage im, final Color color) {
        final ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for (white)... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFFFFFFFF;

            public final int filterRGB(final int x, final int y, final int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
}