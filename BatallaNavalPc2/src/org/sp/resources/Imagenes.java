/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sp.resources;

import java.awt.*;
/**
 *
 * @author Administrador
 */
public class Imagenes {
    public Image cargar(String sRuta){
        return Toolkit.getDefaultToolkit().createImage((getClass().getResource(sRuta)));
    }
}
