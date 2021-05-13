package DominioSolucao;

import DominioProblema.Tabuleiro;
import DominioProblema.Pecas.TipoPeca;
import br.ufsc.inf.leobr.cliente.exception.NaoConectadoException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class InterfaceJogador extends Application {

    protected Stage stage;
    protected InterfaceNetgames interfaceNetgames;
    protected Tabuleiro tabuleiro;
    protected ThreadPoolExecutor threadsTabuleiro;
    protected ReentrantLock lockInstanciacao;
    protected ReentrantLock lockEstado;
    protected ControladorMenuPrincipal controladorMenu;
    protected ControladorCenaBatalha controladorCenaBatalha;
    protected ControladorTelaRecrutamento controladorTelaRecrutamento;
    protected ArrayList<StackPane> panes;
    protected TipoPeca comandanteAliado;
    protected String estadoPartida;
    protected ConcurrentLinkedQueue<Integer> posicoesRealcadasClique, posicoesRealcadasPassadaMouse;

    @Override public void start(Stage stage) {
        this.stage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        Parent root;
        Scene cenaMenu;
        try {
            FXMLLoader fxmlLoaderMenu = new FXMLLoader(getClass().getResource("MenuPrincipal.fxml"));
            root = fxmlLoaderMenu.load();
            controladorMenu = fxmlLoaderMenu.getController();
            controladorMenu.setInterfaceJogador(this);
            cenaMenu = new Scene(root);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        interfaceNetgames = new InterfaceNetgames(this);
        stage.setScene(cenaMenu);
        stage.setResizable(false);
        stage.setTitle("Wargroove");
        stage.show();

        lockInstanciacao = new ReentrantLock();
        lockEstado = new ReentrantLock();
        estadoPartida = null;
        posicoesRealcadasClique = new ConcurrentLinkedQueue<>();
        posicoesRealcadasPassadaMouse = new ConcurrentLinkedQueue<>();
    }

    public void trocarCena (String nomeCena) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(nomeCena));
        Scene cena;
        try {
            Parent root = fxmlLoader.load();
            cena = new Scene(root);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (nomeCena.contains("MenuPrincipal.fxml")) {
            controladorMenu = (ControladorMenuPrincipal) fxmlLoader.getController();
            controladorMenu.setInterfaceJogador(this);
        } else {
            controladorCenaBatalha = (ControladorCenaBatalha) fxmlLoader.getController();
            controladorCenaBatalha.setInterfaceJogador(this);

            ObservableList panesCena = controladorCenaBatalha.getMatrizBatalha().getChildren();
            StackPane[] panesArray = new StackPane[272];
            for (int i = 0; i < 272; i++) {
                StackPane pane = (StackPane) panesCena.get(i);
                int x = GridPane.getRowIndex(pane);
                int y = GridPane.getColumnIndex(pane);
               panesArray[x*16 + y] = pane;
            }
            panes = new ArrayList<StackPane>(Arrays.asList(panesArray));

        }
        stage.setScene(cena);
    }

    public void conectar(String servidor, String nomeJogador) {
        String resultado = interfaceNetgames.conectar(servidor,nomeJogador);
        String mensagem = "A conexão foi recusada, verifique o IP do servidor e tente novamente";
        if (resultado.equals("Sucesso")) {
            controladorMenu.habilitarBotaoIniciar(true);
            controladorMenu.habilitarBotaoSair(false);
            controladorMenu.alterarBotaoConexao();
            mensagem = "Conectado com Sucesso";
        }
        notificarConectado(mensagem, false);
    }

    public void desconectar() {
        try {
            interfaceNetgames.desconectar();
        } catch (NaoConectadoException e) {
            e.printStackTrace();
        }

        controladorMenu.alterarBotaoConexao();
        controladorMenu.habilitarBotaoIniciar(false);
        controladorMenu.habilitarBotaoSair(true);
        this.notificarConectado("Você foi Desconectado", false);
    }

    public void iniciarPartida() {
        try {
            interfaceNetgames.iniciarPartida();

        } catch (NaoConectadoException e) {
            Alert iniciarPartida = new Alert(Alert.AlertType.ERROR);
            iniciarPartida.initOwner(stage);
            iniciarPartida.setHeaderText("Você não está conectado, isso não devia acontecer e algo deu muito errado");
            iniciarPartida.showAndWait();
        }
    }

    public void receberInicio(int ordem,String nome1,String nome2){
        InterfaceJogador interfaceJogador = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lockInstanciacao.lock();
                trocarCena("CenaBatalha.fxml");
                interfaceJogador.setTabuleiro(new Tabuleiro(ordem,nome1,nome2,interfaceJogador,interfaceNetgames));
                threadsTabuleiro = new ThreadPoolExecutor(1,1,1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
                lockInstanciacao.unlock();
                tabuleiro.enviarComandante(comandanteAliado,ordem);
                Alert recebido = new Alert(Alert.AlertType.INFORMATION);
                recebido.initOwner(stage);
                recebido.setHeaderText("Partida iniciada, você é o " + ordem + "º Jogador");
                recebido.showAndWait();
            }
        });
    }

    public void atualizarFaccao(String facao) {
        TipoPeca tipoComandante;
        if (facao.equals("Mercia")) {
            tipoComandante = TipoPeca.MERCIA;
        } else if (facao.equals("Sigrid")) {
            tipoComandante = TipoPeca.SIGRID;
        } else if (facao.equals("Caesar")) {
            tipoComandante = TipoPeca.CAESAR;
        } else {
            tipoComandante = TipoPeca.VALDER;
        }
        this.comandanteAliado = tipoComandante;
    }

    /**
     *
     * @param pos
     * @param pv
     */
    public void atualizarPV(int pos, int pv) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList nodes = panes.get(pos).getChildren();
                ImageView imagemAntiga = (ImageView) nodes.get(2);

                if(pv < 100) {
                    String endereco =  "/images/vida";

                    if(pv > 89) {
                        endereco += 9 + ".png";
                    } else if (pv > 79) {
                        endereco += 8 + ".png";
                    } else if (pv > 69) {
                        endereco += 7 + ".png";
                    } else if (pv > 59) {
                        endereco += 6 + ".png";
                    } else if (pv > 49) {
                        endereco += 5 + ".png";
                    } else if (pv > 39) {
                        endereco += 4 + ".png";
                    } else if (pv > 29) {
                        endereco += 3 + ".png";
                    } else if (pv > 19) {
                        endereco += 2 + ".png";
                    } else {
                        endereco += 1 + ".png";
                    }

                    imagemAntiga.setImage(new Image(endereco));
                } else {
                    imagemAntiga.setImage(null);
                }
            }
        });
    }

    /**
     *
     * @param pos
     */
    public void removerPeca(int pos) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList nodes = panes.get(pos).getChildren();
                ImageView imagemAntiga = (ImageView) nodes.get(1);
                imagemAntiga.setImage(null);
                ImageView pvAntigo = (ImageView) nodes.get(2);
                pvAntigo.setImage(null);
            }
        });
    }

    /**
     *
     * @param pos
     * @param tipo
     * @param PVPeca
     * @param ordem
     */
    public void posicionarPeca(int pos,TipoPeca tipo, int PVPeca, int ordem) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String endereco = tipo.getImagem();
                endereco += ordem + ".png";
                ObservableList nodes = panes.get(pos).getChildren();
                ImageView imagemAntiga = (ImageView) nodes.get(1);
                imagemAntiga.setImage(new Image(endereco));
                imagemAntiga = (ImageView) nodes.get(2);

                if(PVPeca < 100 && PVPeca > 0) {
                    endereco = "/images/vida";

                    if(PVPeca > 89) {
                        endereco += 9 + ".png";
                    } else if (PVPeca > 79) {
                        endereco += 8 + ".png";
                    } else if (PVPeca > 69) {
                        endereco += 7 + ".png";
                    } else if (PVPeca > 59) {
                        endereco += 6 + ".png";
                    } else if (PVPeca > 49) {
                        endereco += 5 + ".png";
                    } else if (PVPeca > 39) {
                        endereco += 4 + ".png";
                    } else if (PVPeca > 29) {
                        endereco += 3 + ".png";
                    } else if (PVPeca > 19) {
                        endereco += 2 + ".png";
                    } else {
                        endereco += 1 + ".png";
                    }

                    imagemAntiga.setImage(new Image(endereco));
                } else {
                    imagemAntiga.setImage(null);
                }
            }
        });
    }

    public void finalizarPartida(String nomeVencedor, boolean temVencedor) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                threadsTabuleiro.shutdown();
                tabuleiro.getSemaforoPosicaoEnviada().release();

                if (temVencedor) {
                    Alert popUpVencedor = new Alert(Alert.AlertType.INFORMATION);
                    popUpVencedor.initOwner(stage);
                    popUpVencedor.setTitle("FIM DE JOGO!");
                    popUpVencedor.setHeaderText(nomeVencedor + " é o Vencedor!");
                    popUpVencedor.showAndWait();
                }

                trocarCena("MenuPrincipal.fxml");
            }
        });
    }

    /**
     *
     * @param ouro
     */
    public void atualizarOuro(String ouro) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controladorCenaBatalha.getLabelOuro().setText("Ouro: " + ouro);
            }
        });
    }

    /**
     *
     * @param pos
     * @param exausto
     */
    public void exaustarPeca(int pos, boolean exausto) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList nodes = panes.get(pos).getChildren();
                ImageView peca = (ImageView) nodes.get(1);

                if (exausto) {
                    ColorAdjust cinza = new ColorAdjust();
                    cinza.setSaturation(-0.7);
                    cinza.setBrightness(-0.3);
                    peca.setEffect(cinza);
                } else {
                    peca.setEffect(null);
                }
            }
        });
    }


    /**
     *
     * @param posicoes
     * @param cor
     */
    public void realcarPosicao(Collection<Integer> posicoes, int cor, boolean ehRealceClique) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ColorAdjust realce = new ColorAdjust();
                realce.setBrightness(-0.1);
                if (cor == 0) {
                    realce.setHue(-0.5);
                } else if (cor == 1) {
                    realce.setHue(-0.8);
                    realce.setSaturation(0.5);
                    realce.setBrightness(0.1);
                } else if (cor == 2){
                    realce.setHue(0.7);
                } else {
                    realce.setSaturation(-0.7);
                    realce.setBrightness(-0.3);
                }
                if (ehRealceClique) {
                    for (Integer pos : posicoes) {
                        ObservableList nodes = panes.get(pos).getChildren();
                        ImageView terreno = (ImageView) nodes.get(0);
                        terreno.setEffect(realce);
                        if (!posicoesRealcadasClique.contains(pos)) {
                            posicoesRealcadasClique.offer(pos);
                        }

                    }
                } else {
                    for (Integer pos : posicoes) {
                        ObservableList nodes = panes.get(pos).getChildren();
                        ImageView terreno = (ImageView) nodes.get(0);
                        terreno.setEffect(realce);
                        posicoesRealcadasPassadaMouse.offer(pos);
                    }
                }
            }
        });

    }

    /**
     *
     * @param texto
     */
    public void escreverAreaTextoLateral(String texto) {
        controladorCenaBatalha.escreverAreaTextoLateral(texto);
    }

    /**
     *
     * @param fonte
     */
    public void tratarPassadaMouse(StackPane fonte) {
        int pos = panes.indexOf(fonte);
        lockEstado.lock();
        String estado = this.estadoPartida;
        lockEstado.unlock();
        String descricao = "";
        if (this.posicoesRealcadasPassadaMouse.size() > 0) {
            desrealcar(false);
        }
        if (estado == "Atacar") {
            this.escreverAreaTextoLateral(descricao);
            if (this.posicoesRealcadasClique.contains(pos)) {
                int[] dano = tabuleiro.previaDano(pos);
                if (dano[0] > 0) {
                    if (dano[4] == 1) {
                        descricao += "CRÍTICO no Ataque\n";
                    }
                    descricao += "Dano mínimo ao Alvo: " + dano[0] + "\n";
                    descricao += "Dano máximo ao Alvo: " + dano[1] + "\n\n";
                    if (dano[5] == 1) {
                        descricao += "CRÍTICO na Defesa\n";
                    }
                    descricao += "Dano mínimo à sua Peça: " + dano[2] + "\n";
                    descricao += "Dano máximo à sua Peça: " + dano[3] + "\n";
                    this.escreverAreaTextoLateral(descricao);
                }
            }
        } else if (estado == "Groove") {
            if (this.posicoesRealcadasClique.contains(pos)) {
                tabuleiro.previaAreaDeEfeito(pos,4);
            }
        } else if (estado == "AcaoEspecial") {
            if (this.posicoesRealcadasClique.contains(pos)) {
                tabuleiro.previaAreaDeEfeito(pos, 6);
            }
        }

    }

    public void rendicao() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.initOwner(stage);
        confirmacao.setHeaderText("Você tem certeza que deseja se render?");
        confirmacao.showAndWait();

        if (confirmacao.getResult() == ButtonType.OK) {
            tabuleiro.rendicao();
        }
    }

    /**
     *
     * @param notificacao
     */
    public void notificarConectado(String notificacao, boolean erro) {
        Alert alertaConexao;
        if (!erro) {
            alertaConexao = new Alert(Alert.AlertType.INFORMATION);
        } else {
            alertaConexao = new Alert(Alert.AlertType.ERROR);
        }
        alertaConexao.initOwner(stage);
        alertaConexao.setHeaderText(notificacao);
        alertaConexao.showAndWait();
    }



    public void passarTurno() {
        threadsTabuleiro.execute(new Runnable() {
            @Override
            public void run() {
                tabuleiro.passarTurno();
            }
        });
    }

    /**
     *
     * @param bol
     */
    public void habilitarBotaoTurno(boolean bol) {
        controladorCenaBatalha.habilitarBotaoTurno(bol);
    }

    /**
     *
     * @param nomeJogador
     */
    public void atualizarTurno(String nomeJogador) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controladorCenaBatalha.getLabelTurno().setText("Turno: " + nomeJogador);
            }
        });
    }

    /**
     *
     * @param tipoTela
     * @param unidadesCompraveis
     */
    public void abrirTelaRecrutamento(String tipoTela,int ouro ,int unidadesCompraveis) {
        InterfaceJogador interfaceJogador = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String endereco = tipoTela + ".fxml";
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(endereco));
                Stage janelaRecrutamento = new Stage();
                janelaRecrutamento.initStyle(StageStyle.UNDECORATED);
                Parent root;
                try {
                    root = fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                controladorTelaRecrutamento = fxmlLoader.getController();
                controladorTelaRecrutamento.inicializarTela(ouro,unidadesCompraveis);
                controladorTelaRecrutamento.setInterfaceJogador(interfaceJogador);
                janelaRecrutamento.initOwner(stage);
                janelaRecrutamento.initModality(Modality.WINDOW_MODAL);
                Scene cenaRecrutamento = new Scene(root);
                janelaRecrutamento.setScene(cenaRecrutamento);
                janelaRecrutamento.setResizable(false);
                janelaRecrutamento.show();
            }
        });

    }

    /**
     *
     * @param unidade
     */
    public void unidadeEscolhida(String unidade) {
        controladorTelaRecrutamento.fecharTelaRecrutamento();
        tabuleiro.enviarTipoPeca(unidade);
    }

    /**
     *
     * @param descricao
     */
    public void abrirTelaInspecao(String descricao) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage inspecao = new Stage();
                inspecao.setResizable(false);
                inspecao.setTitle("Inspeção de terreno");
                inspecao.initOwner(stage);
                inspecao.initModality(Modality.APPLICATION_MODAL);
                BorderPane borderPane = new BorderPane();
                borderPane.setPrefSize(250,300);
                TextArea textArea = new TextArea(descricao);
                textArea.setEditable(false);
                textArea.setFocusTraversable(false);
                textArea.setWrapText(true);
                textArea.setFont(new Font(14));
                borderPane.setCenter(textArea);
                Scene cenaInspecao = new Scene(borderPane);
                inspecao.setScene(cenaInspecao);
                inspecao.show();
            }
        });

    }

    /**
     *
     * @param opcoes
     */
    public void mostrarOpcoes(ArrayList<String> opcoes, int pos) {
        InterfaceJogador interfaceJogador = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                StackPane node = panes.get(pos);
                Bounds bounds = node.localToScreen(node.getBoundsInLocal());
                ButtonPopUp popUp = new ButtonPopUp(interfaceJogador,stage,opcoes, bounds);
            }
        });
    }

    /**
     *
     * @param opcao
     */
    public void escolherOpcaoAgir(String opcao) {
        threadsTabuleiro.execute(new Runnable() {
            @Override
            public void run() {
                tabuleiro.escolherOpcaoAgir(opcao);
            }
        });
    }


    public void tratarClique(StackPane fonte, boolean ehM1) {
        this.lockEstado.lock();
        this.escreverAreaTextoLateral("");
        if (this.estadoPartida == null) {
            this.desrealcar(true);
            int pos = this.getPanes().indexOf(fonte);
            if (ehM1) {
                threadsTabuleiro.execute(new Runnable() {
                    @Override
                    public void run() {
                        tabuleiro.selecionarQuadrado(pos);
                    }
                });
            } else {
                threadsTabuleiro.execute(new Runnable() {
                    @Override
                    public void run() {
                        tabuleiro.inspecionarTerreno(pos);
                    }
                });
            }
        } else {
            int pos = this.getPanes().indexOf(fonte);
            if (!ehM1) {
                if (!this.posicoesRealcadasClique.contains(0)) {
                    pos = 0;
                } else {
                    pos = 271;
                }
            }
            this.desrealcar(true);
            tabuleiro.enviarPosicao(pos);
            this.estadoPartida = null;
        }

        this.lockEstado.unlock();
    }

    public void estabelecerFaccao(String facao) {
        String fileName = facao + ".txt";
        Scanner scan = new Scanner(getClass().getResourceAsStream("DescricoesPecas/" + fileName),"utf-8");
        String texto = "";
        while (scan.hasNextLine()){
            texto += scan.nextLine() + "\n";
        }
        this.atualizarFaccao(facao);
        controladorMenu.alterarTextoMenuPrincipal(texto);
        controladorMenu.habilitarBotaoConexao();
    }


    public ReentrantLock getLockInstanciacao() {return this.lockInstanciacao;}

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public void setTabuleiro(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    public ThreadPoolExecutor getThreadsTabuleiro() {
        return this.threadsTabuleiro;
    }

    public ArrayList<StackPane> getPanes() {
        return panes;
    }

    public void setEstadoPartida(String estado) {
        this.lockEstado.lock();
        this.estadoPartida = estado;
        this.lockEstado.unlock();
    }

    public void desrealcar (boolean ehRealceClique) {
        ConcurrentLinkedQueue<Integer> lista;
        if (ehRealceClique) {
            lista = this.posicoesRealcadasClique;

            while (!lista.isEmpty()) {
                int posicao = lista.poll();
                ObservableList nodes = panes.get(posicao).getChildren();
                ImageView terreno = (ImageView) nodes.get(0);
                terreno.setEffect(null);
            }

            lista = this.posicoesRealcadasPassadaMouse;
            while (!lista.isEmpty()) {
                int posicao = lista.poll();
                ObservableList nodes = panes.get(posicao).getChildren();
                ImageView terreno = (ImageView) nodes.get(0);
                terreno.setEffect(null);
            }

        } else {
            lista = this.posicoesRealcadasPassadaMouse;
            if (this.posicoesRealcadasPassadaMouse.size() > 0) {
                    lista.removeAll(this.posicoesRealcadasClique);
                    this.realcarPosicao(this.posicoesRealcadasClique, 0, true);
            }

            while (!lista.isEmpty()) {
                int posicao = lista.poll();
                ObservableList nodes = panes.get(posicao).getChildren();
                ImageView terreno = (ImageView) nodes.get(0);
                terreno.setEffect(null);
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}