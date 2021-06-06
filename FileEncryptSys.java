package proj;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Centurybbx
 * This is a encrypting file system finished on June 3rd, 2021 by Century
 * Needless to say, all the stuffs here are bunches of shit...
 * but it still works well if you input the right arguments hahahaha
 * Warning: All the comments writen here may won't be helpful.
 * Cuz im just prictising writing comments in English. :)
 */

public class FileEncryptSys {
    private static HashMap<FileEntity, byte[]> contentMap;
    private static byte[] defaultKey;
    private static byte[] currentUserKey;
    private static String currentMountPath = "";

    /**
     * initialize the global parameters with default key 2018154812345678
     */
    private static void init() {
        contentMap = new HashMap<>();
        defaultKey = "2018154812345678".getBytes(StandardCharsets.UTF_8);
    }

    /**
     * judge whether the file path is legitimate
     * judged by the currentMountPath param
     * @param filePath specified file path
     * @param mountPath mounted path
     * @return legal status
     */
    private static boolean judgeLegality(String filePath, String mountPath) {
        Path child = Paths.get(filePath).toAbsolutePath();
        Path father = Paths.get(mountPath).toAbsolutePath();
        return child.startsWith(father);
    }


    /**
     * find all the files in specific directory path,
     * and initialize the contentMap with fileEntity and
     * its encrypted data (which is byte[] type)
     * @param dirPath specified directory path
     */
    private void findFile(String dirPath) {
        File file = new File(dirPath);
        File[] files = file.listFiles();
        // detect if this is legal file path
        if (!file.exists()) {
            System.out.println("Illegal file path!");
        } else {
            for (File value : files) {
                if (value.isFile()) {
                    FileEntity fileEntity = new FileEntity(value.getName(), value.getAbsolutePath());
                    try {
                        byte[] data = FileSysUtils.convertFile2Bytes(value.getPath());
                        byte[] encryptedData = encrypt(defaultKey, data);
                        contentMap.put(fileEntity, encryptedData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (value.isDirectory()) {
                    findFile(value.getPath());
                }
            }
        }
    }

    /**
     * encrypts file content using AES
     * returns a byte[] type encrypted data
     * @param key symmetric key
     * @param input byte[] type data
     * @return encrypted data
     * @throws GeneralSecurityException throws an exception when it meets sec problems
     */
    private byte[] encrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher aes = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        aes.init(Cipher.ENCRYPT_MODE, keySpec);
        return aes.doFinal(input);
    }

    /**
     * decrypts encrypted data using AES
     * returns a byte[] type data
     * @param key symmetric key
     * @param input encrypted data
     * @return decrypted data
     * @throws GeneralSecurityException throws an exception when it meets sec problems
     */
    private byte[] decrypt(byte[] key, byte[] input) throws GeneralSecurityException {
        Cipher aes = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        aes.init(Cipher.DECRYPT_MODE, keySpec);
        return aes.doFinal(input);
    }

    /**
     * mount the path on specific directory path
     */
    private void mount() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the mount path: ");
        String path = sc.next();
        if (new File(path).exists()) {
            currentMountPath = path;
            findFile(path);
            int size = contentMap.size();
            System.out.println("Successfully initialized! There are " + size + " files in the directory");
        } else {
            System.out.println("Please retype the legal path!");
            mount();
        }
    }

    /**
     * this is a console which you can use this EFS
     * 1 equals write content to files and it will determine if it's
     * under you mount path, if not, it will not be encrypted.
     * 2 equals read content from specific path
     * 3 equals remount path
     * 4 equals quit EFS
     * @return status(it denotes the stop flag)
     */
    private int consoleControl() {
        int status = 1;
        Scanner sc = new Scanner(System.in);
        System.out.println("----------------------------------------------");
        System.out.println("Welcome to Century's Encrypting file system!");
        System.out.println("Please enter your choice to proceed: \n 1.write\n 2.read\n 3.remount\n 4.quit");
        int num = sc.nextInt();
        switch (num) {
            case 1:
                write();
                break;
            case 2:
                read();
                break;
            case 3:
                remount();
                break;
            case 4:
                System.out.println("System quit!");
                status = 0;
                break;
            default:
                System.out.println("Please enter a valid number!");
                break;
        }
        System.out.println("----------------------------------------------");
        return status;
    }

    /**
     * write content to specified path and encrypt the data if it's under
     * the mount path, if not, just write in plain text.
     */
    private void write() {
        StringBuilder content = new StringBuilder();
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the content of the file:");
        String line = sc.nextLine();
        while (line != null && !line.equals("")) {
            content.append(line).append("\n");
            line = sc.nextLine();
        }
        System.out.println("Please enter the path where the file is to be stored (ending with the file name): ");
        sc = new Scanner(System.in);
        String filePath = sc.next();
        boolean flag = judgeLegality(filePath, currentMountPath);
        if (flag) {
            System.out.println("The file write path belongs to the mount point, and the file is automatically encrypted...");
            try {
                byte[] encrypt = encrypt(defaultKey, content.toString().getBytes(StandardCharsets.UTF_8));
                String encryptedStr = FileSysUtils.convertEncryptedToStr(encrypt);
                System.out.println("The encrypted content is: " + encryptedStr);
                // real write file operations
                FileSysUtils.writeContent2File(content.toString(), filePath);
                File newlyWriteFile = new File(filePath);
                FileEntity fileEntity = new FileEntity(newlyWriteFile.getName(), newlyWriteFile.getAbsolutePath());
                // put newly created file into contentMap
                contentMap.put(fileEntity, encrypt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("The file write path is not a mount point, the contents of the file are stored in plain text...");
            FileSysUtils.writeContent2File(content.toString(), filePath);
        }
    }

    /**
     * read content from specified path, it can determine if you input
     * the correct path. Similar to write function, it works differently
     * between different environments.
     */
    private void read() {
        boolean flag;
        String result;
        System.out.println("Please enter the path of the file you are looking for: ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.next();
        File file = new File(path);
        while (!file.isFile()) {
            scanner = new Scanner(System.in);
            System.out.println("File path does not exist! Please re-enter!");
            path = scanner.next();
            file = new File(path);
        }
        flag = judgeLegality(path, currentMountPath);
        if (flag) {
            FileEntity fileEntity = new FileEntity(file.getName(), file.getAbsolutePath());
            byte[] data = contentMap.get(fileEntity);
            if (currentUserKey == null) {
                System.out.println("The system detects that you have not initialized the key, ready to enter key?(Y/N)");
                scanner = new Scanner(System.in);
                String confirmation = scanner.next();
                if (confirmation.equals("Y") || confirmation.equals("y")) {
                    scanner = new Scanner(System.in);
                    try {
                        System.out.println("Please enter the key you have(16 bytes):");
                        currentUserKey = scanner.next().getBytes(StandardCharsets.UTF_8);
                        byte[] decryptedResult = decrypt(currentUserKey, data);
                        result = FileSysUtils.convertDecryptedToStr(decryptedResult);
                        System.out.println("Key Matching Success! The file contents are :\n" + result);
                    } catch (Exception e) {
                        System.out.println("Key Match Error!");
                            result = FileSysUtils.convertDecryptedToStr(contentMap.get(fileEntity));
                            System.out.println("The file contents are: \n" + result);
                    }
                } else if (confirmation.equals("N") || confirmation.equals("n")) {
                        result = FileSysUtils.convertDecryptedToStr(contentMap.get(fileEntity));
                        System.out.println("The file contents are: \n" + result);
                } else {
                    System.out.println("The action you entered is invalid!");
                }
            }
            // clear the user's key
            currentUserKey = null;
        } else {
            String content = FileSysUtils.readFileContent(path);
            System.out.println("The read file does not belong to the mount path, the contents are : " + content);
        }

    }

    /**
     * remount the path
     */
    private void remount() {
        // reinitialize
        init();
        mount();
    }

    /**
     * run the EFS
     */
    public void run() {
        init();
        mount();
        while (true) {
            int status = consoleControl();
            if (status == 0)
                break;
        }
    }

}
