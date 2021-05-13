package DominioProblema.Pecas.Voador;

import DominioProblema.Jogador;
import DominioProblema.Pecas.Peca;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

import java.util.ArrayList;

public class Bruxa extends Voador {

    public Bruxa(boolean exausto, Jogador dono, int posicao) {

        super(exausto, dono, TipoPeca.BRUXA, posicao, 1, 2f, 7,0);
    }


    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        ArrayList<Quadrado> adjacentes = tabuleiro.obterQuadradosAdjacentes(posAlvo);
        Quadrado quadDestino = tabuleiro.getQuadrado(pos);
        adjacentes.remove(quadDestino);
        Quadrado quadOrigem = tabuleiro.getQuadrado(posIni);
        adjacentes.remove(quadOrigem);

        int iteracao = 0;
        boolean bruxaEncontrada = false;
        while (iteracao < adjacentes.size() && bruxaEncontrada == false) {
            Quadrado quadAtual = adjacentes.get(iteracao);
            Peca pecaAtual = quadAtual.getPeca();
            if (pecaAtual != null) {
                TipoPeca tipoAtual = pecaAtual.getTipo();
                if (tipoAtual == TipoPeca.BRUXA) {
                    Jogador donoAtual = pecaAtual.getDono();
                    if (donoAtual != this.dono) {
                        bruxaEncontrada = true;
                    }
                }
            }
            iteracao++;
        }

        return !bruxaEncontrada;
    }
}
