/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpreter.bytecode;

import interpreter.VirtualMachine;

/**
 *
 * @author senatori
 */
public class HaltCode extends ByteCode {
    
    public void init(java.util.ArrayList args){
    }
    public void execute(VirtualMachine vm){
        vm.stopVM();
        
        
    }
    
}
