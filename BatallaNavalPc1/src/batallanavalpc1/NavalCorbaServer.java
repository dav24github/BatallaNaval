/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package batallanavalpc1;

import navalApp.*;
//importar la interfaz idl
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
/**
 *
 * @author David
 */
public class NavalCorbaServer {
	public static void main(String args[]) {
		try {
			ORB orb = ORB.init(args, null);
			//inicializacion de conexion
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			//abrimos el root del POA
			rootpoa.the_POAManager().activate();

			NavalImpl navalImpl = new NavalImpl();
			//implementamos referencia
			navalImpl.setORB(orb);
			//seteamos ORB

			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(navalImpl);
			naval href = navalHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			//le damos nombre al servicio
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			String name = "cadena";
                        //int s=sumaImpl.sumar(1,2);
			//se envia el nombre para que resiva la interface
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);
                        
			System.out.println("Servidor de suma listo y en espera");
			
                        //confirmacion de servidor conectado
                        orb.run();
                        
		} catch (Exception e) {
			System.err.println("Error: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("Adios cerrando servidor");
	}
}