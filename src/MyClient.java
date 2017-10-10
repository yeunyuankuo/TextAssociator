import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

/**
 * CSE 373 Summer
 * July 14, 2017
 * @Author: Yeun-Yuan(Jessie) Kuo
 * 
 * MyClient takes a text file of synonyms for words as specified by 
 * THESAURUS_FILE and input from standard.in and outputs the "messed up" 
 * but shorter version of the user's input by selecting the shorter 
 * synonyms for all input words that have synonyms. Can be used by 
 * people who think that they are extremely wordy writing essays :)
 * 
 * Words wrapped by or contains punctuations will not be shorten by MyClient,
 * since those kind of string like: "something@gmail.com", "quoteUnquote", 
 * "(e.g. thisBlaBla)" can be weird or too out of context if it is shorten
 * or modified.
 * 
 * This Client program is dependent on TextAssociator.
 * 
 * Exception: throws the I/O exception when some sort of failed or 
 * 			  interrupted I/O operations occurred.
 */

public class MyClient {
	// A list of pronouns.
	private final static String[] PRONOUNS = {"I", "i", "you", "me", "we", "us", "my", "your", "anybody", "anyone", 
												 "anything", "each", "either", "everybody", "everyone", "everyting", 
												 "neither", "nobody", "no one", "nothing", "one", "somebody", 
												 "something", "both", "few", "many", "several", "all", "any", 
												 "most", "none", "some", "this", "these", "that", "those"};
	// A list of auxiliary verbs.
	private final static String[] AUXILIARYVERBS = {"am", "is", "are", "do", "be", "have", "has", "does", 
														"had", "can", "could", "ought", "may", "might", "must", 
														"should", "would", "were"};
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Welcome to \"Let's Keep It Short!\"");
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the thesaurus prefered: ");
		
		// Takes in the desired thesaurus from user.
		String input = scan.nextLine();
		File file = new File(input);
		
		// Initializes a new TextAssociator.
		TextAssociator sc = new TextAssociator();	
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String text = null;
		
		// Load the TextAssociator with WISC of words
		while((text = reader.readLine()) != null) {
			String[] words = text.split(",");
			String curr = words[0].trim();
			sc.addNewWord(curr);
			
			// Associate words with their associations.
			for (int i = 1; i < words.length; i++) {
				sc.addAssociation(curr, words[i].trim());
			}
		}
		
		String inputString = "";
		boolean itsOn = true;
		
		while(itsOn) {
			System.out.println("Please enter the text you would like to keep it short (enter \"exit\" to exit):");
		
			// Takes in the text to modify.
			inputString = scan.nextLine();
			if (inputString.equals("exit")) {
				break;
			}
			String[] tokens = inputString.split(" ");
			String result = "";
			for (String token : tokens) {
				// Deletes pronouns and auxiliary verbs with all lower case in the text.
				if (!Arrays.asList(PRONOUNS).contains(token) && !Arrays.asList(AUXILIARYVERBS).contains(token)) {
					Set<String> words = sc.getAssociations(token.toLowerCase());
					if (words == null) {
						result += " " + token;
					} else {
						String newWord = token;
						// Takes the shortest association of the word.
						for (String word : words) {
							if (word.length() < newWord.length()) {
								newWord = word;
							} 
						}
						result += " " + newWord;
					}
				}
			}
			System.out.println("Here you go! This should keep you under the word limit (if there is one)!");
			System.out.println();
			System.out.println(result.trim());
			System.out.println();
		}
		reader.close();
	}
}
