
// Student ID: 3118197
// Name: WEI-TSUNG CHENG

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class WordProcessor2 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Bag<String> bag = new BSTBag<>();

        for (String fileName : args) {
            try (Scanner scanner = new Scanner(new File(fileName))) {
                while (scanner.hasNext()) {
                    String word = scanner.next().toLowerCase();
                    bag.add(word);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Size: "  + bag.size());

        for (String word : bag) {
            System.out.println(word);
        }

        // Test (file0.txt)
        while (bag.contains("zeal")) {
            bag.remove("zeal");
            System.out.println("size:" + bag.size());

            System.out.println("Does contain ant:" + bag.contains("zeal"));
        }

        System.out.println("after all remove all zeal:");
        System.out.println("size:" + bag.size());
        System.out.println("Does contain zeal:" + bag.contains("zeal"));

        for (String s : bag) {
            System.out.println(s);
        }

        bag.remove("dragon");
        System.out.println("size: " + bag.size());
        System.out.println("contains dragon: " + bag.contains("dragon"));
    }
}
