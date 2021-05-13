package DominioProblema.Pecas.Veiculo;

import DominioProblema.Jogador;
import DominioProblema.Pecas.TipoPeca;
import DominioProblema.Pecas.Unidade;

public abstract class Veiculo extends Unidade {

    public Veiculo(boolean exausto, Jogador dono, TipoPeca tipoPeca, int posicao, int alcanceDeAtaque,
                   float fatorCritico, int movimento, int alcanceEspecial) {
        super(exausto, dono, tipoPeca, posicao, alcanceDeAtaque, fatorCritico,false,
                movimento,alcanceEspecial);
    }

    @Override
    public int obterCustoMovimento(int tipoTerreno) {
        if (tipoTerreno == 1) {
            return 1;
        } else if (tipoTerreno == 0) {
            return 2;
        } else {
            return this.movimento + 1;
        }
    }

}
