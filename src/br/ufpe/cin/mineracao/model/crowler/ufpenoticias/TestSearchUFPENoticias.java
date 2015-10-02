package br.ufpe.cin.mineracao.model.crowler.ufpenoticias;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.ufpe.cin.mineracao.model.crowler.alepe.Documento;
import br.ufpe.cin.mineracao.model.crowler.alepe.Pagina;

public class TestSearchUFPENoticias {

	public static int DIAS = 31;
	public static int MES = 8;
	public static int ANO = 2015;

	public static ArrayList<Noticia> noticias;

	public static void main(String[] args) {
		for(int ano=ANO; ano>=2010; ano--)
		{
			for(int mes=MES; mes>=1; mes--){


				noticias = new ArrayList<Noticia>();
				try {
					Document d = Jsoup.parse(getURLContent(
							String.format(
									"https://www.ufpe.br/agencia/index.php?option=com_content&view=article&id=38&Itemid=72&mes=%d&ano=%d",
									mes, ano)));

					Elements els = d.getElementsByClass("textovalendo");
					Elements tbody = els.get(0).getElementsByTag("tbody");

					System.out.println("#tBody = "+tbody.size());

					Element e = tbody.get(0);
					Elements chamadas = e.getElementsByClass("chamada"); 


					for(Element chamada: chamadas){
						String legenda = chamada.getElementsByClass("legenda").get(0).text();
						String url = "https://www.ufpe.br/agencia/"+chamada.getElementsByTag("a").get(0).attr("href");
						String titulo = chamada.text();
						noticias.add(new Noticia(titulo, url, legenda));

					}

					int i = 1;
					for(Noticia n : noticias){

						getNoticiaContent(n);
						printNoticia(n,i, mes, ano);
						i++;
					}


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	private static void printNoticia(Noticia n, int index, int mes, int ano) throws IOException {
		File doc = new File("documents/"+ano+"/"+mes+"/Noticia_"+ano+"_"+mes+"_"+index+".txt");
		System.out.println(doc.getAbsolutePath());

		if(!doc.exists()){
			doc.createNewFile();
		}

		FileWriter fw = new FileWriter(doc.getAbsoluteFile());

		//System.out.println(fw.toString());
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write(n.url);
		bw.write("\n");
		bw.write(n.legenda);
		bw.write("\n");
		bw.write(n.titulo);
		bw.write("\n");
		bw.write(n.conteudo);
		bw.close();


	}


	private static void getNoticiaContent(Noticia n) throws IOException {
		Document d = Jsoup.parse(getURLContent(n.url));

		Elements el = d.getElementsByTag("p");
		Element e;
		StringBuilder sb = new StringBuilder();

		for(int i=0; i<el.size()-2; i++){
			e = el.get(i);
			sb.append(e.text()).append("\n");
		}

		n.conteudo = sb.toString();

	}


	public static void printDocumento(Documento documento, String data ) throws IOException {
		File doc = new File("documents/Noticia"+data+".txt");
		System.out.println(doc.getAbsolutePath());

		if(!doc.exists()){
			doc.createNewFile();
		}

		FileWriter fw = new FileWriter(doc.getAbsoluteFile());

		//System.out.println(fw.toString());
		BufferedWriter bw = new BufferedWriter(fw);

		//bw.write("URL do Documento: "+ documento.url);
		ArrayList<Pagina> paginas = documento.paginas;
		Pagina p;
		for(int i=0; i<paginas.size(); i++){
			p = paginas.get(i);
			//bw.write("\nPagina #"+i+":\n");

			String pText = tratarPagina(p);

			bw.write(pText+"\n");
		}

		bw.close();


	}


	private static String tratarPagina(Pagina pag) {
		String html = pag.conteudo;

		Document d = Jsoup.parse(html);
		Element body = d.body();

		Element text = body.getElementsByClass("text").get(0);

		Elements ps = text.getElementsByTag("p");

		StringBuilder sb = new StringBuilder();

		for(Element p : ps){
			sb.append(p.text()).append("\n");
		}


		return sb.toString();
	}


	public static String getURLContent(String url) throws IOException{
		//System.out.println("getURLContent("+url+")");
		StringBuilder input = new StringBuilder();

		URL u;
		u = new URL(url);

		URLConnection con = u.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null){
			input.append(inputLine);
		}

		in.close();

		return input.toString();
	}

}
