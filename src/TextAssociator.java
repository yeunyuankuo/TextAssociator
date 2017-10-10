import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* CSE 373 Starter Code
 * @Author Kevin Quinn
 * @Author Yeun-Yuan(Jessie) Kuo (UW student number: 1428223)
 * 
 * 
 * TextAssociator represents a collection of associations between words.
 * See write-up for implementation details and hints.
 * 
 */
public class TextAssociator {
	private WordInfoSeparateChain[] table;
	private int size;
	// Stores a  list of prime numbers for resizing the table, the values double in size.
	private static final int[] PRIMES = {11, 23, 47, 97, 197, 397, 797, 1579, 3187,
					                      6311, 12611, 25219, 50441, 100907, 201731,
					                      403483, 806917};
	// The index inside PRIMES that is the current size that the table is resized to.
	private int primeNow;	
	
	
	/* INNER CLASS
	 * Represents a separate chain in your implementation of your hash table.
	 * A WordInfoSeparateChain is a list of WordInfo objects that have all
	 * been hashed to the same index of the TextAssociator.
	 */
	private class WordInfoSeparateChain {
		private List<WordInfo> chain;
		
		/* Creates an empty WordInfoSeparateChain without any WordInfo.
		 */
		public WordInfoSeparateChain() {
			this.chain = new ArrayList<WordInfo>();
		}
		
		/* Adds a WordInfo object to the SeparateCahin.
		 * Returns true if the WordInfo was successfully added, false otherwise.
		 */
		public boolean add(WordInfo wi) {
			if (!chain.contains(wi)) {
				chain.add(wi);
				return true;
			} 
			return false;
		}
		
		/* Removes the given WordInfo object from the separate chain.
		 * Returns true if the WordInfo was successfully removed, false otherwise.
		 */
		public boolean remove(WordInfo wi) {
			if (chain.contains(wi)) {
				chain.remove(wi);
				return true;
			}
			return false;
		}
		
		// Returns the size of this separate chain.
		public int size() {
			return chain.size();
		}
		
		// Returns the String representation of this separate chain.
		public String toString() {
			return chain.toString();
		}
		
		// Returns the list of WordInfo objects in this chain.
		public List<WordInfo> getElements() {
			return chain;
		}
	}
	
	
	/* Creates a new TextAssociator without any associations.
	 */
	public TextAssociator() {
		table = new WordInfoSeparateChain[PRIMES[primeNow]];
		size = 0;
		primeNow = 0;
	}
	
	
	/* Adds a word with no associations to the TextAssociator.
	 * Returns False if this word is already contained in your TextAssociator,
	 * Returns True if this word is successfully added.
	 */
	public boolean addNewWord(String word) {
		// check if the table needs to be resized.
		if ((double)(size / table.length) >= 0.75) {
			resize();
		}
		// generates index for the word.
		int index = hash(word) % table.length;
		if (table[index] == null) {
			table[index] = new WordInfoSeparateChain();
			size++;
			return table[index].add(new WordInfo(word));
		} else if (!containsInWisc(word, index)) {
			size++;
			return table[index].add(new WordInfo(word));
		} else {
			return false;
		}		
	}
	
	
	/* Adds an association between the given words. Returns true if association correctly added, 
	 * returns false if first parameter does not already exist in the TextAssociator or if 
	 * the association between the two words already exists.
	 */
	public boolean addAssociation(String word, String association) {
		int index = hash(word) % table.length;
		if (table[index] != null) {
			WordInfo curr = getWordInfo(word, index);
			if (curr != null && !curr.getAssociations().contains(association)) {
				return curr.addAssociation(association);
			}
		}
		return false;
	}
	
	
	/* Remove the given word from the TextAssociator, returns false if word 
	 * was not contained, returns true if the word was successfully removed.
	 * Note that only a source word can be removed by this method, not an association.
	 */
	public boolean remove(String word) {
		int index = hash(word) % table.length;
		if (table[index] != null && containsInWisc(word, index)) {
			WordInfo curr = getWordInfo(word, index);
			if (curr != null) {
				size--;
				return table[index].remove(curr);
			}
		}
		return false;
	}
	
	
	/* Returns a set of all the words associated with the given String.
	 * Returns null if the given String does not exist in the TextAssociator.
	 */
	public Set<String> getAssociations(String word) {
		int index = hash(word) % table.length;
		if (table[index] != null && containsInWisc(word, index)) {
			return getWordInfo(word, index).getAssociations();
		} 
		return null;
	}
	
	
	/* Prints the current associations between words being stored
	 * to System.out
	 */
	public void prettyPrint() {
		System.out.println("Current number of elements : " + size);
		System.out.println("Current table size: " + table.length);
		// Walk through every possible index in the table.
		for (int i = 0; i < table.length; i++) {
			if (table[i] != null) {
				WordInfoSeparateChain bucket = table[i];
				// For each separate chain, grab each individual WordInfo.
				for (WordInfo curr : bucket.getElements()) {
					System.out.println("\tin table index, " + i + ": " + curr);
				}
			}
		}
		System.out.println();
	}
	
	
	/**
	 * Generates a hash code for given word.
	 * 
	 * @param word the word the you wish to hash.
	 * @return a integer hash code.
	 */
	private int hash(String word) {
		return Math.abs(word.hashCode());
	}
	
	
    /** 
	 * Checks if the WordInfoSeparateChain contains the word.
	 * 
	 * @param word the word that users want to check if exist in WordInfoSeparateChain.
	 * @param index the index that the WordInfoSeparateChain is located in the table.
	 * @return true if the word is found in the WordInfoSeparateChain, otherwise false.
	 * -> Precondition is that WISC shouldn't be null.
	 */
	private boolean containsInWisc(String word, int index) {
		List<WordInfo> list = table[index].getElements();
		for (WordInfo curr: list) {
			if (curr.getWord().equals(word)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Resize the current table size into a bigger prime number in PRIMES.
	 * Prime numbers in PRIMES are doubles in size.
	 */
	private void resize() {
		primeNow++;
		WordInfoSeparateChain[] newTable = new WordInfoSeparateChain[PRIMES[primeNow]];
		for (WordInfoSeparateChain chain: table) {
			if (chain != null) {
				for (WordInfo curr: chain.getElements()) {
					int index = hash(curr.getWord()) % newTable.length;
					
					if (newTable[index] == null) {
						newTable[index] = new WordInfoSeparateChain();
					}
					newTable[index].add(curr);
				}
			}
		}
		table = newTable;
	}
	
	
	/**
	 * Grabs the WordInfo of the word.
	 * 
	 * @param word the word that the user wants to get as WordInfo.
	 * @param index the index where the WISC of the WordInfo for the word is located.
	 * @return the WordInfo of the word, otherwise null if the WISC is empty.
	 */
	private WordInfo getWordInfo(String word, int index) {
		WordInfo wordInfo = null;
		List<WordInfo> chain = table[index].getElements();
		if (chain != null && chain.size() > 0) {
			for (WordInfo curr: chain) {
				if (curr.getWord().equals(word)) {
					wordInfo = curr;
				}
			}
		}
		return wordInfo;
	}
}
