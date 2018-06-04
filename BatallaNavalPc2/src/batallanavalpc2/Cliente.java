/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package batallanavalpc2;
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
import navalApp.naval;
import navalApp.navalHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.sp.resources.Imagenes;

/**
 *
 * @author David
 */
public class Cliente extends javax.swing.JFrame {
    
    static naval navalImpl;
    
    Image background;
    Image ponerBarcos;
    Image esperando;
    Image esperandoPc2;
    Image miTurno;
    Image suTurno;
    Image miTurnoP2;
    Image suTurnoP2;
    Image hasGanado;
    Image hasPerdido;
    
    int estado=0;
    
    int id;
  
    int fil=0;
    int col=0;
    int tam=5;
    int dir=0;
    
    
    boolean flag;
    /**
     * Creates new form Cliente
     */
    public void inicio(){
        flag=true;                
        id=navalImpl.getId();
        navalImpl.reiniciarPartida(id);
        int[][] tableroMio = new int[11][11];
        boolean[][] bTableroMio = new boolean[11][11];
        tableroMio=navalImpl.getMiMatrix(id);
        bTableroMio=navalImpl.getMiMatrixBool(id);
        for (int n=0;n<10;n++){
            for (int m=0;m<10;m++){
                tableroMio[n][m]=0;
                bTableroMio[n][m]=false;
            }
        }
        navalImpl.setMiMatrix(tableroMio,id);
        navalImpl.setMiMatrixBool(bTableroMio,id);
        tam=5;
    }
    
    public void corregirPosicion(){ // no deja salir al barco del tablero
        int f=0;
        int c=0;
        if (dir==1) f=1;
        else c=1;
        if (fil+tam*f>=10){
            fil=10-tam*f;
        }
        if (col+tam*c>=10){
            col=10-tam*c; 
        }
    }
    
