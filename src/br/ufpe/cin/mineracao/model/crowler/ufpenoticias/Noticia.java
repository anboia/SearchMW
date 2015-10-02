package br.ufpe.cin.mineracao.model.crowler.ufpenoticias;

public class Noticia {
	
	public String titulo;
	public String url;
	public String legenda;
	public String conteudo;
	
	
	public Noticia(String titulo, String url, String legenda) {
		this.titulo = titulo;
		this.url = url;
		this.legenda = legenda;
	}
	
	
	public void print(){
		System.out.println("Titulo: "+titulo);
		System.out.println("URL: "+url);
		System.out.println("legenda: "+legenda);
		System.out.println("");
	}
}
