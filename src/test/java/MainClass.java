import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

public class MainClass {
  public static void main(String[] args)throws Exception {
	  
	  JFrame parentFrame = new JFrame();
		 
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");   
		 
		int userSelection = fileChooser.showSaveDialog(parentFrame);
		 
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileChooser.getSelectedFile();
		    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
		}
		
    Document document = new Document();
      PdfWriter.getInstance(document, new FileOutputStream("13.pdf"));

      document.open();

      BaseFont bf;
      Font font;
      bf = BaseFont.createFont("KozMinPro-Regular", "UniJIS-UCS2-H",BaseFont.NOT_EMBEDDED);
      font = new Font(bf, 12);
      String str = "諏訪山音楽学院は 内や海外で活躍中の実力あるt";
      document.add(new Paragraph("asdf",font));
      document.add(new Paragraph("Font: " + bf.getPostscriptFontName(),font));
      document.add(new Paragraph("\u8ab0\u3082\u77e5\u3089\u306a\u3044",font));
      document.add(new Paragraph(str,font));

    document.close();
  }
}