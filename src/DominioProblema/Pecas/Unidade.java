package DominioProblema.Pecas;

import DominioProblema.Jogador;
import DominioProblema.Tabuleiro;

public abstract class Unidade extends Peca {

	protected int movimento;
	protected float fatorCritico;
	protected boolean podeCapturar;
	protected int alcanceEspecial;

	public Unidade(boolean exausto, Jogador dono, TipoPeca tipoPeca, int posicao,int alcanceDeAtaque, float fatorCritico,
				   boolean podeCapturar, int movimento, int alcanceEspecial) {
		super(exausto,dono,tipoPeca,posicao,alcanceDeAtaque);
		this.fatorCritico = fatorCritico;
		this.podeCapturar = podeCapturar;
		this.movimento = movimento;
		this.alcanceEspecial = alcanceEspecial;
	}

	public int getAlcanceMovimento() {
		return this.movimento;
	}

	public int getAlcanceAtaque() {
		return alcanceDeAtaque;
	}

	public float getFatorCritico() {return this.fatorCritico;}


	public void setAlcanceMovimento(int alcance) {
		this.movimento = alcance;
	}

	public abstract int obterCustoMovimento(int tipoTerreno);

	public abstract boolean avaliarCritico(int posIni,int pos, int posAlvo, Tabuleiro tabuleiro);

    public int getAlcanceEspecial(){return this.alcanceEspecial;}

    public boolean podeCapturar() {
    	return this.podeCapturar;
	}

	@Override
	public String obterDescricao() {
		String descricao = "";
		descricao += "PVs Atuais: " + this.pv + "\n";
		return descricao;
	}
}