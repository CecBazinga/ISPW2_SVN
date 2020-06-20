package logic;

/*
 THIS CODE WAS DEVELOPED BY DAVIDE FALESSI
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class RetrieveTicketsID {




   private static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	   }

   public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
         String jsonText = readAll(rd);
         JSONArray json = new JSONArray(jsonText);
         return json;
       } finally {
         is.close();
       }
   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
         String jsonText = readAll(rd);
         JSONObject json = new JSONObject(jsonText);
         return json;
       } finally {
         is.close();
       }
   }


   // return value is a list containing the IDs of all closed/fixed bugs in that project
   
  	public static void getIdFixedTicketList(String projName,List<String> tickets) throws IOException, JSONException {
		   
	  Integer j = 0, i = 0, total = 1;
      //Get JSON API for closed bugs w/ AV in the project
      do {
         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
         j = i + 1000;
         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                + i.toString() + "&maxResults=" + j.toString();
         JSONObject json = readJsonFromUrl(url);
         JSONArray issues = json.getJSONArray("issues");
         total = json.getInt("total");
         for (; i < total && i < j; i++) {
            //Iterate through each bug
            String key = issues.getJSONObject(i%1000).get("key").toString();
            tickets.add(key);
            //System.out.println(key);
         }  
      } while (i < total);
      return;
   }
  	
  	
  	public static void getFixedTicketList(String projName,List<Ticket> tickets) throws IOException, JSONException {
		   
  	  Integer j = 0, i = 0, total = 1;
        //Get JSON API for closed bugs w/ AV in the project
  	  
  	  
        do {
        
           //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
           j = i + 1000;
           String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                  + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                  + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                  + i.toString() + "&maxResults=" + j.toString();
           JSONObject json = readJsonFromUrl(url);
           JSONArray issues = json.getJSONArray("issues");
           total = json.getInt("total");
           for (; i < total && i < j; i++) {
              //Iterate through each bug
        	  
        	  String expand = issues.getJSONObject(i%1000).get("expand").toString();
        	  String id = issues.getJSONObject(i%1000).get("id").toString();
              String key = issues.getJSONObject(i%1000).get("key").toString();
              String createdDate = issues.getJSONObject(i%1000).getJSONObject("fields").get("created").toString();
              String resolutionDate = issues.getJSONObject(i%1000).getJSONObject("fields").get("resolutiondate").toString();
              
              JSONArray affectedVersions = issues.getJSONObject(i%1000).getJSONObject("fields").getJSONArray("versions");
              
              Ticket ticket = new Ticket();
              
              ticket.setExpand(expand);
              ticket.setId(id);
              ticket.setKey(key);
              ticket.setCreated(createdDate);
              ticket.setResolutionDate(resolutionDate);
              
              if(affectedVersions!=null) {
            	  
            	  ticket.initializeAV();
            	  
            	  int v=0;
            	  
	              for(; v < affectedVersions.length();v++) {
	            	  
	            	  String aVRealeaseDate = affectedVersions.getJSONObject(v).get("releaseDate").toString();
	            	  String aVName = affectedVersions.getJSONObject(v).get("name").toString();
	            	  String aVId = affectedVersions.getJSONObject(v).get("id").toString();
	            	  
	            	  AffectedVersion aV = new AffectedVersion();
	            	  aV.setDate(aVRealeaseDate);
	            	  aV.setName(aVName);
	            	  aV.setId(aVId);
	            	  
	            	  ticket.getAffectedVersions().add(aV);
	            	  
	              }
              }
             
              tickets.add(ticket);
           
           }  
        } while (i < total);
       
           
        return ;
     }

   
}


























