package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Pecas.Peca;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

import java.util.ArrayList;

public class Lanceiro extends Marchante {

    public Lanceiro(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.LANCEIRO, posicao, 1, 1.5f,
                true,  3,-1);
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        ArrayList<Quadrado> adjacentes = tabuleiro.obterQuadradosAdjacentes(pos);
        Quadrado quadOrigem = tabuleiro.getQuadrado(posIni);
        adjacentes.remove(quadOrigem);

        int iteracao = 0;
        boolean lanceiroEncontrado = false;
        while (iteracao < adjacentes.size() && lanceiroEncontrado == false) {
            Quadrado quadAtual = adjacentes.get(iteracao);
            Peca pecaAtual = quadAtual.getPeca();
            if (pecaAtual != null) {
                TipoPeca tipoAtual = pecaAtual.getTipo();
                if (tipoAtual == TipoPeca.LANCEIRO) {
                    Jogador donoAtual = pecaAtual.getDono();
                    if (donoAtual == this.dono) {
                        lanceiroEncontrado = true;
                    }
                }
            }
            iteracao++;
        }

        return lanceiroEncontrado;
    }
}