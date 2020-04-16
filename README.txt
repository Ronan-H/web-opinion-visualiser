
== Package breakdown ==
ie.gmit.sw.
    cloud: classes relating to the generation of the word cloud.
    comparator: different comparators for use in a web crawl. When used in the shared PriorityQueue, causes the search to mimic different types of searches, I.e. BFS, DFS, and random walk.
    crawler: classes which perform the web crawl.
		QueryCloudGenerator acts as a facade, allowing the generation of a word cloud based on a query in a single method call.
		QueryCrawler is the Runnable class which expands and crawls nodes taken from the shared PriorityQueue.
	term: classes which gather terms based on the query, and perform statistics on term occurances to decide term weightings.
		TfpdfCalculator is my implementation of tf*pdf, a stastistic used to find "hot" terms in a document corpus.
	test: not strictly part of my submission, but these were the test classes I used to isolate and test various areas of the project.
		Notably, TestQueryCloudGenerator can be used to generate and save a word cloud to a file based on a query, without the use of Tomcat.


== Fuzzy logic implementation ==
A fuzzy logic heuristic value is used to estimate a page's appeal to the search, using three variables:
	Relevance: 
		Range: Between 0 and 1
		Measures: How often the seach query appears in the page.
				  (0 -> no occurences, 1 -> all text was the query)
		Purpose: To prioritise pages where the query appears more, allowing more terms to be scored for the cloud.
		Details: Occurences are weighted according to the HTML element where it was found, I.e. finding the query in a heading element is weighted more than finding the query in a paragraph.
		
	Domain usage:
		Range: Between 0 and 1
		Measures: How often the seach query appears in the page.
				  (0 -> no domain visits, 1 -> all visits were to this page's domain)
		Purpose: To avoid going to pages on a certain domain too many times, to get a better mix of words for the cloud. Making too many requests to one web server can also be disruptive.
		Details: The domain name and extension are extracted from the page URL, E.g. "wikipedia.org"
		
	Depth:
		Range: 0 or more, integer values
		Measures: How deep the page is, I.e. how many pages there are between a page and a root page.
				  (0 -> result page from DuckDuckGo, 2, 3, 4 ... -> "deeper" pages in the search)
		Purpose: To prevent the search from going "down a rabbit hole", assuming that pages further away from the root DuckDuckGo search results are less likely to be relevant.
		Details: Each page has a depth value associated with it, where the page's depth value is their parent's depth value, plus one.

More information about this, including the rules used, is available in the FCL file page-scoring.fcl
	

== Search algorithms ==
Three search algorithms are available to the user, and can be selected on the webpage. These were implemented by simply using a different Comparator for the PriorityQueue of pages.
The search algorithms available are:
	1. Breadth first search & Fuzzy logic heuristic
		Uses the fuzzy logic shown above to order the Queue.
		Makes use of a broad search, getting a diverse mix of words for the cloud. This should be the most useful search implemented for this project.
			
	2. Depth first search & Relevance heuristic
		Performs a depth first search, using a Comparator that compares page depth. Continuously goes deeper on one branch of the search, until it runs out of links to search, and then backtracks. Uses the relevance heuristic to decide on how many links to add to the queue from each page. (Note, this does NOT use fuzzy logic)
		
	3. Random search & Relevance heuristic
		Uses a Comparator which sorts the queue randomly, effectivly making the search a "random walk". The same heuristic as above is used to decide how many new pages to expand from each explored page. Works quite well, but many searches end prematurely because it runs out of pages to explore.


== Stop conditions ==
There are two main reasons why a search might end:
	
	1. Queue of pages to explore becomes empty
	2. Maximum page load limit, specified by the user, has been reached


== Threading ==
One crawler runs per thread. They all work from the same PriorityBlockingQueue. Care was taken to make sure that data structures are accessed corectly between threads, using either the "synchronized" keyword, or using thread-safe data structures. The user can select the number of threads to use for the search.


== Extras ==
I implemented a variation of tf-idf, referred to as "tf-pdf" (Term Frequency * Proportional Document Frequency), which is a statistic I used to weight the words in the word cloud. The weight of each term is linearly proportional to the frequency of the term within each domain, and exponentially proportional to the ratio of documents containing the term over the total number of documents. Implementing this gave a more representitive selection of keywords from the search query, and also gave a smoother weighting distribution. The implementation for this is contained within the TfpdfCalculator class.

The formula I used is the one used in this paper from 2003: https://www.researchgate.net/publication/4004411_Topic_extraction_from_news_archive_using_TFPDF_algorithm

