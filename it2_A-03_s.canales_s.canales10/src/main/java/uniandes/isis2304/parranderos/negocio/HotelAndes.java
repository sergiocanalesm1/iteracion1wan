/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Universidad	de	los	Andes	(Bogotá	- Colombia)
 * Departamento	de	Ingeniería	de	Sistemas	y	Computación
 * Licenciado	bajo	el	esquema	Academic Free License versión 2.1
 * 		
 * Curso: isis2304 - Sistemas Transaccionales
 * Proyecto: Parranderos Uniandes
 * @version 1.0
 * @author Germán Bravo
 * Julio de 2018
 * 
 * Revisado por: Claudia Jiménez, Christian Ariza
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package uniandes.isis2304.parranderos.negocio;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import com.google.gson.JsonObject;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import uniandes.isis2304.parranderos.persistencia.Persistencia;

/**
 * Clase principal del negocio
 * Sarisface todos los requerimientos funcionales del negocio
 *
 * @author Germán Bravo
 */
public class HotelAndes 
{
	/* ****************************************************************
	 * 			Constantes
	 *****************************************************************/
	/**
	 * Logger para escribir la traza de la ejecución
	 */
	private static Logger log = Logger.getLogger(HotelAndes.class.getName());

	/* ****************************************************************
	 * 			Atributos
	 *****************************************************************/
	/**
	 * El manejador de persistencia
	 */
	private Persistencia p;

	/* ****************************************************************
	 * 			Métodos
	 *****************************************************************/
	/**
	 * El constructor por defecto
	 */
	public HotelAndes ()
	{
		p = Persistencia.getInstance ();
	}

	/**
	 * El constructor qye recibe los nombres de las tablas en tableConfig
	 * @param tableConfig - Objeto Json con los nombres de las tablas y de la unidad de persistencia
	 */
	public HotelAndes (JsonObject tableConfig)
	{
		p = Persistencia.getInstance (tableConfig);
	}

	/**
	 * Cierra la conexión con la base de datos (Unidad de persistencia)
	 */
	public void cerrarUnidadPersistencia ()
	{
		p.cerrarUnidadPersistencia ();
	}

	

	public RolDeUsuario adicionarRolDeUsuario(String cargo, String desc) 
	{

		log.info("Adicionando rol de usuario: "+ cargo);
		RolDeUsuario rol = p.adicionarRolesDeUsuario( cargo, desc );
		log.info ("Adicionando Tipo de bebida: " + rol);
		return rol;
	}

	public Usuario registrarUsuario(String nombre, String edad, String tel, String tipoDoc, String numeroDoc,
			String correo, String cargo, String idConvencion) throws Exception {
		log.info("Adicionando usuario: "+ nombre);
		Usuario us = null;
		RolDeUsuario rol = p.getRolDeUsuario( cargo );
		if( rol != null )
		{
			if( idConvencion.equals( "N/A") ) idConvencion = null;
			else
			{
				if( !p.getRolDeUsuarioById( idConvencion ).equals( "Organizador Eventos"))throw new Exception("El id de convencion no es valido");
				us = p.adicionarUsuario(  nombre,  edad,  tel,  tipoDoc,  numeroDoc,
						correo,  cargo, idConvencion);
			}
			

		}
		else
		{
			throw new Exception("No existe el rol de usuario especificado");
		}
		log.info ("Adicionando usuario: "+ nombre);
		;

		return us;
	}

	public TipoHabitacion adicionarTipoDeHabitacion(String nombre, String capacidad, String costoXNoche,
			String cantidadDisponible) throws Exception
	{
		log.info("Registrando tipo de habitacion: "+ nombre);
		TipoHabitacion th = p.adicionarTipoDeHabitacion( nombre, Integer.parseInt(capacidad), Integer.parseInt(costoXNoche), Integer.parseInt(cantidadDisponible));
		log.info ("Registrando tipo de habitacion: "+ nombre);
		;

		return th;
	}

