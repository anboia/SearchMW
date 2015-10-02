package br.ufpe.cin.mineracao.model.crowler.alepe;

import java.util.ArrayList;

public class Documento {

	public String url;
	public ArrayList<Pagina> paginas;
	
	public Documento(String url) {
		this.url = url;
		paginas = new ArrayList<Pagina>();
	}
	
	
	public void add(Pagina newPagina){
		paginas.add(newPagina);
	}
	
	
}
