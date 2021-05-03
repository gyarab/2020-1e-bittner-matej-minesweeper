/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocnikova_prace_minesweeper;

/**
 *
 * @author ja
 */
public class ButtonInfo {

    boolean jeBomba = false;
    boolean otocene = false;
    int cislo = 0;
    boolean vlajka = false;
    boolean prvniBomba = false;
    
    public void nastavBombu() {
        jeBomba = true;
    }
    public void otoc() {
        otocene = true;
    }
    public void zvecCislo() {
        cislo += 1;
    }
}