	public Habitacion adicionarHabitacion(String nombre, String tipoHabitacion) throws Exception
	{
		log.info("Adicionando habitacion: "+ nombre);
		Habitacion h = null;
		TipoHabitacion th = p.getTipoHabitacion( tipoHabitacion );

		if( th != null )
		{
			if( th.getCantidadDisponible() == 0 )
				throw new Exception("No hay habitaciones disponibles para el tipo especificado");

			h = p.adicionarHabitacion(nombre, tipoHabitacion);
			p.cambiarCantidadDisponibleTipoHabitacion( nombre, th.getCantidadDisponible()-1);
		}
		else
		{
			throw new Exception("No existe el tipo de habitacion especificado");
		}
		log.info ("Adicionando habitacion: "+ nombre);
		return h;
	}

	public Servicio adicionarServicio(String nombre, String descripcion, String costo) throws Exception
	{
		log.info("Adicionando servicio: "+ nombre);

		Servicio s = p.adicionarServicio(nombre, descripcion, Integer.parseInt(costo));
		log.info ("Adicionando habitacion: "+ nombre);
		return s;
	}

	public PlanConsumo adicionarPlanConsumo(String nombre, String por, String des) 
	{
		log.info("Adicionando plan consumo: "+ nombre);

		PlanConsumo s = p.adicionarPlanConsumo(nombre, Integer.parseInt(por), des);
		log.info ("Adicionando plan consumo: "+ nombre);
		return s;
	}
	//Reserva hab, salon de reuniones, salon de conferencias, SPA
	public Reserva adicionarReserva(String metodoDePago, String numeroPersonas, String fechaComienzo, String fechaFin,
			String tipoHabitacion, String planConsumo, String idUsuario, String idProducto) throws Exception
	{
		log.info("Adicionando reserva para usuario: "+ idUsuario);

		Timestamp fechaCo = Timestamp.valueOf(fechaComienzo);
		Timestamp fechaFi = Timestamp.valueOf(fechaFin);
		if( p.getUsuario( idUsuario) == null )
			throw new Exception("No existe el usuario");

		if( !tipoHabitacion.isEmpty() && p.getTipoHabitacion( tipoHabitacion ) == null)
			throw new Exception("No existe ese tipo de habitacion");
		if ( !planConsumo.isEmpty() && p.getPlanConsumo(planConsumo) == null )
			throw new Exception("No existe el plan consumo");
		System.out.println(idProducto);
		if( idProducto.equals("-1") && !reservaDeHabitacionEstaDisponible( tipoHabitacion, fechaCo, fechaFi))
			throw new Exception("No hay habitaciones disponibles de ese tipo para esa fecha");
		else if( !idProducto.equals("-1"))
		{
			if( p.getProducto( idProducto ) == null)
				throw new Exception("No existe el producto con el id especificado");
			if(!reservaDeServicioDisponible( idProducto, fechaCo, fechaFi) )
				throw new Exception("No esta disponible el servicio para esa fecha");
		}
		Reserva s = p.adicionarReserva( metodoDePago,  Integer.parseInt(numeroPersonas),  fechaCo,  fechaFi,
				tipoHabitacion,  planConsumo,  idUsuario, idProducto);
		log.info ("Adicionando plan consumo: "+ idUsuario);
		return s;
	}
	public Usuario getUsuario(String idUsuario) throws Exception
	{
		return p.getUsuario( idUsuario) ;
	}
	public boolean registrarLlegadaCliente(String idUsuario, String fechaLlegada, String idConvencion) throws Exception
	{
		log.info("Adicionando reserva para usuario: "+ idUsuario);

		if( p.getUsuario( idUsuario) == null ) throw new Exception("No existe el usuario");
		if( idConvencion.equals("N/A")) idConvencion = null;
		else
		{
			if( p.getRolDeUsuario(idConvencion) == null ) throw new Exception( "El id de convencion no existe.");
		}
		String logrado = p.registrarLlegadaUsuario( idUsuario, fechaLlegada,idConvencion);
		String numeroHabitacion = p.getNumeroHabitacion(idUsuario);
		Reserva reserva = p.getReserva(idUsuario, fechaLlegada);
		//el de la reserva por si hubo cambios por MANTENIMIENTO
		Double costoPorNoche = p.getTipoHabitacion(reserva.getTipoHabitacion()).getCostoPorNoche();
		Timestamp llegadaTS = Timestamp.valueOf(fechaLlegada);
		Timestamp salidaTs = reserva.getFechaFin();
		
		long milliseconds = salidaTs.getTime() - llegadaTS.getTime();
		int days = (int) (milliseconds / 1000 / 60 / 60 / 24);
		double valor = days * costoPorNoche;
		//adicionar consumo
		p.adicionarConsumo(valor+"", llegadaTS, p.getNumeroHabitacion(idUsuario), null, idConvencion);
		log.info ("Adicionando plan consumo: "+ logrado);
		return !logrado.equals("");
	}
	public Producto adicionarProducto(String nombre, String duracion, String idServicio) throws Exception {

		log.info("Adicionando producto: "+ nombre);
		if( p.getServicio( idServicio ) == null)
			throw new Exception( "No existe el servicio con el id especificado");
		Producto s = p.adicionarProducto(nombre, duracion, idServicio);
		log.info ("Adicionando producto: "+ nombre);
		return s;
	}
	public Consumo adicionarConsumo(String valor, String numeroHabitacionACargar, String idServicio, String idConvencion) throws Exception
	{
		log.info("Adicionando consumo para servicio: "+ valor + ";"+idServicio);
		if( numeroHabitacionACargar.equals("N/A")) numeroHabitacionACargar = null;
		if( idServicio.equals("N/A"))  
			idServicio = null;
		if( idConvencion.equals("N/A")) idConvencion = null;
		if( idServicio!=null && p.getServicio( idServicio ) == null) throw new Exception( "No existe el servicio con el id especificado");
		System.out.println("servicio");
		if( numeroHabitacionACargar!=null && p.getHabitacion(numeroHabitacionACargar) == null )throw new Exception( "No existe el numero de habitacion "+numeroHabitacionACargar);
		System.out.println("habitacion");
		if( idConvencion!=null && !p.getRolDeUsuarioById( idConvencion ).equals( "Organizador Eventos") )throw new Exception( "No existe la convencion con el id especificado");
		System.out.println("convencion");
		
		Consumo s = p.adicionarConsumo(valor, new Timestamp(System.currentTimeMillis()),numeroHabitacionACargar, idServicio, idConvencion);
		System.out.println("consumo");
		log.info("Adicionando consumo para servicio: "+ valor + ";"+idServicio);
		return s;
	}

