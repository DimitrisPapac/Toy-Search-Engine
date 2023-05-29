/* Brief Description: Class Site models a web site. A site itself can also be
 * viewed as a web page (a seed page to be more specific); for that purpose
 * we've added a function for performing this conversion - from Site to Page. */

//Developer: Dimitrius G. Papachristoudis
//email: zelgius23@freemail.gr
//Last Update: 5/8/2012

public class Site
{

	private int id;   		 //The site's id
	private String domain;   //The site's domain
	private String url;		 //The site's url

	//Constructor
	public Site(int i, String n, String u)
	{
		id = i;
		domain = n;
		url = u;
	}

	//A method for retrieving the site's id
	public int getId()
	{
		return id;
	}

	//A method for retrieving the site's domain
	public String getDomain()
	{
		return domain;
	}

	//A method for retrieving the site's url address
	public String getUrl()
	{
		return url;
	}

	//A method for modifying the site's id
	public void setId(int id)
	{
		this.id = id;
	}

	//A method for modifying the site's domain
	public void setDomain(String d)
	{
		this.domain = d;
	}

	//A method for modifying the site's url address
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	//A method that "converts" the site to a web page (a Page instance)
	public Page siteToPage()
	{
		WebParser wp = new WebParser();
		String page_src = wp.getUrl(url);
		return new Page(0, id, 0, wp.htmlTitle(page_src), url);
	}
	
}