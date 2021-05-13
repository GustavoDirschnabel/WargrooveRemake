package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Pecas.Peca;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

import java.util.ArrayList;

public class Cao extends Marchante {

    public Cao(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.CAO, posicao, 1, 1.5f,
                false,  5,-1);
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        ArrayList<Quadrado> adjacentes = tabuleiro.obterQuadradosAdjacentes(posAlvo);
        Quadrado quadDestino = tabuleiro.getQuadrado(pos);
        adjacentes.remove(quadDestino);
        Quadrado quadOrigem = tabuleiro.getQuadrado(posIni);
        adjacentes.remove(quadOrigem);

        int iteracao = 0;
        boolean caoEncontrado = false;
        while (iteracao < adjacentes.size() && caoEncontrado == false) {
            Quadrado quadAtual = adjacentes.get(iteracao);
            Peca pecaAtual = quadAtual.getPeca();
            if (pecaAtual != null) {
                TipoPeca tipoAtual = pecaAtual.getTipo();
                if (tipoAtual == TipoPeca.CAO) {
                    Jogador donoAtual = pecaAtual.getDono();
                    if (donoAtual == this.dono) {
                        caoEncontrado = true;
                    }
                }
            }
            iteracao++;
        }

        return caoEncontrado;
    }

}