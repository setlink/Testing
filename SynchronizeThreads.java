/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package synchronizethreads;

/**
 *
 * @author Kaiheng He
 */
public class SynchronizeThreads {
    static int Sum = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        for(int i=0; i<1000; ++i)
        {
        new Thread(() -> {
        wrapper();
        }).start();
        }
        
        System.out.println(Sum);
        // TODO code application logic here
    }
    public static void wrapper(){
    ++Sum;
    System.out.println(Sum);
    }
    
}
