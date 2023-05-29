/* Brief Description: A FileManager instance allows us to access pre-specified
 * csv files and either retrieve the seed pages (sites) stored there, or
 * record data regarding web pages and words. */

//Developer: Dimitris Papachristoudis
//Last Update: 5/8/2012

//Import the necessary API packages/classes
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

public class FileManager
{

	//A method for reading all recorded sites from a csv file.
	public ArrayList<Site> getSites()
	{
		//A list that will host the results
		ArrayList<Site> sites = new ArrayList<Site>();

		//csv file containing data
		String strFile = "C:/wordFreq/sites.csv";

		//Create BufferedReader to read csv file
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(strFile));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		String strLine = "";
		StringTokenizer st = null;
		int lineNumber = 0, tokenNumber = 0;

		//Read comma separated file line by line
		try
		{
			//For each line
			while ((strLine = br.readLine()) != null)
			{
				lineNumber++;

				// break comma separated line using ";"
				st = new StringTokenizer(strLine, ";");
				ArrayList<String> tokens = new ArrayList<String>();
				while (st.hasMoreTokens())
				{
					// display csv values
					tokenNumber++;
					tokens.add(st.nextToken());
					// System.out.println("Line # " + lineNumber + ", Token # "+
					// tokenNumber + ", Token : " + st.nextToken());
				}

				//Add site to the results list
				sites.add(new Site(Integer.parseInt(tokens.get(0)), tokens
						.get(1), tokens.get(2)));

				// reset token number
				tokenNumber = 0;

			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		//Return the result
		return sites;
		
	}

	//A method for adding new pages to a csv file.
	public void addPages(Stack<Page> pages, int siteId)
	{

		String txtPages = "",   //A String for storing page related data
			   txtWords = "";   //A String for storing word related data
		//int pageId = 1;
		int count=0;   //A counter for keeping track of the number of recorded pages
		
		//For each page in the stack
		for (Page page : pages)
		{
			//Append data regarding the current page to the result variable
			txtPages += page.getPageId() + ";" + page.getParent() + ";"
					+ page.getSite_id() + ";" + page.getDepth() + ";"
					+ page.getTitle() + ";" + page.getUrl() + "\n";

			//For each word entry in the page's dictionary (TreeMap)
			for (Map.Entry<String, Integer> entry : page.getWords().entrySet())
			{
				String key = entry.getKey();        //a word
				Integer value = entry.getValue();   //its frequency

				//Append a <word, frequency> pair to the result variable
				txtWords += page.getPageId() + ";" + key + ";" + value + "\n";

			}
			//pageId++;
			
			//Increment counter
			count++;
			
			//Inform user that the page was successfully recorded
			System.out.println("Successfully wrote page#"+count+"!");
			System.out.println("Remaining pages: "+(pages.size()-count));
			
		}

		try
		{
			//Open a FileWriter stream for recording pages
			FileWriter writerPages = new FileWriter("C:/wordFreq/pages"
					+ siteId + ".csv");
			
			//Open a FileWriter stream for recording words
			FileWriter writerWords = new FileWriter("C:/wordFreq/words"
					+ siteId + ".csv");

			//Write pages
			writerPages.write(txtPages);
			
			//Write words
			writerWords.write(txtWords);

			//Flush both streams
			writerWords.flush();
			writerPages.flush();
			
			//Close both streams
			writerWords.close();
			writerPages.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}
