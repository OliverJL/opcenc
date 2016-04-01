package extdis;

import java.io.*;
import java.lang.*;

public class DisassemblyParser {

	public DisassemblyParser(String pathToDisFile) throws Exception
	{
		try (BufferedReader br = new BufferedReader(new FileReader(pathToDisFile))) {
		    String line;
		    
		    Object currentFunction = null;
		    
		    while ((line = br.readLine()) != null) {
		    	System.out.println(line);
		    	if(line.startsWith("Dump of assembler code for function")){
		    		String functionName = line.replaceAll("Dump of assembler code for function", "").replace(":", "");
		    		currentFunction = new Object();
		    	}else if(currentFunction != null){
		    		// 0x00000000004314c0 <+0>:	0f b6 07	movzbl (%rdi),%eax
		    		String [] instructionLine = line.split("\t");
		    		String address = instructionLine[0];
		    		String opCode = instructionLine[1];		    		
		    		String [] opCodeChars = opCode.split(" ");

		    		for(int opci = 0; opci < opCodeChars.length; opci += 1){
		    			String opcString = opCodeChars[opci].toUpperCase();
		    			byte opcByte = (byte)((Character.digit(opcString.charAt(0), 16) << 4) + Character.digit(opcString.charAt(1), 16));
		    			//System.out.println(String.format("opcByte %s %x", opcString, opcByte));
		    		}
		    		String mnemonics = instructionLine[2];
		    	}	    	
		    	if(line.equalsIgnoreCase("End of assembler dump.")){
		    		currentFunction = null;
		    	}
		    }
		}		
	}
}