	private boolean reservaDeServicioDisponible(String idProducto, Timestamp fechaCo, Timestamp fechaFi) 
	{
		
		return p.getReservasHabitacionEn(idProducto, fechaCo, fechaFi) == 0;
	}

	private boolean reservaDeHabitacionEstaDisponible(String tipoHabitacion, Timestamp fechaCo, Timestamp fechaFi) 
	{
		long totalHabitaciones = p.darTotalHabitaciones( tipoHabitacion );
		long totalHabitacionesParaRangoFechas = p.getReservasHabitacionEn( tipoHabitacion, fechaCo, fechaFi );
		return totalHabitacionesParaRangoFechas < totalHabitaciones;

	}
	public RolDeUsuario getRolDeUsuario(String cargo){
		return p.getRolDeUsuario(cargo);
	}
	public Habitacion getHabitacion(String numeroHabitacion){
		return p.getHabitacion(numeroHabitacion);
	}

	public boolean registrarSalidaCliente(String idUsuario, String salida) throws Exception {
		
		log.info("Adicionando reserva para usuario: "+ idUsuario);

		if( p.getUsuario( idUsuario) == null )
			throw new Exception("No existe el usuario");
		String logrado = p.registrarSalidaUsuario( idUsuario, salida );
		log.info ("Adicionando plan consumo: "+ logrado);
		return !logrado.equals("");	}

