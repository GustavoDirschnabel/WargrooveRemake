package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Pecas.Peca;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

import java.util.ArrayList;

public class Mercia extends Comandante {

    public Mercia(Jogador dono, int posicao) {
        super(false, dono, TipoPeca.MERCIA, posicao,10,0,3);
    }

    /**
     *
     * @param pos
     * @param tabuleiro
     */
    public void groove(int pos, Tabuleiro tabuleiro) {
        ArrayList<Quadrado> area = tabuleiro.verificarQuadradosAlcance(pos,pos,4);
        for (int i = 0; i < area.size(); i++) {
            Quadrado quadAtual = area.get(i);
            Peca peca = quadAtual.getPeca();
            if (peca != null) {
                Jogador donoPeca = peca.getDono();
                if (donoPeca == this.getDono()) {
                    TipoPeca tipoPeca = peca.getTipo();
                    if (tipoPeca.getNum() >= TipoPeca.SOLDADO.getNum()) {
                        int pvPeca = peca.ajustePV(50);
                        int posicaoPeca = peca.getPosicao();
                        tabuleiro.notificarAjustePV(posicaoPeca,pvPeca);
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