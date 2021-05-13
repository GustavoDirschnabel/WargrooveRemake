package DominioSolucao;

import DominioProblema.Pecas.TipoPeca;
import br.ufsc.inf.leobr.cliente.Jogada;

public class JogadaGenerica implements Jogada {

	protected int posIni;
	protected int pos;
	protected int posAlvo;
	protected String tipoJogada;
	protected TipoPeca tipoUnidade;
	protected int danoAtacante;
	protected int danoAlvo;

	/**
	 *
	 * @param posIni
	 * @param pos
	 * @param posAlvo
	 * @param tipoJogada
	 * @param tipoUnidade
	 */
	public JogadaGenerica(int posIni, int pos, int posAlvo, String tipoJogada, TipoPeca tipoUnidade, int danoAtacante, int danoAlvo) {
		this.posIni = posIni;
		this.pos = pos;
		this.posAlvo = posAlvo;
		this.tipoJogada = tipoJogada;
		this.tipoUnidade = tipoUnidade;
		this.danoAtacante = danoAtacante;
		this.danoAlvo = danoAlvo;
	}

	public String getTipoJogada() {
		return this.tipoJogada;
	}

	public int getPosIni() {
		return this.posIni;
	}

	public int getPos() {
		return this.pos;
	}

	public int getPosAlvo() {
		return this.posAlvo;
	}

	public TipoPeca getTipoUnidade() {
		return this.tipoUnidade;
	}

	public int getDanoAtacante() {return this.danoAtacante; }

	public int getDanoAlvo() {return this.danoAlvo; }
}