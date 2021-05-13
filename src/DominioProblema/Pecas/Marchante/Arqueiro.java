package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public class Arqueiro extends Marchante {

    public Arqueiro(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.ARQUEIRO, posicao, 3, 1.35f,
                true, 3,-1);
    }


    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        if (posIni == pos) {
            return true;
        }
        return false;
    }

}