package br.ufpe.cin.mineracao.model.crowler.stackoverflow;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TestStackOverflowResult {
	
	public static ArrayList<String> getStackOverflowResult(String query){
		ArrayList<String> result = new ArrayList<>();
		
		
		
		
		try {

			String finalURL;
			Question q;
			for(int i=1; i<=1; i++){
				finalURL = String.format(TestStackOverflow.baseURL+" "+query,i);

				//--> Document document =  Jsoup.connect(finalURL).get();//.parse(getContentFromURL(finalURL));
				Document document =  Jsoup.connect(finalURL).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();//.parse(getContentFromURL(finalURL));
				
				for(Element el : document.getElementsByClass("question-summary")){
					q = new Question();
					Element a = el.getElementsByTag("a").get(0);
					String url = "http://stackoverflow.com/" + a.attr("href");
					result.add(url);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	


		return result;
	}
		
}
