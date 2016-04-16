package com.opcenc.domain.entity;

import javax.persistence.*;
import java.sql.*;
import java.util.*;


@Entity
@Table(name="opcodeSet", 
	   uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class OpcodeSet {

	public OpcodeSet()
	{
		opcodes = new HashSet<Opcode>();
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true)	
	public long id;
	
	@Column(name="createdAt")
	public Timestamp createdAt;

	@Column(name="compressionLevel")
	public int compressionLevel;
	
	@Column(name="compressionName")
	public String compressionName;

	@Column(name="encryptionAlgorythm")
	public int encryptionAlgorythm;
	
	@Column(name="encryptionAlgorythmName")
	public String encryptionAlgorythmName;	
	
    @ManyToOne
    @JoinColumn(name="execFunctionId")
    private ExecFunction execFunction;
    
    public void setExecFunction(ExecFunction execFunction) {
		this.execFunction = execFunction;
	}

	public int getCompressionLevel() {
		return compressionLevel;
	}

	public void setCompressionLevel(int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	public String getCompressionName() {
		return compressionName;
	}

	public void setCompressionName(String compressionName) {
		this.compressionName = compressionName;
	}

	public int getEncryptionAlgorythm() {
		return encryptionAlgorythm;
	}

	public void setEncryptionAlgorythm(int encryptionAlgorythm) {
		this.encryptionAlgorythm = encryptionAlgorythm;
	}

	public String getEncryptionAlgorythmName() {
		return encryptionAlgorythmName;
	}

	public void setEncryptionAlgorythmName(String encryptionAlgorythmName) {
		this.encryptionAlgorythmName = encryptionAlgorythmName;
	}

	public Set<Opcode> getOpcodes() {
		return opcodes;
	}

	public void setOpcodes(Set<Opcode> opcodes) {
		if(opcodes != null){
			this.opcodes.clear();
			for(Opcode oc : opcodes) addOpcode(oc);
		}
	}
	
	public void addOpcode(Opcode opcode) {
		if(opcode != null && !opcodes.contains(opcode)){
			opcode.setOpcodeSet(this);
			this.opcodes.add(opcode);
		}
	}	

	public long getId() {
		return id;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public ExecFunction getExecFunction() {
		return execFunction;
	}

	@OneToMany(mappedBy="opcodeSet", fetch=FetchType.EAGER)
    public Set<Opcode> opcodes;    
}
