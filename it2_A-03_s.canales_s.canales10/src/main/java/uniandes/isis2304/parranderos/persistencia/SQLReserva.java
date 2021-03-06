package uniandes.isis2304.parranderos.persistencia;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import uniandes.isis2304.parranderos.negocio.Producto;
import uniandes.isis2304.parranderos.negocio.Reserva;

public class SQLReserva {
	/**
	 * tipo de consulta a sql
	 */
	private final static String SQL = Persistencia.SQL;
	
	/**
	 * manejador de persistencia
	 */
	private Persistencia persistencia;
	
	/**
	 * constructor
	 */

	public SQLReserva(Persistencia p) {
		persistencia = p;
	}

	public long adicionarReserva(PersistenceManager pm, long id, String metodoDePago,
			int numeroPersonas, Timestamp fechaComienzo, Timestamp fechaFin, String tipoHabitacion, String planConsumo, String idUsuario, String idProducto, String pagado) {
		Query q = null;
		if( idProducto.equals("-1") )
		{
			q = pm.newQuery(SQL, "INSERT INTO " + persistencia.darTablaReserva() + "(id, metodoDePago, cantidadpersonas, fechaComienzo, fechaFin, tipohabitacion, planConsumo, idUsuario, pagado ) values (?,?,?,?,?,?,?,?,?)");
			q.setParameters(id, metodoDePago, numeroPersonas, fechaComienzo, fechaFin, tipoHabitacion, planConsumo, idUsuario, pagado);
		}
		else
		{
			q = pm.newQuery(SQL, "INSERT INTO " + persistencia.darTablaReserva() + "(id, metodoDePago, cantidadpersonas, fechaComienzo, fechaFin, tipohabitacion, planConsumo, idUsuario, idProducto, pagado) values (?,?,?,?,?,?,?,?,?,?)");
			q.setParameters(id, metodoDePago, numeroPersonas, fechaComienzo, fechaFin, tipoHabitacion, planConsumo, idUsuario, idProducto, pagado);
		}
			
		
		return (long) q.executeUnique();
	}

	public long darReservasHabitacion(PersistenceManager pm, String valor, Timestamp fechaCo, Timestamp fechaFi) 
	{
		String comparacion = "tipohabitacion";
		if( !valor.equals("-1"))
		{
			comparacion = "idProducto";
		}
			
		String sql1 = "AND ((fechacomienzo >=  ? AND fechacomienzo < ?) or ( fechafin > ? and fechaFin <= ?) or (fechacomienzo <= ? AND fechafin >= ?))";
		Query q = pm.newQuery(SQL, "SELECT COUNT(*) FROM " + persistencia.darTablaReserva() + " WHERE "+comparacion+" = ? "+sql1);
		
		q.setParameters(valor, fechaCo, fechaFi, fechaCo, fechaFi, fechaCo, fechaFi);
		return ((BigDecimal) q.executeUnique()).longValue();
	}

	public Reserva getReservado(PersistenceManager pm,String idUsuario, String llegada) {
		
		Query q = pm.newQuery(SQL, "SELECT * FROM " + persistencia.darTablaReserva() + " WHERE idUsuario = ? AND fechaComienzo = to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS')");
		q.setResultClass(Reserva.class);
		q.setParameters(idUsuario, llegada);
		return ((List<Reserva>) q.executeList()).get(0); 
	}

	public Reserva getReservadoBySalida(PersistenceManager pm, String idUsuario,
			String fechaSalida) {

		Query q = pm.newQuery(SQL, "SELECT * FROM " + persistencia.darTablaReserva() + " WHERE idUsuario = ? AND fechafin = to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS')");
		q.setResultClass(Reserva.class);
		q.setParameters(idUsuario, fechaSalida);
		return ((List<Reserva>) q.executeList()).get(0);
	}

	public Integer getReservasTipoHabitacionXFecha(PersistenceManager pm, Timestamp fechaCo, Timestamp fechaFi, String tipoHabitacion) {
		String fechas = " AND ((fechacomienzo >=  ? AND fechacomienzo < ?) or ( fechafin > ? and fechaFin <= ?) or (fechacomienzo <= ? AND fechafin >= ?))";
		Query q = pm.newQuery(SQL, "SELECT COUNT(*) "
				+ "FROM " + persistencia.darTablaReserva()
						+ " WHERE tipohabitacion = ? "
						+ fechas);
		q.setResultClass(Integer.class);
		q.setParameters(tipoHabitacion, fechaCo, fechaFi, fechaCo, fechaFi, fechaCo, fechaFi);
		return (Integer) q.executeUnique();
					
	}

	public Integer getCanitdadReservada(PersistenceManager pm, Long id, Timestamp fechaCo,
			Timestamp fechaFi) {
		
		String join = " WHERE r.idProducto = p.id AND p.idServicio = s.id AND s.id = ?";
		String fechas = " AND ((fechacomienzo >=  ? AND fechacomienzo < ?) or ( fechafin > ? and fechaFin <= ?) or (fechacomienzo <= ? AND fechafin >= ?))";
		Query q = pm.newQuery(SQL, "SELECT COUNT(*) FROM Reserva r, Servicio s, Producto p " + join + fechas);
		q.setResultClass(Integer.class);
		q.setParameters(id ,fechaCo, fechaFi, fechaCo, fechaFi, fechaCo, fechaFi );
		return (Integer) q.executeUnique();
		
	}

	public long cancelarReservasConvencion(PersistenceManager pm, String idOrganizador) throws Exception{
		Query q = pm.newQuery(SQL, "DELETE " + persistencia.darTablaReserva() + " WHERE idUsuario = ?");// no tiene set result class
		q.setResultClass(Long.class);
		q.setParameters(idOrganizador);
		return (long) q.executeUnique();
	}

	public void pagar(PersistenceManager pm,String idOrganizador) {
			
			Query q = pm.newQuery(SQL, "UPDATE " + persistencia.darTablaReserva () + " SET pagado = ? WHERE idUsuario = ?");
			q.setResultClass(String.class);
			q.setParameters("Y", idOrganizador );
		    q.executeUnique();
		}

	public long cancelarMantenimiento(PersistenceManager pm, String numeroHabitacion) {
		Query q = pm.newQuery(SQL, "delete reserva where id = (\r\n" + 
				"select *\r\n" + 
				"from(\r\n" + 
				"     select res.id\r\n" + 
				"     from habitacion hab, reserva res\r\n" + 
				"     where hab.tipohabitacion = res.tipohabitacion AND res.metododepago = 'MANTENIMIENTO' AND hab.numero = ? AND hab.iniciomantenimiento = res.fechacomienzo AND hab.finmantenimiento = res.fechafin\r\n" + 
				" )\r\n" + 
				" where rownum = 1\r\n" + 
				" )");
		q.setResultClass(Long.class);
		q.setParameters(numeroHabitacion);
		return (long) q.executeUnique();
		
	}

	

	

	

	
}
