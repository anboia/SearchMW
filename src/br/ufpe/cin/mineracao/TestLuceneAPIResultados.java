package br.ufpe.cin.mineracao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import br.ufpe.cin.mineracao.model.business.DocumentProcessor;
import br.ufpe.cin.mineracao.model.crowler.stackoverflow.TestStackOverflowResult;

public class TestLuceneAPIResultados {

	static DocumentProcessor processor;
	static final int titleRepeatCount = 5;
	
	public static void main(String[] args) {
		String docsPath = "documents/stackoverflow/";
		String indexPath = "index";

//		processor = DocumentProcessor.getInstance();
		
		final Path docDir = Paths.get(docsPath);
//		System.out.println(docDir.getFileName());
		if(!Files.isReadable(docDir)){
			System.out.println("NAO DÁ PRA LER A PASTA");
		}
		try {
			 {
			Analyzer analyzer = null;
			
			
			
			Scanner scan = new Scanner(System.in);
			System.out.println(">Digite sua query: ");
			String inputQuery = "starting an activity"; //scan.nextLine();
			System.out.println(inputQuery);

			

			for (int config = 0; config < 4; config++) {
				Directory dir = FSDirectory.open(Paths.get(indexPath));
				

				
				switch(config){
				/** SEM STOP_WORD e SEM STEMMING **/
					case 0: analyzer = new StandardAnalyzer(new CharArraySet(new Vector(), true)); break;
				
				/** COM STOP_WORD e SEM STEMMING **/
					case 1: analyzer = new StandardAnalyzer(StandardAnalyzer.STOP_WORDS_SET); break;
				
				/** SEM STOP_WORD e COM STEMMING **/
					case 2: analyzer = new EnglishAnalyzer(new CharArraySet(new Vector(), true),new CharArraySet(new Vector(), true)); break;
				
				/** COM STOP_WORD e COM STEMMING **/
					case 3: analyzer = new EnglishAnalyzer(StandardAnalyzer.STOP_WORDS_SET,new CharArraySet(new Vector(), true)); break;
				}
				
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				
				iwc.setOpenMode(OpenMode.CREATE);
				
				IndexWriter writer = new IndexWriter(dir, iwc);
				indexDocs(writer, docDir);
				
				writer.close();

				Query q = new QueryParser("contents", analyzer).parse(inputQuery);
				System.out.println("my_query = "+q.toString());
				

				int hitsPerPage = 10;

				IndexReader reader = DirectoryReader.open(dir);
				IndexSearcher seacher = new IndexSearcher(reader);
				

				TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);

				seacher.search(q,  collector);
				
				ScoreDoc[] hits = collector.topDocs().scoreDocs;


				// 4. display results
				ArrayList<String> urlsFromQuery = new ArrayList<String>();
				for(int i=0;i<hits.length;++i) {
					int docId = hits[i].doc;
					Document d = seacher.doc(docId);
					urlsFromQuery.add(d.get("url"));
				}
				
				
				System.out.println(getConfigName(config));
				System.out.println("Total Hits: "+collector.getTotalHits());
				// inicia calculos de cobertura
				CalculoResultado.iniciarCalculos(urlsFromQuery, TestStackOverflowResult.getStackOverflowResult( inputQuery), reader.numDocs(), collector.getTotalHits());
				
				reader.close();
			}
			
			
			
		}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String getConfigName(int config) {
		// TODO Auto-generated method stub
		String configName = "";
		if( (config&1) == 1) configName+= "COM ";
		else configName +="SEM ";
		configName += "stopwords ";
		
		if( ((config>>1)&1) == 1) configName+= "COM ";
		else configName +="SEM ";
		configName += "stemming ";
		
		return configName;
	}

	static void indexDocs(final IndexWriter writer, Path path) throws IOException {
		if(Files.isDirectory(path)){
			Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					indexDoc(writer, file, attrs.lastModifiedTime().toMillis());


					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	static void indexDoc(IndexWriter writer, Path file, long lastModified) {

//		System.out.println(file.getFileName().toString());
		if (!file.getFileName().toString().contains(".txt")) return;
		
		try(InputStream stream = Files.newInputStream(file)){
			Document doc = new Document();

			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);

			LongField lastModifiedField = new LongField("modified",lastModified, Field.Store.NO);
			doc.add(lastModifiedField);

			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			
			String line;
			
			//*/
			line = br.readLine();
			sb.append(line).append("\n");
			
			line = br.readLine();
			sb.append(line).append("\n");
			
			line = br.readLine();
			sb.append(line).append("\n");
			TextField urlField = new TextField("url",line,Store.YES);
			doc.add(urlField);
			
			line = br.readLine();
			for(int i = 0; i< titleRepeatCount ; i++){
				sb.append(line).append("\n");

			}
			
			line = br.readLine();
			sb.append(line).append("\n");
			//*/
			
			
					
			/*/
			if((line = br.readLine())!=null){
				sb.append(line).append("\n");
			} else System.out.println(file.toString());
			if((line = br.readLine())!=null){
				sb.append(line).append("\n");
			} else System.out.println(file.toString());
			
			if((line = br.readLine())!=null){
				System.out.println(line);
				TextField urlField = new TextField("url",line,Store.YES);
				doc.add(urlField);
				sb.append(line).append("\n");
			} else System.out.println(file.toString());
			while((line = br.readLine())!=null){
				if(line.startsWith("Q:")){
					for(int i = 0; i< titleRepeatCount ; i++){
						sb.append(line).append("\n");

					}
				} else {
					sb.append(line).append("\n");
				}
			}
			//*/
			
		

			TextField txtField = new TextField("contents", sb.toString(), Store.YES);
			doc.add(txtField);

			writer.addDocument(doc);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
