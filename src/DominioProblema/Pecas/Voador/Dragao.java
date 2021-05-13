package DominioProblema.Pecas.Voador;

import DominioProblema.Jogador;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public class Dragao extends Voador {

    public Dragao(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.DRAGAO, posicao, 1, 2, 8,-1);
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        Quadrado quadAlvo = tabuleiro.getQuadrado(posAlvo);
        if (quadAlvo.getTipoTerreno() == 1) {
            return true;
        }
        return false;
    }
}
