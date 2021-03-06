package main;

import ar.com.textillevel.entidades.portal.UsuarioSistema;
import ar.com.textillevel.modulos.terminal.entidades.Terminal;

public class GTLLiteGlobalCache {
	
	private UsuarioSistema usuarioSistema;
	private Terminal terminalData;
	
	private static GTLLiteGlobalCache instance = new GTLLiteGlobalCache();

	public Terminal getTerminalData() {
		return terminalData;
	}

	public void setTerminalData(Terminal terminalData) {
		this.terminalData = terminalData;
	}
	
	public static GTLLiteGlobalCache getInstance(){
		return instance;
	}

	public UsuarioSistema getUsuarioSistema() {
		return usuarioSistema;
	}

	public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
		this.usuarioSistema = usuarioSistema;
	}

}
