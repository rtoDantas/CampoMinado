package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObservador{

	private int linha;
	private int coluna;
	private int minas;
	
	List<Campo> campos = new ArrayList<>();
	private List<Consumer<ResultadoEvento>> observadores = new ArrayList<>();
	
	public Tabuleiro(int linhas, int coluna, int minas) {
		this.linha = linhas;
		this.coluna = coluna;
		this.minas = minas;
		
		gerarCampos();
		associarVizinhos();
		sortearMinas();
	}
	
	public void paraCada(Consumer<Campo> funcao) {
		campos.forEach(funcao);
	}
	
	public void registrarObservadores(Consumer<ResultadoEvento> observador) {
		observadores.add(observador);
	}
	
	public void notificarObservadores(boolean resultado) {
		observadores.stream()
			.forEach(obs -> obs.accept(new ResultadoEvento(resultado)));
	}
	
	public void abrir(int linha, int coluna) {
			campos.parallelStream()
				.filter(p -> p.getLinha() == linha && p.getColuna() == coluna)
				.findFirst()
				.ifPresent(p -> p.abrir());
	}

	public void alterarMarcacao(int linha, int coluna) {
		campos.parallelStream()
			.filter(p -> p.getLinha() == linha && p.getColuna() == coluna)
			.findFirst()
			.ifPresent(p -> p.alterarMarcacao());
	}
	
	private void gerarCampos() {
		for(int i = 0; i < linha; i++) {
			for(int j = 0; j < coluna; j++) {
				Campo campo = new Campo(i, j);
				campo.registrarObeservador(this);
				campos.add(campo);
			}
		}
	}
	
	private void associarVizinhos() {
		for(Campo c1: campos) {
			for(Campo c2: campos) {
				c1.adicionarVizinho(c2);
			}
		}
	}
	
	private void sortearMinas() {
		long minasArmadas = 0;
		Predicate<Campo> minado = c -> c.isMinado();
		
		do {
			int aleatorio = (int) (Math.random() * campos.size());
			campos.get(aleatorio).minar();
			minasArmadas = campos.stream().filter(minado).count();
		} while(minasArmadas < minas);
	}
	
	public boolean objetivoAlcancado() {
		return campos.stream().allMatch(c -> c.objetivoAlcancao());
	}
	
	public void reiniciar() {
		campos.stream().forEach(c -> c.reiniciar());
		sortearMinas();
	}
	
	public int getLinha() {
		return linha;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}

	public int getColuna() {
		return coluna;
	}

	public void setColuna(int coluna) {
		this.coluna = coluna;
	}

	@Override
	public void eventoOcorreu(Campo campo, CampoEvent evento) {
		if(evento == CampoEvent.EXPLODIR) {
			mostrarMina();
			System.out.println("Perdeu....");
			notificarObservadores(false);
		} else if(objetivoAlcancado()) {
			System.out.println("Ganhouu...");
			notificarObservadores(true);
		}
	}
	
	private void mostrarMina() {
		campos.stream()
			.filter(p -> p.isMinado())
			.filter(p -> !p.isMarcado())
			.forEach(p -> p.setAberto(true));
	}
	
}














