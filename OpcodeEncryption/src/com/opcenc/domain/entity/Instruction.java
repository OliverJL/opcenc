package com.opcenc.domain.entity;

import javax.persistence.*;
import java.sql.*;

@Entity
@Table(name="instruction", 
	   uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class Instruction {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true)	
	public long id;
	
	@Column(name="createdAt")
	public Timestamp createdAt;

	public String getDisassemblyLine() {
		return disassemblyLine;
	}

	public void setDisassemblyLine(String disassemblyLine) {
		this.disassemblyLine = disassemblyLine;
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

	public void setExecFunction(ExecFunction execFunction) {
		this.execFunction = execFunction;
	}

	@Column(name="disassemblyLine")
	public String disassemblyLine;
	
    @ManyToOne
    @JoinColumn(name="execFunctionId")
    private ExecFunction execFunction;	

}
