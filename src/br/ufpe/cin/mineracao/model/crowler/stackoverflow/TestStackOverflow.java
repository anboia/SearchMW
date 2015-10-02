package br.ufpe.cin.mineracao.model.crowler.stackoverflow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestStackOverflow {

	//--> public static String baseURL = "http://stackoverflow.com/questions/tagged/android?sort=frequent&pageSize=15&page=%d";
	// public static String baseURL = "http://stackoverflow.com/search?tab=relevance&page=%d&pagesize=15&q=[android] views:70000..";
	public static String baseURL = "http://stackoverflow.com/search?page=%d&tab=relevance&pagesize=50&q=[android]views:237000..";
	public static final int totalPaginas = 3;


	public static void main(String[] args) {

		try {
			ArrayList<Question> perguntas;

			String finalURL;
			Question q;
			for(int i=1; i<=totalPaginas; i++){
				finalURL = String.format(baseURL,i);
				perguntas = new ArrayList<Question>();
				
				//--> Document document =  Jsoup.connect(finalURL).get();//.parse(getContentFromURL(finalURL));
				Document document =  Jsoup.connect(finalURL).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();//.parse(getContentFromURL(finalURL));
				
				//--> for(Element el : document.getElementById("questions").getElementsByClass("summary")){
				
				//System.out.println(document.select(".question-hyperlink").get(0).val());
				
				for(Element el : document.getElementsByClass("question-summary")){
					q = new Question();
					Element a = el.getElementsByTag("a").get(0);
					
					String url = "http://stackoverflow.com/" + a.attr("href");
					String pergunta = a.text();
					q.titulo = pergunta;
					q.url = url;
					
					perguntas.add(q);
					
					
				}
				
				int k =1;
				System.out.println("total de perguntas = " + perguntas.size());
				for(Question quest : perguntas){
					//printQuestion(quest);
					getAnswersOfQuestion(quest);
					
					printDoc(quest, k, i);
					k++;
				}
				

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}


	private static void printDoc(Question quest, int index, int pag) throws IOException {

		File doc = new File("documents/stackoverflow/question_pag_"+pag+"_"+index+".txt");
		System.out.println(doc.getAbsolutePath());

		if(!doc.exists()){
			doc.createNewFile();
		}

		FileWriter fw = new FileWriter(doc.getAbsoluteFile());

		//System.out.println(fw.toString());
		BufferedWriter bw = new BufferedWriter(fw);

		//bw.write("URL do Documento: "+ documento.url);
		bw.write("\t#question\n");
		bw.write(quest.totalVotos+"\n");
		bw.write(quest.url+"\n");
		
		bw.write(quest.titulo+"\n");
		
		bw.write(quest.texto+"\n");
		
		/*bw.write("\t#answers\n");
		
		ArrayList<Answer> answers = quest.respostas;
		Answer a;
		for(int i=0; i<answers.size(); i++){
			a = answers.get(i);

			bw.write(a.totalVotos +"\n");
			bw.write(a.texto+"\n");
		}
*/
		bw.close();

	}


	private static void getAnswersOfQuestion(Question quest) throws MalformedURLException, IOException {
		Document d = Jsoup.parse(getContentFromURL(quest.url));
		Element question = d.getElementById("question");
		
		Elements trs = question.getElementsByTag("tr");
		
		//content
		Element tr0 = trs.get(0);
		
		quest.totalVotos = Integer.parseInt((tr0.getElementsByClass("vote-count-post").get(0).text()));
		
		Elements ps = tr0.getElementsByClass("post-text");
		
		quest.texto = ps.get(0).text();
		
		
		//coments
		Element tr1 = trs.get(1);
		
		
		
		//answers
		Element answers = d.getElementById("answers");
		
		int totalAnswaer = Integer.parseInt(answers.getElementById("answers-header").getElementsByTag("span").get(0).text());
		
		//System.out.println(answers.getElementsByClass("answer"));
		
		Answer resp;
		
		for(Element answer : answers.getElementsByClass("answer")){
			resp = new Answer();
			resp.totalVotos = Integer.parseInt(answer.getElementsByClass("vote-count-post").get(0).text());
			resp.texto = answer.getElementsByClass("post-text").get(0).text();

			quest.addAnswer(resp);
		}
		
		
	}


	private static void printQuestion(Question quest) {
		System.out.println("URL - "+quest.url); 
		System.out.println("pergunta - "+quest.texto);
		System.out.println("");
		
	}


	private static String getContentFromURL(String finalURL) throws MalformedURLException, IOException {
		URL u;
		u = new URL(finalURL);

		URLConnection con = u.openConnection();
		con.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:40.0) Gecko/20100101 Firefox/40.0");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));

		StringBuilder input = new StringBuilder("");

		String inputLine;
		while ((inputLine = in.readLine()) != null){
			input.append(inputLine);
		}

		in.close();


		return input.toString();
	}

}
