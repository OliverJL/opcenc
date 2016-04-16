package com.opcenc.client;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.opcenc.domain.entity.BinaryFile;
import com.opcenc.domain.entity.ExecFunction;
import com.opcenc.domain.service.DisassemblyParser;
import com.opcenc.domain.service.IBinaryFileParsingCompleted;
import com.opcenc.domain.service.IBinaryFileParsingStarted;
import com.opcenc.domain.service.IExecFunctionParsingCompleted;
import com.opcenc.domain.service.IExecFunctionParsingStarted;
import com.opcenc.hibernate.HibernateUtil;

public class HibernateAnnotationMain 
implements IBinaryFileParsingStarted,IExecFunctionParsingStarted, IBinaryFileParsingCompleted, IExecFunctionParsingCompleted

{
	SessionFactory sessionFactory;

	public static void main(String[] args) {
				
		new HibernateAnnotationMain();
		
	}	
	
	public HibernateAnnotationMain(){		
		sessionFactory = HibernateUtil.getSessionJavaConfigFactory();
		
		DisassemblyParser disassemblyParser = null;
		try {
			disassemblyParser = new DisassemblyParser("vim", "/home/lvr/src/Area51/dissas/out/vim/exp");
			disassemblyParser.AddBinaryFileParsingStartedListener(this);
			disassemblyParser.AddExecFunctionsParsingStartedListener(this);
			disassemblyParser.AddBinaryFileParsingCompletedListener(this);
			disassemblyParser.AddExecFunctionsParsingCompletedListener(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			disassemblyParser.ParseFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sessionFactory.close();
	}

	@Override
	public void binaryFileParsingStarted(BinaryFile binaryFile) {
		System.out.println(String.format("binaryFileParsingStarted: %s", binaryFile.getName()));
		saveToSession(binaryFile);
		System.out.println(String.format("binaryFileParsingStarted - writen to DB: %s", binaryFile.getName()));
	}

	@Override
	public void execFunctionParsingStarted(ExecFunction execFunction) {
		System.out.println(String.format("execFunctionParsingStarted: %s", execFunction.getFunctionName()));
		//saveToSession(execFunction);
		//System.out.println(String.format("execFunctionParsingStarted - writen to DB: %s", execFunction.getFunctionName()));		
	}

	@Override
	public void binaryFileParsingCompleted(BinaryFile binaryFile) {
		System.out.println(String.format("binaryFileParsingCompleted: %s", binaryFile.getName()));
		saveToSession(binaryFile);
		System.out.println(String.format("binaryFileParsingCompleted - writen to DB: %s", binaryFile.getName()));
	}
	
	private Session currentSession;
	
	private Session getCurrentSession()
	{
		if(currentSession == null)
		{
			SessionFactory sessionFactory = HibernateUtil.getSessionJavaConfigFactory();
			currentSession = sessionFactory.getCurrentSession();
		}
		return currentSession;
	}
	
	private <T> void saveToSession(T enttiy){
		Session session = getCurrentSession();
		session.beginTransaction();				
		session.save(enttiy);
		session.getTransaction().commit();
	}

	@Override
	public void execFunctionParsingCompleted(ExecFunction execFunction) {
		System.out.println(String.format("execFunctionParsingCompleted: %s", execFunction.getFunctionName()));
		saveToSession(execFunction);
		System.out.println(String.format("execFunctionParsingCompleted - writen to DB: %s", execFunction.getFunctionName()));				
	}
}
