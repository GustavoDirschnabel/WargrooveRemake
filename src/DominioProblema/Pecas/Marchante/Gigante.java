package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public class Gigante extends Marchante {

    public Gigante(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.GIGANTE, posicao, 1, 2.5f,
                false,  5,-1);
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        if (this.pv <= 40) {
            return true;
        }
        return false;
    }
}