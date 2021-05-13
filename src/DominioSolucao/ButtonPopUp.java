package DominioSolucao;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

public class ButtonPopUp {
    protected InterfaceJogador interfaceJogador;
    VBox vbox;
    Stage parent, janela;

    public ButtonPopUp(InterfaceJogador interfaceJogador, Stage parent, ArrayList<String> opcoes, Bounds bounds) {
        this.interfaceJogador = interfaceJogador;
        this.vbox = new VBox();
        this.parent = parent;

        janela = new Stage();
        janela.setX(bounds.getMaxX());
        janela.setY(bounds.getMinY());
        janela.initModality(Modality.APPLICATION_MODAL);
        janela.initOwner(parent);
        HandlerOpcoes handlerOpcoes = new HandlerOpcoes();

        for (String opt : opcoes) {
            Button botao = new Button(opt);
            botao.setOnAction(handlerOpcoes);
            botao.setPrefWidth(vbox.getPrefWidth());
            vbox.getChildren().add(botao);
        }

        Button botaoCancelar = new Button("Cancelar");
        Button botaoEsperar = new Button("Esperar");
        botaoCancelar.setOnAction(handlerOpcoes);
        botaoEsperar.setOnAction(handlerOpcoes);
        botaoCancelar.setPrefWidth(vbox.getPrefWidth());
        botaoEsperar.setPrefWidth(vbox.getPrefWidth());
        vbox.getChildren().add(botaoEsperar);
        vbox.getChildren().add(botaoCancelar);


        Scene cenaOpcoes = new Scene(vbox);
        vbox.layout();
        janela.setScene(cenaOpcoes);
        janela.initStyle(StageStyle.UNDECORATED);
        janela.show();
    }

    private class HandlerOpcoes implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            String tipoBotao = ((Button) actionEvent.getSource()).getText();
            if (!tipoBotao.equals("Cancelar")) {
                interfaceJogador.escolherOpcaoAgir(tipoBotao);
            }
            janela.close();
        }
    }
}
