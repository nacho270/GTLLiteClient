package ar.com.lite.textillevel.gui.acciones;

public interface DialogLectorCodigoCallback<T> {

	T buscar(String codigo) throws Exception;
	String validar(T obj);
	void encontrado(T obj);
}
