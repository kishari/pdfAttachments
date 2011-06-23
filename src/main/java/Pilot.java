import com.itextpdf.text.pdf.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Csaba Harangozó
 * Date: 2011.06.23.
 * Time: 23:13
 */
public class Pilot {

    public void getPdfAttachments() {
        PdfReader reader = null;
        try {
            reader = new PdfReader("/home/csaba/Asztal/shuffle_vv/veddvelem_VV000034.pdf");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        PdfDictionary root = reader.getCatalog();
        PdfDictionary documentnames = root.getAsDict(PdfName.NAMES);
        PdfDictionary embeddedfiles = documentnames.getAsDict(PdfName.EMBEDDEDFILES);
        PdfArray filespecs = embeddedfiles.getAsArray(PdfName.NAMES);

        PdfDictionary filespec;
        PdfDictionary refs;
        FileOutputStream fos;
        PRStream stream;
        for (int i = 0; i < filespecs.size(); ) {
          filespecs.getAsString(i++);
          filespec = filespecs.getAsDict(i++);
          refs = filespec.getAsDict(PdfName.EF);
          for (PdfName key : refs.getKeys()) {
              stream = (PRStream) PdfReader.getPdfObject(refs.getAsIndirectObject(key));
              try {
                  fos = new FileOutputStream(String.format("/home/csaba/Asztal/shuffle_vv/%s", filespec.getAsString(key).toString()));
                  fos.write(PdfReader.getStreamBytes(stream));
                  fos.flush();
                  fos.close();
              } catch (IOException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
              }
          }
        }
    }

    public static void main(String[] args) {
        Pilot p = new Pilot();
        p.getPdfAttachments();
    }
}
