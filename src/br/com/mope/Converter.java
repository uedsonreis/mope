package br.com.mope;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import mobi.core.Mobi;
import mobi.core.cardinality.Cardinality;
import mobi.core.common.Relation;
import mobi.core.concept.Attribute;
import mobi.core.concept.Class;
import mobi.core.concept.Instance;

public class Converter {

	private static final String OUTPUT_FOLDER = "output/";

	private final Map<String, JDefinedClass> classes = new HashMap<String, JDefinedClass>();
	private final JCodeModel codeModel = new JCodeModel();
	
	private final Mobi mobi;
	private final Mar mar;

	public Converter(Mobi mobi) {
		this.mobi = mobi;
		
		String packageName = "br.com."+ this.mobi.getContext().getUri().toLowerCase() +".mope";
		this.mar = new Mar(packageName, this.codeModel, this.mobi);
	}

	/**
	 * Calls all necessary functions to generate Java code
	 * @throws IOException
	 */
	public void exportCode() throws Exception {
		this.generateAllDomainClasses();
		this.generateAllMethods();
		this.generateJavaFiles();
	}
	
	private void generateJavaFiles() throws IOException {
		File file = new File(OUTPUT_FOLDER);
		file.mkdirs();
		this.codeModel.build(file);
	}
	
	/**
	 * Finds all Domain Classes from MOBI through the mobi.getAllClasses()
	 * method and generates the necessary Java code to represent them. Stores
	 * generated class in the HashMap classes for future references.
	 */
	private void generateAllDomainClasses() {
		HashMap<String, Class> mobiClasses = this.mobi.getAllClasses();
		for (String key : mobiClasses.keySet()) {
			try {
				this.generateDomainClass(key);
			} catch (JClassAlreadyExistsException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates the domain.[uri] class that will contains all the Mobi2Java generated code
	 * @param uri : The class name from MOBI
	 * @return The generated class on JCodeModel
	 * @throws Exception
	 */
	private void generateDomainClass(final String uri) throws Exception {
		this.mar.loadAllCodeFiles();
		JDefinedClass domainClass = this.classes.get(this.mar.getPackagePath(uri));
		
		if (domainClass == null) {
			domainClass = this.codeModel._class(this.mar.getPackagePath(uri));
			this.classes.put(domainClass.name(), domainClass);
		}

		try {
			this.generateClassAttributes(domainClass);
			this.generateClassProperties(domainClass);
		} catch (Exception e) {
			throw e;
		}
	}

	private void generateClassAttributes(final JDefinedClass definedClass) throws Exception {
		System.out.println("generateClassAttributes for " + definedClass.name() + " : " + definedClass.fullName());

		Class mobiClass = this.mobi.getClass(definedClass.name());
		System.out.println("Class Mobi: " + mobiClass.toString());

		Iterator<Instance> iterator = this.mobi.getClassInstances(mobiClass).iterator();
		Instance instance = iterator.next();
		if (iterator.hasNext()) instance = null;
		
		for (Attribute attribute : this.mobi.getAllClassAttributes(mobiClass)) {

			System.out.println("field " + attribute.getUri() + " for " + definedClass.name() + " - " + definedClass.fullName());
//			JFieldVar dataType = definedClass.field(JMod.PRIVATE, this.mar.getClassType(attribute), attribute.getUri());
			
			JFieldVar dataType = this.mar.createAttribute(definedClass, this.mar.getClassType(attribute), attribute.getUri());
			
			if (instance != null) {
				String value = instance.getAttributeValueHashMap().get(attribute.getUri()).getValue();
				
				dataType.init(this.mar.getLitValue(attribute.getType(), value));
			}

//			this.generateGetMethod(definedClass, dataType);
//			this.generateSetMethod(definedClass, dataType);
		}

		this.generateEqualsMethod(definedClass);
	}

	private void generateClassProperties(final JDefinedClass definedClass) throws Exception {
		System.out.println("generateClassProperties for " + definedClass.name() + " - " + definedClass.fullName());
		
		Class mobiClass = this.mobi.getClass(definedClass.name());
		System.out.println("ClassMobi: " + mobiClass.toString());
		
		for (Relation relation : this.mobi.getAllClassCompositionRelations(mobiClass)) {
			
			if (relation.getClassB().getUri().equals(definedClass.name())) continue;
			
			System.out.println("field " + relation.getUri() + " for " + definedClass.name() + " - " + definedClass.fullName());
			
			JFieldVar objProperty;
			
			if (relation.getCardinalityA().getType() == Cardinality.ONE_N || relation.getCardinalityA().getType() == Cardinality.ZERO_N) {

				JType type = this.mar.getClassType("List-"+relation.getClassB().getUri());
				objProperty = definedClass.field(JMod.PRIVATE | JMod.FINAL, type, relation.getUri().split("_")[1]);
				objProperty.init(JExpr._new(this.codeModel.ref(ArrayList.class).narrow(this.mar.getClassType(relation.getClassB().getUri()))));
				
			} else {
				JType type = this.mar.getClassType(relation.getClassB().getUri());
				objProperty = definedClass.field(JMod.PRIVATE, type, relation.getUri().split("_")[1]);
				this.mar.generateSetMethod(definedClass, objProperty);
			}
			
			this.mar.generateGetMethod(definedClass, objProperty);
		}
	}
	
	private void generateEqualsMethod(final JDefinedClass definedClass) {

		if (this.mobi.getKeyAttribute(definedClass.name()) == null) return;

		String unique = this.mobi.getKeyAttribute(definedClass.name()).iterator().next().getUri();
		unique = "get" + unique.substring(0, 1).toUpperCase() + unique.substring(1, unique.length());

		JMethod equalsMethod = definedClass.method(JMod.PUBLIC, this.codeModel.BOOLEAN, "equals");
		equalsMethod.annotate(java.lang.Override.class);
		JVar objParam = equalsMethod.param(Object.class, "obj");
		JBlock methodBody = equalsMethod.body();

		methodBody._if(objParam.eq(JExpr._null()))._then()._return(JExpr.FALSE);

		methodBody._if(JExpr._this().eq(objParam))._then()._return(JExpr.TRUE);

		methodBody._if(objParam._instanceof(definedClass).not())._then()._return(JExpr.FALSE);

		JVar other = methodBody.decl(definedClass, "other", JExpr.cast(definedClass, objParam));
		methodBody._if(other.invoke(unique).invoke("equals").arg(JExpr._this().invoke(unique)).not())._then()._return(JExpr.FALSE);

		methodBody._return(JExpr.TRUE);
	}
	
	private void generateAllMethods() throws JClassAlreadyExistsException {

		for (String classUri : this.mar.getAllImplementedClasses()) {
			JDefinedClass jDefClass = this.codeModel._getClass(this.mar.getPackagePath(classUri));
			if (jDefClass == null) jDefClass = this.codeModel._class(this.mar.getPackagePath(classUri));
			
			this.mar.createClassCode(classUri, jDefClass);
		}
		
	}
}