	public void reservarAlojamientoYServicio(String alojamientos, String fechaInicioAloja, String fechaFinAloja, String infoServicios, String idUsuario ) throws Exception
	{
		//Verificar si se puede alojar
		String[] ar = alojamientos.split(";");
		int cantidadSolicitada = 0;
		for (int i = 0; i < ar.length; i++) 
		{
			String nombreTipoHabitacion = ar[i].split(":")[0];
			System.out.println("Nombre Tipo de Habitacion: "+nombreTipoHabitacion);
			int cantidadMaxima = p.getCantidadDeHabitacionPorTipo(nombreTipoHabitacion);
			System.out.println("Cantidad Maxima: "+ cantidadMaxima);
			cantidadSolicitada = Integer.parseInt(ar[i].split(":")[1]);
			System.out.println("Cantidad Solicitada: "+cantidadSolicitada);
			if( cantidadSolicitada > cantidadMaxima ) throw new Exception ("No hay suficientes habitaciones disponibles en el hotel de tipo "+nombreTipoHabitacion);
			System.out.println("Hay suficientes habitaciones en el hotel");
			//Ver cantidad en esas fechas para los tipos de habitacion. int getReservasTipoHabitacionXFecha(fechaInicio, fechaFin, tipo)
			int cantidadTipoHabitacionReservadas = p.getReservasTipoHabitacionXFecha(Timestamp.valueOf(fechaInicioAloja), Timestamp.valueOf(fechaFinAloja), nombreTipoHabitacion);
			System.out.println("Cantidad de habitaciones reservadas en el rango de fechas: " + cantidadTipoHabitacionReservadas );
			if( cantidadTipoHabitacionReservadas + cantidadSolicitada > cantidadMaxima ) throw new Exception ("Para el rango de fechas definido, no hay suficientes habitaciones de tipo "+nombreTipoHabitacion);
			System.out.println("Hay habitaciones para el rango definido de fechas");
			
		}
		//Verificar si se puede reservar servicios
		ArrayList<String[]> infoServiciosAAgregar = reservarServicioParaConvencion(infoServicios, idUsuario);
		if( infoServiciosAAgregar.isEmpty() ) throw new Exception( "No se pudo agregar el servicio. Ver el log para mas informacion");
		
		//Reservar habitaciones
		for (int j = 0; j < ar.length; j++) 
		{
			String[] it = ar[j].split(":");
			for (int i = 0; i < Integer.parseInt(it[1]); i++) adicionarReserva("Tarjeta de credito", "1", fechaInicioAloja, fechaFinAloja, it[0], "", "1", "-1");
		}
		
		//Reservar servicios
		
		//0idUsuario,1nombreServicio,2idServicio,3cantidad,4fechaCom,5fechaFin
		for (String[] itInfo : infoServiciosAAgregar) 
		{
			String idUsuarioo = itInfo[0];
			String duracion = ""+ (int)(Math.random()*150)+50;
			int cantidad = Integer.parseInt(itInfo[3]);
			Timestamp fechaComienzServicio = Timestamp.valueOf(itInfo[4]);
			Timestamp fechaFinServicio = Timestamp.valueOf(itInfo[5]);
			Producto pro = p.adicionarProducto(idUsuarioo+":"+itInfo[1], duracion, itInfo[2]);
			String idproducto = pro.getId(); 
			p.adicionarReserva("Tarjeta de Credito", cantidad, fechaComienzServicio, fechaFinServicio, null, "", idUsuarioo, idproducto);
			
			
		}
		
		
		
		
	
		
		
		//if(nombreServicio == salon ) ver la cantidad reservada para esos servicios para esas fechas int getCantidadReservada( fechaInicio, fechaFin, nombreServicio)	
		//else ver si hay reserva para esas fechas int( fechaInicio, fechaFin, nombreServicio )
		//Reservar ambos dos 
	}
	private ArrayList<String[]> reservarServicioParaConvencion( String infoServicios, String idUsuario ) throws Exception
	{
		List<String> serviciosPermitidos = new ArrayList<>(Arrays.asList("Restaurante", "Bar","Salon de Reunion", "Salon de Conferencia"));
		String[] servicios = infoServicios.split(";");
		ArrayList<String[]> infoServiciosAAgregar = new ArrayList<String[]>();
		
		
		for (int i = 0; i < servicios.length; i++) 
		{
			String[] servicio = servicios[i].split("#");
			String nombre = servicio[0];
			if( !serviciosPermitidos.contains(nombre)) throw new Exception("No se puede reservar el servicio con el nombre: "+nombre);
			String cantidad = servicio[1];
			String fechaComienzo = servicio[2]+":00";
			String fechaFin = servicio[3]+":00";
			List<Servicio> serv =  p.getServicios( nombre );
			if( serv.isEmpty() )throw new Exception( "No existe el servicio: "+nombre);
			for (Servicio itServ : serv) 
			{
				String capacidadSt = itServ.getDescripcion().split(";")[0];
				System.out.println("capacidad " +capacidadSt);
				if(  Integer.parseInt(cantidad)> Integer.parseInt(capacidadSt.split(":")[1]) ) 
				{
					log.info("id servicio " +itServ.getId()+"\tError: La cantidad solicitada es mayor a la cantidad del servicio");
					continue;
				}
				int numeroReservas = p.getCantidadReservada( itServ.getId(), Timestamp.valueOf(fechaComienzo), Timestamp.valueOf(fechaFin));
				if(nombre.contains("Salon") && numeroReservas > 0) 
				{
					log.info("id servicio " +itServ.getId()+"\tError: No se pudo reservar el salon para esas fechas");
					continue;
				}
				if( numeroReservas + Integer.parseInt(cantidad)>= Integer.parseInt(capacidadSt.split(":")[1])) 
				{
					log.info("id servicio " +itServ.getId()+"\tError: No se pudo reservar el servicio para esas fechas porque ya esta lleno");
					continue;
				}
				String[] info = new String[7];
				info[0] = idUsuario;
				info[1] = itServ.getNombre();
				info[2] = ""+itServ.getId();
				info[3] = cantidad;
				info[4] = fechaComienzo;
				info[5] = fechaFin;
				infoServiciosAAgregar.add(info);
				break;
				
			}
		
		}
		return infoServiciosAAgregar;
		
		
		
	}

