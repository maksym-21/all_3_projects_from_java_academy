package blockchain.block;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class BlockTools {
    private BlockTools() {
    }

    public static List<Block> checkExistingOnHardDrive(){
        File file = new File("data.txt");

        if (!file.exists()) return new LinkedList<>();
        else {
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data.txt"))){
                List<Block> output;

                output = (LinkedList<Block>) ois.readObject();

                return output;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();

                return new LinkedList<>();
            }
        }
    }
}
