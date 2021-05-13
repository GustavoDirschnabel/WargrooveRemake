package DominioSolucao;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ControladorTelaRecrutamento {
    @FXML
    protected Label labelOuro;
    @FXML
    protected VBox botoes;
    @FXML
    protected TextArea textArea;
    @FXML
    protected Button botaoRecrutar;
    @FXML
    protected Button botaoCancelar;

    protected InterfaceJogador interfaceJogador;
    protected String unidadeSelecionada = "";


    public void inicializarTela(int ouro, int unidadesCompraveis){
        this.labelOuro.setText("Ouro: " + ouro);
        ObservableList<Node> listaBotoes = botoes.getChildren();
        for (int i = 0; i < unidadesCompraveis; i++) {
            HBox hbox = (HBox) listaBotoes.get(i);
            ObservableList<Node> elementos = hbox.getChildren();
            elementos.get(2).setDisable(false);
        }
    }
    @FXML
    public void controladorBotoesUnidades(ActionEvent pressionado){
        Button botao = (Button) pressionado.getSource();
        HBox hbox = (HBox) botao.getParent();
        ObservableList<Node> elementos = hbox.getChildren();
        Label label = (Label) elementos.get(1);
        unidadeSelecionada = label.getText();
        botaoRecrutar.setDisable(false);
        Scanner scan = null;
        String fileName = unidadeSelecionada + ".txt";
        scan = new Scanner(getClass().getResourceAsStream("DescricoesPecas/" + fileName),"utf-8");
        String texto = "";
        while (scan.hasNextLine()){
            texto += scan.nextLine() + "\n";
        }
        textArea.setText(texto);
    }

    @FXML
    public void controladorBotaoRecrutar (ActionEvent pressionado) {
        interfaceJogador.unidadeEscolhida(unidadeSelecionada);
    }

    @FXML
    public void controladorBotaoCancelar (ActionEvent pressionado) {
        interfaceJogador.unidadeEscolhida(null);
    }

    public void setInterfaceJogador(InterfaceJogador interfaceJogador) {
        this.interfaceJogador = interfaceJogador;
    }

    public void fecharTelaRecrutamento() {
        ((Stage) textArea.getScene().getWindow()).close();
    }
}
