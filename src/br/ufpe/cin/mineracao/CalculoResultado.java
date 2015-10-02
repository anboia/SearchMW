package br.ufpe.cin.mineracao;

import java.util.ArrayList;

public class CalculoResultado {
	//MatrizBoleano
	
	public static void iniciarCalculos(ArrayList<String> myResults, ArrayList<String> soResults, int totalDoc){
		int docRetrieved = myResults.size();
		int docRelevant = soResults.size();
		int docRelevantRetrived = 0;
		for (String r : soResults) {
			if(myResults.contains(r)) docRelevantRetrived ++;
		}
		double cobertura = docRelevantRetrived*1.0/docRelevant;
		double precisao = docRelevantRetrived*1.0/docRetrieved;
		
		System.out.println("Cobertura: "+cobertura);
		System.out.println("Precisao: "+precisao);
		System.out.println("F-Mensure: "+(docRetrieved-docRelevantRetrived)*1.0/(totalDoc-docRelevant));
	}

}
