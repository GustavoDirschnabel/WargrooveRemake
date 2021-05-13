package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Pecas.Peca;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

import java.util.ArrayList;

public class Soldado extends Marchante {


    public Soldado(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.SOLDADO, posicao, 1, 1.5f,
                true,4,-1);
    }


    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        ArrayList<Quadrado> adjacentes = tabuleiro.obterQuadradosAdjacentes(pos);
        Quadrado quadOrigem = tabuleiro.getQuadrado(posIni);
        adjacentes.remove(quadOrigem);

        int iteracao = 0;
        boolean comandanteAliadoEncontrado = false;
        while (iteracao < adjacentes.size() && comandanteAliadoEncontrado == false) {
            Quadrado quadAtual = adjacentes.get(iteracao);
            Peca pecaAtual = quadAtual.getPeca();
            if (pecaAtual != null) {
                TipoPeca tipoAtual = pecaAtual.getTipo();
                if (tipoAtual.getNum() >= TipoPeca.MERCIA.getNum()) {
                    Jogador donoAtual = pecaAtual.getDono();
                    if (donoAtual == this.dono) {
                        comandanteAliadoEncontrado = true;
                    }
                }
            }
            iteracao++;
        }

        return comandanteAliadoEncontrado;
    }
}