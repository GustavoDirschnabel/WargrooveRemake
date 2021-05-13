package DominioProblema.Pecas;

import DominioProblema.Jogador;
import DominioProblema.Tabuleiro;

public class Cavaleiro extends Unidade {

    public Cavaleiro(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.CAVALEIRO, posicao, 1, 1.5f,
                false, 6, -1);
    }

    @Override
    public int obterCustoMovimento(int tipoTerreno) {
        if (tipoTerreno == 0 || tipoTerreno == 1 || tipoTerreno == 5) {
            return 1;
        } else if (tipoTerreno == 3) {
            return 3;
        } else if (tipoTerreno == 2) {
            return 4;
        } else {
            return this.movimento + 1;
        }
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        int deltaX = Math.abs(Math.floorDiv(pos,16) - Math.floorDiv(posIni,16));
        int deltaY = Math.abs(pos % 16 - posIni % 16);
        if (deltaX + deltaY == 6) {
            return true;
        }
        return false;
    }
}
