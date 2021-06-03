package proj;


public class Main {

    /**
     * Entrance to EFS
     * @param args command args
     */
    public static void main(String[] args) {
        FileEncryptSys sys = new FileEncryptSys();
        try {
            sys.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
