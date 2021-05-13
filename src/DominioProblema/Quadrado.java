package DominioProblema;

import DominioProblema.Pecas.Peca;
import DominioProblema.Pecas.Unidade;

public class Quadrado {

	protected Peca peca;
	protected int defesaFornecida;
	protected int tipoTerreno;
	protected int visitado = 0;

	public Quadrado(int tipoTerreno) {
		if(tipoTerreno == 0) {
			this.defesaFornecida = 1;
		} else if (tipoTerreno == 1) {
			this.defesaFornecida = 1;
		} else if (tipoTerreno == 2) {
			this.defesaFornecida = -2;
		} else if (tipoTerreno == 3) {
			this.defesaFornecida = 3;
		} else if (tipoTerreno == 4) {
			this.defesaFornecida = 4;
		} else if (tipoTerreno == 5) {
			this.defesaFornecida = -1;
		} else {
			this.defesaFornecida = 0;
		}
		this.tipoTerreno = tipoTerreno;
	}

	public Unidade getUnidade() {
		// TODO - implement DominioProblema.Quadrado.getUnidade
		throw new UnsupportedOperationException();
	}

	public Peca getPeca() {
		return this.peca;
	}

	public int getTipoTerreno() {
		return this.tipoTerreno;
	}

	public Unidade removerUnidade() {
		Unidade retorno = (Unidade) this.peca;
		this.peca = null;
		return retorno;
	}

	/**
	 * 
	 * @param peca
	 */
	public void posicionarPeca(Peca peca) {
		this.peca = peca;
	}

	public int getDefesa() {
		return this.defesaFornecida;
	}

	/**
	 * 
	 * @param visitado
	 */
	public void setVisitado(int visitado) {
		this.visitado = visitado;
	}


	public int getVisitado() {
		return this.visitado;
	}

}