package com.opcenc.domain.service;

import java.io.*;
import java.lang.*;
import java.util.*;

import com.opcenc.domain.entity.*;

public class DisassemblyParser {
	
	//private String pathToDisFiles; 
	private String binFileName;
	private File [] disFiles;
	private List<IBinaryFileParsingStarted> listenersBinFileParsingStarted = new ArrayList<IBinaryFileParsingStarted>();
	private List<IBinaryFileParsingCompleted> listenersBinFileParsingCompleted = new ArrayList<IBinaryFileParsingCompleted>();  
	private List<IExecFunctionParsingStarted> listenersExecFunctionsParsingStarted = new ArrayList<IExecFunctionParsingStarted>();
	private List<IExecFunctionParsingCompleted> listenersExecFunctionsCompleted = new ArrayList<IExecFunctionParsingCompleted>();  

	
	public DisassemblyParser(String binFileName, String pathToDisFiles) throws Exception
	{
		File f = new File(pathToDisFiles);
		if(!f.exists() || !f.isDirectory()) { 
			throw new IllegalArgumentException(String.format("file does not exist or is no directory: %s", pathToDisFiles));
		}
		this.binFileName = binFileName;
		//this.pathToDisFiles = pathToDisFiles;
		
		java.io.FileFilter filter = new java.io.FileFilter() {
            @Override
            public boolean accept(File pathname) {
               return pathname.isFile();
            }
         };
		
		disFiles = f.listFiles(filter);
		//disFiles = Collections.sort(disFiles);
	}

	public IBinaryFileParsingStarted AddBinaryFileParsingStartedListener(IBinaryFileParsingStarted listener)
	{
		if(listener != null && !listenersBinFileParsingStarted.contains(listener)) 
		{
			listenersBinFileParsingStarted.add(listener);
		}
		return listener;
	}
	
	public IBinaryFileParsingCompleted AddExecFunctionsParsingCompletedListener(IBinaryFileParsingCompleted listener)
	{
		if(listener != null && !listenersBinFileParsingCompleted.contains(listener)) 
		{
			listenersBinFileParsingCompleted.add(listener);
		}		
		return listener;
	}
	
	public IExecFunctionParsingStarted AddExecFunctionsParsingStartedListener(IExecFunctionParsingStarted listener)
	{
		if(listener != null && !listenersExecFunctionsParsingStarted.contains(listener)) 
		{
			listenersExecFunctionsParsingStarted.add(listener);
		}		
		return listener;
	}
	
	public IExecFunctionParsingCompleted AddBinaryFileParsingCompletedListener(IExecFunctionParsingCompleted listener)
	{
		if(listener != null && !listenersExecFunctionsCompleted.contains(listener)) 
		{
			listenersExecFunctionsCompleted.add(listener);
		}		
		return listener;
	}
	
	
	public void ParseFile() throws FileNotFoundException, IOException{
		
		BinaryFile binaryFile = new BinaryFile();
		binaryFile.setName(this.binFileName);
		binaryFile.setFullPath("/usr/bin/todo");
		binaryFile.setSizeInKb(12345);
		for (IBinaryFileParsingStarted l : listenersBinFileParsingStarted) l.binaryFileParsingStarted(binaryFile);
		
		for(int dfCntr = 0; dfCntr < disFiles.length; dfCntr ++)
		{
			String currentDifFile = disFiles[dfCntr].getAbsolutePath();
			
			try (BufferedReader br = new BufferedReader(new FileReader(currentDifFile))) {
			    String line;
			    
			    ExecFunction currentFunction = null;
			    OpcodeSet opcodeSet = null;
			    while ((line = br.readLine()) != null) {
			    	System.out.println(String.format("netxLine: %s",line));
			    	if(line.startsWith("Dump of assembler code for function")){
			    		String functionName = line.replaceAll("Dump of assembler code for function", "").replace(":", "");
			    		currentFunction = new ExecFunction();
			    		currentFunction.setFunctionName(functionName);
			    		opcodeSet = currentFunction.addOpcodeSet(new OpcodeSet());			    		
				       for (IExecFunctionParsingStarted l : listenersExecFunctionsParsingStarted) l.execFunctionParsingStarted(currentFunction);
			    		
			    	}else 
			    		if(line.equalsIgnoreCase("End of assembler dump.")){
				    		binaryFile.addExecFunction(currentFunction);
				    		for (IExecFunctionParsingCompleted l : listenersExecFunctionsCompleted) l.execFunctionParsingCompleted(currentFunction);
				    		currentFunction = null;
			    	}else if(currentFunction != null){
			    		Instruction instruction = new Instruction();
			    		instruction.setDisassemblyLine(line);
			    		currentFunction.addInstruction(instruction);
			    		
			    		// 0x00000000004314c0 <+0>:	0f b6 07	movzbl (%rdi),%eax
			    		String [] instructionLine = line.split("\t");
			    		
			    		//System.out.println(String.format("instructionLine.length: %s", instructionLine.length));			    		
			    		//System.out.println(String.format("instructionLine[0]: %s", instructionLine[0]));
			    		
			    		String address = instructionLine[0];
			    		String opCode = instructionLine[1];		    		
			    		String [] opCodeChars = opCode.split(" ");

			    		//opcodeSet.setCompressionLevel(null);
			    		for(int opci = 0; opci < opCodeChars.length; opci += 1){
			    			String opcString = opCodeChars[opci].toUpperCase();
			    			byte opcByte = (byte)((Character.digit(opcString.charAt(0), 16) << 4) + Character.digit(opcString.charAt(1), 16));
			    			//System.out.println(String.format("opcByte %s %x", opcString, opcByte));
			    			Opcode opcode = new Opcode();
			    			opcode.setRawCode(opcByte);
			    			opcodeSet.addOpcode(opcode);
			    		}
			    		String mnemonics = instructionLine[2];			    		
			    	}
			    }
			}			
		}
	       for (IBinaryFileParsingCompleted l : listenersBinFileParsingCompleted)
	            l.binaryFileParsingCompleted(binaryFile);		
	}	
}
