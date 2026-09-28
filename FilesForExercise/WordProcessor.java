
// Student ID: 3118197
// Name: WEI-TSUNG CHENG

//import classes for file input - scanner etc.
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

// Part A: 優點是使用現成集合
// 使用現成集合: TreeSet
public class WordProcessor {
	private static <E> String displaySet(Set<E> inputSet){
		//implement this static method to create a
		// String representation of set - 5 comma separated elements per line
		// assume that type E has a toString method
        StringBuilder output = new StringBuilder();
        int count = 0;

        for (E e : inputSet) {
            output.append(e.toString());
            count++;
            if (count % 5 == 0) {
                output.append(",\n");
            } else {
                output.append(", ");
            }
        }

        if (output.length() > 0) {
            output.setLength(output.length() - 2);
        }
        return  output.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        Set<String> wordSet = new TreeSet<>();
        Set<CountedElement<String>> countedWordSet = new TreeSet<>();

        for (String fileName : args) {
            try (Scanner scanner = new Scanner(new File(fileName))) {
                while (scanner.hasNext()) {
                    String word = scanner.next().toLowerCase();
                    if (wordSet.add(word)) {
                        countedWordSet.add(new CountedElement<>(word, 1));
                    } else {
                        for (CountedElement<String> w : countedWordSet) {
                            if (w.getElement().equals(word)) {
                                int count = w.getCount() + 1;
                                countedWordSet.remove(w);
                                countedWordSet.add(new CountedElement<>(word, count));
                                break;
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
		
        System.out.println(displaySet(countedWordSet));
	}
}
