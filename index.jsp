<%@ include file="includes/header.jsp" %>

<div class="animated bounceInDown" style="font-size:48pt; font-family:arial; color:#990000; font-weight:bold">Web Opinion Visualiser</div>

</p>&nbsp;</p>&nbsp;</p>

<table width="600" cellspacing="0" cellpadding="7" border="0">
	<tr>
		<td valign="top">

			<form bgcolor="white" method="POST" action="doProcess">
				<fieldset>
					<legend><h3>Specify Details</h3></legend>

					<p>
						<b>Search heuristic: </b>
						<select name="searchAlg">
							<option selected>1. Breadth first search & Fuzzy logic heuristic</option>
							<option>2. Depth first search & Relevance heuristic</option>
							<option>3. Random search & Relevance heuristic</option>
						</select>
					</p>

					<p>
						<b>Max page loads: </b>
						<select name="maxPageLoads">
							<option>50</option>
							<option selected>100</option>
							<option>250</option>
						</select>
					</p>

					<p>
						<b>Number of threads: </b>
						<select name="numThreads">
							<option>10</option>
							<option selected>25</option>
							<option>50</option>
						</select>
					</p>

					<p>
						<b>Number of cloud words: </b>
						<select name="numCloudWords">
							<option>20</option>
							<option selected>60</option>
							<option>80</option>
						</select>
					</p>

					<p>
					<b>Enter Text :</b><br>
					<input name="query" size="100">	
					</p>

					<center><input type="submit" value="Search & Visualise!"></center>
				</fieldset>							
			</form>	

		</td>
	</tr>
</table>
<%@ include file="includes/footer.jsp" %>

