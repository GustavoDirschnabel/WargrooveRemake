package DominioProblema.Pecas.Marchante;

import DominioProblema.Jogador;
import DominioProblema.Quadrado;
import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;

public class Valder extends Comandante {

    public Valder(Jogador dono, int posicao) {
        super(false, dono, TipoPeca.VALDER, posicao, 20, 1,0);
    }

    /**
     *
     * @param pos
     * @param tabuleiro
     */
    public void groove(int pos, Tabuleiro tabuleiro) {
        Soldado soldado = new Soldado(false,this.dono,pos);
        Quadrado quad = tabuleiro.getQuadrado(pos);
        quad.posicionarPeca(soldado);
        Jogador jogadorLocal = tabuleiro.getJogadorLocal();
        int ordem = 0;
        if (this.dono == jogadorLocal) {
            jogadorLocal.addPeca(soldado);
            ordem = jogadorLocal.getOrdem();
        } else {
            Jogador jogadorRemoto = tabuleiro.getJogadorRemoto();
            jogadorRemoto.addPeca(soldado);
            ordem = jogadorRemoto.getOrdem();
        }
        tabuleiro.notificarPosicionamentoPeca(pos,TipoPeca.SOLDADO,ordem);
    }

    @Override
    public boolean avaliarCritico(int posIni, int pos, int posAlvo, Tabuleiro tabuleiro) {
        return false;
    }
}