	public long cancelarReservaConvencion(String idUsuario) throws Exception
	{
		if( getUsuario(idUsuario) == null) throw new Exception ("No existe el usuario.");
		if( !p.getRolDeUsuarioById( idUsuario ).equals( "Organizador Eventos")) throw new Exception("El usuario no es organizador de eventos");
		return p.cancelarReservasConvencion( idUsuario );
		
	}

	public long registrarFinConvencion( String idUsuario ) throws Exception
	{
		if( getUsuario(idUsuario) == null) throw new Exception ("No existe el usuario.");
		if( !p.getRolDeUsuarioById( idUsuario ).equals( "Organizador Eventos")) throw new Exception("El usuario no es organizador de eventos");
		return p.finDeConvencion(idUsuario);
	}

	public void registarMantenimientoParaAlojamientoYServicio(String habitacionesParaMantenimiento,
			String serviciosParaMantenimiento, String fechaInicioMantenimiento, String fechaFinMantenimiento) throws Exception
	{
		fechaInicioMantenimiento+=" 00:00:00";
		fechaFinMantenimiento+=" 00:00:00";
		//Verificar la existencia de las habitaciones y los servicios
		
		//Ver que existan las habitaciones
		String[] arrHabitacionesParaMantenimiento = habitacionesParaMantenimiento.split(";");
		for (int i = 0; i < arrHabitacionesParaMantenimiento.length; i++) {
			if( p.getHabitacion(arrHabitacionesParaMantenimiento[i]) == null ) throw new Exception("No existe la habitacion " +arrHabitacionesParaMantenimiento[i]);
		}
		System.out.println("termino 1");
		//Ver que existan los servicios
		String[] arrServiciosParaMantenimiento = serviciosParaMantenimiento.split(";");
		for (int j = 0; j < arrServiciosParaMantenimiento.length; j++) {
			if(p.getServicio(arrServiciosParaMantenimiento[j]) == null ) throw new Exception("No existe el servicio con ID " + arrServiciosParaMantenimiento[j]);
		}
		System.out.println("termino 2");
		//Update en las fechas de mantenimiento para los servicios y las habitaciones
		updateFechasDeMantenimientoParaAlojamientoYServicios(arrHabitacionesParaMantenimiento, arrServiciosParaMantenimiento, fechaInicioMantenimiento, fechaFinMantenimiento);
		//ALOJAMIENTO
		//RESERVAS
		
		//Agregar reserva de mantenimiento para el tipo de habitacion del numero de habitacion
		for (int i = 0; i < arrHabitacionesParaMantenimiento.length; i++) {
			Reserva res = p.adicionarReserva("MANTENIMIENTO", 0, Timestamp.valueOf(fechaInicioMantenimiento), Timestamp.valueOf(fechaFinMantenimiento), p.getHabitacion(arrHabitacionesParaMantenimiento[i]).getTipoHabitacion(), null, null, "-1");
			System.out.println(res.getPlanConsumo());
			//p.adicionarReservaMantenimientoAlojamiento( fechaInicioMantenimiento, fechaFinMantenimiento, p.getHabitacion(arrHabitacionesParaMantenimiento[i]).getTipoHabitacion()  );
			
		}
		for (int i = 0; i < arrServiciosParaMantenimiento.length; i++) {
			Producto pro = p.adicionarProducto("MANTENIMIENTO", "0", arrServiciosParaMantenimiento[i]);
			System.out.println("prod: "+ pro.getNombre());
			Reserva reser = p.adicionarReserva("MANTENIMIENTO", 0, Timestamp.valueOf(fechaInicioMantenimiento), Timestamp.valueOf(fechaFinMantenimiento), null, null, null, pro.getId());
			System.out.println(reser.getPlanConsumo());
			//p.adicionarReservaMantenimientoServicios( fechaInicioMantenimiento, fechaFinMantenimiento, arrServiciosParaMantenimiento[i]);
		}
		
	}
	private void updateFechasDeMantenimientoParaAlojamientoYServicios(String[] arrHabitacionesParaMantenimiento,
			String[] arrServiciosParaMantenimiento, String fechaInicioMantenimiento, String fechaFinMantenimiento)
	{
		
		//update fechas de mantenimiento para habitaciones
		for (int i = 0; i < arrHabitacionesParaMantenimiento.length; i++) {
			p.registrarMantenimientoAlojamiento(arrHabitacionesParaMantenimiento[i], fechaInicioMantenimiento, fechaFinMantenimiento);
		}
		System.out.println("termino 3");
		//update fechas de mantenimiento para servicios
		for (int j = 0; j < arrServiciosParaMantenimiento.length; j++) {
			p.registrarMantenimientoServicios( arrServiciosParaMantenimiento[j], fechaInicioMantenimiento, fechaFinMantenimiento);
		}
		System.out.println("termino 4");
	}

