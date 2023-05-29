/* Brief Description: This class is the heart for our custom web crawler. It allows
 * us to crawl through the web, starting from an initial number of seed pages (which
 * can be retrieved either from a CSV file or a database). The crawling depth can be
 * modified through the MAX_DEPTH variable to allow more detailed crawling. The crawling
 * method itself is based off of the Breadth First Search Algorithm (BFS) for graphs using a
 * queue to store the pages that are to be crawled. Please note that .png images are ommited
 * in order to avoid adding "trash" keywords to the database (other formats can be handled
 * in a similar way). Finally, pages with a url that exceeds the maximum length that "fits"
 * in the database are also ommited. */

//Developer: Dimitris Papachristoudis
//Last Update: 5/8/2012

//Import the necessary API packages/classes
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class Main
{

	public static void main(String args[])
	{
		//Initialization
		
		//Define the maximum allowed url length
		final int MAX_URL_LENGTH = 130;
		
		//Define the maximum crawling depth during 
		final int MAX_DEPTH = 2;
		
		//Create a WebParser instance that will allow us to crawl web pages
		WebParser wp = new WebParser();
		
		//Connect to the database
		DBConnector db = new DBConnector();
		
		//Create an object to manage communication with the database
		DBManager dbm = new DBManager();

		//FileManager fm = new FileManager();
		
		//Retrieve seed pages from the database
		final ArrayList<Site> sites = dbm.getSites(db.getConn());
		//OR retrieve seed pages from a CSV file
		//final ArrayList<Site> sites = fm.getSites();
		HashSet<String> seen = new HashSet<String>();
		
		//For each seed page
		for (Site site : sites)
		{
			System.out.println("Crawling site: "+site.getUrl());
			
			Stack<Page> pages = new Stack<Page>();
			Queue<Page> q = new LinkedList<Page>();
			q.add(site.siteToPage());
			while (!q.isEmpty())
			{
				//Dequeue the next page to be crawled
				Page current_page = q.poll();
				String url = current_page.getUrl();
				
				//Mark page as spotted
				seen.add(url);
				
				
				System.out.println("Dequeued: "+url+" Depth: "+current_page.getDepth());
				System.out.println("Queue size: "+q.size());
				
				//Ignore images
				if (current_page.getUrl().contains(".png"))
					continue;
				
				//Ommit pages with very long URLs that won't "fit" in the database
				if (current_page.getUrl().length()>MAX_URL_LENGTH)
					continue;

				
				String page_source = null;
				//Attempt to retrieve page source
				try
				{
					page_source = wp.getUrl(url);
				}
				catch (Exception e)
				{
					System.out.println("Exception Caught!");
					e.printStackTrace();
					continue;
				}
				//If retrieval was successful
				if (page_source != null)
				{
					String pageTitle = wp.htmlTitle(page_source);
					
					//Set page title
					current_page.setTitle(pageTitle);
					
					//Retrieve text from the web page
					String pageBody = wp.htmlToText(page_source);
					
					//Retrieve image titles
					String imgTitles = wp.getImageTitles(page_source);
					
					//Retrieve image alts
					String imgAlts = wp.getImageAlts(page_source);
					
					//Parse this page's words
					current_page.setWords(calcFreq(pageBody + " " + imgTitles + " " + imgAlts));
					
					if (current_page.getWordCount()>0)
						pages.add(current_page);
					
					//Check if we need to crawl deeper
					if (current_page.getDepth() < MAX_DEPTH)
					{
						ArrayList<String> links = wp.findAllLinks(site.getDomain(), current_page.getUrl());
						for (String link : links)
						{
							if (!seen.contains(link))   //Found link to a new page
							{
								System.out.println("Adding page: "+link);
								q.add(new Page(current_page.getPageId(), site.getId(), current_page.getDepth()+1, "", link));
								seen.add(link);
							}
							else
								System.out.println("Page: "+link+" has already been detected!");
						}
					}
				}
			}
			// Add pages to CSV file
			//fm.addPages(pages, site.getId());
			
			// Add pages to DB
			dbm.addPages(db.getConn(), pages);
			
			//Free up some space
			pages = null;
		}
		
	}

	//A static method for calculating the frequency for each word
	//in the given text. The returned result is a TreeMap (dictionary)
	//containing pairs of words along with their respective frequencies
	public static TreeMap<String, Integer> calcFreq(String txt)
	{

		final TreeMap<String, Integer> frequencyMap = new TreeMap<String, Integer>();

		String lines[] = txt.split("\\r?\\n");
		
		//For each line
		for (String line : lines)
		{

			line = line.toLowerCase();

			final StringTokenizer parser = new StringTokenizer(line,
					" \t\n\r\f.,;:!?_~^'\"(){}[]-=+–—’'|«»><=//…");
			
			//For each token
			while (parser.hasMoreTokens())
			{
				final String currentWord = parser.nextToken();
				
				//Ignore words containing less that 3 characters
				if (currentWord.length() > 2)
				{
					Integer frequency = frequencyMap.get(currentWord);
					//If the word is NOT in our dictionary set variable frequency to zero
					if (frequency == null)
						frequency = 0;
					//Replace/Insert a new <word, frequency+1> pair in the dictionary
					frequencyMap.put(currentWord, frequency + 1);
				}
			}

		}

		return frequencyMap;
	}
	
}
