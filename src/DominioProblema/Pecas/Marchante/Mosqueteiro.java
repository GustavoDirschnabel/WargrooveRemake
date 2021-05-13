package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public class Mosqueteiro extends Marchante {

    protected int balas = 3;

    public Mosqueteiro(boolean exausto, Jogador dono, int posicao) {
        super(exausto, dono, TipoPeca.MOSQUETEIRO, posicao, 9, 1.5f,
                true,  4,-1);
    }

    public int getBalas() {
        return this.balas;
    }

    public void setBalas(int balas) {this.balas = balas;}

    public void maximizarBalas() {
        this.balas = 3;
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        if (this.balas == 1) {
            return true;
        }
        return false;
    }

    @Override
    public String obterDescricao() {
        String descricao = "";
        descricao += "PVs Atuais: " + this.pv + "\n";
        descricao += "Munição atual: " + this.balas + "\n";
        return descricao;
    }
}
