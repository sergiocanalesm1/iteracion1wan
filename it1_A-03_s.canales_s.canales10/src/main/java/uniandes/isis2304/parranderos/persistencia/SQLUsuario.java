package uniandes.isis2304.parranderos.persistencia;

import java.sql.Timestamp;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import uniandes.isis2304.parranderos.negocio.Habitacion;
import uniandes.isis2304.parranderos.negocio.Usuario;

public class SQLUsuario {
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
	public SQLUsuario(Persistencia p){
		
		persistencia = p;
	}

	public String adicionarUsuario(PersistenceManager pm, String nombre,  String edad,  String tel,  String tipoDoc, String numeroDoc,
			 String correo,  Timestamp fechaLlegada,  Timestamp fechaSalida,  String cargo,  Habitacion hab, String idConvencion) {
		
		Query q = pm.newQuery(SQL, "INSERT INTO " + persistencia.darTablaUsuario() + "(nombre, edad, telefono,tipoDocumento, numerodocumento ,correo, fechaLlegada , fechaSalida, rol,numerohabitacion, idConvencion) values (?,?,?,?,?,?,?,?,?,?,?)");
		q.setParameters(nombre, edad, tel,tipoDoc, numeroDoc ,correo, fechaLlegada , fechaSalida, cargo,hab, idConvencion);
		
		return q.executeUnique() + "";
	}

	public Usuario getUsuario(PersistenceManager pm, String id) {
		
		Query q = pm.newQuery(SQL, "SELECT * FROM " + persistencia.darTablaUsuario() + " WHERE numeroDocumento = ?");
		q.setResultClass(Usuario.class);
		q.setParameters(id);
		return (Usuario) q.executeUnique();	
		}


		public long updateReserva(PersistenceManager pm,String idUsuario, String llegada) {
			
			Query q = pm.newQuery(SQL, "UPDATE " + persistencia.darTablaUsuario () + " SET fechaLlegada = to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS')  WHERE numeroDocumento = ?");
			q.setResultClass(Long.class);
			q.setParameters(llegada, idUsuario );
		    return (long) q.executeUnique();
		}

		public long updateReservaBySalida(PersistenceManager pm, String idUsuario, String fechaSalida) {
			Query q = pm.newQuery(SQL, "UPDATE " + persistencia.darTablaUsuario () + " SET fechaSalida = to_timestamp(?, 'YYYY-MM-DD HH24:MI:SS') , numeroHabitacion = ? WHERE numeroDocumento = ?");
			q.setResultClass(Long.class);
		    q.setParameters(fechaSalida, null, idUsuario );
		     
		     return (long) q.executeUnique();
		}

		public String getRolDeUsuarioById(PersistenceManager pm, String id) {
			Query q = pm.newQuery(SQL, "SELECT rol FROM " + persistencia.darTablaUsuario() + " WHERE numeroDocumento = ?");
			q.setResultClass(String.class);
			q.setParameters(id);
			return (String) q.executeUnique();
		}

		public void asociarHabitacion(PersistenceManager pm, String idUsuario) {
			
			String sql = "UPDATE USUARIO\r\n" + 
					" SET numeroHabitacion = \r\n" + 
					"   (\r\n" + 
					"    SELECT *\r\n" + 
					"    FROM (\r\n" + 
					"            SELECT numero\r\n" + 
					"            FROM HABITACION\r\n" + 
					"            WHERE numero not in\r\n" + 
					"            (\r\n" + 
					"                SELECT numeroHabitacion\r\n" + 
					"                FROM USUARIO\r\n" + 
					"				 WHERE numeroHabitacion is not null" +
					"\r\n" + 
					"            ) \r\n" + 
					"\r\n" + 
					"         )\r\n" + 
					"     WHERE rownum = 1  \r\n" + 
					"    )\r\n" + 
					"WHERE numerodocumento = ?" ;
					
			Query q = pm.newQuery(SQL, sql );
			q.setParameters(idUsuario);
			q.executeUnique();

		}

		public String getNumeroHabitacion(PersistenceManager pm, String idUsuario) {
			Query q = pm.newQuery(SQL,"SELECT numeroHabitacion FROM USUARIO WHERE numeroDocumento = ?" );
			q.setResultClass(String.class);
			q.setParameters(idUsuario);
			return (String) q.executeUnique();
		}
	
	

}
