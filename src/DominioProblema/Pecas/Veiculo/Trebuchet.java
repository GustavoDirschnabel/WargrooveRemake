package DominioProblema.Pecas.Veiculo;

import DominioProblema.Jogador;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public class Trebuchet extends Veiculo {


    public Trebuchet(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.TREBUCHET, posicao, 5, 1.5f,6,-1);
    }


    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        int deltaX = Math.abs(Math.floorDiv(pos,16) - Math.floorDiv(posAlvo,16));
        int deltaY = Math.abs(pos % 16 - posAlvo % 16);
        if (deltaX + deltaY == 5) {
            return true;
        }
        return false;
    }
}
