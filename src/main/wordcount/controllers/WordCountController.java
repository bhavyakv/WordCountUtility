package main.wordcount.controllers;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import main.wordcount.parser.WordParser;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class WordCountController {
	
	static WordParser _wordParser;
	
	@Value("${input.file.path}")
	private String filePathName;
	
	public WordCountController() {
		System.out.println("WordCountController constructor");
		if(null == _wordParser) {
			_wordParser = new WordParser();
			if(false == _wordParser.hasParsedData()) {
				parseData();
			}
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/countword/{word}")
	@ResponseBody
	public String countWord(@PathVariable("word") String word) throws ClassNotFoundException, IOException {
		JSONObject response = new JSONObject();
		if(null != word) {
			int count = _wordParser.getWordCount(word.toLowerCase());
			
			response.put(word, count);
			
		}
		else {
			response.put("Error", "Input word not given");
		}
		return response.toString();
	}

	private void parseData() {
//		URL fileUrl = WordCountController.class.getClassLoader().getResource("/random.txt");
		URL fileUrl = WordCountController.class.getClassLoader().getResource(filePathName);
		File textFile = new File(fileUrl.getPath());
		if(true == textFile.exists()) {
			try {
				_wordParser.parseWords(new InputStreamReader(new FileInputStream(
						textFile)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Main method Words parsed");
		}
		else {
			System.err.println("Input file could not be read!");
			System.exit(1);
		}
		
	}
}
