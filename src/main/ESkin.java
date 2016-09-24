package main;

public enum ESkin {
	ROJO ("main/sgpthemepack_rojo.zip"),
	AZUL ("main/sgpthemepack_azul.zip");
	
	private ESkin(String path){
		this.path = path;
	}
	
	private String path;

	
	public String getPath() {
		return path;
	}

	
	public void setPath(String path) {
		this.path = path;
	}
}
