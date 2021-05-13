package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Pecas.Peca;
import DominioProblema.Pecas.Unidade;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

import java.util.ArrayList;

public class Sigrid extends Comandante {

    public Sigrid(Jogador dono, int posicao) {
        super(false, dono, TipoPeca.SIGRID, posicao,7,1,0);
    }

    /**
     *
     * @param pos
     * @param tabuleiro
     */
    public void groove(int pos, Tabuleiro tabuleiro) {
        Quadrado quadradoAlvo = tabuleiro.getQuadrado(pos);
        Unidade alvo = quadradoAlvo.removerUnidade();
        Jogador jogadorLocal = tabuleiro.getJogadorLocal();
        ArrayList<Peca> listaPecas;
        if (this.dono != jogadorLocal) {
            listaPecas = jogadorLocal.getPecas();
        } else {
            Jogador jogadorRemoto = tabuleiro.getJogadorRemoto();
            listaPecas = jogadorRemoto.getPecas();
        }

        listaPecas.remove(alvo);
        int pvAlvo = alvo.getPV();
        this.ajustePV(pvAlvo);
        tabuleiro.notificarRemocaoPeca(pos);
        tabuleiro.notificarAjustePV(this.posicao,this.pv);
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        return false;
    }
}
