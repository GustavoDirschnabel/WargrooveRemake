package DominioProblema;

import DominioProblema.Pecas.Peca;

import java.util.ArrayList;

public class Jogador {

	protected boolean turno;
	protected String nome;
	protected int ouro;
	protected int ordem;
	protected ArrayList<Peca> pecas;

	public Jogador (String nome, boolean turno, int ordem, int ouro) {
		this.nome = nome;
		this.turno = turno;
		this.ouro = ouro;
		this.ordem = ordem;
		this.pecas = new ArrayList<Peca>();
	}

	public int getOuro() {
		return this.ouro;
	}

	/**
	 * 
	 * @param ouro
	 */
	public void setOuro(int ouro) {
		this.ouro = ouro;
	}

	public boolean getTurno() {
		return this.turno;
	}

	/**
	 * 
	 * @param bol
	 */
	public void setTurno(boolean bol) {
		this.turno = bol;
	}

	public ArrayList<Peca> getPecas() {
		return this.pecas;
	}

	/**
	 * 
	 * @param peca
	 */
	public void addPeca(Peca peca) {
		this.pecas.add(peca);
	}

	public int getOrdem() {
		return ordem;
	}

	public String getNome() {
		return nome;
	}
}