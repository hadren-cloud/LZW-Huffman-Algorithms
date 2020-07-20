import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class LZWCompressor {

    private static final int dictionnarySize = 256;

    public void compress(String filein, String fileout) {
        // Build the dictionary.
        int dictSize = dictionnarySize;
        HashMap<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < dictionnarySize; i++)
            dictionary.put(Character.toString((char)i), i);

        String w = "";
        File file = new File(filein);
        File file2 = new File(fileout);

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file)); BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
             DataOutputStream os = new DataOutputStream(bufferedOutputStream);) {
            int singleCharInt;
            char singleChar;
            while((singleCharInt = bufferedInputStream.read()) != -1) {
                singleChar = (char) singleCharInt;
                String wc = w + singleChar;
                if (dictionary.containsKey(wc))
                    w = wc;
                else {
                    os.writeInt(dictionary.get(w));
                    // Add wc to the dictionary.
                    dictionary.put(wc, dictSize++);
                    w = Character.toString(singleChar);
                }
            }

            // Output the code for w.
            if (!w.equals(""))
                os.writeInt(dictionary.get(w));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void decompress(String filein, String fileout) {
        // Build the dictionary.
        int dictSize = dictionnarySize;
        HashMap<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < dictionnarySize; i++)
            dictionary.put(i, Character.toString((char)i));

        File file = new File(filein);
        File file2 = new File(fileout);
        String w = null;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file2));
             DataInputStream is = new DataInputStream(bufferedInputStream); DataOutputStream os = new DataOutputStream(out);) {

            int k = 0;
            while (is.available() > 0) {
                k = is.readInt();
                String entry = dictionary.get(k);
                if(entry == null){
                    entry = w + w.charAt(0);
                }
                os.writeBytes(entry);
                if(w != null){
                    dictionary.put(dictSize++, w + entry.charAt(0));
                }
                w = entry;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
