/* Brief Description: Class SearchEngine provides a client GUI for our keyword
 * lookup service. This simple interface displays an array containing the top 10
 * results at most for the specified keywords or a message stating that no results
 * were found. */

//Developer: Dimitris Papachristoudis
//Last Update: 5/8/2012

//Import the necessary API packages/classes
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

public class SearchEngine extends JFrame implements ActionListener
{

	private JTextField txt;
	private JButton search,
			clear;
	private JLabel message;
	JTable table;
	Container C;

	private static final String headers[] = {"No.", "Page Url", "Frequency"};
	private String data[][] = null;
	private DefaultTableModel model;

	private static int port_number = 4000;

	private InetAddress server_address = null;
	private Socket client = null;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	
	private JPanel resultsPanel;

	//Constructor
	public SearchEngine()
	{
		//Connect to the lookup server
		try
		{
			server_address = InetAddress.getLocalHost();
			client = new Socket(server_address, port_number);
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
		}
		catch (UnknownHostException ex)
		{}
		catch (IOException ex)
		{}

		message = new JLabel("");

		//Window settings
		this.setTitle("A Search Engine by Dimitris Papachristoudis 2312");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(new Dimension(550, 430));

		//Retrieve the frame's Content Pane
		C = this.getContentPane();
		C.setBackground(Color.white);

		JPanel logo = new JPanel();
		
		logo.add(new JLabel("<html><h1><font face=\"Lucida Calligraphy\" size=7 color=blue>M </font>"+
							"<font face=\"Lucida Calligraphy\" size=7 color=red>o </font>"+
							"<font face=\"Lucida Calligraphy\" size=7 color=orange>o </font>"+
							"<font face=\"Lucida Calligraphy\" size=7 color=blue>f </font>"+
							"<font face=\"Lucida Calligraphy\" size=7 color=green>l </font>"+
							"<font face=\"Lucida Calligraphy\" size=7 color=red>e</font></h1></html>"));
		logo.setBackground(Color.white);
		logo.setToolTipText("Dedicated to my dear friend Dora for her steadfast support");
		C.add(logo, BorderLayout.NORTH);
		
		txt = new JTextField(20);
		txt.setToolTipText("Enter your keywords here");
		search = new JButton("Search");
		search.setToolTipText("Click here to commence search");
		clear = new JButton("Clear");
		clear.setToolTipText("Click here to clear all fields");
		String html_text = "<html></html>";
		message = new JLabel(html_text);

		//Add actionListeners to the components
		txt.addActionListener(this);
		search.addActionListener(this);
		clear.addActionListener(this);

		//Create a layout manager for the JFrame
		GridBagLayout GrBag=new GridBagLayout();
		
		JPanel center = new JPanel();
		center.setLayout(GrBag);


		// --------------------------------------------------

		GridBagConstraints gbc_txt = new GridBagConstraints();
		gbc_txt.gridx = 0;
		gbc_txt.gridy = 0;
		gbc_txt.gridwidth = 4;
		gbc_txt.gridheight = 1;
		gbc_txt.fill = GridBagConstraints.HORIZONTAL;

		GrBag.setConstraints(txt, gbc_txt);
		txt.setPreferredSize(new Dimension(165, 30));
		center.add(txt);

		// --------------------------------------------------------

		GridBagConstraints gbc_search = new GridBagConstraints();
		gbc_search.gridx = 0;
		gbc_search.gridy = 1;
		gbc_search.gridwidth = 2;
		gbc_search.gridheight = 1;
		gbc_search.fill = GridBagConstraints.BOTH;
		gbc_search.insets = new Insets(9, 10, 3, 10);

		GrBag.setConstraints(search, gbc_search);
		search.setPreferredSize(new Dimension(100, 30));
		center.add(search);

		// --------------------------------------------------------

		GridBagConstraints gbc_clear = new GridBagConstraints();
		gbc_clear.gridx = 2;
		gbc_clear.gridy = 1;
		gbc_clear.gridwidth = 2;
		gbc_clear.gridheight = 1;
		gbc_clear.fill = GridBagConstraints.BOTH;
		gbc_clear.insets = new Insets(9, 10, 3, 3);

		GrBag.setConstraints(clear, gbc_clear);
		clear.setPreferredSize(new Dimension(65, 30));
		center.add(clear);

		// --------------------------------------------------------

		GridBagConstraints gbc_message = new GridBagConstraints();
		gbc_message.gridx = 0;
		gbc_message.gridy = 3;
		gbc_message.gridwidth = 4;
		gbc_message.gridheight = 2;
		gbc_message.fill = GridBagConstraints.HORIZONTAL;
		gbc_message.insets = new Insets(9, 10, 0, 0);

		GrBag.setConstraints(message, gbc_message);
		center.add(message);


		// --------------------------------------------------------

		center.setBackground(Color.white);
		C.add(center, BorderLayout.CENTER);

		resultsPanel = new JPanel();
		
		//Display the frame
		this.setVisible(true);

	}

