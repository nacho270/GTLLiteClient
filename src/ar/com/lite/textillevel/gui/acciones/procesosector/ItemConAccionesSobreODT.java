package ar.com.lite.textillevel.gui.acciones.procesosector;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.enums.EAvanceODT;

public class ItemConAccionesSobreODT extends JPanel {

	private static final long serialVersionUID = 1L;

	private OrdenDeTrabajo odt;
	
	private JLabel descLblODT;
	private JPanel panBotones;
	private JButton btnIniciarOrCancelar;
	private JButton btnFinalizar;
	private boolean modoIniciar;
	private ItemConAccionesSobreODTEventListener listener;

	public ItemConAccionesSobreODT(OrdenDeTrabajo odt, ItemConAccionesSobreODTEventListener listener) {
		this.odt = odt;
		this.listener = listener;

		setLayout(new FlowLayout());
		add(getLblDescODT());
		add(getPanBotones());
		
		setModoIniciar(true);
	}

	private JLabel getLblDescODT() {
		if(descLblODT == null) {
			descLblODT = new JLabel("<html><div style=\"font-size: 11px;font-family: sans-serif;\"><b>"+ odt.getCodigo() + "</b><div>"
					+"<div style=\"font-size: 8px;font-family: sans-serif;\">" + odt.getProductoArticulo() +"</div></html>");
			descLblODT.setBorder(null);
			descLblODT.setSize(new Dimension(330, 40));
			descLblODT.setPreferredSize(new Dimension(330, 40));
		}
		return descLblODT;
	}

	private JPanel getPanBotones() {
		if(panBotones == null) {
			panBotones = new JPanel();
			panBotones.setLayout(new FlowLayout());
			panBotones.add(getBtnIniciarOrCancelar());
			panBotones.add(getBtnFinalizar());

			panBotones.setSize(new Dimension(200, 35));
			panBotones.setPreferredSize(new Dimension(200, 35));
		}
		return panBotones;
	}
	
	private JButton getBtnIniciarOrCancelar() {
		if(btnIniciarOrCancelar == null) {
			btnIniciarOrCancelar = new JButton("INICIAR");
			btnIniciarOrCancelar.addActionListener(new ActionListener() {
 
				@Override
				public void actionPerformed(ActionEvent e) {
					if(modoIniciar) {
						iniciar();
					} else {
						cancelar();
					}
				}

			});
		}
		return btnIniciarOrCancelar;
	}

	private JButton getBtnFinalizar() {
		if(btnFinalizar == null) {
			btnFinalizar = new JButton("FINALIZAR");
			btnFinalizar.setEnabled(false);
			btnFinalizar.addActionListener(new ActionListener() {
		
				@Override
				public void actionPerformed(ActionEvent e) {
					listener.itemODTChangeAvance(new ItemConAccionesSobreODTEvent(ItemConAccionesSobreODT.this), EAvanceODT.FINALIZADO);
				}

			});
		}
		return btnFinalizar;
	}

	private void setModoIniciar(boolean modoIniciar) {
		this.modoIniciar = modoIniciar;
	}

	public void esperar() {
		getBtnIniciarOrCancelar().setEnabled(false);
		getBtnFinalizar().setEnabled(false);
	}

	public void ready() {
		getBtnIniciarOrCancelar().setEnabled(true);
		getBtnFinalizar().setEnabled(false);
	}

	public OrdenDeTrabajo getOdt() {
		return odt;
	}

	private void iniciar() {
		enProceso();
		listener.itemODTChangeAvance(new ItemConAccionesSobreODTEvent(this), EAvanceODT.EN_PROCESO);
	}

	public void enProceso() {
		getBtnIniciarOrCancelar().setText("CANCELAR");
		getBtnFinalizar().setEnabled(true);
		setModoIniciar(false);
	}

	private void cancelar() {
		getBtnIniciarOrCancelar().setText("INICIAR");
		setModoIniciar(true);
		listener.itemODTChangeAvance(new ItemConAccionesSobreODTEvent(this), EAvanceODT.POR_COMENZAR);
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof ItemConAccionesSobreODT)) {
			return false;
		} else {
			return getOdt().equals(((ItemConAccionesSobreODT)o).getOdt());
		}
	}
	
}