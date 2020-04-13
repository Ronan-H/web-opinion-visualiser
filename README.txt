
== Package breakdown & Design patterns ==
ie.gmit.sw.
    (subpackage here)

== Fuzzy logic implementation ==
A fuzzy logic heuristic value is used to estimate a page's appeal to the search, using three categories:
	Relevance: 
		Range: Between 0 and 1
		Measures: How often the seach query appears in the page
				  (0 -> no occurences, 1 -> all text was the query)
		Purpose: To prioritise pages where the query appears more, allowing more terms to be scored for the cloud.
		Details: Occurences are weighted according to the HTML element where it was found, I.e. finding the query in a heading element is weighted more than finding the query in a paragraph.
		
	Domain usage:
		Range: Between 0 and 1
		Measures: How often the seach query appears in the page
				  (0 -> no domain visits, 1 -> all visits were to this page's domain)
		Purpose: To avoid going to pages on a certain domain too many times, to get a better mix of words for the cloud. Making too many requests to one web server can also be disruptive.
		Details: The domain name and extension are extracted from the page URL, E.g. "wikipedia.org"
		
	Depth:
		Range: Any, integer values
		Measures: How deep the page is, I.e. how many pages there are between this page and a root page
				  (0 -> result page from DuckDuckGo, 2, 3, 4 ... -> "deeper" pages in the search)
		Purpose: To prevent the search from going "down a rabbit hole", assuming that pages further away from the root DuckDuckGo search results are more likely to be less relevant.
		Details: Each page has a depth value associated with it, where the page's depth value is their parent's depth value, plus one.
	

== Search algorithms ==
Three search algorithms are available to the user, and can be selected on the webpage:
	1. Breadth first search & Fuzzy logic heuristic
			Uses a PriorityQueue to store pages according to an estimate of their value to the search, which is the fuzzy score of their parent.
			


== Extras ==


== References ==