	//Implementation of the actionPerformed() method
	public void actionPerformed(ActionEvent e)
	{
		//If the table is drawn from a previous search
		//we remove it, set the table to null and finally
		//repaint the frame to replace
		if (table != null)
		{
			C.remove(resultsPanel);
			table = null;
			data = null;
			this.repaint();
		}
		
		//If the ActionEvent's source was the clear button we
		//reset the content in all of the interface's components
		if (e.getSource() == clear)
		{
			txt.setText("");
			message.setText("");
		}
		else if (e.getSource() == search || e.getSource() == txt)
		{
			String request = txt.getText();
			try
			{
				out.writeUTF(request);
				String response = in.readUTF();
				StringTokenizer lines = new StringTokenizer(response, "\n");
				int tokens = lines.countTokens();
				if (tokens == 0)   //No keywords were given!
				{
					message.setText("<html><font color=red>Please enter keyword(s) and retry!</font></html>");
				}
				else if (tokens == 1)   //No matches were found!
				{
					message.setText("<html><font color=red>"+lines.nextToken()+"</font></html>");
				}
				else   //tokens > 1 i.e.: results were found!
				{
					message.setText("<html><font color=green>"+lines.nextToken()+"</font></html>");
					data = new String[tokens-1][3];
					int i = 0;
					while (lines.hasMoreTokens())
					{
						int j = 0;
						String line = lines.nextToken();
						StringTokenizer tokenizer = new StringTokenizer(line, " ");
						while (tokenizer.hasMoreTokens())
						{
							data[i][j] = tokenizer.nextToken();
							++j;
						}
						i++;
					}
					
					//
					model = new DefaultTableModel(data, headers){
						
						//Method implementation that activates the standard renderers
						public Class getColumnClass(int col)
						{
							return String.class;
						}
						
						//Override method to display the desired headers
						public String getColumnName(int col)
						{
							return headers[col];
						}
					};
					
					//Create the table that will host the returned results
					table = new JTable(model);
					table.setIntercellSpacing(new Dimension(2, 1));
					
					//Recreate resultsPanel
					resultsPanel = new JPanel();
					resultsPanel.setLayout(new BorderLayout());
					//resultsPanel.add(headerPanel, BorderLayout.NORTH);
					//resultsPanel.add(tablePanel, BorderLayout.CENTER);
					
					
					//Add components to the resultsPanel
					resultsPanel.add(table.getTableHeader(), BorderLayout.NORTH);
					resultsPanel.add(table, BorderLayout.CENTER);
					
					
					resultsPanel.setBackground(Color.white);
					resultsPanel.setBorder(BorderFactory.createTitledBorder(message.getText()));
					message.setText("");
					C.add(resultsPanel, BorderLayout.SOUTH);
				}
			}
			catch (IOException ex)
			{
				//Ignore
			}
			repaint();   //repaint the frame
		}

		
	}

	//Main Method
	public static void main(String args[])
	{
		SearchEngine moofle = new SearchEngine();
	}

}
