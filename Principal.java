package examen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Principal {
	private static Conexiones conn = new Conexiones();

	public static void main(String[] args) throws SQLException {
		Scanner sc = new Scanner(System.in);
		int opcion = 0;
		do {
			mostrarMenu();
			opcion = sc.nextInt();
			switch (opcion) {
			case 1:
				visualizarcentros(100);
				break;
			case 2:
				añadircolumnas();
				break;
			case 3:
				añadirregistro("MT0002", 3000);
				break;
			case 0:
				break;
			default:
				System.out.println("Seleccione una opción válida!");
				break;
			}
		} while (opcion != 0);
		System.out.println("FIN!");
		sc.close();
	}

	private static void añadirregistro(String codasig, int codProf) throws SQLException {
		Connection conexion = conn.getOracle("CENTROS", "centros");
		String sql = "INSERT INTO C1_ASIGPROF(C1_ASIGNATURAS_COD_ASIG,C1_PROFESORES_COD_PROF)VALUES(?,?)";
		boolean error = false;
		// Comprobar codigooficina
		String sqlcodprof = "select count(*) from C1_PROFESORES where COD_PROF = ? ";
		PreparedStatement senProf = conexion.prepareStatement(sqlcodprof);
		senProf.setInt(1, codProf);
		ResultSet resProf = senProf.executeQuery();
		resProf.next();
		int comprobarVi = resProf.getInt(1);
		if (comprobarVi <= 0) {
			error = true;
			System.out.println("ERROR - EL CODIGO DE PROFESOR NO EXISTE:" + codProf);
		}
		String sqlcodasig = "select count(*) from C1_ASIGNATURAS where COD_ASIG = ? ";
		PreparedStatement senAsig = conexion.prepareStatement(sqlcodasig);
		senAsig.setString(1, codasig);
		ResultSet resAsig = senAsig.executeQuery();
		resAsig.next();
		int comprovarAsig = resAsig.getInt(1);
		if (comprovarAsig <= 0) {
			error = true;
			System.out.println("ERROR - EL CODIGO DE ASIGNATURA NO EXISTE:" + codasig);
		}
		String sqlcomprobarAsigProf = "SELECT COUNT(*) FROM C1_ASIGPROF WHERE C1_ASIGNATURAS_COD_ASIG = ? AND C1_PROFESORES_COD_PROF = ?";
		PreparedStatement senComprobarAsigProf = conexion.prepareStatement(sqlcomprobarAsigProf);
		senComprobarAsigProf.setString(1, codasig);
		senComprobarAsigProf.setInt(2, codProf);
		ResultSet resComprobarAsigProf = senComprobarAsigProf.executeQuery();
		resComprobarAsigProf.next();
		int comprobarAsigProf = resComprobarAsigProf.getInt(1);
		if (comprobarAsigProf > 0) {
			error = true;
			System.out.println("ERROR - EL PROFESOR YA IMPARTE ESA ASIGNATURA:" + codProf + " - " + codasig);
		}
		if (!error) {
			PreparedStatement senIns = conexion.prepareStatement(sql);
			senIns.setString(1, codasig);
			senIns.setInt(2, codProf);
			int resIns = senIns.executeUpdate();
			System.out.println("Insertado: " + resIns);

			String sqlUpd = "UPDATE C1_ASIGNATURAS SET NUMPROFASIG = NUMPROFASIG+1 WHERE cod_asig =?";
			PreparedStatement senUpd = conexion.prepareStatement(sqlUpd);
			senUpd.setString(1, codasig);
			;
			int resUpd = senUpd.executeUpdate();
			System.out.println("Actualizado: " + resUpd + " vez con con el codigo: " + codasig);

			String sqlUpd2 = "UPDATE C1_PROFESORES SET NUMASIGPROF = NUMASIGPROF+1 WHERE cod_prof =?";
			PreparedStatement senUpd2 = conexion.prepareStatement(sqlUpd2);
			senUpd2.setInt(1, codProf);
			;
			int resUpd2 = senUpd2.executeUpdate();
			System.out.println("Actualizado: " + resUpd2 + " vez con con el codigo: " + codProf);
		}
	}

	private static void añadircolumnas() throws SQLException {
		Connection conexion = conn.getOracle("CENTROS", "centros");
		try {
			String rest1 = "alter table C1_ASIGNATURAS add NUMPROFASIG int default 0";
			PreparedStatement sencrear = conexion.prepareStatement(rest1);
			int nn = sencrear.executeUpdate();
			System.out.println("LA COLUMNA HA SIDO CREADA");
		} catch (SQLException e) {
			System.out.println("ERROR LA COLUMNA YA EXISTE.");
		}
		try {
			String rest2 = "alter table C1_PROFESORES add NUMASIGPROF int default 0";
			PreparedStatement sencrear = conexion.prepareStatement(rest2);
			int nn2 = sencrear.executeUpdate();
			System.out.println("LA COLUMNA HA SIDO CREADA");
		} catch (SQLException e) {
			System.out.println("ERROR LA COLUMNA YA EXISTE.");
		}
		try {
			String update = "UPDATE C1_ASIGNATURAS a SET NUMPROFASIG=( SELECT COUNT (C1_PROFESORES_COD_PROF) FROM C1_ASIGPROF WHERE C1_ASIGNATURAS_COD_ASIG=a.COD_ASIG)";
			PreparedStatement senAct = conexion.prepareStatement(update);
			int nn = senAct.executeUpdate();
			System.out.println("LA COLUMNA HA SIDO ACTUALIZA EN " + nn + " FILAS");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			String update = "UPDATE C1_PROFESORES p SET NUMASIGPROF=( SELECT COUNT (C1_ASIGNATURAS_COD_ASIG) FROM C1_ASIGPROF WHERE C1_PROFESORES_COD_PROF=p.COD_PROF)";
			PreparedStatement senAct = conexion.prepareStatement(update);
			int nn = senAct.executeUpdate();
			System.out.println("LA COLUMNA HA SIDO ACTUALIZA EN " + nn + " FILAS");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql = "SELECT COD_ASIG, NOMBRE_ASI,COD_PROF,NOMBRE_APE FROM C1_ASIGNATURAS JOIN C1_ASIGPROF ON C1_ASIGNATURAS_COD_ASIG=COD_ASIG JOIN C1_PROFESORES ON C1_PROFESORES_COD_PROF=COD_PROF";
		PreparedStatement senAsigProf = conexion.prepareStatement(sql);
		ResultSet rescentros = senAsigProf.executeQuery();
		if (rescentros.next()) {
			System.out.printf("%10s %25s %15s %12s%n", "COD ASIG", "NOMBRE ASIG", "COD PROF", "NOMBRE PROF");
			System.out.printf("%10s %25s %15s %12s%n", "--------", "----------------------", "--------------",
					"--------");
			while (rescentros.next()) {
				System.out.printf("%10s %25s %15s %12s%n", rescentros.getString(1), rescentros.getString(2),
						rescentros.getInt(3), rescentros.getString(4));
			}
			System.out.printf("%10s %25s %15s %12s%n", "--------", "----------------------", "--------------",
					"--------");
		}
	}

	private static void visualizarcentros(int cod) throws SQLException {
		Connection conexion = conn.getOracle("CENTROS", "centros");
		String sqlcentros = "SELECT NOM_CENTRO ,NVL((SELECT NOMBRE_APE FROM C1_PROFESORES WHERE COD_PROF = c.DIRECTOR),'NO TIENE'),(SELECT COUNT(*) FROM C1_PROFESORES p WHERE p.COD_CENTRO=c.COD_CENTRO)  FROM C1_CENTROS c WHERE COD_CENTRO="
				+ cod + "";
		PreparedStatement senCentros = conexion.prepareStatement(sqlcentros);
		ResultSet rescentros = senCentros.executeQuery();
		if (rescentros.next()) {
			System.out.println("COD-CENTRO: " + cod + " NOMBRE CENTRO: " + rescentros.getString(1));
			System.out.println(
					"NOMBRE-DIRECTOR: " + rescentros.getString(2) + " NUM-PROFESORES: " + rescentros.getInt(3));
			System.out.println("------------------------------------------------------------------");
			int profmasasig = 0;
			int max = 0;
			String nombre = "";
			String nombre1 = "";

			System.out.println("PROFESORES DEL CENTRO");
			System.out.printf("%10s %25s %15s %25s %12s%n", "COD-PROF", "NOMBRE", "ESPECIALIDAD", "NOMBRE JEFE",
					"NUM-ASIG");
			System.out.printf("%10s %25s %15s %25s %12s%n", "--------", "----------------------", "-------------",
					"--------------", "--------");
			String sqlProfes = "SELECT COD_PROF,NOMBRE_APE,ESPECIALIDAD, NVL((SELECT pr.NOMBRE_APE FROM C1_PROFESORES pr WHERE  p.COD_PROF1=pr.COD_PROF),'SIN JEFE'), (SELECT COUNT (*) FROM C1_ASIGPROF WHERE C1_PROFESORES_COD_PROF=p.COD_PROF) FROM C1_PROFESORES p WHERE COD_CENTRO="
					+ cod + "";
			PreparedStatement senProfes = conexion.prepareStatement(sqlProfes);
			ResultSet resProfes = senProfes.executeQuery();

			while (resProfes.next()) {
				System.out.printf("%10s %25s %15s %25s %12s%n", resProfes.getInt(1), resProfes.getString(2),
						resProfes.getString(3), resProfes.getString(4), resProfes.getInt(5));
				profmasasig = profmasasig + resProfes.getInt(5);
				if (max < resProfes.getInt(5)) {
					nombre = resProfes.getString(2);
					max = resProfes.getInt(5);
				} else {
					if (max == resProfes.getInt(5)) {
						nombre = nombre + " y " + resProfes.getString(2);
					}
				}
			}
			System.out.printf("%10s %25s %15s %25s %12s%n", "--------", "----------------------", "-------------",
					"--------------", "--------");

			System.out.println("Nombre del profesor que imparte mas asignaturas:  " + nombre);
		} else {
			System.out.println("El codigo del centro " + cod + " no es valido utiliza otro");
		}

	}

	private static void mostrarMenu() {
		System.out.println("------------------------------------------------------");
		System.out.println("EXAMEN");
		System.out.println("  1 Visualizar información del centro");
		System.out.println("  2 Añadir columnas en asignaturas y profesores");
		System.out.println("  3 Añadir registro en asigprof");
		System.out.println("  0. Salir");
		System.out.println("------------------------------------------------------");
	}

}
