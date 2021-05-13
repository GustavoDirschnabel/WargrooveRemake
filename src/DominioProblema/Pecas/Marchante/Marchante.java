package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Pecas.TipoPeca;
import DominioProblema.Pecas.Unidade;

public abstract class Marchante extends Unidade {
    public Marchante(boolean exausto, Jogador dono, TipoPeca tipoPeca, int posicao, int alcanceDeAtaque, float fatorCritico,
                     boolean podeCapturar, int movimento, int alcanceEspecial) {
        super(exausto, dono, tipoPeca, posicao, alcanceDeAtaque, fatorCritico, podeCapturar, movimento,alcanceEspecial);
    }

    @Override
    public int obterCustoMovimento(int tipoTerreno) {
        if (tipoTerreno == 0 || tipoTerreno == 1 || tipoTerreno == 5) {
            return 1;
        } else if (tipoTerreno == 2 || tipoTerreno == 3) {
            return 2;
        } else if (tipoTerreno == 4) {
            return 3;
        } else {
            return this.movimento + 1;
        }
    }
}