	public void finalizarMantenimientoParaAlojamientoYServicio(String habitacionesParaFinalizarMantenimiento,
			String serviciosParaFinalizarMantenimiento)throws Exception {
		String[] habitaciones = habitacionesParaFinalizarMantenimiento.split(";");
		String[] servicios = serviciosParaFinalizarMantenimiento.split(";");
		for (int i = 0; i < habitaciones.length; i++) {
			if( p.getHabitacion(habitaciones[i]) == null ) throw new Exception("No existe la habitacion " +habitaciones[i]);
		}
		System.out.println("termino 1");
		for (int j = 0; j < servicios.length; j++) {
			if(p.getServicio(servicios[j]) == null ) throw new Exception("No existe el servicio con ID " + servicios[j]);
		}
		System.out.println("termino 2");
		
		//Alojamiento
		int cantidadMantenimientoAlojamiento = 0;
		for (int i = 0; i < habitaciones.length; i++) {
			String numeroHabitacion = habitaciones[i];
			p.cancelarMantenimientoAlojamiento(numeroHabitacion);
		}
		//Servicios
		for (int j = 0; j < servicios.length; j++) {
			String idMantenimiento = servicios[j];
			p.cancelarMantenimientoServicio(idMantenimiento);
		}
		
	}

	public List<String> serviciosSinMuchaDemanda() 
	{
		
		Calendar ca = Calendar.getInstance();
		Date timeNow = ca.getTime();
		ca.add(Calendar.YEAR, -1);
		Date startTime = ca.getTime();
		System.out.println("Present year: "+timeNow);
		System.out.println("Past year: "+startTime);
		List<String> servicios = p.getAllServices();
		while( startTime.before(timeNow) )
		{
			Date base = (Date) startTime.clone();
			ca.add(Calendar.WEEK_OF_YEAR, 1);
			startTime = ca.getTime();
			List<String> mayores = p.demandaMayorA3Semanal( new Timestamp(base.getTime()),new Timestamp(startTime.getTime()) );
			for (int i = 0; i < mayores.size(); i++) {
				if( servicios.contains(mayores.get(i))) servicios.remove(mayores.get(i));
			}
		}
			
			
		return servicios;
	}

