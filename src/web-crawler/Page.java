/* Brief Description: Class Page models a web page object */

//Developer: Dimitrius G. Papachristoudis
//email: zelgius23@freemail.gr
//Last Update: 5/8/2012

//Import the necessary API packages/classes
import java.util.TreeMap;

public class Page
{

	private static int count=0;   //A counter for the number of pages created (note that it is defined as static)

	private int parent;     //The page's parent page id
	private int site_id;    //The site to which the page belongs
	private int page_id;    //An integer number identifying this page
	private int depth;      //The page's distance/depth from the seed page
	private String title;   //The page's title
	private String url;     //The page's url
	private TreeMap<String, Integer> words;   //A dictionary containing the words found in this
	                                          //page along with their respective frequencies

	//Constructor
	public Page(int p, int s, int d, String t, String u)
	{
		//Increment count and set this number as the current page's id
		page_id = ++count;
	
		parent = p;
		site_id = s;
		depth = d;
		title = t;
		url = u;
		words = new TreeMap<String, Integer>();
	}
	
	//A method for retrieving this page's dictionary
	public TreeMap<String, Integer> getWords()
	{
		return words;
	}

	//A method for setting this page's dictionary to a given dictionary
	public void setWords(TreeMap<String, Integer> words)
	{
		this.words = words;
	}
	
	//A method for retrieving this page's id
	public int getPageId()
	{
		return page_id;
	}
	
	//A method for setting this page's id to a given integer
	public void setPageId(int id)
	{
		page_id = id;
	}

	//A method for retrieving this page's depth
	public int getDepth()
	{
		return depth;
	}

	//A method for setting this page's depth to a given integer
	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	//A method for retrieving the parent id for this page
	public int getParent()
	{
		return parent;
	}

	//A method for retrieving the owner site's id for this page
	public int getSite_id()
	{
		return site_id;
	}

	//A method for retrieving this page's title
	public String getTitle()
	{
		return title;
	}

	//A method for retrieving this page's url address
	public String getUrl()
	{
		return url;
	}

	//A method for setting this page's parent id to a given integer
	public void setParent(int parent)
	{
		this.parent = parent;
	}

	//A method for setting this site's id to a given integer
	public void setSite_id(int site_id)
	{
		this.site_id = site_id;
	}

	//A method for setting this page's title to a given String
	public void setTitle(String title)
	{
		this.title = title;
	}

	//A method for setting this page's url to a given String
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	//A method for retrieving the number of distinct words in this page
	public int getWordCount()
	{
		return words.size();
	}

}
