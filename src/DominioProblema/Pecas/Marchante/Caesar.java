package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Pecas.Peca;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

import java.util.ArrayList;

public class Caesar extends Comandante {

    public Caesar(Jogador dono, int posicao) {
        super(false, dono, TipoPeca.CAESAR, posicao,7,0,1);
    }

    /**
     *
     * @param pos
     * @param tabuleiro
     */
    public void groove(int pos, Tabuleiro tabuleiro) {
        ArrayList<Quadrado> quadrados = tabuleiro.obterQuadradosAdjacentes(pos);
        for (int i = 0; i < 4; i++) {
            Quadrado quadAtual = quadrados.get(i);
            Peca peca = quadAtual.getPeca();
            if (peca != null) {
                Jogador donoPeca = peca.getDono();
                if (this.dono == donoPeca) {
                    TipoPeca tipoPeca = peca.getTipo();
                    if (tipoPeca.getNum() >= TipoPeca.SOLDADO.getNum()) {
                        peca.setExausto(false);
                        tabuleiro.notificarExaustao(quadAtual, false);
                    }
                }
            }
        }

    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        return false;
    }
}
