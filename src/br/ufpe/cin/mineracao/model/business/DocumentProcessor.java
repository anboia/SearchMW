package br.ufpe.cin.mineracao.model.business;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DocumentProcessor {

	public static DocumentProcessor instance;

	public String regex;
	
	private DocumentProcessor(){

		FileReader fr;
		try {
			fr = new FileReader("stoplist-ingles.txt");
		
			BufferedReader br = new BufferedReader(fr);
			
			StringBuilder sb = new StringBuilder("\\b(?:").append(br.readLine());
		
			String line;
			
			while((line = br.readLine())!= null){
				sb.append("|").append(line);
			}
			
			sb.append(")\\b\\s*");
			
			regex = sb.toString();
			System.out.println(regex);
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public static DocumentProcessor getInstance(){
		if(instance == null){
			instance = new DocumentProcessor();
		}

		return instance;
	}

	public void createDocumentLogicView(String fromPath, String toPath) throws IOException{
		Files.walk(Paths.get(fromPath),1).forEach(filePath -> {
			try {
				
				if(!filePath.toFile().isDirectory()){
					processFile(filePath, toPath);
				} else {
					System.out.println(filePath.getFileName());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}


	private void processFile(Path filePath, String toPath) throws IOException {
		String newName = filePath.toFile().getName();
		//System.out.println(filePath);
		FileReader fr = new FileReader(filePath.toFile());
		BufferedReader br = new BufferedReader(fr);

		FileWriter fw = new FileWriter(toPath+newName);
		BufferedWriter bw = new BufferedWriter(fw);					


		StringBuilder doc = new StringBuilder();

		String line;
		while((line = br.readLine()) != null){
			//System.out.println(line);
			doc.append(processeLine(line)).append("\n");
		}

		bw.write(doc.toString());

		bw.close();

		br.close();					
	}


	private String processeLine(String line){
			
		return line.replaceAll(regex, " ");
	}
}