    public Cliente() {
        Imagenes i =new Imagenes();    // instancia de la clase imagenes
        background=i.cargar("Portada.jpg");//Image cargar(String sRuta){return Toolkit.getDefaultToolkit().createImage((getClass().getResource(sRuta)));}
        ponerBarcos=i.cargar("PonerBarcos.jpg");
        esperando=i.cargar("Esperando.jpg");
        esperandoPc2=i.cargar("EsperandoPc2.jpg");
        miTurno=i.cargar("MiTurno.jpg");
        suTurno=i.cargar("SuTurno.jpg");
        miTurnoP2=i.cargar("MiTurnoP2.jpg");
        suTurnoP2=i.cargar("SuTurnoP2.jpg");
        hasGanado=i.cargar("hasGanado.jpg");
        hasPerdido=i.cargar("HasPerdido.jpg");
        initComponents();
        setBounds(0,0,1067,600); //tamaño de la ventana
        
        addMouseListener(      //(Interface con metodos implementados) hace que al mover el mouse pase de un estado a otro
            new MouseAdapter() {    //clase que tiene implementado el adapter pero con metodos vacios. mplementamos solo metodos que nos interesan
                public void mouseClicked(MouseEvent e) {// <-metodo
                    if(estado==3 || estado==4)
                        estado=0;
                    if (e.getModifiers() == MouseEvent.BUTTON3_MASK && estado==1){ // se ha hecho click derecho. Estado=1 (poniendo los barcos)
                        dir=1-dir; //cambia la direccion
                        corregirPosicion(); //evitamos que se salga el barco del tablero al cambiar de la orientacion
                        repaint();
                        return;
                    }
                    if (estado==0){ //Estado 0 -> inciarpartida 
                        estado=1; //Si el estado es 0 se cambia a 1
                        inicio(); //matrices en 0 y false y TableroSuyo con barcos ya puestos
                        repaint();
                    }else if (estado==1){ //Estado 1 -> poner mis barcos
                        int[][] tableroMio = new int[11][11];
                        tableroMio=navalImpl.getMiMatrix(id);
                        if (navalImpl.validarPosicionBarco(tableroMio, tam, fil, col, dir)){ //Esta dentro del barco
                            int f=0;
                            int c=0;
                            if (dir==1){ //Vertical
                                f=1;
                            }else{  //Horizontal
                                c=1;
                            }
                            //Se procede a poner el barco en el tableroMio (misma logica que ponerbarco)
                            for (int m=fil;m<=fil+(tam-1)*f;m++){ //nro de iteracion m=tamaño-1  
                                for (int n=col;n<=col+(tam-1)*c;n++){ //nro de iteracion n=tamaño-1 
                                    tableroMio[m][n]=tam; //pone en la casilla el numero del tam
                                }
                            }
                            navalImpl.setMiMatrix(tableroMio, id);
                            tam--; //se decrementa el tamaño
                            if (tam==0){
                                estado=2; //Si el tamaño es 0 pasamos al estado 2
                                navalImpl.ListoParaJugar(id);
                                dir=0;
                                repaint();
                            }
                        }
                    }else if (estado==2 && navalImpl.pedirTurno(id)){
                        // obtener la fila y columna del tablero contrario (momento antes de disparar)
                        int f=(e.getY()-170)/30; // -200 porque ahi empieza el TableroSuyo->(0,0)es el vetice de la celda, /30 porque cada celda mide 30*30
                        int c=(e.getX()-650)/30; // -450 porque ahi empieza el TableroSuyo->(0,0)es el vetice de la celda, /30 porque cada celda mide 30*30
                        if (f!=fil || c!=col){ //Se iguala las coordenadas de pFila y pCol con f y c
                            fil=f;
                            col=c;
                            if (navalImpl.validarCelda(f, c)){ //El disparo esta en el tablero?
                                 boolean[][] bTableroSuyo = new boolean[11][11];
                                 bTableroSuyo=navalImpl.getSuMatrixBool(id);
                                if (bTableroSuyo[f][c]==false){ //Las coordenadas del disparo (f,c) no fueron ya realizadas
                                    bTableroSuyo[f][c]=true; //Se realiza el disparo
                                    navalImpl.setSuMatrixBool(bTableroSuyo,id);
                                    repaint();
                                    int[][] tableroSuyo = new int[11][11];
                                    tableroSuyo=navalImpl.getSuMatrix(id);
                                    bTableroSuyo=navalImpl.getSuMatrixBool(id);                                   
                                    repaint();
                                    
                                    if (tableroSuyo[f][c]==0 && bTableroSuyo[f][c]){ //Hay un barco(tab[n][m]>0), He disparado(bVisible[n][m])
                                        navalImpl.pasarTurno(id);
                                    }
                                    
                                    //Vitoria                    
                                    if (navalImpl.victoria(navalImpl.getSuMatrix(id), navalImpl.getSuMatrixBool(id))){ //Con cada disparo que hacemos se verifica si hemos ganado
                                        navalImpl.heGanado(id);
                                        navalImpl.pasarTurno(id);
                                        estado=3; //reiniciamos partida                                        
                                    }                                  
                                    
                                }
                            }
                        }
                    }
                } //MouseClicked
            } //MouseAdapter
        ); //addMouseListener
        addMouseMotionListener( //movimineto del mouse
            new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) { //actualiza la pfila y pcol y evitar que el barco se salga del tablero(rectificar)
                    //Obtener las coordenadas del mouse
                    int x=e.getX();
                    int y=e.getY();
                    if (estado==1 && x>=70 && y>=170 && x<70+30*10 && y<170+30*10){ //El mouse esta en nuestro tablero
                        //obtenemos la fila y columna de nuestro tablero
                        int f=(y-170)/30;
                        int c=(x-70)/30;
                        if (f!=fil || c!=col){ //Se iguala las coordenadas de pFila y pCol con f y c. Se ha cambiado la f o c respecto al anterior actualizamos
                            fil=f;
                            col=c;
                            corregirPosicion(); //Se evita que el barco se salga del tablero cuando se cambia de f o c
                            repaint();
                        }
                    }  
                    if(estado==2)
                        repaint();
                    
                    //Derrota 
                    boolean[] ganadorPerdedor=new boolean[2];
                    ganadorPerdedor=navalImpl.ganadorPerdedor(id);                                    
                    if(!ganadorPerdedor[0] && ganadorPerdedor[1] && flag && estado!=0){
                        flag=false;
                        estado = 4;
                    }
                                        
                }
            }
        );
    }

     public void graficar(Graphics g, int tab[][], int x, int y, boolean bVisible[][], boolean flag){
        //Recorremos toooodo el tablero
        for (int n=0;n<10;n++){
            for (int m=0;m<10;m++){
                
                    
                    if (tab[n][m]>0 && bVisible[n][m]){ //Hay un barco(tab[n][m]>0), He disparado(bVisible[n][m])
                        g.setColor(Color.yellow);
                        if (navalImpl.disparo(tab, tab[n][m], bVisible)){
                            g.setColor(Color.red);
                        }
                        g.fillRect(x+m*30, y+n*30, 30, 30); //x o y->coordenada, m o n->tamñano. Dibujamos cuadrado (yellow/red)
                    }
                    
                    if (tab[n][m]==0 && bVisible[n][m]){ //No hay barco (tab[n][m]>0), He diparado
                        g.setColor(Color.blue);
                        g.fillRect(x+m*30, y+n*30, 30, 30);
                    }
                    
                    
                    
                    
                    if (tab[n][m]>0 && flag && !bVisible[n][m]){ // Hay un barco (tab[n][m]>0), y no han disparado en mi tablero
                        g.setColor(Color.gray);
                        g.fillRect(x+m*30, y+n*30, 30, 30);
                    }
                    //Pintar las lineas
                    g.setColor(Color.white);
                    g.drawRect(x+m*30, y+n*30, 30, 30);
                    //antes de empezar el juego (poniendo los barcos en mi tablero)
                    
                    if (estado==1 && flag){
                        int f=0;
                        int c=0;
                        if (dir==1){
                            f=1; //Vertical
                        }else{
                            c=1; //Horizontal
                        }
                        //visualizamos los barco en verde
                        if (n>=fil && m>=col && n<=fil+(tam-1)*f && m<=col+(tam-1)*c){
                            //n/m >= pFila/pCol solo se pinta verde a partir el cursor
                            //n/m <= pFila+(pTam-1)*pDF / m<=pCol+(pTam-1)*pDC solo se pinta hasta el tam barco
                            g.setColor(Color.gray);
                            g.fillRect(x+m*30, y+n*30, 30, 30);
                            
                        }
                    }
            }
        }//fin de la matriz
    }
 
     //cargan las dos imagenes
    public void paint(Graphics g){
        if (estado==0){
            g.drawImage(background, 0, 0, this);
        }else {
            if(estado==1){
                g.drawImage(ponerBarcos, 0, 0, this);
            }else{
                if(estado==2 && navalImpl.getListoParaJugarRival(id)==false){
                    if(id==1)
                            g.drawImage(esperando, 0, 0, this);
                        else
                            g.drawImage(esperandoPc2, 0, 0, this);
                }else{
                    if(estado==2 && navalImpl.pedirTurno(id)){
                        if(id==1)
                            g.drawImage(miTurno, 0, 0, this);
                        else
                            g.drawImage(miTurnoP2, 0, 0, this);
                    }else{
                        if(id==1)
                            g.drawImage(suTurno, 0, 0, this); 
                        else
                            g.drawImage(suTurnoP2, 0, 0, this); 
                    }
                }
            }
            graficar(g, navalImpl.getMiMatrix(id), 70, 170, navalImpl.getMiMatrixBool(id), true);
            if(navalImpl.getListoParaJugarRival(id) && estado==2)
               graficar(g, navalImpl.getSuMatrix(id), 650, 170, navalImpl.getSuMatrixBool(id), false);
        }
        if(estado==3){
            g.drawImage(hasGanado, 0, 0, this);
        }
        if(estado==4){
            g.drawImage(hasPerdido, 0, 0, this);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 482, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 367, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cliente().setVisible(true);
                try{
                    ORB orb = ORB.init(args, null);
                    //inicializar orb para enviar peticion
                    org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

                    String name = "cadena";
                    navalImpl = navalHelper.narrow(ncRef.resolve_str(name));
                    // System.out.println("Obteniendo las cabeceras del objeto servidor:
                    // "+navalImpl);
                    
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                    e.printStackTrace(System.out);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
