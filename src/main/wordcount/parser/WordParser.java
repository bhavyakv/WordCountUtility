package main.wordcount.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class WordParser {
	HashMap<Integer, HashMap<String, Integer>> _listsMap = new HashMap<Integer, HashMap<String,Integer>>(10);
	private final int HASH_SIZE = 10;
	private HashMap<String, Integer> _currentLoadedList = null;
	private boolean _bParsedData = false;
	
	private static WordParser _wordParser = null;
	
	public WordParser() {
		System.out.println("WordParser constructor");
		initListsMap();
	}
	
	private void initListsMap() {
		for(int i=0; i< HASH_SIZE; i++) {
			_listsMap.put(i, new HashMap<String, Integer>());
		}
	}
	
	/*public static WordParser getInstance() {
		if(null == _wordParser) {
			_wordParser = new WordParser();
		}
		return _wordParser;
	}*/
	
	public void parseWords(InputStreamReader iStream) throws IOException {
		
		BufferedReader bufReader = new BufferedReader(iStream);
		String line = "";
		//line = "Blind would equal while oh mr do style. Lain led and fact none. One preferred sportsmen resolving the ";
		while(null != (line = bufReader.readLine())) {
			//while(null != line) {
			String[] words = line.split(" ");
			for(String word:words) {
				try {
					word = word.toLowerCase(Locale.US);
					HashMap<String, Integer> wordsCountMap = _listsMap.get(getFileIndexForWord(word));
					Integer count = wordsCountMap.get(word);
					if(null != count) {
						count++;
					}
					else {
						count = 1;
					}
					wordsCountMap.put(word, count);
				}
				catch(StringIndexOutOfBoundsException e) {
					System.err.println("Caught StringIndexOutOfBoundsException");
				}
			}
		}
		persistWordsCount();
		_bParsedData  = true;
	}

	private void persistWordsCount() throws IOException {
		Set<Entry<Integer, HashMap<String, Integer>>> entrySet = _listsMap.entrySet();
		Iterator<Entry<Integer, HashMap<String, Integer>>> iterator = entrySet.iterator();
		while(iterator.hasNext()) {
			iterator.next();
		}
		
		Set<Integer> keySet = _listsMap.keySet();
		for(Integer key: keySet) {
			File file = createOrGetFile(key);
			ObjectOutputStream outputStream = null;
			try {
				outputStream = new ObjectOutputStream(new FileOutputStream(file));
				outputStream.writeObject(_listsMap.get(key));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			finally {
				if(null != outputStream) {
					outputStream.close();
				}
			}
		}
	}

	private File createOrGetFile(Integer key) throws IOException {
		File file = new File(getFileName(key));
		if(false == file.exists()) {
			file.createNewFile();
		}
		return file;
	}
	
	public int getWordCount(String word) throws IOException, ClassNotFoundException {
		HashMap<String, Integer> wordsList = getListForWord(word);
		Integer wordCount = wordsList.get(word);
		int count = 0;
		if(null != wordCount) {
			count = wordCount;
		}
		return count;
	}
	
	private HashMap<String,Integer> getListForWord(String word) throws IOException, ClassNotFoundException {
		if(null == _currentLoadedList || false == _currentLoadedList.containsKey(word)) {
			File wordFile = getFileForWord(word);
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(wordFile));
			_currentLoadedList = (HashMap<String, Integer>) inputStream.readObject();
		}
		return _currentLoadedList;
	}

	private File getFileForWord(String word) throws IOException {
		return createOrGetFile(getFileIndexForWord(word));
	}

	public int getFileIndexForWord(String word) {
		return word.charAt(0)%HASH_SIZE;
	}
	
	private String getFileName(int fileIndex) {
		return "File-"+fileIndex;
	}

	public boolean hasParsedData() {
		return _bParsedData;
	}

}
