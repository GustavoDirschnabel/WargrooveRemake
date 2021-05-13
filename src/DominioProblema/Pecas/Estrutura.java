package DominioProblema.Pecas;

import DominioProblema.Jogador;

public class Estrutura extends Peca {

	protected int renda;

	public Estrutura(boolean exausto, Jogador dono, TipoPeca tipoPeca, int posicao, int renda) {
		super(exausto, dono, tipoPeca, posicao, 1);
		this.renda = renda;
		if (this.dono == null) {
			this.pv = 0;
		}
	}

	@Override
	public String obterDescricao() {
		String descricao = "";
		descricao += "PVs Atuais: " + this.pv + "\n";
		return descricao;
	}

	public void setPV(int pv) {
		this.pv = pv;
	}
}