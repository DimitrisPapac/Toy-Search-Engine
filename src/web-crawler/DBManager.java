/* Brief Description: DBManager handles the web crawler's SQL transactions 
 * with the database. It allows us to retrieve the sites stored in our
 * database and to add new tupples corresponding to web pages and their
 * respective keywords. */

//Developer: Dimitrius G. Papachristoudis
//email: zelgius23@freemail.gr
//Last Update: 5/8/2012

//Import the necessary API packages/classes
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.sql.Connection;

public class DBManager
{

	//A method for retrieving all recorded sites from our database (wordfreq)
	public ArrayList<Site> getSites(Connection conn)
	{
		//A list for storing up the results
		ArrayList<Site> sites = new ArrayList<Site>();

		//Query the database for all recorded sites
		try
		{
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM  `site`");
			while (rs.next())
			{
				sites.add(new Site(rs.getInt("id"), rs.getString("domain"), rs
						.getString("url")));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		//Return the result
		return sites;

	}
	
	//A method for adding a new site to the database
	public void addSite(Connection conn, Site s)
	{
		try
		{
			PreparedStatement ps = conn.prepareStatement("INSERT IGNORE INTO site(id, domain, url) VALUES (?, ?, ?);");
			ps.setInt(1, s.getId());
			ps.setString(2, s.getDomain());
			ps.setString(3, s.getUrl());
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	//A method for adding web pages along with their sets of keywords to our database (wordfreq)
	public void addPages(Connection conn, Stack<Page> pages)
	{
		//Maximum word length is 30 (see create_wordfreq.sql for details)
		final int MAX_WORD_LENGTH = 30;
		
		//A PreparedStatement for adding words
		PreparedStatement psWords = null;
		
		//A PreparedStatement for adding root pages (root pages do not have a parent page)
		PreparedStatement psRootPages = null;
		
		//A PreparedStatement for adding simple pages
		PreparedStatement psPages = null;
		
		//Compile PreparedStatements (this is done once to increase
		//performance in terms of both time and memory)
		try
		{
			psWords = conn.prepareStatement("INSERT IGNORE INTO `word` (page_id, word, freq) VALUES (?, ?, ?);");
			psRootPages = conn.prepareStatement(
					"INSERT IGNORE INTO `page` (id, site_id, depth, title, url) VALUES (?, ?, ?, ?, ?);");
			psPages = conn.prepareStatement(
					"INSERT IGNORE INTO `page` (id, parent, site_id, depth, title, url) VALUES (?, ?, ?, ?, ?, ?);");
		}
		catch (SQLException ex)
		{
			System.err.println("Error while constructing PreparedStatements!!!");
			ex.printStackTrace();
		}
		
		//For each page in the stack
		for (Page page : pages)
		{
			//Process page
			try
			{
				//Check if current page is a root page or not
				boolean hasParent = (page.getParent() != 0);
				
				if (hasParent)   //page is not a root page
				{
						psPages.setInt(1, page.getPageId());
						psPages.setInt(2, page.getParent());
						psPages.setInt(3, page.getSite_id());
						psPages.setInt(4, page.getDepth());
						psPages.setString(5, page.getTitle());
						psPages.setString(6, page.getUrl());
						psPages.executeUpdate();
				}
				else   //page is a root page since it does not have a parent
				{
						psRootPages.setInt(1, page.getPageId());
						psRootPages.setInt(2, page.getSite_id());
						psRootPages.setInt(3, page.getDepth());
						psRootPages.setString(4, page.getTitle());
						psRootPages.setString(5, page.getUrl());
						psRootPages.executeUpdate();
				}

				System.out.println("PAGE No."+page.getPageId()+" WAS SUCCESSFULLY INSERTED TO THE DATABASE!");

				//For each word in the current page's dictionary (TreeMap)
				for (Map.Entry<String, Integer> entry : page.getWords().entrySet())
				{
					String key = entry.getKey();        //a keyword
					Integer value = entry.getValue();   //its respective frequency
					
					//Ignore words that exceed the maximum allowed length
					if (key.length()>MAX_WORD_LENGTH)
						continue;

					
					psWords.setInt(1, page.getPageId());
					psWords.setString(2, key);
					psWords.setInt(3, value);

					//Execute update and inform user
					psWords.executeUpdate();
					System.out.println("WORD: "+key+" WAS SUCCESSFULLY INSERTED TO THE DATABASE!");
				}
				
			}
			catch (SQLException e)
			{
				System.out.println("OH NO!");
				System.out.println("I got stuck at page: "+page.getPageId()+" its url is: "+page.getUrl());
				e.printStackTrace();
				return;
			}
			//Free up some space
			page = null;
		}

	}

}