import com.itextpdf.text.pdf.*;
import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Csaba Harangozó
 * Date: 2011.06.23.
 * Time: 23:13
 */
public class Pilot {

    public static byte[] readFileToByteArray(File file) {
		  byte[] fileAsByteArray = null;
		  FileInputStream fis = null;
		  try {
			  fis = new FileInputStream(file);
			  int numOfBytes = fis.available();
			  fileAsByteArray = new byte[numOfBytes];
			  fis.read(fileAsByteArray);
		  }
		  catch (IOException e) {
			  e.printStackTrace();
		  }
		  finally {
			  if (fis != null)
				  try {
					  fis.close();
				  }
			  	catch (IOException e) {}
		  }

		  return fileAsByteArray;
	  }

    public void getPdfAttachments(byte[] pdfBytes) {

        if (pdfBytes == null) {
            throw new RuntimeException("Pdf byte is null!");
        }

        byte[] pdf = pdfBytes;

        if (Base64.isArrayByteBase64(pdfBytes)) {
            System.out.println("Base64 jött be!");
           pdf = Base64.decodeBase64(pdfBytes);
        }

        PdfReader reader = null;
        try {
            reader = new PdfReader(pdf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PdfDictionary root = reader.getCatalog();
        PdfDictionary documentNames = root.getAsDict(PdfName.NAMES);
        PdfDictionary embeddedFiles = documentNames.getAsDict(PdfName.EMBEDDEDFILES);
        PdfArray filespecs = embeddedFiles.getAsArray(PdfName.NAMES);

        PdfDictionary fileSpec;
        PdfDictionary refs;
        FileOutputStream fos;
        PRStream stream;
        for (int i = 0; i < filespecs.size(); ) {
          filespecs.getAsString(i++);
          fileSpec = filespecs.getAsDict(i++);
          refs = fileSpec.getAsDict(PdfName.EF);
          for (PdfName key : refs.getKeys()) {
              stream = (PRStream) PdfReader.getPdfObject(refs.getAsIndirectObject(key));
              try {
                  fos = new FileOutputStream(String.format("/home/csaba/src/PdfAttachments/test/%s", fileSpec.getAsString(key).toString()));
                  fos.write(PdfReader.getStreamBytes(stream));
                  fos.flush();
                  fos.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        }
    }

    public static void main(String[] args) {
        Pilot p = new Pilot();
        byte[] pdfBytes = readFileToByteArray(new File("/home/csaba/src/PdfAttachments/test/veddvelem_VV000034.pdf"));
        p.getPdfAttachments(pdfBytes);
    }
}
