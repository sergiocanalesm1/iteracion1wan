package uniandes.isis2304.parranderos.negocio;

import java.sql.Timestamp;

public class Consumo 
{
	private long id;
	
	private int valor;
	
	private Timestamp fechaRegistro;
	
	private String numeroHabitacionACargar;
	
	private long idServicioCargado;

	public Consumo(long id, int valor, Timestamp fechaRegistro, String numeroHabitacionACargar,
			long idServicioCargado) {
		this.id = id;
		this.valor = valor;
		this.fechaRegistro = fechaRegistro;
		this.numeroHabitacionACargar = numeroHabitacionACargar;
		this.idServicioCargado = idServicioCargado;
	}
	public Consumo()
	{
		
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getValor() {
		return valor;
	}
	public void setValor(int valor) {
		this.valor = valor;
	}
	public Timestamp getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(Timestamp fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public String getNumeroHabitacionACargar() {
		return numeroHabitacionACargar;
	}
	public void setNumeroHabitacionACargar(String numeroHabitacionACargar) {
		this.numeroHabitacionACargar = numeroHabitacionACargar;
	}
	public long getIdServicioCargado() {
		return idServicioCargado;
	}
	public void setIdServicioCargado(long idServicioCargado) {
		this.idServicioCargado = idServicioCargado;
	}
	
	
	
}
