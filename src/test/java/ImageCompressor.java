

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.sun.jersey.core.util.Base64;
import com.vspl.music.util.VSPLUtil;

public class ImageCompressor {

	public static void main(String[] args) throws IOException {
		   
	      //File input = new File("D:/NVB_BIS_UI_20190214/VEMS_NVB/src/test/java/architecture.jpg");
	      File input = new File("D:/plane.jpg");
	      System.out.println("input: "+input);
	      String encodstring = encodeFileToBase64Binary(input);
          //System.out.println(encodstring);
          System.out.println("encodstring:: "+encodstring);
	      System.out.println(getFileSizeKiloBytes(input));
	      //System.out.println(getFileSizeMegaBytes(input));
	      BufferedImage image = ImageIO.read(input);

	      File compressedImageFile = new File("compress.jpg");
	      OutputStream os =new FileOutputStream(compressedImageFile);

	      Iterator<ImageWriter>writers =  ImageIO.getImageWritersByFormatName("jpg");
	      ImageWriter writer = (ImageWriter) writers.next();

	      ImageOutputStream ios = ImageIO.createImageOutputStream(os);
	      writer.setOutput(ios);

	      ImageWriteParam param = writer.getDefaultWriteParam();
	      
	      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	      param.setCompressionQuality(0.05f);
	      writer.write(null, new IIOImage(image, null, null), param);
	      System.out.println("compressed Image "+getFileSizeKiloBytes(compressedImageFile));
	      //String byts = new String(compressedImageFile);
	      //File decoded = VSPLUtil.encodeBase64(compressedImageFile);
	      
	      os.close();
	      ios.close();
	      writer.dispose();
	   }

	private static String getFileSizeKiloBytes(File input) {
		return (double) input.length() / 1024 + "  kb";
	}
	private static String getFileSizeMegaBytes(File file) {
		return (double) file.length() / (1024 * 1024) + " mb";
	}
	
	

     private static String encodeFileToBase64Binary(File file){
          String encodedfile = null;
          try {
              FileInputStream fileInputStreamReader = new FileInputStream(file);
              byte[] bytes = new byte[(int)file.length()];
              fileInputStreamReader.read(bytes);
              encodedfile = new String(Base64.encode(bytes), "UTF-8");
          } catch (FileNotFoundException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
          
          
          return encodedfile;
      }
}
