package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public class Mago extends Marchante {

    public Mago(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.MAGO, posicao, 1, 1.5f,
                true,  5,2);
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        Quadrado quadDestino = tabuleiro.getQuadrado(pos);
        if (quadDestino.getDefesa() >= 3) {
            return true;
        }
        return false;
    }
}