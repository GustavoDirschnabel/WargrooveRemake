package DominioProblema;

import DominioSolucao.InterfaceJogador;
import DominioSolucao.InterfaceNetgames;
import DominioSolucao.JogadaGenerica;
import DominioProblema.Pecas.*;
import DominioProblema.Pecas.Marchante.*;
import DominioProblema.Pecas.Veiculo.Balista;
import DominioProblema.Pecas.Veiculo.Trebuchet;
import DominioProblema.Pecas.Voador.Bruxa;
import DominioProblema.Pecas.Voador.Dragao;
import DominioProblema.Pecas.Voador.Harpia;
import br.ufsc.inf.leobr.cliente.exception.NaoConectadoException;
import javafx.scene.Scene;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class Tabuleiro {

	protected ArrayList<Quadrado> quadrados;
	protected Jogador jogadorLocal;
	protected Jogador jogadorRemoto;
	protected InterfaceJogador interfaceJogador;
	protected InterfaceNetgames interfaceNetgames;
	protected Semaphore semaforoInformacoesEnviadas;
	protected int posicaoEnviada, posOrigem, posDestino;
	protected TipoPeca tipoPecaEnviado;

	public Tabuleiro(int posicao, String nomeJogador1, String nomeJogador2,InterfaceJogador interfaceJogador, InterfaceNetgames interfaceNetgames) {
		this.interfaceJogador = interfaceJogador;
		this.interfaceNetgames = interfaceNetgames;
		this.semaforoInformacoesEnviadas = new Semaphore(0);

		if (posicao == 1) {
			this.jogadorLocal = new Jogador(nomeJogador1,true,1, 400);
			this.jogadorRemoto = new Jogador(nomeJogador2,false,2, 100);
			interfaceJogador.habilitarBotaoTurno(true);
		} else {
			this.jogadorLocal = new Jogador(nomeJogador2,false,2,100);
			this.jogadorRemoto = new Jogador(nomeJogador1,true,1, 400);
		}
        interfaceJogador.atualizarTurno(nomeJogador1);
		interfaceJogador.atualizarOuro(Integer.toString(jogadorLocal.getOuro()));
		quadrados = new ArrayList<Quadrado>(272);

		Scanner scan = new Scanner(getClass().getResourceAsStream("mapa.map"));
		String linha = scan.nextLine();
		String[] terreno = linha.split(" ");
		for (int i = 0; i < 272; i++) {
			Quadrado quad;
			if(terreno[i].equals("e")) {
				quad = new Quadrado(1);
			} else if (terreno[i].equals("f")) {
				quad = new Quadrado(3);
			} else if (terreno[i].equals("r")) {
				quad = new Quadrado(2);
			} else if (terreno[i].equals("m")) {
				quad = new Quadrado(4);
			} else if (terreno[i].equals("p")) {
				quad = new Quadrado(5);
			} else if (terreno[i].equals("o")) {
				quad = new Quadrado(6);
			} else {
				quad = new Quadrado(0);
			}
			quadrados.add(quad);
		}
		linha = scan.nextLine();
		String[] pecas = linha.split(" ");

		for(int i = 0; i < 21; i++) {
			String[] info = pecas[i].split("-");
			String tipo = info[0];
			String dono = info[1];

			Jogador jog = null;
			if (Integer.parseInt(dono) != 0) {
				if(Integer.parseInt(dono) == posicao) {
					jog = jogadorLocal;
				} else {
					jog = jogadorRemoto;
				}
			}

			int indiceQuad = Integer.parseInt(info[2]);
			Peca peca;
			if(tipo.equals("0")) {
				peca = new Estrutura(false,jog,TipoPeca.BASE, indiceQuad,0);
			} else if (tipo.equals("1")) {
				peca = new Estrutura(false,jog,TipoPeca.QUARTEL,indiceQuad,0);
			} else if (tipo.equals("2")) {
				peca = new Estrutura(false,jog,TipoPeca.TORRE, indiceQuad,0);
			} else if (tipo.equals("3")) {
				peca = new Estrutura(false,jog,TipoPeca.ESCONDERIJO, indiceQuad,0);
			} else if (tipo.equals("4")) {
				peca = new Estrutura(false,jog,TipoPeca.VILA, indiceQuad,100);
			} else {
				peca = new Soldado(false,jog,indiceQuad);
			}

			int ordem = 0;
			if(jog != null) {
				jog.addPeca(peca);
				ordem = jog.getOrdem();
			}


			interfaceJogador.posicionarPeca(indiceQuad,peca.getTipo(),100,ordem);
			quadrados.get(indiceQuad).posicionarPeca(peca);
		}

	}
	/**
	 * 
	 * @param posIni
	 * @param pos
	 * @param posAlvo
	 */
	public void realizarGroove(int posIni, int pos, int posAlvo) {
		boolean turno = jogadorLocal.getTurno();
		if (turno) {
			JogadaGenerica jogada = new JogadaGenerica(posIni,pos,posAlvo,"Groove",null,0,0);
			interfaceNetgames.enviarJogada(jogada);
		}
		moverPeca(posIni,pos);

		Quadrado quadComandante = this.quadrados.get(pos);
		Comandante comandante = (Comandante) quadComandante.getPeca();

		comandante.groove(posAlvo,this);

		comandante.setCargaGroove(0);
		comandante.setExausto(true);
		interfaceJogador.exaustarPeca(pos,true);
	}

	/**
	 * 
	 * @param posIni
	 * @param pos
	 * @param posAlvo
	 */
	public void realizarCaptura(int posIni, int pos, int posAlvo) {
		boolean turno = jogadorLocal.getTurno();
		Jogador jogadorVez;
		if (turno) {
			JogadaGenerica jogada = new JogadaGenerica(posIni,pos,posAlvo,"Capturar",null,0,0);
			interfaceNetgames.enviarJogada(jogada);
			jogadorVez = this.jogadorLocal;
		} else {
			jogadorVez = this.jogadorRemoto;
		}

		moverPeca(posIni,pos);
		Quadrado quadPeca = quadrados.get(pos);
		Peca capturante = quadPeca.getPeca();

		Quadrado quadAlvo = quadrados.get(posAlvo);
		Peca estrut = quadAlvo.getPeca();
		TipoPeca tipoEstrut = estrut.getTipo();
		estrut.setDono(jogadorVez);
		int novoPVEstrutura = estrut.ajustePV(capturante.getPV()/2);
		interfaceJogador.posicionarPeca(posAlvo,tipoEstrut,novoPVEstrutura,jogadorVez.getOrdem());
		capturante.setExausto(true);
		interfaceJogador.exaustarPeca(pos,true);

		if (estrut.getTipo() != TipoPeca.VILA) {
			estrut.setExausto(true);
			interfaceJogador.exaustarPeca(posAlvo,true);
		}
		jogadorVez.addPeca(estrut);
	}

	/**
	 * 
	 * @param opcao
	 */
	public void escolherOpcaoAgir(String opcao) {
		switch (opcao) {
			case "Atacar":
				this.atacar(this.posOrigem,this.posDestino);
				return;
			case "Esperar":
				this.esperar(this.posOrigem,this.posDestino);
				return;
			case "Capturar":
				this.capturarEstrutura(this.posOrigem,this.posDestino);
				return;
			case "Reforcar":
				this.reforcarUnidade(this.posOrigem,this.posDestino);
				return;
			case "Groove":
				this.groove(this.posOrigem,this.posDestino);
				return;
			default:
				this.acaoEspecial(this.posOrigem,this.posDestino,opcao);
		}
	}

	/**
	 * 
	 * @param posIni
	 * @param pos
	 */
	public void esperar(int posIni, int pos) {
		boolean turno = jogadorLocal.getTurno();
		if (turno) {
			JogadaGenerica jogada = new JogadaGenerica(posIni,pos,0,"Esperar",null,0,0);
			interfaceNetgames.enviarJogada(jogada);
		}

		Unidade unidade = moverPeca(posIni,pos);
		unidade.setExausto(true);
		interfaceJogador.exaustarPeca(pos,true);
	}

	/**
	 * 
	 * @param vencedor
	 */
	public void finalizarPartida(boolean vencedor) {
		String nomeVencedor;
		if (vencedor) {
			nomeVencedor = this.jogadorLocal.getNome();
		} else {
			nomeVencedor = this.jogadorRemoto.getNome();
		}
		interfaceJogador.finalizarPartida(nomeVencedor,true);
		try {
			interfaceNetgames.desconectar();
		} catch (NaoConectadoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param pos
	 */
	public Quadrado getQuadrado(int pos) {
		return this.quadrados.get(pos);
	}

	/**
	 * 
	 * @param pos
	 */
	public void inspecionarTerreno(int pos) {
		Quadrado quadrado = this.quadrados.get(pos);
		String descricao = "Tipo de Terreno: ";
		int tipoTerreno = quadrado.getTipoTerreno();
		int defesa = quadrado.getDefesa();
		switch (tipoTerreno) {
			case 0:
				descricao += "Planície\n\n";
				descricao += "Defesa Fornecida: " + defesa + "\n\n";
				descricao += "Apesar de acessível a todas as unidades, planícies fornecem pouca defesa.\n\n";
				descricao += "*Custos de movimento: \n";
				descricao += "MARCHANTE: 1\n";
				descricao += "VEÍCULO: 2\n";
				descricao += "MONTADO: 1\n";
				descricao += "VOADOR: 1";
				break;
			case 1:
				descricao += "Estrada\n\n";
				descricao += "Defesa Fornecida: " + defesa + "\n\n";
				descricao += "Estradas de pedra que garantem mobilidade a veículos, mas oferecem pouca proteção.\n\n";
				descricao += "Custos de movimento: \n";
				descricao += "MARCHANTE: 1\n";
				descricao += "VEÍCULO: 1\n";
				descricao += "MONTADO: 1\n";
				descricao += "VOADOR: 1";
				break;
			case 2:
				descricao += "Rio\n\n";
				descricao += "Defesa Fornecida: " + defesa + "\n\n";
				descricao += "Não acessível a veículos, unidades em um rio estão em uma desvantagem distinta.\n\n";
				descricao += "Custos de movimento: \n";
				descricao += "MARCHANTE: 2\n";
				descricao += "VEÍCULO: INACESSÍVEL\n";
				descricao += "MONTADO: 4\n";
				descricao += "VOADOR: 1";
				break;
			case 3:
				descricao += "Floresta\n\n";
				descricao += "Defesa Fornecida: " + defesa + "\n\n";
				descricao += "Florestas são difíceis de atravessar, mas provém uma boa defesa e uma maneira de evitar veículos.\n\n";
				descricao += "Custos de movimento: \n";
				descricao += "MARCHANTE: 2\n";
				descricao += "VEÍCULO: INACESSÍVEL\n";
				descricao += "MONTADO: 3\n";
				descricao += "VOADOR: 1";
				break;
			case 4:
				descricao += "Montanha\n\n";
				descricao += "Defesa Fornecida: " + defesa + "\n\n";
				descricao += "Acessível apenas a aqueles a pé ou a voo. Difíceis de atravessar, mas provém alta defesa\n\n";
				descricao += "Custos de movimento: \n";
				descricao += "MARCHANTE: 3\n";
				descricao += "VEÍCULO: INACESSÍVEL\n";
				descricao += "MONTADO: INACESSÍVEL\n";
				descricao += "VOADOR: 1";
				break;
			case 5:
				descricao += "Praia\n\n";
				descricao += "Defesa Fornecida: " + defesa + "\n\n";
				descricao += "Inacessível a veículos, aqueles atacados em uma praia estão em desvantagem.\n\n";
				descricao += "Custos de movimento: \n";
				descricao += "MARCHANTE: 1\n";
				descricao += "VEÍCULO: INACESSÍVEL\n";
				descricao += "MONTADO: 1\n";
				descricao += "VOADOR: 1";
				break;
			default:
				descricao += "Mar\n\n";
				descricao += "Defesa Fornecida: " + defesa + "\n\n";
				descricao += "Mar aberto. Acessível somente a unidades voadoras.\n\n";
				descricao += "Custos de movimento: \n";
				descricao += "MARCHANTE: INACESSÍVEL\n";
				descricao += "VEÍCULO: INACESSÍVEL\n";
				descricao += "MONTADO: INACESSÍVEL\n";
				descricao += "VOADOR: 1";
		}

		interfaceJogador.abrirTelaInspecao(descricao);
	}

	/**
	 * 
	 * @param posIni
	 * @param pos
	 */
	public Unidade moverPeca(int posIni, int pos) {
		Quadrado quad = quadrados.get(posIni);
		Quadrado quadDestino = quadrados.get(pos);
		Unidade unidade = quad.removerUnidade();
		interfaceJogador.removerPeca(posIni);
		unidade.setPosicao(pos);
		quadDestino.posicionarPeca(unidade);
		interfaceJogador.posicionarPeca(pos,unidade.getTipo(),unidade.getPV(),unidade.getDono().getOrdem());
		return unidade;
	}

	/**
	 * 
	 * @param pos
	 * @param novoPV
	 */
	public void notificarAjustePV(int pos, int novoPV) {
		interfaceJogador.atualizarPV(pos,novoPV);
	}

	/**
	 * 
	 * @param quadrado
	 * @param exausto
	 */
	public void notificarExaustao(Quadrado quadrado, boolean exausto) {
		int pos = this.quadrados.indexOf(quadrado);
		interfaceJogador.exaustarPeca(pos,exausto);
	}

	/**
	 * 
	 * @param pos
	 * @param tipoUnidade
	 * @param ordem
	 */
	public void notificarPosicionamentoPeca(int pos, TipoPeca tipoUnidade, int ordem) {
		interfaceJogador.posicionarPeca(pos,tipoUnidade,100,ordem);
	}

	/**
	 * 
	 * @param pos
	 */
	public void notificarRemocaoPeca(int pos) {
		interfaceJogador.removerPeca(pos);
	}

	public void passarTurno() {
		JogadaGenerica jogada = new JogadaGenerica(0,0,0,"Passar Turno",null,0,0);
		interfaceNetgames.enviarJogada(jogada);
		jogadorLocal.setTurno(false);
		ArrayList<Peca> pecasRemoto = jogadorRemoto.getPecas();
		int ouroAtual = jogadorRemoto.getOuro() + 100;

		for (int i = 0; i < pecasRemoto.size(); i++) {
			Peca pecaAtual = pecasRemoto.get(i);
		    int pos = pecaAtual.getPosicao();
		    TipoPeca tipo = pecasRemoto.get(i).getTipo();
		    if (tipo.getNum() <= TipoPeca.VILA.getNum() || tipo.getNum() >= TipoPeca.MERCIA.getNum()) {
		        int novoPv = pecaAtual.ajustePV(5);
		        interfaceJogador.atualizarPV(pos,novoPv);
		        if (tipo.getNum() >= TipoPeca.MERCIA.getNum()) {
					Comandante comandante = (Comandante) pecaAtual;
					int taxaG = comandante.getTaxaDeCarga();
					int cargaG = comandante.getCargaGroove();
					comandante.setCargaGroove(cargaG + taxaG);
				} else if (tipo == TipoPeca.VILA) {
		        	ouroAtual += 100;
				}
            }
        }

		this.jogadorRemoto.setOuro(ouroAtual);
		interfaceJogador.atualizarTurno(jogadorRemoto.getNome());
		interfaceJogador.habilitarBotaoTurno(false);
	}

	public int[] previaDano(int posAlvo) {
		Quadrado quadAlvo = quadrados.get(posAlvo);
		Peca alvo = quadAlvo.getPeca();
		int dano[];
		dano = new int[]{0, 0, 0, 0, 0, 0};
		if (alvo != null) {
			Jogador donoAlvo = alvo.getDono();
			if (donoAlvo == jogadorRemoto) {
				//Os atributos a seguir definidos com constantes se referem a um caso que uma estrutura é o alvo
				//A única exceção sendo sorte, que é atrelado ao argumento ehPrevia

				Quadrado quadAtacante;
				quadAtacante = quadrados.get(this.posOrigem);
				Peca atacante = quadAtacante.getPeca();
				TipoPeca tipoAlvo = alvo.getTipo();
				TipoPeca tipoAtacante = atacante.getTipo();
				int danoBaseAtacante = tipoAtacante.getDanoBase()[tipoAlvo.getNum()];

				if (danoBaseAtacante < 1) {
					return dano;
				}

				int defesaAtacante = quadAtacante.getDefesa();
				int defesaAlvo = 0;
				int danoBaseAlvo = tipoAlvo.getDanoBase()[tipoAtacante.getNum()];
				int pvAtacante = atacante.getPV();
				int pvAlvo = alvo.getPV();
				boolean criticoAtacante = ((Unidade) atacante).avaliarCritico(this.posOrigem, this.posDestino, posAlvo, this);

				float fatorCriticoAtacante = 1f;
				float fatorCriticoAlvo = 1f;

				if (criticoAtacante) {
					fatorCriticoAtacante = ((Unidade) atacante).getFatorCritico();
					dano[4] = 1;
				}

				int alcanceAlvo = alvo.getAlcanceDeAtaque();
				boolean revida = false;
				int deltaX = Math.abs(Math.floorDiv(posAlvo,16) - Math.floorDiv(posDestino,16));
				int deltaY = Math.abs(posAlvo % 16 - posDestino % 16);

				if (deltaX + deltaY <= alcanceAlvo && danoBaseAlvo > 0) {
					revida = true;
					if ((tipoAlvo == TipoPeca.BALISTA || tipoAlvo == TipoPeca.TREBUCHET) && deltaX + deltaY < 2 || tipoAlvo == TipoPeca.MOSQUETEIRO) {
						revida = false;
					}
				}

				if (tipoAlvo.getNum() > TipoPeca.VILA.getNum()) {
					defesaAlvo = quadAlvo.getDefesa();
					boolean criticoAlvo = ((Unidade) alvo).avaliarCritico(posAlvo, posAlvo, this.posDestino, this);

					if (criticoAlvo && revida) {
						fatorCriticoAlvo = ((Unidade) alvo).getFatorCritico();
						dano[5] = 1;
					}

					int danoMinimoAtacante = (int) Math.round((danoBaseAtacante*pvAtacante/100f*(1-0.1*defesaAlvo*pvAlvo/100f))*fatorCriticoAtacante);
					int danoMaximoAtacante = (int) Math.round(((danoBaseAtacante+10)*pvAtacante/100f*(1-0.1*defesaAlvo*pvAlvo/100f))*fatorCriticoAtacante);
					dano[0] = danoMinimoAtacante;
					dano[1] = danoMaximoAtacante;

					if (revida) {
						dano[2] = (int) Math.max(0, Math.round((danoBaseAlvo * (pvAlvo - danoMaximoAtacante) / 100f * (1 - 0.1 * defesaAtacante * pvAtacante / 100f)) * fatorCriticoAlvo));
						dano[3] = (int) Math.max(0, Math.round(((danoBaseAlvo + 10) * (pvAlvo - danoMinimoAtacante) / 100f * (1 - 0.1 * defesaAtacante * pvAtacante / 100f)) * fatorCriticoAlvo));
					}
				} else {

					int danoMinimoAtacante = (int) Math.round((danoBaseAtacante*pvAtacante/100f*(1-0.1*defesaAlvo*pvAlvo/100f))*fatorCriticoAtacante);
					int danoMaximoAtacante = (int) Math.round(((danoBaseAtacante+10)*pvAtacante/100f*(1-0.1*defesaAlvo*pvAlvo/100f))*fatorCriticoAtacante);
					dano[0] = danoMinimoAtacante;
					dano[1] = danoMaximoAtacante;

					if (pvAlvo - danoMaximoAtacante > 0 && revida) {
						dano[2] = (int) Math.max(0, Math.round((danoBaseAlvo * (100) / 100f * (1 - 0.1 * defesaAtacante / 100f)) * fatorCriticoAlvo));
					}

					if (pvAlvo - danoMinimoAtacante > 0 && revida) {
						dano[3] = (int) Math.max(0, Math.round(((danoBaseAlvo + 10) * (100) / 100f * (1 - 0.1 * defesaAtacante / 100f)) * fatorCriticoAlvo));
					}

				}
			}

		}

		return dano;
	}

	/**
	 * @param posAlvo
	 */
	public int[] calcularDano(int posAlvo) {
		Quadrado quadAlvo = quadrados.get(posAlvo);
		Peca alvo = quadAlvo.getPeca();
		int dano[];
		dano = new int[]{0,0};

		Jogador donoAlvo = alvo.getDono();
		//Os atributos a seguir definidos com constantes se referem a um caso que uma estrutura é o alvo
		//A única exceção sendo sorte, que é atrelado ao argumento ehPrevia

		Quadrado quadAtacante;
		quadAtacante = quadrados.get(this.posOrigem);
		Peca atacante = quadAtacante.getPeca();
		int defesaAtacante = quadAtacante.getDefesa();
		int defesaAlvo = 0;
		TipoPeca tipoAtacante = atacante.getTipo();
		TipoPeca tipoAlvo = alvo.getTipo();
		int danoBaseAtacante = tipoAtacante.getDanoBase()[tipoAlvo.getNum()];
		int danoBaseAlvo = tipoAlvo.getDanoBase()[tipoAtacante.getNum()];
		int pvAtacante = atacante.getPV();
		int pvAlvo = alvo.getPV();
		boolean criticoAtacante = ((Unidade)atacante).avaliarCritico(this.posOrigem,this.posDestino,posAlvo,this);

		float fatorCriticoAtacante = 1f;
		float fatorCriticoAlvo = 1f;

		if (criticoAtacante) {
			fatorCriticoAtacante = ((Unidade) atacante).getFatorCritico();
		}

		int sorteAtacante = (int) Math.round(Math.random()*10);
		int sorteDefensor = (int) Math.round(Math.random()*10);

		int alcanceAlvo = alvo.getAlcanceDeAtaque();
		boolean revida = false;
		int deltaX = Math.abs(Math.floorDiv(posAlvo,16) - Math.floorDiv(posDestino,16));
		int deltaY = Math.abs(posAlvo % 16 - posDestino % 16);

		if (deltaX + deltaY <= alcanceAlvo && danoBaseAlvo > 0) {
			revida = true;
			if ((tipoAlvo == TipoPeca.BALISTA || tipoAlvo == TipoPeca.TREBUCHET) && deltaX + deltaY < 2 || tipoAlvo == TipoPeca.MOSQUETEIRO) {
				revida = false;
			}
		}

		int danoAtacante;
		int danoAlvo = 0;
		//(DanoBase+Sorte)*PV/100*(1-0.1*Def*PVRecip/100)
		if (tipoAlvo.getNum() > TipoPeca.VILA.getNum()) {
			defesaAlvo = quadAlvo.getDefesa();
			boolean criticoAlvo = ((Unidade) alvo).avaliarCritico(posAlvo, posAlvo, this.posDestino, this);

			if (criticoAlvo) {
				fatorCriticoAlvo = ((Unidade) alvo).getFatorCritico();
			}

			danoAtacante = (int) Math.round(((danoBaseAtacante+sorteAtacante)*pvAtacante/100f*(1-0.1*defesaAlvo*pvAlvo/100f))*fatorCriticoAtacante);

			if (revida) {
				danoAlvo = (int) Math.max(0, Math.round(((danoBaseAlvo + sorteDefensor) * (pvAlvo - danoAtacante) / 100f * (1 - 0.1 * defesaAtacante * pvAtacante / 100f)) * fatorCriticoAlvo));
			}
		} else {
			danoAtacante = (int) Math.round(((danoBaseAtacante+sorteAtacante)*pvAtacante/100f*(1-0.1*defesaAlvo*pvAlvo/100f))*fatorCriticoAtacante);
			if (pvAlvo - danoAtacante > 0 && revida) {
				danoAlvo = (int) Math.max(0, Math.round(((danoBaseAlvo + sorteDefensor) * (100f) / 100f * (1 - 0.1 * defesaAtacante / 100f)) * fatorCriticoAlvo));
			}
		}

		dano[0] = danoAtacante;
		dano[1] = danoAlvo;

		return dano;
	}

	/**
	 * 
	 * @param tipoRealce
	 * @param pos
	 */
	public ArrayList<Quadrado> realceQuadrados(int posIni,int pos, int tipoRealce, int cor, boolean ehRealceClique) {
		ArrayList<Quadrado> quadradosVerificados = this.verificarQuadradosAlcance(posIni,pos,tipoRealce);
		ArrayList<Integer> posicaoQuadradosVerificados = new ArrayList<>();

		for (Quadrado quad: quadradosVerificados) {
			posicaoQuadradosVerificados.add(quadrados.indexOf(quad));
		}

		interfaceJogador.realcarPosicao(posicaoQuadradosVerificados,cor,ehRealceClique);
		return quadradosVerificados;
	}

	/**
	 * 
	 * @param posIni
	 * @param pos
	 * @param posAlvo
	 */
	public void realizarAtaque(int posIni,int pos,int posAlvo, int danoAtaque, int danoDefesa) {
        boolean turnoLocal = jogadorLocal.getTurno();
        if (turnoLocal) {
            JogadaGenerica jogada = new JogadaGenerica(posIni ,pos ,posAlvo,"Ataque",null, danoAtaque, danoDefesa);
            interfaceNetgames.enviarJogada(jogada);
        }

        this.moverPeca(posIni,pos);

        Quadrado quadAtacante = quadrados.get(pos);
        Peca atacante = quadAtacante.getPeca();
        TipoPeca tipoAtacante = atacante.getTipo();
        int novoPVAtacante = atacante.ajustePV(danoDefesa);

        Quadrado quadAlvo = quadrados.get(posAlvo);
        Peca alvo = quadAlvo.getPeca();
        TipoPeca tipoAlvo = alvo.getTipo();
        int novoPVAlvo = alvo.ajustePV(danoAtaque);

        int carregamentoGrooveAtacante = 2;
		int carregamentoGrooveAlvo = 1;

        if (novoPVAlvo < 1) {
			carregamentoGrooveAtacante = 5;
            if (tipoAlvo.getNum() > TipoPeca.BASE.getNum() && tipoAlvo.getNum() <= TipoPeca.VILA.getNum()) {
                alvo.setDono(null);
				((Estrutura) alvo).setPV(0);
                interfaceJogador.posicionarPeca(posAlvo,tipoAlvo,0,0);
            } else {
                quadAlvo.removerUnidade();
                interfaceJogador.removerPeca(posAlvo);
                if (tipoAlvo.getNum() == TipoPeca.BASE.getNum() || tipoAlvo.getNum() >= TipoPeca.MERCIA.getNum()) {
                    if (turnoLocal) {
                        this.finalizarPartida(true);
                    } else {
                        this.finalizarPartida(false);
                    }
                } else if (tipoAlvo == TipoPeca.LADRAO) {
                	Ladrao ladrao = (Ladrao) alvo;
                	int ouroRoubado = ladrao.getOuroRoubado();
					if (turnoLocal) {
						int ouroAtual = jogadorLocal.getOuro() + ouroRoubado;
						jogadorLocal.setOuro(ouroAtual);
						interfaceJogador.atualizarOuro(Integer.toString(ouroAtual));
					} else {
						int ouroAtual = jogadorRemoto.getOuro() + ouroRoubado;
						jogadorRemoto.setOuro(ouroAtual);
					}
				}
            }

            if (turnoLocal) {
                jogadorRemoto.getPecas().remove(alvo);
            } else {
                jogadorLocal.getPecas().remove(alvo);
            }

        } else {
            interfaceJogador.atualizarPV(posAlvo,novoPVAlvo);
        }

        if (novoPVAtacante < 1) {
            quadAtacante.removerUnidade();
            interfaceJogador.removerPeca(pos);;
            if (turnoLocal) {
                if (tipoAtacante.getNum() >= TipoPeca.MERCIA.getNum()) {
                    this.finalizarPartida(false);
                }
                jogadorLocal.getPecas().remove(atacante);
            } else {
                if (tipoAtacante.getNum() >= TipoPeca.MERCIA.getNum()) {
                    this.finalizarPartida(true);
                }
                jogadorRemoto.getPecas().remove(atacante);
            }
			carregamentoGrooveAlvo = 4;

        } else {
            interfaceJogador.atualizarPV(pos,novoPVAtacante);
            atacante.setExausto(true);
            interfaceJogador.exaustarPeca(pos,true);
        }

        if (tipoAtacante == TipoPeca.MOSQUETEIRO) {
        	int balas = ((Mosqueteiro)atacante).getBalas();
			((Mosqueteiro)atacante).setBalas(balas - 1);
		}

        if (tipoAtacante.getNum() >= TipoPeca.MERCIA.getNum()) {
        	Comandante atacanteComand = (Comandante) atacante;
        	int taxaG = atacanteComand.getTaxaDeCarga() * carregamentoGrooveAtacante;
        	int cargaG = atacanteComand.getCargaGroove();
        	atacanteComand.setCargaGroove(cargaG + taxaG);
		}

        if (tipoAlvo.getNum() >= TipoPeca.MERCIA.getNum()) {
			Comandante alvoComand = (Comandante) alvo;
			int taxaG = alvoComand.getTaxaDeCarga() * carregamentoGrooveAlvo;
			int cargaG = alvoComand.getCargaGroove();
			alvoComand.setCargaGroove(cargaG + taxaG);
		}
	}

	/**
	 * 
	 * @param jogada
	 */
	public void receberJogada(JogadaGenerica jogada) {
		String tipoJogada = jogada.getTipoJogada();
		int posIni = jogada.getPosIni();
		int pos = jogada.getPos();
		int posAlvo = jogada.getPosAlvo();

		switch (tipoJogada) {
			case "Passar Turno":
				this.trocarTurno();
				break;

			case "Ataque":
				int danoAtaque = jogada.getDanoAtacante();
				int danoDefesa = jogada.getDanoAlvo();
				this.realizarAtaque(posIni,pos,posAlvo,danoAtaque,danoDefesa);
				break;

			case "Groove":
				this.realizarGroove(posIni,pos,posAlvo);
				break;

			case "Reforcar":
				this.realizarReforco(posIni,pos,posAlvo);
				break;

			case "Recrutamento":
				this.realizarRecrutamento(posIni,posAlvo,jogada.getTipoUnidade());
				break;

			case "Capturar":
				this.realizarCaptura(posIni,pos,posAlvo);
				break;

			case "Rendicao":
				this.finalizarPartida(true);
				break;

			case "Esperar":
				this.esperar(posIni,pos);
				break;

			case "Receber Faccao":
				TipoPeca comandanteJogadorRemoto = jogada.getTipoUnidade();
				Comandante comandanteInimigo;
				int indiceComandante;
				int ordem = jogadorRemoto.getOrdem();

				if (ordem == 1) {
					indiceComandante = 262;
				} else {
					indiceComandante = 9;
				}

				if (comandanteJogadorRemoto == TipoPeca.MERCIA) {
					comandanteInimigo = new Mercia(jogadorRemoto,indiceComandante);
				} else if (comandanteJogadorRemoto == TipoPeca.SIGRID) {
					comandanteInimigo = new Sigrid(jogadorRemoto,indiceComandante);
				} else if (comandanteJogadorRemoto == TipoPeca.CAESAR) {
					comandanteInimigo = new Caesar(jogadorRemoto,indiceComandante);
				} else {
					comandanteInimigo = new Valder(jogadorRemoto,indiceComandante);
				}

				jogadorRemoto.addPeca(comandanteInimigo);
				interfaceJogador.posicionarPeca(indiceComandante,comandanteInimigo.getTipo(),100,ordem);
				quadrados.get(indiceComandante).posicionarPeca(comandanteInimigo);
				break;

			default:
				realizarAcaoEspecial(posIni,pos,posAlvo,tipoJogada);
		}
	}

	/**
	 * @param posIni
	 * @param posAlvo
	 * @param tipoUnidade
	 */
	public void realizarRecrutamento(int posIni, int posAlvo, TipoPeca tipoUnidade) {
		Jogador jogadorVez;
		boolean turnoLocal = jogadorLocal.getTurno();
		if (turnoLocal) {
			JogadaGenerica jogada = new JogadaGenerica(posIni ,0,posAlvo,"Recrutamento",tipoUnidade,0,0);
			interfaceNetgames.enviarJogada(jogada);
			jogadorVez = jogadorLocal;
		} else {
			jogadorVez = jogadorRemoto;
		}

		Unidade recrutada;
		switch (tipoUnidade) {
			case SOLDADO:
				recrutada = new Soldado(true,jogadorVez,posAlvo);
				break;
			case CAO:
				recrutada = new Cao(true,jogadorVez,posAlvo);
				break;
			case LANCEIRO:
				recrutada = new Lanceiro(true,jogadorVez,posAlvo);
				break;
			case MAGO:
				recrutada = new Mago(true,jogadorVez,posAlvo);
				break;
			case LADRAO:
				recrutada = new Ladrao(true,jogadorVez,posAlvo);
				break;
			case ARQUEIRO:
				recrutada = new Arqueiro(true,jogadorVez,posAlvo);
				break;
			case CAVALEIRO:
				recrutada = new Cavaleiro(true,jogadorVez,posAlvo);
				break;
			case HARPIA:
				recrutada = new Harpia(true,jogadorVez,posAlvo);
				break;
			case MOSQUETEIRO:
				recrutada = new Mosqueteiro(true,jogadorVez,posAlvo);
				break;
			case BALISTA:
				recrutada = new Balista(true,jogadorVez,posAlvo);
				break;
			case BRUXA:
				recrutada = new Bruxa(true,jogadorVez,posAlvo);
				break;
			case TREBUCHET:
				recrutada = new Trebuchet(true,jogadorVez,posAlvo);
				break;
			case GIGANTE:
				recrutada = new Gigante(true,jogadorVez,posAlvo);
				break;
			default:
				recrutada = new Dragao(true,jogadorVez,posAlvo);
		}

		jogadorVez.addPeca(recrutada);
		int custo = tipoUnidade.getCusto();
		int ouroAtual = jogadorVez.getOuro() - custo;
		jogadorVez.setOuro(ouroAtual);
		if (jogadorVez == jogadorLocal) {
			interfaceJogador.atualizarOuro(Integer.toString(ouroAtual));
		}

		Quadrado quadAlvo = quadrados.get(posAlvo);
		quadAlvo.posicionarPeca(recrutada);
		int ordem = jogadorVez.getOrdem();
		interfaceJogador.posicionarPeca(posAlvo,tipoUnidade,100,ordem);
		interfaceJogador.exaustarPeca(posAlvo,true);

		Quadrado quadEstrut = quadrados.get(posIni);
		Peca estrutura = quadEstrut.getPeca();
		estrutura.setExausto(true);
		interfaceJogador.exaustarPeca(posIni,true);
	}

	/**
	 * 
	 * @param posIni
	 * @param pos
	 * @param posEstrut
	 */
	public void realizarReforco(int posIni, int pos, int posEstrut) {
		boolean turno = jogadorLocal.getTurno();
		if (turno) {
			JogadaGenerica jogada = new JogadaGenerica(posIni ,pos,posEstrut,"Reforcar",null,0,0);
			interfaceNetgames.enviarJogada(jogada);
		}
		this.moverPeca(posIni,pos);

		Quadrado quadPeca = this.quadrados.get(pos);
		Peca peca = quadPeca.getPeca();
		Jogador donoPeca = peca.getDono();
		TipoPeca tipoPeca = peca.getTipo();
		Quadrado quadEstrut = this.quadrados.get(posEstrut);
		Peca estrut = quadEstrut.getPeca();
		int custoPorPV = Math.floorDiv(tipoPeca.getCusto(),100);
		int pvFaltante = 100 - peca.getPV();
		int pvMaximo = estrut.getPV() - 1;
		int ouro = donoPeca.getOuro();

		int ouroGasto = Math.min(Math.min(pvMaximo*custoPorPV, pvFaltante*custoPorPV),ouro);
		int pvRecuperado = Math.floorDiv(ouroGasto,custoPorPV);

		int novoPVUnidade = peca.ajustePV(pvRecuperado);
		int novoOuro = ouro - ouroGasto;
		donoPeca.setOuro(novoOuro);
		if (turno) {
			interfaceJogador.atualizarOuro(Integer.toString(novoOuro));
		}
		int novoPVEstrutura = estrut.ajustePV(-pvRecuperado);
		interfaceJogador.atualizarPV(pos,novoPVUnidade);
		interfaceJogador.atualizarPV(posEstrut, novoPVEstrutura);
		peca.setExausto(true);
		interfaceJogador.exaustarPeca(pos,true);
	}

	public void rendicao() {
		JogadaGenerica jogada = new JogadaGenerica(0 ,0,0,"Rendicao",null,0,0);
		interfaceNetgames.enviarJogada(jogada);
		this.finalizarPartida(false);
	}

	/**
	 * 
	 * @param pos
	 */
	public void selecionarQuadrado(int pos) {
		Quadrado quad = this.quadrados.get(pos);
		Peca peca = quad.getPeca();
		ArrayList<String> listaAcoes = new ArrayList<>();

		if (peca != null) {
			TipoPeca tipo = peca.getTipo();
			Jogador dono = peca.getDono();
			boolean exausto = peca.getExausto();
			boolean turno = jogadorLocal.getTurno();
			boolean aliada;

			if (dono == this.jogadorLocal) {
				aliada = true;
			} else {
				aliada = false;
			}
			this.inspecionarPeca(peca,tipo);

			if (tipo.getNum() > TipoPeca.BASE.getNum() && tipo.getNum() < TipoPeca.VILA.getNum()
					&& aliada && turno && !exausto) {
				this.recrutarUnidade(pos);
			} else if (tipo.getNum() > TipoPeca.VILA.getNum()) {
			    ArrayList<Quadrado> quadsAlcance;
			    if (aliada && !exausto && turno) {
                    quadsAlcance = this.realceQuadrados(pos, pos,1, 1, true);
                } else {
			        quadsAlcance = this.realceQuadrados(pos, pos,1, 3, true);
                }
				interfaceJogador.setEstadoPartida("ReceberClique");
				try {
					this.semaforoInformacoesEnviadas.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int destino = this.posicaoEnviada;
				Quadrado quadD = this.quadrados.get(destino);
				boolean dentroAlcance = quadsAlcance.contains(quadD);

				if (turno && aliada && dentroAlcance && !exausto) {
					Peca pecaDestino = quadD.getPeca();

					if (pecaDestino == null || pecaDestino == peca) {
						ArrayList<Integer> estrutNeutras = null;
						boolean podeCapturar = ((Unidade) peca).podeCapturar();
						if (podeCapturar) {
							estrutNeutras = this.verificarEstruturasAdjacentes(destino);
						}
						int cargaG = 0;
						if (tipo.getNum() >= TipoPeca.MERCIA.getNum()) {
							cargaG = ((Comandante) peca).getCargaGroove();
						}
						ArrayList<Integer> estrutAliadas = null;
						if (peca.getPV() != 100 && jogadorLocal.getOuro() > 0) {
							estrutAliadas = this.verificarEstruturasPossiveis(destino);
						}
						quadsAlcance = this.verificarQuadradosAlcance(pos,destino,2);
						String acao = this.verificarAcaoEspecial(destino,peca);

						boolean podeAtacar = true;
						if (tipo == TipoPeca.BALISTA || tipo == TipoPeca.TREBUCHET || tipo == TipoPeca.MOSQUETEIRO) {
							if (pos != destino) {
								podeAtacar = false;
							} else if (tipo == TipoPeca.MOSQUETEIRO) {
								int balas = ((Mosqueteiro)peca).getBalas();
								if (balas == 0) {
									podeAtacar = false;
								}
							}
						}

						boolean temInimigo = false;
						int iteracao = 0;
						Quadrado quadAtual;
						Peca pecaAtual;
						Jogador donoAtual;
						int [] danoBasePeca = tipo.getDanoBase();

						while (iteracao < quadsAlcance.size() && !temInimigo && podeAtacar) {
							quadAtual = quadsAlcance.get(iteracao);
							pecaAtual = quadAtual.getPeca();
							if (pecaAtual != null) {
								donoAtual = pecaAtual.getDono();
								if (donoAtual == jogadorRemoto) {
									TipoPeca tipoAtual = pecaAtual.getTipo();
									int num = tipoAtual.getNum();
									if (danoBasePeca[num] > 0) {
										temInimigo = true;
									}
								}
							}
							iteracao++;
						}

						if (temInimigo) {
							listaAcoes.add("Atacar");
						}

						if (estrutNeutras != null && estrutNeutras.size() > 0) {
							listaAcoes.add("Capturar");
						}

						if (estrutAliadas != null && estrutAliadas.size() > 0) {
							listaAcoes.add("Reforcar");
						}

						if (cargaG == 100) {
							listaAcoes.add("Groove");
						}

						if (acao != null) {
							listaAcoes.add(acao);
						}

						this.posOrigem = pos;
						this.posDestino = destino;
						interfaceJogador.mostrarOpcoes(listaAcoes,destino);
					}
				}
			}
		}
	}

	public void trocarTurno() {
		jogadorLocal.setTurno(true);
		ArrayList<Peca> pecasLocal = jogadorLocal.getPecas();
        int ouro = jogadorLocal.getOuro() + 100;

		for (int i = 0; i < pecasLocal.size(); i++) {
		    Peca pecaAtual = pecasLocal.get(i);
		    int pos = pecaAtual.getPosicao();
		    TipoPeca tipo = pecaAtual.getTipo();
		    pecaAtual.setExausto(false);
		    interfaceJogador.exaustarPeca(pos,false);

		    if (tipo.getNum() <= TipoPeca.VILA.getNum() || tipo.getNum() >=TipoPeca.MERCIA.getNum()) {
		        int novoPV = pecaAtual.ajustePV(5);
		        interfaceJogador.atualizarPV(pos,novoPV);

		        if (tipo == TipoPeca.VILA) {
		            ouro += 100;
                } else if (tipo.getNum() >= TipoPeca.MERCIA.getNum()) {
		            Comandante comandante = (Comandante) pecaAtual;
                    int taxa = comandante.getTaxaDeCarga();
                    int valor = comandante.getCargaGroove() + taxa;
                    comandante.setCargaGroove(valor);
                }
            }
        }

        jogadorLocal.setOuro(ouro);
        interfaceJogador.atualizarOuro(Integer.toString(ouro));

		ArrayList<Peca> pecasRemoto = jogadorRemoto.getPecas();

		for(int i = 0; i < pecasRemoto.size(); i++) {
		    Peca pecaAtual = pecasRemoto.get(i);
		    int pos = pecaAtual.getPosicao();
		    pecaAtual.setExausto(false);
		    interfaceJogador.exaustarPeca(pos,false);
        }

		interfaceJogador.atualizarTurno(jogadorLocal.getNome());
		interfaceJogador.habilitarBotaoTurno(true);
	}

	/**
	 * 
	 * @param pos
	 * @param peca
	 */
	public String verificarAcaoEspecial(int pos, Peca peca) {
		TipoPeca tipo = peca.getTipo();
		String acao = null;
		int ouro = 0;
		if (tipo == TipoPeca.BRUXA || tipo == TipoPeca.MAGO) {
			ouro = jogadorLocal.getOuro();
			if(ouro >= 300) {
				if(tipo == TipoPeca.BRUXA) {
					acao = "Enfeiticar";
				} else {
					acao = "Curar";
				}
			}
		} else if (tipo == TipoPeca.LADRAO){
			Ladrao ladrao = (Ladrao) peca;
			ouro = ladrao.getOuroRoubado();
			ArrayList<Quadrado> adjacentes = this.obterQuadradosAdjacentes(pos);
			Peca pecaQuad;
			Quadrado quad;
			Jogador dono;
			TipoPeca tipoPeca;
			for(int i = 0; i < adjacentes.size(); i++) {
				quad = adjacentes.get(i);
				pecaQuad = quad.getPeca();
				if(pecaQuad != null) {
					dono = pecaQuad.getDono();
					tipoPeca = pecaQuad.getTipo();
					if (ouro > 0 && dono == jogadorLocal && tipoPeca == TipoPeca.ESCONDERIJO) {
						return "Depositar";
					} else if (ouro == 0 && dono == jogadorRemoto && tipoPeca.getNum() <= tipoPeca.VILA.getNum()) {
						return "Roubar";
					}
				}
			}
		} else if (tipo == TipoPeca.MOSQUETEIRO) {
			Mosqueteiro mosq = (Mosqueteiro) peca;
			if (mosq.getBalas() < 3) {
				acao = "Recarregar";
			}
		}
		return acao;
	}

	/**
	 *
	 * @param posIni
	 * @param pos
	 */
	public void atacar(int posIni,int pos) {
		ArrayList<Quadrado> quadradosNoAlcance = this.realceQuadrados(posIni,pos,2,2,true);
		interfaceJogador.setEstadoPartida("Atacar");
		try {
			this.semaforoInformacoesEnviadas.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Quadrado quadradoEnviado = quadrados.get(posicaoEnviada);
		if (quadradosNoAlcance.contains(quadradoEnviado)) {
			Peca alvo = quadradoEnviado.getPeca();
			if (alvo != null) {
				Jogador dono = alvo.getDono();
				if (dono != jogadorLocal) {
					Quadrado quadAtacante = quadrados.get(posIni);
					Peca atacante = quadAtacante.getPeca();
					TipoPeca tipoAtacante = atacante.getTipo();
					TipoPeca tipoAlvo = alvo.getTipo();
					if (tipoAtacante.getDanoBase()[tipoAlvo.getNum()] > 0) {
						int[] dano = this.calcularDano(posicaoEnviada);
						this.realizarAtaque(posIni,pos,posicaoEnviada,-dano[0],-dano[1]);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param pos
	 */
	public void groove(int posIni,int pos) {
		ArrayList<Quadrado> quadradosRealcados = this.realceQuadrados(posIni,pos,3,0,true);
		interfaceJogador.setEstadoPartida("Groove");

		try {
			this.semaforoInformacoesEnviadas.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Quadrado quadradoEnviado = this.quadrados.get(posicaoEnviada);

		if (quadradosRealcados.contains(quadradoEnviado)) {
			Quadrado quadComandante = this.quadrados.get(posIni);
			Peca comandante = quadComandante.getPeca();
			TipoPeca tipoComandante = comandante.getTipo();
			boolean grooveValido = true;
			if (tipoComandante == TipoPeca.SIGRID) {
				Quadrado quadAlvo = this.quadrados.get(posicaoEnviada);
				Peca alvo = quadAlvo.getPeca();
				if (alvo != null) {
					Jogador donoAlvo = alvo.getDono();
					TipoPeca tipoAlvo = alvo.getTipo();
					if (donoAlvo == comandante.getDono() || tipoAlvo.getNum() <= TipoPeca.VILA.getNum() || tipoAlvo.getNum() >= TipoPeca.MERCIA.getNum()) {
						grooveValido = false;
					}
				} else {
					grooveValido = false;
				}
			} else if (tipoComandante == TipoPeca.VALDER) {
				Quadrado quadAlvo = this.quadrados.get(posicaoEnviada);
				Peca alvo = quadAlvo.getPeca();
				int tipoTerreno = quadAlvo.getTipoTerreno();
				if (alvo != null || pos == posicaoEnviada || tipoTerreno == 6) {
					grooveValido = false;
				}
			}

			if (grooveValido) {
				this.realizarGroove(posIni, pos, posicaoEnviada);
			}
		}
	}

	/**
	 *
	 * @param posAlvo
	 */
	public void previaAreaDeEfeito(int posAlvo, int tipoRealce) {
		this.realceQuadrados(this.posOrigem,posAlvo,tipoRealce,1, false);
	}

	/**
	 * 
	 * @param pos
	 */
	public ArrayList<Integer> verificarEstruturasAdjacentes(int pos) {
		ArrayList<Quadrado> adjacentes = this.obterQuadradosAdjacentes(pos);
		ArrayList<Integer> neutrasAdjacentes = new ArrayList<>();
		Peca pecaAtual;
		TipoPeca tipoPecaAtual;
		Jogador donoAtual;
		for(Quadrado quad: adjacentes) {
			pecaAtual = quad.getPeca();
			if (pecaAtual != null) {
				tipoPecaAtual = pecaAtual.getTipo();
				if (tipoPecaAtual.getNum() > TipoPeca.BASE.getNum() && tipoPecaAtual.getNum() <= TipoPeca.VILA.getNum()) {
					donoAtual = pecaAtual.getDono();
					if (donoAtual == null) {
						neutrasAdjacentes.add(quadrados.indexOf(quad));
					}
				}
			}
		}
		return neutrasAdjacentes;
	}

	/**
	 *
	 * @param pos
	 */
	public ArrayList<Integer> verificarEstruturasPossiveis(int pos) {
		ArrayList<Quadrado> adjacentes = this.obterQuadradosAdjacentes(pos);
		ArrayList<Integer> estruturasPossiveis = new ArrayList<Integer>();
		Peca estrutura;
		TipoPeca tipo;

		for (Quadrado quad: adjacentes) {
			estrutura = quad.getPeca();

			if (estrutura != null) {
				tipo = estrutura.getTipo();

				if(tipo.getNum() > TipoPeca.BASE.getNum() && tipo.getNum() <= TipoPeca.VILA.getNum()) {
					Jogador dono = estrutura.getDono();
					int pv = estrutura.getPV();

					if (dono == jogadorLocal && pv >= 2) {
						int indice = quadrados.indexOf(quad);
						estruturasPossiveis.add(indice);
					}
				}
			}
		}
		return estruturasPossiveis;
	}

	/**
	 * 
	 * @param pos
	 */
	public ArrayList<Integer> verificarPosicoesRecrutamento(int pos) {
		ArrayList<Quadrado> adjacentes = obterQuadradosAdjacentes(pos);
		ArrayList<Integer> quadradosLivres = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			Quadrado quadAtual = adjacentes.get(i);
			Peca pecaAtual = quadAtual.getPeca();
			if (pecaAtual == null) {
				int index = this.quadrados.indexOf(quadAtual);
				quadradosLivres.add(index);
			}
		}

		return quadradosLivres;
	}

	/**
	 *
	 * @param posIni
	 * @param atual
	 * @param max
	 * @param tipoAlcance
	 * @param unidade
	 */
	public ArrayList<Quadrado> verificarQuadradosAdjacentes(int posIni, int atual, int max, int tipoAlcance, Unidade unidade) {
		ArrayList<Quadrado> quadradosAlcance = new ArrayList<Quadrado>();
		ArrayList<Quadrado> adjacentes = this.obterQuadradosAdjacentes(posIni);
		Quadrado quadradoAtual;
		int visitado;
		for(int i = 0; i < adjacentes.size(); i++) {
			quadradoAtual = adjacentes.get(i);
			visitado = quadradoAtual.getVisitado();

			if(visitado == 0 || atual < visitado && visitado <= max) {

				int proximo;
				boolean pecaNoQuadrado = false;

				if(tipoAlcance == 1) {
					int tipoTerreno = quadradoAtual.getTipoTerreno();
					int custoMovimento = unidade.obterCustoMovimento(tipoTerreno);
					proximo = atual + custoMovimento;
					Peca pecaAtual = quadradoAtual.getPeca();
					if (pecaAtual != null) {
						Jogador donoAtual = pecaAtual.getDono();
						if (donoAtual != unidade.getDono() && donoAtual != null) {
							proximo = max + 1;
						}
						pecaNoQuadrado = true;
					}
				} else {
					proximo = atual + 1;
				}

				if(proximo <= max) {
					quadradoAtual.setVisitado(proximo);
					if (!pecaNoQuadrado && visitado == 0) {
						quadradosAlcance.add(quadradoAtual);
					} else if (pecaNoQuadrado) {
						quadradoAtual.setVisitado(0);
					}
					int indice = quadrados.indexOf(quadradoAtual);
					quadradosAlcance.addAll(this.verificarQuadradosAdjacentes(indice,proximo,max,tipoAlcance,unidade));
				}
			}
		}

		return quadradosAlcance;
	}

	/**
	 *
	 * @param posIni
	 * @param atual
	 * @param max
	 * @param direcao
	 */
	private ArrayList<Quadrado> verificarQuadradosAdjacentesAtaqueMosqueteiro(int posIni, int atual, int max, int direcao) {
		ArrayList<Quadrado> quadradosAlcance = new ArrayList<Quadrado>();
		ArrayList<Quadrado> adjacentes = this.obterQuadradosAdjacentes(posIni);
		Quadrado quadradoAtual;
		int visitado;
		int proximo = atual + 1;
		for(int i = 0; i < adjacentes.size(); i++) {
			quadradoAtual = adjacentes.get(i);
			visitado = quadradoAtual.getVisitado();

			if(visitado == 0) {
				if(proximo <= max) {
					if (quadradoAtual.getTipoTerreno() != 3) {
						quadradoAtual.setVisitado(proximo);
						quadradosAlcance.add(quadradoAtual);
					}
					int indice = quadrados.indexOf(quadradoAtual);
					if (indice == posIni + direcao) {
						quadradosAlcance.addAll(this.verificarQuadradosAdjacentesAtaqueMosqueteiro(indice, proximo, max, direcao));
					} else if (direcao == 0) {
						quadradosAlcance.addAll(this.verificarQuadradosAdjacentesAtaqueMosqueteiro(indice, proximo, max, indice - posIni));
					}
				}
			}
		}

		return quadradosAlcance;
	}

	/**
	 * 
	 * @param pos
	 * @param tipoAlcance
	 */
	public ArrayList<Quadrado> verificarQuadradosAlcance(int posIni, int pos, int tipoAlcance) {
		Quadrado quadPeca = quadrados.get(posIni);
		Unidade unidade = (Unidade) quadPeca.getPeca();
		TipoPeca tipoPeca = unidade.getTipo();
		int alcance = 0;
		boolean ehAtaque = true;
		boolean areaMercia = false;
		switch (tipoAlcance) {
			case 1:
				alcance = unidade.getAlcanceMovimento();
				ehAtaque = false;
				break;
			case 2:
				alcance = unidade.getAlcanceAtaque();
				break;
			case 3:
				alcance = ((Comandante) unidade).getAlcanceGroove();
				break;
			case 4:
				alcance = ((Comandante) unidade).getAreaGroove();
				ehAtaque = false;
				if(tipoPeca == TipoPeca.MERCIA){
					areaMercia = true;
				}
				break;
			case 5:
				alcance = unidade.getAlcanceEspecial();
				ehAtaque = false;
				break;
			default:
				if (tipoPeca == TipoPeca.MAGO) {
					ehAtaque = false;
					alcance = 1;
				} else if (tipoPeca == TipoPeca.BRUXA) {
					ehAtaque = true;
					alcance = 4;
				}
		}
		Quadrado quadInicial = quadrados.get(pos);
		quadInicial.setVisitado(unidade.getAlcanceMovimento() + 1);
		ArrayList<Quadrado> quadradosAlcance;
		if (tipoAlcance == 2 && tipoPeca == TipoPeca.MOSQUETEIRO) {
			quadradosAlcance = this.verificarQuadradosAdjacentesAtaqueMosqueteiro(pos,0,alcance,0);
		} else {
			quadradosAlcance = this.verificarQuadradosAdjacentes(pos,0,alcance,tipoAlcance,unidade);
		}

		if(!ehAtaque || alcance == 0 || areaMercia) {
			quadradosAlcance.add(quadInicial);
		} else {
			quadInicial.setVisitado(0);
		}

		for(Quadrado quadAlc: quadradosAlcance){
			quadAlc.setVisitado(0);
		}

		if ((tipoPeca == TipoPeca.BALISTA || tipoPeca == TipoPeca.TREBUCHET) && tipoAlcance == 2) {
			ArrayList<Quadrado> adjacentes = this.obterQuadradosAdjacentes(pos);
			quadradosAlcance.removeAll(adjacentes);
		}

		return quadradosAlcance;
	}

	/**
	 * 
	 * @param posIni
	 * @param pos
	 * @param tipoAcao
	 */
	public void acaoEspecial(int posIni, int pos, String tipoAcao) {
		if (!tipoAcao.equals("Recarregar")) {
			ArrayList<Integer> posicoesValidas = new ArrayList<>();
			if (tipoAcao.equals("Curar") || tipoAcao.equals("Enfeiticar")) {
				ArrayList<Quadrado> quadradosRealcados = this.realceQuadrados(posIni, pos, 5, 0, true);
				for (Quadrado quad : quadradosRealcados) {
					posicoesValidas.add(this.quadrados.indexOf(quad));
				}
				interfaceJogador.setEstadoPartida("AcaoEspecial");

			} else {
				ArrayList<Quadrado> adjacentes = this.obterQuadradosAdjacentes(pos);
				for (int i = 0; i < adjacentes.size(); i++) {
					Quadrado quad = adjacentes.get(i);
					Peca peca = quad.getPeca();
					if (peca != null) {
						Jogador dono = peca.getDono();
						TipoPeca tipo = peca.getTipo();
						if (tipoAcao.equals("Roubar") && dono != jogadorLocal && tipo.getNum() <= TipoPeca.VILA.getNum() ||
								tipoAcao.equals("Depositar") && dono == jogadorLocal && tipo == TipoPeca.ESCONDERIJO) {
							posicoesValidas.add(quadrados.indexOf(quad));
						}
					}
				}

				interfaceJogador.realcarPosicao(posicoesValidas, 0, true);
				interfaceJogador.setEstadoPartida("ReceberClique");
			}

			try {
				this.semaforoInformacoesEnviadas.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (posicoesValidas.contains(this.posicaoEnviada)) {
				this.realizarAcaoEspecial(posIni, pos, this.posicaoEnviada, tipoAcao);
			}

		} else {
			this.realizarAcaoEspecial(posIni,pos, 0, tipoAcao);
		}
	}

	/**
	 * 
	 * @param pos
	 */
	public void enviarPosicao(int pos) {
		this.posicaoEnviada = pos;
		this.semaforoInformacoesEnviadas.release();
	}

	public void enviarTipoPeca(String nomeTipoPeca) {
		if (nomeTipoPeca != null) {
			this.tipoPecaEnviado = TipoPeca.valueOf(nomeTipoPeca.toUpperCase());
		} else {
			this.tipoPecaEnviado = null;
		}
		this.semaforoInformacoesEnviadas.release();
	}

	/**
	 * 
	 * @param posIni
	 * @param pos
	 * @param posAlvo
	 * @param tipoJogada
	 */
	public void realizarAcaoEspecial(int posIni, int pos, int posAlvo, String tipoJogada) {
		boolean turno = jogadorLocal.getTurno();
		Jogador jogadorVez = this.jogadorRemoto;
		if (turno) {
			JogadaGenerica jogada = new JogadaGenerica(posIni,pos,posAlvo,tipoJogada,null,0,0);
			interfaceNetgames.enviarJogada(jogada);
			jogadorVez = this.jogadorLocal;
		}
		moverPeca(posIni,pos);
		Quadrado quadPeca = this.quadrados.get(pos);
		Peca peca = quadPeca.getPeca();

		if (tipoJogada.equals("Curar")) {
			ArrayList<Quadrado> quadradosAlcance = this.verificarQuadradosAlcance(pos, posAlvo, 6);
			for (int i = 0; i < quadradosAlcance.size(); i++) {
				Quadrado quadAtual = quadradosAlcance.get(i);
				Peca pecaAtual = quadAtual.getPeca();
				if (pecaAtual != null) {
					Jogador dono = pecaAtual.getDono();
					if (dono == jogadorVez) {
						TipoPeca tipoPecaAtual = pecaAtual.getTipo();
						if (tipoPecaAtual.getNum() >= TipoPeca.SOLDADO.getNum()) {
							int novoPV = pecaAtual.ajustePV(20);
							interfaceJogador.atualizarPV(this.quadrados.indexOf(quadAtual), novoPV);
						}
					}
				}
			}

			int ouro = jogadorVez.getOuro();
			jogadorVez.setOuro(ouro - 300);

			if (jogadorVez == jogadorLocal) {
				interfaceJogador.atualizarOuro(Integer.toString(ouro - 300));
			}

		} else if (tipoJogada.equals("Enfeiticar")) {

				ArrayList<Quadrado> quadradosAlcance = this.verificarQuadradosAlcance(pos,posAlvo,6);
				for (int i = 0; i < quadradosAlcance.size(); i++) {
					Quadrado quadAtual = quadradosAlcance.get(i);
					Peca pecaAtual = quadAtual.getPeca();
					if (pecaAtual != null) {
						Jogador dono = pecaAtual.getDono();
						if (dono != jogadorVez) {
							TipoPeca tipoPecaAtual = pecaAtual.getTipo();
							if (tipoPecaAtual.getNum() >= TipoPeca.SOLDADO.getNum()) {
								int novoPV = pecaAtual.ajustePV(-10);
								interfaceJogador.atualizarPV(this.quadrados.indexOf(quadAtual), novoPV);
							}
						}
					}
				}

				int ouro = jogadorVez.getOuro();
				jogadorVez.setOuro(ouro - 300);

				if (jogadorVez == this.jogadorLocal) {
					interfaceJogador.atualizarOuro(Integer.toString(ouro - 300));
				}
		} else if (tipoJogada.equals("Roubar")) {
			Quadrado quadEstrut = this.quadrados.get(posAlvo);
			Peca estrutura = quadEstrut.getPeca();
			TipoPeca tipo = estrutura.getTipo();
			int ouroRoubado = 1000;
			if (tipo != TipoPeca.BASE) {
				estrutura.setDono(null);
				((Estrutura) estrutura).setPV(0);
				ouroRoubado = 300;
			}

			if (jogadorVez == this.jogadorRemoto) {
				int ouroAtual = this.jogadorLocal.getOuro() - ouroRoubado;
				this.jogadorLocal.setOuro(ouroAtual);
				ArrayList<Peca> pecas = this.jogadorLocal.getPecas();
				pecas.remove(estrutura);
				interfaceJogador.atualizarOuro(Integer.toString(ouroAtual));
			} else {
				int ouroAtual = this.jogadorRemoto.getOuro() - ouroRoubado;
				this.jogadorRemoto.setOuro(ouroAtual);
				ArrayList<Peca> pecas = this.jogadorRemoto.getPecas();
				pecas.remove(estrutura);
			}
			interfaceJogador.posicionarPeca(posAlvo,tipo,0,0);

			Ladrao ladrao = (Ladrao) peca;
			ladrao.setAlcanceMovimento(3);
			ladrao.setOuroRoubado(ouroRoubado);

		} else if (tipoJogada.equals("Depositar")) {
			Ladrao ladrao = (Ladrao) peca;
			int ouroRoubado = ladrao.getOuroRoubado();

			int ouroAtual = jogadorVez.getOuro() + ouroRoubado;
			jogadorVez.setOuro(ouroAtual);

			if (jogadorVez == this.jogadorLocal) {
				interfaceJogador.atualizarOuro(Integer.toString(ouroAtual));
			}

			ladrao.setOuroRoubado(0);
			ladrao.setAlcanceMovimento(6);
		} else {
			Mosqueteiro mosqueteiro = (Mosqueteiro) peca;
			mosqueteiro.maximizarBalas();
		}

		peca.setExausto(true);
		interfaceJogador.exaustarPeca(pos,true);
	}

	/**
	 * 
	 * @param posIni
	 */
	public void recrutarUnidade(int posIni) {
		int ouro = this.jogadorLocal.getOuro();
		Peca peca = quadrados.get(posIni).getPeca();
		TipoPeca tipo = peca.getTipo();
		TipoPeca[] tipoPecas = TipoPeca.values();
		int unidadesCompraveis = 0;
		String tipoTela = "";
		if(tipo == TipoPeca.QUARTEL){
			int i = TipoPeca.SOLDADO.getNum();
			boolean maxAtingido = false;
			while (i <= TipoPeca.GIGANTE.getNum() && !maxAtingido){
				if(ouro >= tipoPecas[i].getCusto()) {
					unidadesCompraveis++;
				} else {
					maxAtingido = true;
				}
				i++;
			}
			tipoTela = "TelaRecrutamento1";
		} else if (tipo == TipoPeca.TORRE) {
			int i = TipoPeca.HARPIA.getNum();
			boolean maxAtingido = false;
			while (i <= TipoPeca.DRAGAO.getNum() && !maxAtingido){
				if(ouro >= tipoPecas[i].getCusto()) {
					unidadesCompraveis++;
				} else {
					maxAtingido = true;
				}
				i++;
			}
			tipoTela = "TelaRecrutamento2";
		} else {
			int i = TipoPeca.LADRAO.getNum();
			boolean maxAtingido = false;
			while (i <= TipoPeca.MOSQUETEIRO.getNum() && !maxAtingido){
				if(ouro >= tipoPecas[i].getCusto()) {
					unidadesCompraveis++;
				} else {
					maxAtingido = true;
				}
				i++;
			}
			tipoTela = "TelaRecrutamento3";
		}

		interfaceJogador.abrirTelaRecrutamento(tipoTela,ouro,unidadesCompraveis);

		try {
			this.semaforoInformacoesEnviadas.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (tipoPecaEnviado != null) {

			ArrayList<Integer> posicoesPossiveis = this.verificarPosicoesRecrutamento(posIni);
			interfaceJogador.realcarPosicao(posicoesPossiveis, 0, true);
			interfaceJogador.setEstadoPartida("ReceberClique");

			try {
				this.semaforoInformacoesEnviadas.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (posicoesPossiveis.contains(posicaoEnviada)) {
				this.realizarRecrutamento(posIni,posicaoEnviada, tipoPecaEnviado);
			}
		}
	}

	public Jogador getJogadorLocal() {
		return this.jogadorLocal;
	}

	public Jogador getJogadorRemoto() {
		return this.jogadorRemoto;
	}

	/**
	 * 
	 * @param posIni
	 * @param pos
	 */
	public void reforcarUnidade(int posIni, int pos) {
		ArrayList<Integer> estrutAliadas = this.verificarEstruturasPossiveis(pos);
		interfaceJogador.realcarPosicao(estrutAliadas,0,true);

		interfaceJogador.setEstadoPartida("ReceberClique");
		try {
			this.semaforoInformacoesEnviadas.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (estrutAliadas.contains(this.posicaoEnviada)) {
			this.realizarReforco(posIni,pos,this.posicaoEnviada);
		}
	}

	/**
	 * 
	 * @param posIni
	 * @param pos
	 */
	public void capturarEstrutura(int posIni, int pos) {
		ArrayList<Integer> estruturas = verificarEstruturasAdjacentes(pos);
		interfaceJogador.realcarPosicao(estruturas,0,true);
		interfaceJogador.setEstadoPartida("ReceberClique");
		try {
			this.semaforoInformacoesEnviadas.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (estruturas.contains(this.posicaoEnviada)) {
			realizarCaptura(posIni,pos, posicaoEnviada);
		}
	}

	/**
	 * 
	 * @param pos
	 */
	public ArrayList<Quadrado> obterQuadradosAdjacentes(int pos) {
		ArrayList<Quadrado> adjacentes = new ArrayList<Quadrado>();
		Quadrado quad;
		if (pos % 16 != 0) {
			quad = quadrados.get(pos - 1);
			adjacentes.add(quad);
		}

		if (pos - 16 >= 0) {
			quad = quadrados.get(pos - 16);
			adjacentes.add(quad);
		}

		if (pos % 16 != 15) {
			quad = quadrados.get(pos + 1);
			adjacentes.add(quad);
		}

		if (pos + 16 <= quadrados.size() - 1) {
			quad = quadrados.get(pos + 16);
			adjacentes.add(quad);
		}
		return adjacentes;
	}

	public void enviarComandante(TipoPeca comandanteJogadorLocal, int posicao) {
		Comandante comandanteLocal;
		int indiceComandante;
		if (posicao == 1) {
			indiceComandante = 262;
		} else {
			indiceComandante = 9;
		}

		if (comandanteJogadorLocal == TipoPeca.MERCIA) {
			comandanteLocal = new Mercia(jogadorLocal,indiceComandante);
		} else if (comandanteJogadorLocal == TipoPeca.SIGRID) {
			comandanteLocal = new Sigrid(jogadorLocal,indiceComandante);
		} else if (comandanteJogadorLocal == TipoPeca.CAESAR) {
			comandanteLocal = new Caesar(jogadorLocal,indiceComandante);
		} else {
			comandanteLocal = new Valder(jogadorLocal,indiceComandante);
		}

		jogadorLocal.addPeca(comandanteLocal);
		interfaceJogador.posicionarPeca(indiceComandante,comandanteLocal.getTipo(),100,posicao);
		quadrados.get(indiceComandante).posicionarPeca(comandanteLocal);

		JogadaGenerica envioDeComandante = new JogadaGenerica(0,0,0, "Receber Faccao",
				comandanteJogadorLocal,0,0);
		interfaceNetgames.enviarJogada(envioDeComandante);
	}

	public void inspecionarPeca(Peca peca, TipoPeca tipoPeca) {
		String descricao = peca.obterDescricao();
		if (tipoPeca.getNum() >= TipoPeca.SOLDADO.getNum()) {
			String fileName = tipoPeca.name().toLowerCase();
			fileName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1) + ".txt";
			Scanner scan;
			scan = new Scanner(interfaceJogador.getClass().getResourceAsStream("DescricoesPecas/" + fileName),"utf-8");
			descricao += "\n";
			while (scan.hasNextLine()){
				descricao += scan.nextLine() + "\n";
			}
		}

		interfaceJogador.escreverAreaTextoLateral(descricao);
	}

    public Semaphore getSemaforoPosicaoEnviada() {
        return semaforoInformacoesEnviadas;
    }
}