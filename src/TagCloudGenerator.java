import java.util.Comparator;

import components.map.Map;
import components.map.Map.Pair;
import components.map.Map1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine1L;

/**
 * Program reads words from an input file and displays the words alphabetically
 * in an HTML table followed by the word count.
 *
 * @author Brian Evans and Trevor Osbourne
 *
 */
public final class TagCloudGenerator {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGenerator() {
    }

    /**
     * comparator<String> that sorts in alphabetical order.
     *
     *
     * @param str1
     *            the first string to be compared
     * @param str2
     *            the second string to be compared
     * @return res 1 if str1 is alphabetically before str2, 0 if they are equal
     *         and -1 if str1 is alphabetically after str2
     */

    /**
     * Alphabetical Comparator for Sorting Machine.
     */

    private static class wordAlpha
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public final int compare(Pair<String, Integer> arg0,
                Pair<String, Integer> arg1) {
            int result = arg0.key().compareTo(arg1.key());
            if (result == 0) {
                result = arg0.value().compareTo(arg1.value());
            }
            return result;
        }
    }

    /**
     * Numerical Comparator for Sorting Machine.
     */

    private static class wordFrequency
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public final int compare(Pair<String, Integer> arg0,
                Pair<String, Integer> arg1) {
            int result = arg1.value().compareTo(arg0.value());
            if (result == 0) {
                result = arg0.key().compareTo(arg1.key());
            }
            return result;
        }
    }

    /**
     * Minimum frequency of a word.
     */
    private static int minFrequency = 0;
    /**
     * Maximum frequency of a word.
     */
    private static int maxFrequency = 0;

    /**
     */
    public static void generateElements(String str, Set<Character> strSet) {
        assert str != null : "Violation of: str is not null";
        assert strSet != null : "Violation of: strSet is not null";

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            strSet.add(c);
            //adds each character in str to strSet
        }
    }

    /**
     * Outputs the "opening" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * <html> <head> <title>Words Counted in loc</title> </head> <body>
     * <h2>Words Counted in loc</h2>
     * <hr />
     * <table border="1">
     * <tr>
     * <th>Words</th>
     * <th>Counts</th>
     * </tr>
     *
     * @param out
     *            the output stream
     * @param loc
     *            the input file location
     * @updates out.content
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    private static void outputHeader(SimpleWriter out, String loc, int num) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Top " + num + " words in " + loc + "</title>");
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2/assignments/projects/tag-cloud-generator/data/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Top " + num + " words in " + loc + "</h2>");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">");
        out.println("<p class=\"cbox\">");
    }

    /**
     * Outputs the words with font size proportional to word counts. \
     *
     * @param out
     *            the output stream
     * @param words
     *            SortingMachine of words and word counts of words that will be
     *            printed to the cloud
     * @updates out.content
     * @ensures out.content = #out.content * [the HTML code for definition
     *          links]
     */
    private static void outputWords(
            SortingMachine<Map.Pair<String, Integer>> words, SimpleWriter out,
            int maxFrequency, int minFrequency) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        int maxFont = 48;
        int minFont = 11;
        int ratio = (maxFrequency - minFrequency + maxFont - minFont)
                / (maxFont - minFont);
        while (words.size() > 0) {
            Pair<String, Integer> pair = words.removeFirst();
            String word = pair.key();
            int count = pair.value();
            int fontSize = (count - minFrequency) / ratio + minFont;
            out.println("<span style=\"cursor:default\" class=\"f" + fontSize
                    + "\" title=\"count: " + count + "\">" + word + "</span>");
        }
    }

    /**
     * Outputs the "closing" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * </table>
     * </body> </html>
     *
     * @param out
     *            the output stream
     * @updates out.contents
     * @requires out.is_open
     * @ensures out.content = #out.content * [the HTML "closing" tags]
     */
    private static void outputFooter(SimpleWriter out) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Generates a map containing all the words in an input file sorted
     * alphabetically.
     *
     * @param input
     *            input file to read from
     * @return words = each word in input sorted alphabetically
     */
    public static Map<String, Integer> generateMap(SimpleReader input) {
        assert input != null : "Violation of: input is not null";

        String separatorStr = " \t\n\r'!~@#$%^&*()-=;:<>?.,[]{}|\"";
        //string of separator characters
        Set<Character> separatorSet = new Set1L<>();
        generateElements(separatorStr, separatorSet);
        //creates set of characters from separatorStr

        Map<String, Integer> wordCounts = new Map1L<String, Integer>();
        while (!input.atEOS()) {
            String line = input.nextLine();
            StringBuilder wordBuilder = new StringBuilder();
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (!separatorSet.contains(c)) {
                    wordBuilder.append(c);
                }
                if (wordBuilder.length() > 0 && separatorSet.contains(c)
                        || wordBuilder.length() > 0 && i == line.length() - 1) {
                    String word = wordBuilder.toString().toLowerCase();
                    if (!wordCounts.hasKey(word)) {
                        wordCounts.add(word, 1);
                    } else {
                        wordCounts.replaceValue(word,
                                wordCounts.value(word) + 1);
                    }
                    wordBuilder = new StringBuilder();
                }
            }
        }

        //sort map alphabetically
        return wordCounts;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        /*
         * Open input and output streams
         */
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        out.println("Please enter the name of a valid input file");
        String inFile = in.nextLine();
        SimpleReader input = new SimpleReader1L(inFile);
        out.println("Please enter the name of a valid output file");
        SimpleWriter output = new SimpleWriter1L(in.nextLine());
        //creates input and output files to read from and print to
        Map<String, Integer> map = generateMap(input);

        out.println(
                "Please enter the number of words to be included in the generated tag cloud:");
        int cloudSize = Integer.parseInt(in.nextLine());

        while (cloudSize > map.size()) {
            out.println(
                    "That number is too large. Please enter a smaller number:");
            cloudSize = Integer.parseInt(in.nextLine());
        }

        outputHeader(output, inFile, cloudSize);
        //prints the HTML headers to output

        // Sort the map according to numerical order to get the minimum and
        // maximum frequencies
        Comparator<Map.Pair<String, Integer>> wordFrequency = new wordFrequency();
        SortingMachine<Map.Pair<String, Integer>> sort = new SortingMachine1L<>(
                wordFrequency);
        for (Map.Pair<String, Integer> pair : map) {
            sort.add(pair);
        }

        sort.changeToExtractionMode();
        Map<String, Integer> sortedNumbers = new Map1L<String, Integer>();
        for (int i = 0; i < cloudSize; i++) {
            Map.Pair<String, Integer> currentPair = sort.removeFirst();
            sortedNumbers.add(currentPair.key(), currentPair.value());
            if (i == 0) {
                maxFrequency = currentPair.value();
            } else if (i == cloudSize - 1) {
                minFrequency = currentPair.value();
            }
        }

        // Sort the map according to alphabetical order
        Comparator<Map.Pair<String, Integer>> wordAlpha = new wordAlpha();
        SortingMachine<Map.Pair<String, Integer>> alphaSort = new SortingMachine1L<>(
                wordAlpha);
        for (Map.Pair<String, Integer> letterPairs : sortedNumbers) {
            alphaSort.add(letterPairs);
        }
        alphaSort.changeToExtractionMode();

        outputWords(alphaSort, output, maxFrequency, minFrequency);
        //prints the terms with links to their definitions
        outputFooter(output);
        //prints the closing HTML tags to output

        //Close input and output streams
        in.close();
        out.close();
        input.close();
        output.close();
    }
}