package DominioProblema.Pecas.Voador;

import DominioProblema.Jogador;
import DominioProblema.Pecas.TipoPeca;
import DominioProblema.Pecas.Unidade;

public abstract class Voador extends Unidade {

    public Voador(boolean exausto, Jogador dono, TipoPeca tipoPeca, int posicao, int alcanceDeAtaque,
                  float fatorCritico, int movimento, int alcanceEspecial) {

        super(exausto, dono, tipoPeca, posicao, alcanceDeAtaque, fatorCritico, false,
                movimento,alcanceEspecial);
    }

    @Override
    public int obterCustoMovimento(int tipoTerreno) {
        return 1;
    }
}
