package com.digitalstrom.dshub.esb.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ReadConfigs {

	private static final String FILENAME = "C:\\Users\\antonio.bruno\\workspace\\monitoringagent\\config\\server_conf.cfg";
	private static Map<String, String> configParams;
	
	public static void main(String[] args) {

		Map<String, String> map = new ReadConfigs().getConfParameters();
		System.out.println(Arrays.toString(map.entrySet().toArray()));
	}
	
	public static Map<String, String> getInstance(){
        return configParams;
    }
	
	private ReadConfigs(){};
	
	//static block initialization for exception handling
    static{
        try{
        	configParams = new ReadConfigs().getConfParameters();
        }catch(Exception e){
            throw new RuntimeException("Exception occured in creating singleton instance");
        }
    }
    
	private Map<String, String> getConfParameters(){
		
		BufferedReader br = null;
		FileReader fr = null;
		Map<String, String> map = new HashMap<String, String>();
		
		try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(FILENAME));

			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.startsWith("#"))
					continue;
				String[] tokens = sCurrentLine.split("\\s+");
				if(tokens.length > 1)
					map.put(tokens[0].trim().toLowerCase(), tokens[1].trim().toLowerCase());
				else
					map.put(tokens[0].trim().toLowerCase(), "");
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
			
		}
		
		return map;
	}

}