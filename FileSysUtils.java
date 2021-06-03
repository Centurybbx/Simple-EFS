package proj;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileSysUtils {

    /**
     * write content to file by specified path
     * @param content file content, stored in String
     * @param writePath path to write
     */
    public static void writeContent2File(String content, String writePath) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(writePath));
            out.write(content);
            out.close();
            System.out.println("File written successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read file content from specified path
     * @param path file path to read
     * @return file content in String type
     */
    public static String readFileContent(String path) {
        StringBuilder rst = new StringBuilder();
        File file = new File(path);
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            while (line != null) {
                rst.append(line).append("\n");
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rst.toString();
    }

    /**
     * convert file to byte[], this function helps developers promote
     * their proficiency when coding. So it's a helper function.
     * @param path file path
     * @return byte[] type file data
     */
    public static byte[] convertFile2Bytes(String path) {
        File file = new File(path);
        byte[] bytesArray = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytesArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesArray;
    }

    /**
     * convert encrypted byte[] data to String and encode with Base64.
     * @param encrypted encrypted data (byte[])
     * @return encrypted data (String)
     */
    public static String convertEncryptedToStr(byte[] encrypted) {
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * decrypted byte[] encrypted data to String type.
     * @param decrypted decrypted data (byte[])
     * @return decrypted data (String)
     */
    public static String convertDecryptedToStr(byte[] decrypted) {
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
