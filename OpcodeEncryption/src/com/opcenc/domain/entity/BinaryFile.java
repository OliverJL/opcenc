package com.opcenc.domain.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;


import org.hibernate.annotations.*;

@Entity
@Table(name="binaryFile", 
	   uniqueConstraints={@UniqueConstraint(columnNames={"id"})})
public class BinaryFile {
	
	public BinaryFile()
	{
		execFunctions = new HashSet<ExecFunction>();
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false, unique=true)	
	private long id;
	
	@Column(name="createdAt")
	private Timestamp createdAt;
	
	@Column(name="name", nullable=true)
	private String name;
	
	@Column(name="fullPath", nullable=true)
	private String fullPath;
	
	@Column(name="sizeInKb", nullable=true)
	private int sizeInKb;
	
    @OneToMany(mappedBy="binaryFile", fetch=FetchType.EAGER)
    private Set<ExecFunction> execFunctions;	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public int getSizeInKb() {
		return sizeInKb;
	}

	public void setSizeInKb(int sizeInKb) {
		this.sizeInKb = sizeInKb;
	}

	public Set<ExecFunction> getExecFunctions() {
		return execFunctions;
	}

	public void setExecFunctions(Set<ExecFunction> execFunctions) {
		if(execFunctions != null){
			this.execFunctions.clear();
			for(ExecFunction ef : execFunctions) addExecFunction(ef);
		}
	}
	
	public void addExecFunction(ExecFunction execFunction) {
		if(execFunction != null &&  !this.execFunctions.contains(execFunction)){			
			execFunction.setBinaryFile(this);
			this.execFunctions.add(execFunction);
		}
	}

	public long getId() {
		return id;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}
	
}