package com.opcenc.domain.entity;

import javax.persistence.*;
import java.sql.*;
import java.util.*;


@Entity
@Table(name="execFunction", 
	   uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class ExecFunction {

	public ExecFunction()
	{
		opcodeSets =  new HashSet<OpcodeSet>();
		instructions =  new HashSet<Instruction>();
	}	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true)	
	public long id;
	
	@Column(name="createdAt")
	public Timestamp createdAt;
	
	@Column(name="functionName")
	public String functionName;
	
    @ManyToOne
    @JoinColumn(name="binaryFileId")
    public BinaryFile binaryFile;	
    
    public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public BinaryFile getBinaryFile() {
		return binaryFile;
	}

	public void setBinaryFile(BinaryFile binaryFile) {
		this.binaryFile = binaryFile;
	}

	public Set<OpcodeSet> getOpcodeSets() {
		return opcodeSets;
	}

	public void setOpcodeSets(Set<OpcodeSet> opcodeSets) {
		if(opcodeSets != null){
			this.opcodeSets.clear();
			for(OpcodeSet ocs : opcodeSets) addOpcodeSet(ocs);
		}		
	}
	
	public OpcodeSet addOpcodeSet(OpcodeSet opcodeSet) {
		if(opcodeSet != null &&  !this.opcodeSets.contains(opcodeSet)){			
			opcodeSet.setExecFunction(this);
			this.opcodeSets.add(opcodeSet);
		}
		return opcodeSet;
	}

	public Set<Instruction> getInstructions() {
		return instructions;
	}

	public void setInstructions(Set<Instruction> instructions) {
		if(instructions!=null){
			this.instructions.clear();
			for(Instruction i : instructions) addInstruction(i);
		}		
	}
	
	public void addInstruction(Instruction instruction) {
		if(instruction != null && !this.instructions.contains(instruction)){
			instruction.setExecFunction(this);
			this.instructions.add(instruction);
		}			
	}	

	public long getId() {
		return id;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	@OneToMany(mappedBy="execFunction", fetch=FetchType.EAGER)
    public Set<OpcodeSet> opcodeSets;
    
    @OneToMany(mappedBy="execFunction", fetch=FetchType.EAGER)
    public Set<Instruction> instructions;     
}
