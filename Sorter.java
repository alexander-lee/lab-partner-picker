package labPartnerPicker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Sorter {
	static class Pair {
		public String first, second, third;
		public Pair(String first, String second){
			this.first = first;
			this.second = second;
		}
	}
	
	public static HashSet<Pair> parseFile(File f){
		HashSet<Pair> partners = new HashSet<Pair>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = reader.readLine(); //Skip first line
			while((line = reader.readLine()) != null){
				String[] people = line.split(":");
				// Ignore the triples for now
				if(people.length == 2){
					partners.add(new Pair(people[0], people[1]));
				}
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found! " + f.getName());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return partners;
	}
	
	public static void main(String[] args){
		// Holds a List of files of pairs
		ArrayList< HashSet<Pair> > previousPartners = new ArrayList< HashSet<Pair> >();
		
		// Read old files
		File folder = new File("src/labPartnerPicker/partners");
		if(!folder.exists()){
			folder.mkdir();
			folder = new File("src/labPartnerPicker/partners");
		}

		for(File f : folder.listFiles()){
			String[] fileComponents = f.getName().split("\\.");
			if(fileComponents.length == 2 && 
			   fileComponents[1].equals("txt") &&
			   fileComponents[0].contains("partners_week_")){
				previousPartners.add(parseFile(f));
			}
		}
		
		String fileName = "src/labPartnerPicker/allMembers.txt";
		String fileOutput = "src/labPartnerPicker/partners/partners_week_" + (previousPartners.size()+1 + ".txt");
		BufferedReader buffer;
		PrintWriter pwriter = null;
		
		try {
			FileReader reader = new FileReader(fileName);
			buffer = new BufferedReader(reader);
			
			FileWriter writer = new FileWriter(fileOutput);
			pwriter = new PrintWriter(writer);
			
			List<String> members = new ArrayList<String>();
			
			String line = null;
			while( (line = buffer.readLine()) != null){
				members.add(line.toLowerCase());
			}
			
			pwriter.println("Here are the lab partners for this week: \n");
			
			int halfSize = members.size() /2 ;
			
			for(int i = 0; i < halfSize; i++){
				int partner1Index = (int)(Math.random() * members.size());
				int partner2Index = (int)(Math.random() * members.size());
				
				// Make sure partners aren't the same
				while(partner1Index == partner2Index){
					partner2Index = (int)(Math.random() * members.size());
				}
				
				// Make sure partners aren't previous partners
				boolean same = true;
				while(same && previousPartners.size() > 0){
					for(HashSet<Pair> partners : previousPartners){
						// Get the Pair and Inverse Pair of Parnters and see if it exists in our array of previous partners
						Pair pair = new Pair(members.get(partner1Index), members.get(partner2Index));
						Pair revPair = new Pair(members.get(partner2Index), members.get(partner1Index));
						if(!partners.contains(pair) && !partners.contains(revPair)){
							same = false;
						}
						else {
							partner2Index = (int)(Math.random() * members.size());
						}
					}
				}
				
				pwriter.print( members.get(partner1Index) + " : " + members.get(partner2Index) );
				if( partner1Index < partner2Index ){
					members.remove(partner1Index);
					members.remove(partner2Index-1);
				} else {
					members.remove(partner2Index);
					members.remove(partner1Index-1);
				}
				
				if(i == (halfSize-1) && members.size() != 0){
					pwriter.println(" : " + members.get(0));
				}
				pwriter.println("\n");
			}
			
			
			buffer.close();
			pwriter.flush();
		} catch (FileNotFoundException e) {
			System.out.println("a file was not found");
		} catch (IOException e){
			e.printStackTrace();
		}
		finally {
			if(pwriter != null){
				pwriter.close();
			}
		}
	}
}
