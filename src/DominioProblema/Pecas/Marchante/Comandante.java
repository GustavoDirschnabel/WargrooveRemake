package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public abstract class Comandante extends Marchante {

    protected int cargaGroove;
    protected int taxaDeCarga;
    protected int alcanceGroove, areaGroove;

    public Comandante(boolean exausto, Jogador dono, TipoPeca tipoPeca, int posicao, int taxaDeCarga, int alcanceGroove,
                      int areaGroove) {
        super(exausto, dono, tipoPeca, posicao, 1, 1, true, 4,-1);
        this.taxaDeCarga = taxaDeCarga;
        if (this.dono.getOrdem() == 1) {
            this.cargaGroove = 0;
        } else {
            this.cargaGroove = -this.taxaDeCarga;
        }
        this.alcanceGroove = alcanceGroove;
        this.areaGroove = areaGroove;
    }

    public int getAlcanceGroove() {
        return this.alcanceGroove;
    }

    public int getAreaGroove() {
        return this.areaGroove;
    }

    /**
     *
     * @param carga
     */
    public void setCargaGroove(int carga) {
        if (carga > 100) {
            this.cargaGroove = 100;
        } else {
            this.cargaGroove = carga;
        }
    }

    public int getCargaGroove() {
        return this.cargaGroove;
    }

    public int getTaxaDeCarga() {return this.taxaDeCarga; }

    /**
     *
     * @param pos
     * @param tabuleiro
     */
    public abstract void groove(int pos, Tabuleiro tabuleiro);

    @Override
    public String obterDescricao() {
        String descricao = "";
        descricao += "PVs Atuais: " + this.pv + "\n";
        descricao += "Carga atual de Groove: " + this.cargaGroove + "\n";
        return descricao;
    }

}