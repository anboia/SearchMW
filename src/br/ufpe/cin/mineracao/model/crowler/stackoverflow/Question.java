package br.ufpe.cin.mineracao.model.crowler.stackoverflow;

import java.util.ArrayList;

public class Question {

	public String url;

	public String texto;
	public String titulo;

	public ArrayList<Answer> respostas;

	public int totalVotos;



	public void addAnswer(Answer a){
		if(respostas == null){
			respostas = new ArrayList<Answer>();
		}
		respostas.add(a);

	}
}
