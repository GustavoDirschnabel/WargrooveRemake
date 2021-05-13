package DominioProblema.Pecas;

import DominioProblema.Jogador;

public abstract class Peca {

	protected int alcanceDeAtaque;
	protected Jogador dono;
	protected boolean exausto;
	protected int pv;
	protected int posicao;
	protected TipoPeca tipo;



	public Peca(boolean exausto, Jogador dono, TipoPeca tipoPeca, int posicao, int alcanceDeAtaque) {
		this.exausto = exausto;
		this.dono = dono;
		this.tipo = tipoPeca;
		this.posicao = posicao;
		this.alcanceDeAtaque = alcanceDeAtaque;
		this.pv = 100;

	}

	public TipoPeca getTipo() {
		return this.tipo;
	}

	public int ajustePV(int variacaoPV) {
		int novoPV = this.pv + variacaoPV;
		if (novoPV > 100) {
			novoPV = 100;
		}
		this.pv = novoPV;
		return this.pv;
	}

	public int getPV() { return this.pv; }

	public void setDono(Jogador jogador) {
		this.dono = jogador;
	}

	public void setExausto(boolean exausto) {
		this.exausto = exausto;
	}

	public Jogador getDono() {
		return this.dono;
	}


	public int getPosicao() {
		return this.posicao;
	}

	public void setPosicao(int posicao) {this.posicao = posicao;}

	public boolean getExausto() {
		return this.exausto;
	}

	public int getAlcanceDeAtaque() {
		return alcanceDeAtaque;
	}

	public abstract String obterDescricao();

}