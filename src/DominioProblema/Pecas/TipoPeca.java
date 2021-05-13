package DominioProblema.Pecas;

public enum TipoPeca {
	BASE(0, "/images/base", 0, new int[]{0,0, 0, 0, 0, 30, 35, 30, 35, 30, 10, 0, 0, 5, 0, 40, 30, 0, 5, 5, 5, 5, 5}),
	QUARTEL(1, "/images/quartel", 0,new int[]{0,0, 0, 0, 0, 30, 35, 30, 35, 30, 10, 0, 0, 5, 0, 40, 30, 0, 5, 5, 5, 5, 5}),
	TORRE(2, "/images/torre", 0,new int[]{0,0, 0, 0, 0, 30, 35, 30, 35, 30, 10, 0, 0, 5, 0, 40, 30, 0, 5, 5, 5, 5, 5}),
	ESCONDERIJO(3, "/images/esconderijo", 0,new int[]{0,0, 0, 0, 0, 30, 35, 30, 35, 30, 10, 0, 0, 5, 0, 40, 30, 0, 5, 5, 5, 5, 5}),
	VILA(4, "/images/vila",0,new int[]{0,0, 0, 0, 0, 30, 35, 30, 35, 30, 10, 0, 0, 5, 0, 40, 30, 0, 5, 5, 5, 5, 5}),
	SOLDADO(5, "/images/soldado",100, new int[]{35, 35, 35, 35, 35, 55, 45, 35, 55, 65, 15, 35, 30, 5, 95, 65, 0, 0, 0, 10, 10, 10, 10}),
	CAO(6, "/images/cao",150, new int[]{20, 20, 20, 20, 20, 75, 65, 45, 55, 75, 10, 40, 35, 5, 85, 110, 0, 0, 0, 15, 15, 15, 15}),
	LANCEIRO(7, "/images/lanceiro",250, new int[]{50, 50, 50, 50, 50, 75, 80, 55, 65, 70, 70, 55, 50, 10, 75, 85, 0, 0, 0, 15, 15, 15, 15}),
	MAGO(8, "/images/mago",400, new int[]{35, 35, 35, 35, 35, 85, 80, 75, 35, 85, 30, 30, 45, 20, 85, 95, 140, 130, 80, 20, 20, 20, 20}),
	ARQUEIRO(9, "/images/arqueiro",500, new int[]{20, 20, 20, 20, 20, 70, 75, 60, 75, 65, 40, 30, 25, 10, 70, 80, 30, 20, 10, 5, 5, 5, 5}),
	CAVALEIRO(10, "/images/cavaleiro",600, new int[]{65, 65, 65, 65, 65, 90, 120, 45, 110, 100, 55, 85, 80, 25, 90, 100, 0, 0, 0, 25, 25, 25, 25}),
	BALISTA(11, "/images/balista",800, new int[]{30, 30, 30, 30, 30, 40, 50, 30, 40, 30, 25, 20, 10, 10, 40, 50, 130, 95, 85, 10, 10, 10, 10}),
	TREBUCHET(12, "/images/trebuchet",1000, new int[]{85, 85, 85, 85, 85, 100, 110, 80, 90, 120, 80, 85, 80, 55, 100, 110, 0, 0, 0, 25, 25, 25, 25}),
	GIGANTE(13, "/images/gigante",1200, new int[]{85, 85, 85, 85, 85, 130, 150, 100, 110, 150, 85, 100, 105, 45, 130, 140, 0, 0, 0, 35, 35, 35, 35}),
	LADRAO(14, "/images/ladrao",400, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
	MOSQUETEIRO(15, "/images/mosqueteiro",750, new int[]{5, 5, 5, 5, 5, 120, 20, 100, 100, 85, 10, 5, 5, 5, 100, 85, 0, 0, 0, 5, 5, 5, 5}),
	HARPIA(16, "/images/harpia",600, new int[]{50, 50, 50, 50, 50, 65, 55, 45, 25, 75, 75, 35, 55, 10, 65, 75, 55, 30, 35, 15, 15, 15, 15}),
	BRUXA(17, "/images/bruxa",800, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 35, 35, 0, 0, 0, 0}),
	DRAGAO(18, "/images/dragao",1250, new int[]{65, 65, 65, 65, 65, 120, 150, 120, 65, 140, 100, 110, 105, 65, 120, 130, 0, 0, 0, 35, 35, 35, 35}),
	MERCIA(19, "/images/mercia",500, new int[]{70, 70, 70, 70, 70, 115, 115, 75, 80, 130, 55, 60, 55, 40, 95, 105, 0, 0, 0, 40, 40, 40, 40}),
	SIGRID(20, "/images/sigrid",500, new int[]{70, 70, 70, 70, 70, 115, 115, 75, 80, 130, 55, 60, 55, 40, 95, 105, 0, 0, 0, 40, 40, 40, 40}),
	VALDER(21, "/images/valder",500, new int[]{70, 70, 70, 70, 70, 115, 115, 75, 80, 130, 55, 60, 55, 40, 95, 105, 0, 0, 0, 40, 40, 40, 40}),
	CAESAR(22, "/images/caesar",500, new int[]{70, 70, 70, 70, 70, 115, 115, 75, 80, 130, 55, 60, 55, 40, 95, 105, 0, 0, 0, 40, 40, 40, 40});

	private final int num;
	private final String imagem;
	private final int custo;
	private final int[] danoBase;
	TipoPeca(int i, String imagem, int custo, int[] danoBase) {
		num = i;
		this.imagem = imagem;
		this.custo = custo;
		this.danoBase = danoBase;
	}

	public int getNum(){
		return num;
	}
	public String getImagem() {return imagem;}
	public int getCusto() {return custo;}
	public int[] getDanoBase() {return danoBase;}

}