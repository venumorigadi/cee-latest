<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	String genCsvFileLoc = (String) request.getAttribute("genCsvFileLoc");
	String genCsvErrorFileLoc = (String) request.getAttribute("genCsvErrorFileLoc");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>CEE Project</title>
    <style type="text/css">
        .body {
        	margin-left: 198px;
    		margin-top: 83px;
        }
        .contents {
            border: 1px solid red;
    		display: block;
    		margin-right: 232px;
    		margin-top: 26px;
        }
        .hide{
            display: none;
        }

    </style>
    <script type="text/javascript" src="./js/jquery.1.7.1.js"></script>
    <script type="text/javascript">
       function makeUrl() {
    	   var context = document.getElementById('selectContext').value;
    	   if(context=="") {
    		   jQuery('.onSelection').hide();   
    	   }
    	   document.controlServlet.action =context;
    	   jQuery('.onSelection').show();
    	   showContent();
    	   hideContents ();
       }
       
       function showContent(context) {
    	   jQuery('#selectContext option').each(function(){

    		   var selectedCsv = jQuery('#selectContext option:selected').val()
    		   var allOption = jQuery(this).val()
    		   if(selectedCsv == allOption) {
    			   var ids = jQuery(this).val();
    		      jQuery("#content_"+ids).show();
    		   }
    		 });
       }
       
       function hideContents () {
    	   if(jQuery('#selectContext option:selected').val() == "")
    		   jQuery('.onSelection').hide()

    	   jQuery('#selectContext option').each(function(){

    		   var selectedCsv = jQuery('#selectContext option:selected').val()
    		   var allOption = jQuery(this).val();
    		   if(selectedCsv != allOption) {
    			   var ids = jQuery(this).val();
    		      jQuery("#content_"+ids).hide();
    		   }
    		    
    		})
       }
    </script>
  </head>
  <body class="body">
    <form action=""  name="controlServlet" enctype="multipart/form-data"  method="post">
      <table>
        <tbody>
          <tr>
            <td></td>
          </tr>
          <tr>
            <td>
                <label>Please select your city</label>
            </td>
            <td>
                <select name="uploadingContext" id="selectContext" onchange="javascript:makeUrl();" style="width:218px;">
                    <option value="">--Please Select--</option>
                    <option value="readHoustonExcelFile">Houston</option>
                    <option value="austinCsv">Austin</option>
                    <option value="generateCsv">San Antonio</option>
                    <option value="processSugarLandData">Sugar Land</option>
                    <option value="processBunkerHillVillageData">Bunker Hill Village</option>
                    <option value="processLeanderData">Leander City</option>
                    <option value="processBastropCountyData">Bastrop County</option>
                    <option value="processTravisCountyData">Travis County</option>  
                </select>
            </td>
          <tr>
            <td></td>
            <td class="onSelection" style="display:none"><input type="file" name="fileUpload" size="20" style="margin-left: 0px; margin-top: 17px;"/></td>
          </tr>
          <tr>
            <td></td>
            <td class="onSelection" style="display:none"><input type="submit" value="Process" style="margin-left: 148px; margin-top: 10px;"/></td>
          </tr>
        </tbody>
      </table>
    </form>
    
    <div></div><font color="green">${succesMessage}</font></div>
    <div></div><font color="red">${errorMessage}</font></div>
    <% if (genCsvFileLoc != null) { %>
    	<div> click <a href="${genCsvFileLoc}" target="_blank">here</a>  to download csv.</div>
    <%} %>
    <% if (genCsvErrorFileLoc != null) { %>
    	<div> click <a href="${genCsvErrorFileLoc}">here</a>  to download error csv.</div>
    <%} %>
    <div id="content_austinCsv" class="hide contents">
        <ul>
          <li>
            Formulas to be used in Microsoft Excel are listed in notes.  Keeping Wiki page open to copy and paste Excel formulas is simple way to insert formula.
          </li>
          </li>
			Save after filtering process and before mapping process.
		  <li>
		  </li>
			Turn off any highlights, freeze frames, or conditional formatting that user may have turned on before attempting to upload.
	      <li>
	      </li> 
			There cannot be any typos in the headers if the upload is to work.
          </li>
        </ul>
    </div>
    
    <div id="content_generateCsv" class="hide contents">
        <ul>
			<li>Go to http://www.sanantonio.gov/dsd/crystalreports/.</li>
			<li>Click on Building Permits Granted found under weekly reports.</li>
			<li>Identify date in upper right hand corner and save PDF as YYYYMMDD_SanAntonio (e.g., 20110423_SanAntonio).</li>
			<li>Ensure that date is corresponds to most recent Saturday.  If not alert CEI immediately.</li>
			<li>5. Save copy of PDF in folder 04_San Antonio as YYYYMMDD_SanAntonio.</li>
			</li>Identify all commercial projects with a valuation over $30,000, all residential projects with a valuation over $30,000 and all swimming pool</li> 
			projects..  Highlight those records in PDF editing software with either highlighting option or red box around records.  
			<li>Create in a new spreadsheet in a spreadsheet application</li>
			<li>Across the first row enter the following titles starting in Column A and going right</li>
        </ul>
    </div>
  </body>
</html>
