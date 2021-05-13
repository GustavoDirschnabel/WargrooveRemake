package DominioSolucao;

import DominioProblema.Tabuleiro;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;


public class ControladorCenaBatalha {

    InterfaceJogador interfaceJogador;
    @FXML
    protected Button bPassarTurno;
    @FXML
    protected Button bMatrizDano;
    @FXML
    protected Button bRender;
    @FXML
    protected GridPane matrizBatalha;
    @FXML
    protected Label labelOuro;
    @FXML
    protected Label labelTurno;
    @FXML
    protected TextArea textArea;

    @FXML
    public void controladorBPassarTurno(ActionEvent pressionado) {
        interfaceJogador.passarTurno();
    }

    @FXML
    public void controladorBMatrizDano(ActionEvent pressionado) {
        Stage janela = ((Stage)((Node) pressionado.getSource()).getScene().getWindow());
        Stage matrizJanela = new Stage();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("MatrizDano.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        matrizJanela.initOwner(janela);
        matrizJanela.initModality(Modality.APPLICATION_MODAL);
        Scene cenaMatriz = new Scene(root);
        matrizJanela.setScene(cenaMatriz);
        matrizJanela.show();
    }

    @FXML
    public void controladorBRender(ActionEvent pressionado){
        interfaceJogador.rendicao();
    }

    @FXML
    public void controladorClickStackPane(MouseEvent pressionado) {
        StackPane fonte = ((StackPane)pressionado.getSource());
        if (pressionado.getButton() == MouseButton.PRIMARY) {
            interfaceJogador.tratarClique(fonte,true);
        } else if (pressionado.getButton() == MouseButton.SECONDARY) {
            interfaceJogador.tratarClique(fonte,false);
        }

    }

    @FXML
    public void controladorMouseOverStackPane(MouseEvent passado) {
        StackPane fonte = ((StackPane)passado.getSource());
        interfaceJogador.tratarPassadaMouse(fonte);
    }

    public GridPane getMatrizBatalha() {
        return matrizBatalha;
    }

    public Label getLabelOuro() {
        return labelOuro;
    }

    public Label getLabelTurno() {
        return labelTurno;
    }

    public void habilitarBotaoTurno(boolean bol) {
        bPassarTurno.setDisable(!bol);
    }

    public void escreverAreaTextoLateral(String texto) {
        textArea.setText(texto);
    }

    public void setInterfaceJogador(InterfaceJogador interfaceJogador) {
        this.interfaceJogador = interfaceJogador;
    }
}
