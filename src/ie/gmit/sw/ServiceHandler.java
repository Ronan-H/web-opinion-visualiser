package ie.gmit.sw;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

import java.util.Base64;

public class ServiceHandler extends HttpServlet {
	private File ignoredWords;
	
	public void init() {
		ServletContext ctx = getServletContext(); //Get a handle on the application context
		
		//Reads the value from the <context-param> in web.xml
		String ignoredWordsPath = getServletContext().getRealPath(File.separator) + ctx.getInitParameter("IGNORE_WORDS_FILE_LOCATION");
		ignoredWords = new File(ignoredWordsPath); //A file wrapper around the ignore words...
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html"); //Output the MIME type
		PrintWriter out = resp.getWriter(); //Write out text. We can write out binary too and change the MIME type...
		
		//Initialise some request variables with the submitted form info. These are local to this method and thread safe...
		String searchAlgName = req.getParameter("searchAlg");
		String query = req.getParameter("query");
		int maxPageLoads = Integer.parseInt(req.getParameter("maxPageLoads"));
		int numThreads = Integer.parseInt(req.getParameter("numThreads"));
		int numCloudWords = Integer.parseInt(req.getParameter("numCloudWords"));
		// get search algorithm based on option index
		SearchAlgorithm searchAlg = SearchAlgorithm.values()[Character.getNumericValue(searchAlgName.charAt(0)) - 1];

		out.print("<html><head><title>Artificial Intelligence Assignment</title>");		
		out.print("<link rel=\"stylesheet\" href=\"includes/style.css\">");
		
		out.print("</head>");		
		out.print("<body>");		
		out.print("<div style=\"font-size:48pt; font-family:arial; color:#990000; font-weight:bold\">Web Opinion Visualiser</div>");
		out.print("<p>The &quot;ignore words&quot; file is located at <font color=red><b>" + ignoredWords.getAbsolutePath() + "</b></font> and is <b><u>" + ignoredWords.length() + "</u></b> bytes in size.");
		out.print("<fieldset><legend><h3>Result</h3></legend>");

		// initialise the query cloud generator, using the selected search parameters
		QueryCloudGenerator generator = new QueryCloudGenerator(
				query, maxPageLoads, numThreads, numCloudWords, searchAlg, ignoredWords
		);
		// crawl the web and generate a word cloud for words relating to the query
		BufferedImage cloud = generator.generateWordCloud();

		out.print("<img src=\"data:image/png;base64," + encodeToString(cloud) + "\" alt=\"Word Cloud\">");
		out.print("</fieldset>");

		// display crawl statistics
		out.print("<fieldset><legend><h3>Crawl stats</h3></legend>");
		out.print(generator.getCrawlStats());
		out.print("</fieldset>");

		out.print("</br>");
		out.print("<a href=\"./\">Return to Start Page</a>");
		out.print("</body>");	
		out.print("</html>");	
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
 	}
	
	private String encodeToString(BufferedImage image) {
	    String s = null;
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();

	    try {
	        ImageIO.write(image, "png", bos);
	        byte[] bytes = bos.toByteArray();

	        Base64.Encoder encoder = Base64.getEncoder();
	        s = encoder.encodeToString(bytes);
	        bos.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return s;
	}
}