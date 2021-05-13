package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public class Ladrao extends Marchante {

    protected int ouroRoubado;

    public Ladrao(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.LADRAO, posicao, 0, 0,
                false,  6,1);
    }

    public int getOuroRoubado() {
        return this.ouroRoubado;
    }

    /**
     *
     * @param ouroRoubado
     */
    public void setOuroRoubado(int ouroRoubado) {
        this.ouroRoubado = ouroRoubado;
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        return false;
    }

    @Override
    public String obterDescricao() {
        String descricao = "";
        descricao += "PVs Atuais: " + this.pv + "\n";
        if (this.ouroRoubado == 0) {
            descricao += "Não possui carga." + "\n";
        } else {
            descricao += "Carregado com " + this.ouroRoubado + " de ouro.\n";
        }
        return descricao;
    }

}