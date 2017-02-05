package main;

import ar.com.textillevel.modulos.terminal.entidades.Terminal;

public class GTLLiteGlobalCache {
	
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
}
