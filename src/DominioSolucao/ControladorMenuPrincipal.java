package DominioSolucao;

import DominioProblema.Tabuleiro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.Optional;


public class ControladorMenuPrincipal {

    InterfaceJogador interfaceJogador;
    ObservableList<String> comandantes = FXCollections.observableArrayList(
            "Mercia", "Sigrid", "Caesar", "Valder");

    @FXML
    protected Button bConectar;
    @FXML
    protected Button bIniciar;
    @FXML
    protected Button bSair;
    @FXML
    protected ComboBox selComandante;
    @FXML
    protected TextArea descricao;

    private boolean conectado = false;

    @FXML
    public void controladorBConectar(ActionEvent pressionado) {
        TextInputDialog inputConexao = new TextInputDialog("localhost");
        inputConexao.initOwner((Stage)((Node) pressionado.getSource()).getScene().getWindow());
        inputConexao.setContentText("IP");
        inputConexao.setHeaderText("Insira o Endereço do Servidor");
        inputConexao.setTitle("Conectar");

        Optional<String> resultado = inputConexao.showAndWait();
        if (resultado.isPresent()) {
            String endereco = resultado.get();

            TextInputDialog inputNome = new TextInputDialog("Jogador1");
            inputNome.initOwner((Stage) ((Node) pressionado.getSource()).getScene().getWindow());
            inputNome.setContentText("Nome");
            inputNome.setHeaderText("Insira seu Nome");
            inputNome.setTitle("Conectar");

            resultado = inputNome.showAndWait();
            if(resultado.isPresent()) {
                String nome = resultado.get();

                interfaceJogador.conectar(endereco, nome);
            }
        }
    }

    @FXML
    public void controladorBDesconectar(ActionEvent pressionado) {
        interfaceJogador.desconectar();
    }

    @FXML
    public void controladorBIniciar(ActionEvent pressionado){interfaceJogador.iniciarPartida();}

    @FXML
    public void controladoBSair(ActionEvent pressionado){
        ((Stage)((Node) pressionado.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void controladorSelComandante(ActionEvent pressionado){
        String comandante = (String) selComandante.getValue();
        interfaceJogador.estabelecerFaccao(comandante);
    }

    @FXML
    public void initialize(){
        selComandante.setItems(comandantes);
    }

    public void setInterfaceJogador(InterfaceJogador interfaceJogador){
        this.interfaceJogador = interfaceJogador;
    }

    public void alterarBotaoConexao() {
        ImageView imagem = (ImageView) bConectar.getChildrenUnmodifiable().get(0);
        if(!conectado) {
            imagem.setImage(new Image("/images/desconectar.png"));
            bConectar.setOnAction(this::controladorBDesconectar);
            conectado = true;
        } else {
            imagem.setImage(new Image( "/images/conectar.png"));
            bConectar.setOnAction(this::controladorBConectar);
            conectado = false;
        }
    }

    public void alterarTextoMenuPrincipal(String facao) {
        descricao.setText(facao);
    }

    public void habilitarBotaoSair(boolean bol) {bSair.setDisable(!bol);}

    public void habilitarBotaoIniciar(boolean bol){
        bIniciar.setDisable(!bol);
    }

    public void habilitarBotaoConexao() {
        bConectar.setDisable(false);
    }
}
