package com.opcenc.domain.entity;

import javax.persistence.*;
import java.sql.*;

@Entity
@Table(name="opcode", 
	   uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Opcode {

	public byte getRawCode() {
		return rawCode;
	}

	public void setRawCode(byte rawCode) {
		this.rawCode = rawCode;
	}

	public OpcodeSet getOpcodeSet() {
		return opcodeSet;
	}

	public void setOpcodeSet(OpcodeSet opcodeSet) {
		this.opcodeSet = opcodeSet;
	}

	public long getId() {
		return id;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true)	
	public long id;
	
	@Column(name="createdAt")
	public Timestamp createdAt;
	
	@Column(name="rawCode")
	public byte rawCode;
	
    @ManyToOne
    @JoinColumn(name="opcodeSetId")
    public OpcodeSet opcodeSet;		
	
}
