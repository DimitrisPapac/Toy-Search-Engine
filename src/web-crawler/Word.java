/* Brief Description: Class Word models a word as it is found in a web page */

//Developer: Dimitrius G. Papachristoudis
//email: zelgius23@freemail.gr
//Last Update: 5/8/2012

public class Word
{

	private int page_id;   //The page in which the word was found
	private String word;   //The word itself
	private int freq;      //The number of times this word appears in a specific page

	//Constructor
	public Word(int i, String w, int f)
	{
		page_id = i;
		word = w;
		freq = f;
	}

	//A method for retrieving the word's owner page
	public int getPage_id()
	{
		return page_id;
	}

	//A method for retrieving the word as a String
	public String getWord()
	{
		return word;
	}

	//A method for retrieving the word's frequency in the specific web page
	public int getFreq()
	{
		return freq;
	}

	//A method for setting the word's owner page to a specified page
	public void setPage_id(int page_id)
	{
		this.page_id = page_id;
	}

	//A method for changing the word
	public void setWord(String word)
	{
		this.word = word;
	}

	//A method for setting the word's frequency to a specified integer
	public void setFreq(int freq)
	{
		this.freq = freq;
	}

	//A method for increasing the word's frequency by a given integer
	public void addFreq(int freq)
	{
		this.freq += freq;
	}
}