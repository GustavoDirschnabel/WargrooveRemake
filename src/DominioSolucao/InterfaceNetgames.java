package DominioSolucao;

import DominioProblema.Tabuleiro;
import br.ufsc.inf.leobr.cliente.Jogada;
import br.ufsc.inf.leobr.cliente.OuvidorProxy;
import br.ufsc.inf.leobr.cliente.Proxy;
import br.ufsc.inf.leobr.cliente.exception.*;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

public class InterfaceNetgames implements OuvidorProxy {
    Proxy proxy;
    InterfaceJogador interfaceJogador;

    public InterfaceNetgames(InterfaceJogador interfaceJogador) {
        this.interfaceJogador = interfaceJogador;
        proxy = Proxy.getInstance();
        proxy.addOuvinte(this);
    }

    public String conectar(String servidor, String nomeJogador) {
        try {
            proxy.conectar(servidor, nomeJogador);
        } catch (JahConectadoException e) {
            return "JahConectado";
        } catch (NaoPossivelConectarException e) {
            return "ConexaoRecusada";
        } catch (ArquivoMultiplayerException e) {
            return "ArquivoMultiplayer";
        }
        return "Sucesso";
    }

    public void desconectar() throws NaoConectadoException {
        proxy.desconectar();
    }

    public void iniciarPartida() throws NaoConectadoException {
        proxy.iniciarPartida(2);
    }

    @Override
    public void iniciarNovaPartida(Integer posicao) {
        interfaceJogador.receberInicio(posicao,proxy.obterNomeAdversario(1),proxy.obterNomeAdversario(2));
    }

    @Override
    public void finalizarPartidaComErro(String message) {
        interfaceJogador.notificarConectado(message,true);
        interfaceJogador.finalizarPartida(null,false);
    }

    @Override
    public void receberMensagem(String msg) {

    }

    @Override
    public void receberJogada(Jogada jogada) {
        ReentrantLock lock = interfaceJogador.getLockInstanciacao();
        lock.lock();
        Tabuleiro tab = interfaceJogador.getTabuleiro();
        lock.unlock();
        ThreadPoolExecutor threadsTab = interfaceJogador.getThreadsTabuleiro();
        threadsTab.execute(new Runnable() {
            @Override
            public void run() {
                tab.receberJogada((JogadaGenerica) jogada);
            }
        });
    }

    @Override
    public void tratarConexaoPerdida() {
    }

    @Override
    public void tratarPartidaNaoIniciada(String message) {
        interfaceJogador.notificarConectado(message,true);
    }

    /**
     *
     * @param jogada
     */
    public void enviarJogada(JogadaGenerica jogada)  {
        try {
            proxy.enviaJogada(jogada);
        } catch (NaoJogandoException e) {
            e.printStackTrace();
        }
    }
}
