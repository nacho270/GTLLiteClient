package ar.com.lite.textillevel.gui.acciones;

public interface DialogLectorCodigoCallback<T> {

	T buscar(String codigo) throws Exception;
	void encontrado(T obj);
}
