package ar.com.lite.textillevel.gui.acciones.procesosector;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ar.com.fwcommon.componentes.VerticalFlowLayout;
import ar.com.lite.textillevel.gui.util.GenericUtils;
import ar.com.lite.textillevel.util.GTLLiteRemoteService;
import ar.com.textillevel.modulos.odt.entidades.OrdenDeTrabajo;
import ar.com.textillevel.modulos.odt.enums.EAvanceODT;
import ar.com.textillevel.modulos.odt.enums.EEstadoODT;
import ar.com.textillevel.modulos.odt.enums.ESectorMaquina;

public class ItemConAccionesSobreODTList extends JPanel implements ItemConAccionesSobreODTEventListener {

	private static final long serialVersionUID = 1L;

	private JPanel list;
	private JScrollPane spList;
	private ESectorMaquina sector;
	private ItemConAccionesSobreODTEventListener listener;

	public ItemConAccionesSobreODTList(ESectorMaquina sector, ItemConAccionesSobreODTEventListener listener) {
		this.sector = sector;
		this.listener = listener; 
		spList = new JScrollPane(getList());
        setLayout(new GridBagLayout());
        add(spList, GenericUtils.createGridBagConstraints(0, 0,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1, 1, 1));
	}

	public void updateListadoODTs() {
		getList().removeAll();		
		List<OrdenDeTrabajo> all = GTLLiteRemoteService.getAllNoFinalizadasBySector(sector);
		boolean existeEnProceso = false;
		for(OrdenDeTrabajo odt : all) {
			if(odt.getAvance() == EAvanceODT.EN_PROCESO) {
				existeEnProceso = true;
				break;
			}
		}
        for(OrdenDeTrabajo odt : all) {
        	ItemConAccionesSobreODT comp = new ItemConAccionesSobreODT(odt, this);
        	if(odt.getAvance() == EAvanceODT.EN_PROCESO) {
        		comp.enProceso();
				listener.itemODTChangeAvance(new ItemConAccionesSobreODTEvent(comp), EAvanceODT.EN_PROCESO);
        	}
        	if(odt.getAvance() == EAvanceODT.POR_COMENZAR) {
        		if(existeEnProceso) {
        			comp.esperar();
        		} else {
        			comp.ready();
        		}
        	}
			getList().add(comp);
        }
        getList().updateUI();
	}

	private JPanel getList() {
		if(list == null) {
			list = new JPanel(new VerticalFlowLayout(FlowLayout.CENTER, 0, 0));
		}
		return list;
	}

	public void itemODTChangeAvance(ItemConAccionesSobreODTEvent evt, EAvanceODT avance) {
		GTLLiteRemoteService.grabarAndRegistrarCambioEstadoAndAvance(evt.getItem().getOdt(), EEstadoODT.EN_PROCESO, avance);
		updateListadoODTs();
		if(avance == EAvanceODT.EN_PROCESO) {
			scrollToTop();
		}
		listener.itemODTChangeAvance(evt, avance);
 	}

	public void scrollToBottom() {
		spList.getVerticalScrollBar().setValue(spList.getVerticalScrollBar().getMaximum());		
	}

	private void scrollToTop() {
		spList.getVerticalScrollBar().setValue(spList.getVerticalScrollBar().getMinimum());		
	}

}