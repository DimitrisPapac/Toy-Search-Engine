/* Brief Description: Class WebParser handles both the transaction between the web crawler
 * and a web page and the parsing of a page's source code. This class' objects allow us to
 * request a specified url's page source, locate all links to other pages within this site,
 * remove tags, retrieve image titles etc. */

//Developer: Dimitrius G. Papachristoudis
//email: zelgius23@freemail.gr
//Last Update: 5/8/2012

//Import the necessary API packages/classes
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebParser
{

	//A method for requesting a web page's source code.
	public String getUrl(String url)
	{

		Reader reader = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6");
		// httpget.setHeader("User-Agent","Googlebot/2.1 (+http://www.google.com/bot.html)");
		// httpget.setHeader("Accept-Encoding", "gzip,deflate,sdch");
		// httpget.setHeader("Accept",
		// "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		// httpget.setHeader("Accept-Language", "en-us,en;q=0.8");
		httpget.setHeader("Accept-Charset", "UTF-8,*;q=0.5");
		try
		{
			HttpResponse response = httpclient.execute(httpget);

			reader = new InputStreamReader(response.getEntity().getContent(),
					"utf-8");

			StringBuffer sb = new StringBuffer();

			int read;
			char[] cbuf = new char[1024];
			while ((read = reader.read(cbuf)) != -1)
				sb.append(cbuf, 0, read);

			reader.close();
			return sb.toString();

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	//A method for retrieving a web page's title (ie: what is between <title>
	//and </title> in the web page's source code).
	public String htmlTitle(String txtSource)
	{
		Document document = Jsoup.parse(txtSource);
		String title = document.title().replaceAll("\\r?\\n", " ");

		return title;
	}

	//A method for removing HTML tags.
	//Returns the plaintext in the web page's body.
	public String htmlToText(String txtSource)
	{
		Document document = Jsoup.parse(txtSource);
		Element body = document.body();

		return body.text();
	}
	
	//A method for retrieving the value of alt attributes
	//from images found in a web page's source code.
	public String getImageAlts(String txtSource)
	{
		String res="";

		Document document = Jsoup.parse(txtSource);
		Elements alts = document.select("img[alt]");
		for (Element e : alts)
		{
			String alt = e.attr("alt");
			StringTokenizer tokens = new StringTokenizer(alt, " \t\n\r\f.,;:!?_'\"(){}[]~^-=+–—’'|«»><=//…");
			while (tokens.hasMoreTokens())
			{
				String token=tokens.nextToken();
				if (!res.contains(token))
					res+=token+" ";
			}
		}
		return res;
	}
	
	//A method for retrieving the value of title attributes
	//from images found in a web page's source code.
	public String getImageTitles(String txtSource)
	{
		String res="";
		
		Document document = Jsoup.parse(txtSource);
		Elements titles = document.select("img[title]");
		for (Element e : titles)
		{
			String title = e.attr("title");
			StringTokenizer tokens = new StringTokenizer(title, " \t\n\r\f.,;:!?_'\"(){}[]~^-=+–—’'|«»><=//…");
			while (tokens.hasMoreTokens())
			{
				String token=tokens.nextToken();
				if (!res.contains(token))
					res+=token+" ";
			}
		}
		return res;
	}
	
	//A method for retrieving a list of all the links found in a web page
	//Please note that emails and links to anchors in the current page
	//are ommited. Links to web pages with a different domain are also ignored
	public ArrayList<String> findAllLinks(String domain, String url)
	{
		//A list that will hold the results
		ArrayList<String> res= new ArrayList<String>();
		
		Document doc = null;
		try
		{
			//Connect to the given url address and request its source code
			//Please note that connecting like this allows us to convert
			//relative urls to absolute urls.
			doc = Jsoup.connect(url).get();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Elements links = doc.select("a[href]");
		for (Element link : links)
		{
			//String relHref = link.attr("href");
			String absHref = link.attr("abs:href");

			//Ignore emails, links to specified anchor points and links to
			//web pages whose site has a different domain from the current
			//page's owner site.
			if (absHref.contains(domain) && !absHref.contains("#") && !absHref.contains("@"))
			{
				if (!res.contains(absHref))
					res.add(absHref);
			}

		}
		return res;
	}

	//A method for retrieving all HTML links found in a given page source.
	//NOTE: This method is ommited in favor of findAllLinks() above.
	public ArrayList<String> htmlLinks(String domain, String txtSource)
	{
		System.out.println("Domain: "+domain);
		ArrayList<String> links = new ArrayList<String>();

		Document document = Jsoup.parse(txtSource);
		Elements hrefs = document.select("a[href]");
		for (Element link : hrefs)
		{
			String href = link.attr("abs:href");
			if (href.contains(domain) && !href.contains("#") && !href.contains("@"))
			{
				if (!links.contains(href))
					links.add(href);
			}
		}
		System.out.println("Number of links: "+links.size());
		return links;
	}

}