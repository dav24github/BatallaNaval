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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
/**
 *
 * @author David
 */
public class NavalImpl extends navalPOA{
	private ORB orb;
            
        int tableroPc1[][]=new int[11][11];
        boolean bTableroPc1[][]=new boolean[11][11];
        int tableroPc2[][]=new int[11][11];
        boolean bTableroPc2[][]=new boolean[11][11];
        int i=1;
        boolean turnoPc1=false;
        boolean turnoPc2=false;
        boolean listo_para_jugarPc1=false;
        boolean listo_para_jugarPc2=false;
        boolean ganadorPc1=false;
        boolean ganadorPc2=false;
        
	public void setORB(ORB orb_val) {
		orb = orb_val;
        }
        
        public int getId(){
            if(i==1){
                i=2;
                return 1;
            }
            else{ 
                i=1;
                return 2;            
            }
        }
        
        public void ListoParaJugar(int id) {
		if(id==1){
                    listo_para_jugarPc1=true;
                    turnoPc1=true;
                    turnoPc2=false;
                }
                else{
                    listo_para_jugarPc2=true;
                    if(!turnoPc1){
                        turnoPc2=true;
                        turnoPc2=false;
                    }
                }
	}
       
        public boolean getListoParaJugarRival(int id) {
                if(id==1) return listo_para_jugarPc2;
                else return listo_para_jugarPc1;
	}

        public boolean pedirTurno(int id){
            if(id==1){
                return turnoPc1;
            }
            else return turnoPc2;
        }
        
        public void pasarTurno(int id){
            if(id==1){
                turnoPc1=false;
                turnoPc2=true;
            }
            else{
                turnoPc2=false;
                turnoPc1=true;
            }
        }
                   
        public void setMiMatrix(int[][] c, int id) {
		if(id==1) tableroPc1=c;
                else tableroPc2=c;
	}
       
        public int[][] getMiMatrix(int id) {
                if(id==1) return tableroPc1;
                else return tableroPc2;
	}
        
        public void setMiMatrixBool(boolean[][] c, int id) {
		if(id==1) bTableroPc1=c;
                else bTableroPc2=c;
	}
       
        public boolean[][] getMiMatrixBool(int id) {
		if(id==1) return bTableroPc1;
                else return bTableroPc2;
	}
         
        public void setSuMatrix(int[][] c, int id) {
		if(id==1) tableroPc2=c;
                else tableroPc1=c;
	}
       
        public int[][] getSuMatrix(int id) {
                if(id==1) return tableroPc2;
                else return tableroPc1;
	}
        
        public void setSuMatrixBool(boolean[][] c, int id) {
		if(id==1) bTableroPc2=c;
                else bTableroPc1=c;
	}
       
        public boolean[][] getSuMatrixBool(int id) {
		if(id==1) return bTableroPc2;
                else return bTableroPc1;
	}
        
        public void heGanado(int id) {
		if(id==1) ganadorPc1=true;
                else ganadorPc2=true;
	}
        
        public boolean[] ganadorPerdedor(int id) {
                if(id==1){
                    boolean[] ganadorPerdedor=new boolean[2];
                    ganadorPerdedor[0]=ganadorPc1;
                    ganadorPerdedor[1]=ganadorPc2;
                    return ganadorPerdedor;
                }
                else{
                    boolean[] ganadorPerdedor=new boolean[2];
                    ganadorPerdedor[0]=ganadorPc2;
                    ganadorPerdedor[1]=ganadorPc1;
                    return ganadorPerdedor;
                }
	}
        
        public void reiniciarPartida(int id){   
            if(id==1){
                tableroPc1=new int[11][11];
                bTableroPc1=new boolean[11][11];
                turnoPc1=false;
                listo_para_jugarPc1=false;
                ganadorPc1=false;
            }else{     
                tableroPc2=new int[11][11];
                bTableroPc2=new boolean[11][11];
                turnoPc2=false;
                listo_para_jugarPc2=false;
                ganadorPc2=false;
            }
        }
        
        public boolean validarCelda(int f, int c){
            if (f<0) return false;
            if (c<0) return false;
            if (f>=11) return false;
            if (c>=11) return false;
            return true;
        }
        
        public boolean validarPosicionBarco(int tab[][], int tam, int f, int c, int hor){
            int df=0,dc=0; 
            if (hor==1) df=1; //vertical
            else dc=1; //horizontal
            for (int c2=c; c2<=c+tam*dc; c2++){ //nro de iteracion c2 = tamaño
                for (int f2=f; f2<=f+tam*df; f2++){ //nro de iteracione f2 = tamaño
                    if (!validarCelda(f2, c2)){ 
                        return false;
                    }
                }
            }
            for (int f2=f-1;f2<=f+1+tam*df;f2++){ //-1 y +1 no permite poner barcos juntos. nro de ite. f2 =tamaño+2(una celda de distacia)
                for (int c2=c-1;c2<=c+1+tam*dc;c2++){ //-1 y +1 no permite poner barcos juntos. nro de ite. c2=tamaño+2(una celda de distacia)
                    if (validarCelda(f2,c2)){ 
                        if (tab[f2][c2]!=0){ // 0->no hay barco, x->hay barco
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        public void colocarBarco(int tab[][], int tam){

            int f,c,hor;
            do{
                f=(int)(Math.random()*10);
                c=(int)(Math.random()*10);
                hor=(int)(Math.random()*2);
            }while(!validarPosicionBarco(tab, tam, f, c, hor)); // f, c, hor, tam -> definen al barco
            int df=0,dc=0;
            if (hor==1) df=1;
            else dc=1;
            for (int f2=f;f2<=f+(tam-1)*df;f2++){ //nro de iteracion f2=tamaño-1 
                for (int c2=c;c2<=c+(tam-1)*dc;c2++){ //nro de iteracion c2=tamaño-1
                    tab[f2][c2]=tam; //pone en la casilla el numero del tam
                }
            }
        }

        public boolean victoria(int tab[][], boolean bTab[][]){ 
            for (int n=0;n<10;n++){
                for (int m=0;m<10;m++){
                    if (tab[n][m]!=0 && bTab[n][m]==false){ //si entra no es victoria, por ende(return) sale del for 
                        return false;
                    }
                }
            }
            return true; // 0-f ->v , x-f ->NV , x-t ->v , 0-t ->v
        }

        public boolean disparo(int tab[][], int valor, boolean bVisible[][]){
            //En el caso de mi tablero me indica su disparo
            //En el caso de su tablero visualiza su tablero cuando disparo (agua,tocado,hundido)
            //Nos enfocamos unicamente en un barco->valor
            for (int n=0;n<10;n++){
                for (int m=0;m<10;m++){
                    if (bVisible[n][m]==false){ //si es true return true, si es igual a false entra
                        if (tab[n][m]==valor){ //si (n,m) es igual al valor return false, sino return true
                            return false; //retorna false cuando no he disparado y se trata del barco en cuestion (AMARILLO)
                        }
                    }
                }
            }
            return true; //retorna true cuando no he disparado y no se trata del barco. He disparado
        }
        
          public void shutdown() {
            orb.shutdown(true);
                    //cerrar orb por parte del  servidor
        }
        
}