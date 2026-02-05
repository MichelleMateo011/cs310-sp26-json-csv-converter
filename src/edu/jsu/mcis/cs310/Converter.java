package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.github.cliftonlabs.json_simple.Jsoner;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
        
import java.util.List;

        

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> allRows = reader.readAll();
            
            String[] headers = allRows.get(0);
            
            JsonArray colHeadings = new JsonArray();
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();
            
            for(String h : headers) {
                colHeadings.add(h);
            }
            
            for(int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                
                prodNums.add(row[0]);
                
                JsonArray rowData = new JsonArray();
                for(int j = 1; j < row.length; j++) {
                    String header = headers[j];
                    
                    if(header.equals("Season") || header.equals("Episode")) {
                        try {
                            rowData.add(Integer.parseInt(row[j]));
                        } catch(NumberFormatException e) {
                            rowData.add(row[j]);
                        }
                    }
                    else {
                        rowData.add(row[j]);
                    }
                }
                
                data.add(rowData);
            }
            
            
            JsonObject jsonObj = new JsonObject();
            jsonObj.put("ProdNums", prodNums);
            jsonObj.put("ColHeadings", colHeadings);
            jsonObj.put("Data", data);
            
            result = jsonObj.toJson();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            JsonObject jsonObj = (JsonObject) Jsoner.deserialize(jsonString);
            JsonArray colHeadings = (JsonArray) jsonObj.get("ColHeadings");
            JsonArray prodNums = (JsonArray) jsonObj.get("ProdNums");
            JsonArray dataRows = (JsonArray) jsonObj.get("Data");
            
            StringWriter sw = new StringWriter();
            CSVWriter writer = new CSVWriter(sw);
            
            String[] header = new String[colHeadings.size()];
            for(int i = 0; i < colHeadings.size(); i++) {
                header[i] = (String) colHeadings.getString(i);
            }
            writer.writeNext(header);
            
            for(int i =0; i < dataRows.size(); i++) {
                JsonArray rowData = (JsonArray) dataRows.get(i);
                
                String[] csvRow = new String[colHeadings.size()];
                
                csvRow[0] = (String) prodNums.get(i);
                
                for(int j = 1; j < colHeadings.size(); j++) {
                    Object value = rowData.get(j - 1);
                    
                    if(colHeadings.get(j).toString().equals("Episode") && value instanceof Number) {
                        int ep = ((Number) value).intValue();
                        csvRow[j] = String.format("%02d", ep); 
                    }
                    else {
                        csvRow[j] = value.toString();
                    }
                }
                
                writer.writeNext(csvRow);
            }
            writer.close();
            result = sw.toString();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
        
    }
    
}