	public void analizarOperacion(String respuesta, String unidadDeTiempo) 
	{
//		List<Date> fechasMayorDemanda = new List() <Date>;
//		List<Date> fechasMayoresIngresos = new List() <Date>;
//		List<Date> fechasMenorDemanda = new List() <Date>;
//		int unitOfTime = Calendar.MONTH;
//		if( unidadDeTiempo.equals("Semana")) unitOfTime = Calendar.WEEK_OF_YEAR;
//		Calendar ca = Calendar.getInstance();
//		Date timeNow = ca.getTime();
//		Date timeStart = new Date(2014, 1, 1);
//		while( timeStart.before(timeNow))
//		{
//			Date base = (Date) timeStart.clone();
//			ca.add(unitOfTime, 1);
//			timeStart = ca.getTime();
//			if( respuesta.length() < 3 )
//			{
//				fechasMayorDemanda = p.analizarServicioMayorDemanda( respuesta, base, timeStart );
//				fechasMayoresIngresos = p.analizarServicioMayorIngreso( respuesta, base, timeStart  );
//				fechasMenorDemanda = p.analizarServicioMenorDemanda( respuesta, base, timeStart  );
//			}
//			else
//			{
//				fechasMayorDemanda = p.analizarTHMayorDemanda( respuesta, base, timeStart  );
//				fechasMayoresIngresos = p.analizarTHMayorIngreso( respuesta, base, timeStart  );
//				fechasMenorDemanda = p.analizarTHMenorDemanda( respuesta, base, timeStart  );
//			}
//			
//		}
//		for (int i = 0; i < mayores.size(); i++) {
//			if( servicios.contains(mayores.get(i))) servicios.remove(mayores.get(i));
//		}
		
		


	}
	public List<String> mejoresClientes(String fechaAñoAtras){
		
		return p.mejoresClientes(Timestamp.valueOf(fechaAñoAtras));
	}
	
	public String getClientsByServiceConsumption(String serviceName, String inicialDate, String endDate, 
			 String groupByColumn, String orderByColumn){
		
		String tabla = "";
		List<Object[]> lista = p.getClientsByServiceConsumption(serviceName, inicialDate, endDate, groupByColumn, orderByColumn);
		
		for(int i = 0; i < lista.size(); i++) {			
			for(int j = 0; j < lista.get(i).length ; j++) {		
				
				tabla += lista.get(i)[j] + ",  ";
			}
			tabla += "\n \n";
		}
		System.out.println(tabla);
		
		return tabla;
	}

	public String getClientsNotInServiceConsumption(String serviceName, String inicialDate, String endDate, 
			 String groupByColumn, String orderByColumn) {
		
		String tabla = "";
		List<Object[]> lista = p.getClientsNotInServiceConsumptio(serviceName, inicialDate, endDate, groupByColumn, orderByColumn);
		
				
		for(int i = 0; i < lista.size(); i++) {			
			for(int j = 0; j < lista.get(i).length ; j++) {		
				
				tabla += lista.get(i)[j] + ",  ";
				System.out.println(lista.get(i)[j]);
			}
			tabla += "\n \n";
		}
		System.out.println(tabla);
		
		return tabla;
	
		
	}

	
}
