package visao;

import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import modelo.Tabuleiro;

@SuppressWarnings("serial")
public class PainelTabuleiro extends JPanel{
	
	public PainelTabuleiro(Tabuleiro tabuleiro) {
		
		setLayout(new GridLayout(tabuleiro.getLinha(), tabuleiro.getColuna()));
		
		tabuleiro.paraCada(p -> add(new BotaoCampo(p)));
		
		tabuleiro.registrarObservadores(e -> {
			SwingUtilities.invokeLater(() -> {
				if(e.isGanhou()) {
					JOptionPane.showMessageDialog(this, "Ganhou!!! :)");
				} else {
					JOptionPane.showMessageDialog(this, "Perdeu....");
				}
			
				tabuleiro.reiniciar();
			});
		});
		
	}
	
}
