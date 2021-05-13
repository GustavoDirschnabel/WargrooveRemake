package DominioProblema.Pecas.Voador;

import DominioProblema.Jogador;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public class Harpia extends Voador {

    public Harpia(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.HARPIA, posicao, 1, 1.25f,5,-1);
    }


    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        Quadrado quadDestino = tabuleiro.getQuadrado(pos);
        if (quadDestino.getTipoTerreno() == 4) {
            return true;
        }
        return false;
    }
}