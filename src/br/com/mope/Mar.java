package br.com.mope;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import br.com.mope.littlelanguage.LittleLanguage;
import br.com.mope.littlelanguage.Token;
import mobi.core.Mobi;
import mobi.core.common.Relation;
import mobi.core.concept.Attribute;
import mobi.core.concept.AttributeTypeEnum;
import mobi.core.concept.Class;
import mobi.core.concept.Instance;
import mobi.core.relation.InstanceRelation;

public class Mar {
	
	private static final String INPUT_FOLDER = "input/";
	private static final String EXTENSION = ".mar";
	
	private static final String DOT = "_";
	private static final String GET = "get";
	private static final String SET = "set";
	
	private static final Token BEGIN_TOKEN = new Token(Token.BEGIN);
	private static final Token ELSE_TOKEN = new Token(Token.ELSE);
	private static final Token END_TOKEN = new Token(Token.END);
	private static final Token EQUAL_TOKEN = new Token(Token.EQUAL);
	private static final Token FOR_TOKEN = new Token(Token.FOR);
	private static final Token IF_NOT_TOKEN = new Token(Token.IF_NOT_EQUAL);
	private static final Token IF_TOKEN = new Token(Token.IF);
	private static final Token LESS_TOKEN = new Token(Token.SUB);
	private static final Token MAJOR_TOKEN = new Token(Token.MAJOR);
	private static final Token MINOR_TOKEN = new Token(Token.MINOR);
	private static final Token MORE_TOKEN = new Token(Token.MORE);
	private static final Token RETURN_TOKEN = new Token(Token.RETURN);
	private static final Token TAB_TOKEN = new Token(Token.TAB);
	private static final Token TIMES_TOKEN = new Token(Token.TIMES);

	private final Map<String, List<Token>> filesTokens = new HashMap<String, List<Token>>();
	private final Set<Instance> allInstances = new HashSet<Instance>();
	
	private Set<Instance> thisInstances;
	private Mobi mobi;
	
	private final String packageName;
	private final JCodeModel codeModel;
	
	public Mar(String packageName, JCodeModel codeModel, Mobi mobi) {
		this.packageName = packageName;
		this.codeModel = codeModel;
		this.mobi = mobi;
	}
	
	public void loadAllCodeFiles() {
		File files = new File(INPUT_FOLDER+this.mobi.getContext().getUri()+"/");
		
		for (File file : files.listFiles()) {
			if (file.getName().endsWith(EXTENSION)) {
				String className = file.getName().substring(0, file.getName().length() - EXTENSION.length());
				this.filesTokens.put(className, LittleLanguage.loadLanguage(file));
			}
		}
	}

	public List<Token> getFilesTokens(String fileName) {
		return this.filesTokens.get(fileName);
	}
	
	public Set<String> getAllImplementedClasses() {
		return this.filesTokens.keySet();
	}
	
	public String getPackagePath(String name) {
		return this.packageName +".model."+ name;
	}
	
	public void createClassCode(String uri, JDefinedClass definedClass) {
		List<Token> tokens = this.filesTokens.get(uri);
		if (tokens == null) return;
		
		int instanceIndex = 0;
		
		this.thisInstances = this.mobi.getClassInstances(uri);
		this.allInstances.addAll(this.thisInstances);
		
		for (int i = 0; i < tokens.size(); i++) {
			Instance instance = new Instance(tokens.get(i).getCaracter());
			if (this.thisInstances.contains(instance)) break;
			instanceIndex = i + 1;
		}
		
		if (instanceIndex > 0) this.createStaticCaracteristics(definedClass, tokens.subList(0, instanceIndex));
		if (instanceIndex < tokens.size()) this.createObjectMethods(definedClass, tokens.subList(instanceIndex, tokens.size()));
	}
	
	private void createStaticCaracteristics(JDefinedClass definedClass, List<Token> tokens) {
		if (tokens.size() < 2) return;
				
		for (List<Token> line : Mar.breakInLines(tokens)) {
			this.createInvocationLine(definedClass, line);
		}
	}
	
	private void createInvocationLine(JDefinedClass definedClass, List<Token> line) {
		if (line.size() > 1) {
			
			if (line.get(1).getName().equals(Token.EQUAL)) {
				JType type = this.whichClass(line.get(2).getCaracter());
				JFieldVar field = definedClass.field(JMod.PUBLIC | JMod.STATIC| JMod.FINAL, type, line.get(0).getCaracter());
				field.init(this.createMinAttribution(line.subList(2, line.size())));
			}
		}
	}
	
	private void createMinAttribution(JBlock block, List<Token> tokens) {
		if (tokens.get(0).equals(BEGIN_TOKEN) && tokens.get(tokens.size()-1).equals(END_TOKEN)) {
			JInvocation jInvoke = block.invoke(this.createMinAttribution(tokens.subList(1, 2)), tokens.get(2).getCaracter());
			
			if (tokens.size() > 4) {
				jInvoke.arg(this.createMinAttribution(tokens.subList(3, 4)));
			}
		}
	}
	
	private JExpression createMinAttribution(List<Token> tokens) {
		
		if (tokens.get(0).equals(BEGIN_TOKEN) && tokens.get(tokens.size()-1).equals(END_TOKEN)) {
			JInvocation jInvoke = JExpr.invoke(this.createMinAttribution(tokens.subList(1, 2)), tokens.get(2).getCaracter());
			
			if (tokens.size() > 4) {
				jInvoke.arg(this.createMinAttribution(tokens.subList(3, 4)));
			}
			
			return jInvoke;
		}
		
		String op = Token.MORE;
		int index = tokens.indexOf(MORE_TOKEN);
		
		if (index < 0) {
			index = tokens.indexOf(TIMES_TOKEN);
			op = Token.TIMES;
		}
		
		if (index < 0) {
			index = tokens.indexOf(LESS_TOKEN);
			op = Token.SUB;
		}
		
		if (index < 0) {
			index = tokens.indexOf(MAJOR_TOKEN);
			op = Token.MAJOR;
		}
		
		if (index < 0) {
			index = tokens.indexOf(MINOR_TOKEN);
			op = Token.MINOR;
		}
		
		if (index < 0) {
			index = tokens.indexOf(IF_NOT_TOKEN);
			op = Token.IF_NOT_EQUAL;
		}
		
		if (index < 0) {
			String value = "";
			for (Token token : tokens) {
				if (token.getCaracter() == null) continue;
				value += this.replaceInstance(token.getCaracter());
			}
			
			JType type = this.whichClass(value);
			
			if (type == null) {
				if (value.contains(DOT)) {
					return this.invokeGetAttribute(value);
				}
				return JExpr.ref(value);
			} else {
				return this.getLitValue(type, value);
			}
		} else {
			JExpression expr1 = this.createMinAttribution(tokens.subList(0, index));
			JExpression expr2 = this.createMinAttribution(tokens.subList(index+1, tokens.size()));
			
			if (op.equals(Token.MORE)) {
				return expr1.plus(expr2);
				
			} else if (op.equals(Token.MAJOR)) {
				return expr1.gt(expr2);
			
			} else if (op.equals(Token.MINOR)) {
				return expr1.lte(expr2);

			} else if (op.equals(Token.IF_NOT_EQUAL)) {
				return expr1.ne(expr2);
			
			} else if (op.equals(Token.SUB)) {
				return expr1.minus(expr2);
				
			} else {
				return expr1.mul(expr2);
			}
		}
	}
	
	private String[] convertInstancesToCalled(String instances) {
		String[] called = instances.split(DOT);
		
		String[] result = {
				this.replaceInstance(called[0]),
				this.fromInstanceToVarieble(called[1])[1]
			};
		
		return result;
	}
	
	private JInvocation invokeGetAttribute(String instances) {
		String[] called = this.convertInstancesToCalled(instances);
		called[1] = this.attributeToGetMethod(called[1]);
		return JExpr.invoke(JExpr.ref(called[0]), called[1]);
	}
	
	private JInvocation invokeSetAttribute(String instances, JExpression arg) {
		String[] called = this.convertInstancesToCalled(instances);
		called[1] = this.attributeToSetMethod(called[1]);
		
		JInvocation setMethod = JExpr.invoke(JExpr.ref(called[0]), called[1]);
		setMethod.arg(arg);
		return setMethod;
	}
	
	private void createIfNotExist(String instances, JType type) {
		String[] called = instances.split(DOT);
		for (Instance instance : this.allInstances) {
			if (instance.getUri().equals(called[0])) {
				
				Set<mobi.core.concept.Class> classSet = this.mobi.getAllInstanceClassRelation().get(called[0]);
				mobi.core.concept.Class mobiClass = classSet.iterator().next();
				JDefinedClass definedClass = this.codeModel._getClass(this.getPackagePath(mobiClass.getUri()));
				
				JFieldVar var = definedClass.fields().get(called[1]);
				if (var == null) {
					this.createAttribute(definedClass, type, called[1]);
				}
			}
		}
	}
	
	private String attributeToGetMethod(String value) {
		return GET+ value.substring(0, 1).toUpperCase() + value.substring(1, value.length());
	}
	
	private String attributeToSetMethod(String value) {
		return SET+ value.substring(0, 1).toUpperCase() + value.substring(1, value.length());
	}
	
	private String replaceInstance(final String name) {
		for (Instance instance : this.thisInstances) {
			if (instance.getUri().equals(name)) {
				return "this";
			}
		}
		
		return this.fromInstanceToVarieble(name)[1];
	}

	private void createObjectMethods(JDefinedClass definedClass, List<Token> tokens) {
		if (tokens.size() < 2) return;
		
		List<List<Token>> lines = Mar.breakInLines(tokens);
		List<List<List<Token>>> blocks = Mar.breakInBlocks(lines.subList(1, lines.size()));
		
		for (int i=0; i < blocks.size(); i++) {
			List<List<Token>> block = blocks.get(i);
			
			if ((i+1) < blocks.size()) {
				List<List<Token>> nextBlock = blocks.get(i+1);
				String nextMethodName = nextBlock.get(0).get(0).getCaracter();
				
				if (block.get(0).get(0).getCaracter().equals(nextMethodName)) {
					this.createMethod(definedClass, block, nextBlock);
					i++;
					continue;
				}
			}

			if (block.size() > 1) {
				this.createMethod(definedClass, block, null);
			} else {
				// Se for attributo vem por aqui
				// A fazer
			}
		}
	}

	private void createMethod(JDefinedClass definedClass, List<List<Token>> block, List<List<Token>> nextBlock) {
		String param = null;
		String methodName = block.get(0).get(0).getCaracter();
		
		if (block.get(0).size() > 1) param = block.get(0).get(1).getCaracter();
		
		block = block.subList(1, block.size());
		
		List<Token> terms = new ArrayList<Token>();
		JType type = null;

		for (List<Token> line : block) {
			int index = line.indexOf(new Token(Token.RETURN));
			if (index >= 0) {
				for (Token token : line.subList(index+1, line.size())) terms.add(token);
			}
			
			if (terms.size() < 1) type = this.codeModel.VOID;
			else type = this.getClassType(terms);
		}
		
		JMethod method = definedClass.method(JMod.PUBLIC, type, methodName);
		if (param != null) {
			String[] variable = this.fromInstanceToVarieble(param);
			method.param(this.getClassType(variable[0]), variable[1]);
		}
		
		this.implementMethod(definedClass, method, block, nextBlock);
	}
	
	private String[] fromInstanceToVarieble(String instance) {
		String[] variable = { "", instance };
		Set<mobi.core.concept.Class> classSet = this.mobi.getAllInstanceClassRelation().get(instance);
		
		if (classSet == null || classSet.isEmpty()) return variable;
		
		mobi.core.concept.Class className = classSet.iterator().next();
		
		variable[0] = className.getUri();
		variable[1] = className.getUri().substring(0, 1).toLowerCase();
		variable[1] += className.getUri().substring(1, className.getUri().length());
		return variable;
	}

	private void implementMethod(JDefinedClass definedClass, JMethod method, List<List<Token>> block, List<List<Token>> nextBlock) {
		
		JForEach forInferred = null;
		JConditional jIF = null;
		JBlock pBlock = null;
		int oldIndex = 0;
		
		if (nextBlock != null) {
			InstanceRelation param = new InstanceRelation(block.get(0).get(1).getCaracter());
			
			mobi.core.concept.Class thisClass = this.mobi.getClass(definedClass.name());
			
			JInvocation invoke = null;
			Relation rr = null;
			
			for (Relation relation : this.mobi.getAllClassRelations(thisClass)) {
				if (relation.getInstanceRelationMapB().values().contains(param)) {
					invoke = JExpr._this().invoke(this.attributeToGetMethod(relation.getUri().split(DOT)[1])).invoke("contains");
					invoke.arg(this.createMinAttribution(nextBlock.get(0).subList(1, 2)));
					jIF = method.body()._if(invoke);
				}
				rr = relation;
			}
			
			if (invoke == null) {
				
				if (nextBlock.get(0).size() > 1) {
					invoke = JExpr._this().invoke(this.attributeToGetMethod(rr.getUri().split(DOT)[1])).invoke("contains");
					invoke.arg(this.createMinAttribution(nextBlock.get(0).subList(1, 2)));
				} else {
					String attribute = block.get(0).get(0).getCaracter().split(DOT)[1];
					invoke = JExpr._this().invoke(this.attributeToGetMethod(attribute)).invoke("equals");
					invoke.arg(JExpr.lit(Integer.parseInt(nextBlock.get(1).get(2).getCaracter())));
				}
				jIF = method.body()._if(invoke);
			}
			
			List<Token> newLine = new ArrayList<Token>();
			newLine.add(new Token(Token.ELSE));
			nextBlock.add(newLine);
			
			block.addAll(0, nextBlock);
		}
		
		for (int i = 0; i < block.size(); i++) {
			List<Token> line = block.get(i);
			List<Token> nextLine = null;
			
			int firstIndex = 0;
			try {
				while (line.get(firstIndex).equals(TAB_TOKEN)) firstIndex++;
				if (!line.contains(ELSE_TOKEN) && (oldIndex > firstIndex)) {
					pBlock = null;
					jIF = null;
				}
				oldIndex = firstIndex;
			} catch (IndexOutOfBoundsException iobe) {
				continue;
			}
			
			if (i < block.size()-1) {
				nextLine = block.get(i+1);
				List<Token> newLine = this.convertFor(line, nextLine);
				if (newLine != null) line = newLine;
			}
			
			Token firstToken = line.get(firstIndex);
			
			if (firstToken.getCaracter() != null && firstToken.getCaracter().startsWith("--")) continue;
			
			JBlock mBlock = method.body();
			
			if (jIF != null) mBlock = jIF._then();
			if (pBlock != null) mBlock = pBlock;
			
			if (line.contains(ELSE_TOKEN)) {
				if (jIF != null) {
					if (line.size() > firstIndex+1) {
						jIF = jIF._elseif(this.createIfExpression(definedClass, line.subList(firstIndex+2, line.size())));
						pBlock = null;
					} else {
						pBlock = jIF._else();
						jIF = null;
					}
				}
				
			} else if (line.contains(IF_TOKEN)) {
				
				jIF = mBlock._if(this.createIfExpression(definedClass, line.subList(firstIndex+1, line.size())));
				pBlock = null;
			
			} else if (line.contains(FOR_TOKEN)) {
				String[] called = line.get(1).getCaracter().split(DOT);
				String instanceType = this.fromInstanceToVarieble(called[0])[0];
				String[] attribute = this.fromInstanceToVarieble(called[1]);
				
				forInferred = mBlock.forEach(this.getClassType(attribute[0]), attribute[1], this.invokeCollection(instanceType, attribute[0]));
				pBlock = forInferred.body();

				if (nextLine != null) for (Token token : nextLine) {
					if (token.getCaracter() != null && token.getCaracter().contains(DOT)) {
						token.setName(Token.NO_TOKEN);
						token.setCaracter(attribute[1]);
					}
				}
				
			} else if (line.contains(EQUAL_TOKEN)) {
				
				Token sideA = firstToken;
				List<Token> sideB = line.subList(line.indexOf(EQUAL_TOKEN)+1, line.size());
				
				JFieldVar fieldB = null;
				JType type;
				
				if (sideB.size() == 1) {
					fieldB = definedClass.fields().get(sideB.get(0).getCaracter());
					if (fieldB == null) {
						type = this.whichClass(sideB.get(0).getCaracter());
					} else {
						type = fieldB.type();
					}
				} else {
					type = this.getClassType(sideB);
				}
				
				if (sideA.getCaracter().contains(DOT)) {
					this.createIfNotExist(sideA.getCaracter(), this.getClassType(sideB));
					mBlock.block().add(this.invokeSetAttribute(sideA.getCaracter(), this.createMinAttribution(sideB)));
				} else {
					JVar fieldA = definedClass.fields().get(sideA.getCaracter());
					if ((fieldA == null)) {
						for (Object obj : method.body().getContents()) {
							if (obj instanceof JVar) {
								JVar jVar = (JVar) obj;
								if (jVar.name().equals(sideA.getCaracter())) {
									fieldA = jVar;
								}
							}
						}
					}
					
					if ((fieldA == null)) {
						fieldA = method.body().decl(type, sideA.getCaracter());
						if (fieldB == null) {
							fieldA.init(this.createMinAttribution(sideB));
						} else {
							fieldA.init(JExpr.ref(sideB.get(0).getCaracter()));
						}
					} else {
						if (fieldB == null) {
							mBlock.assign(fieldA, this.createMinAttribution(sideB));
						} else {
							mBlock.assign(fieldA, JExpr.ref(sideB.get(0).getCaracter()));
						}
					}
				}
				
				if (forInferred != null) {
					forInferred = null;
					pBlock = null;
				}
				
			} else if (line.contains(RETURN_TOKEN)) {
				List<Token> sideB = line.subList(firstIndex+1, line.size());
				mBlock._return(this.createMinAttribution(sideB));
				
			} else {
				this.createMinAttribution(mBlock, line.subList(firstIndex, line.size()));
			}
		}
	}
	
	private JExpression invokeCollection(String className, String attributeName) {
		Class mobiClass = this.mobi.getClass(className);
		for (Relation relation : this.mobi.getAllClassCompositionRelations(mobiClass)) {
			System.out.println("$ C="+ className  +" R="+ relation.getUri());
			if (relation.getUri().contains(DOT) && relation.getUri().split(DOT)[2].equals(attributeName)) {
				return JExpr.ref(className.toLowerCase()).invoke(this.attributeToGetMethod(relation.getUri().split(DOT)[1]));
			}
		}
		return null;
	}
	
	private List<Token> convertFor(final List<Token> line, final List<Token> nextLine) {
		
		int il = -1; int in = -1;
		
		for (int i = 0; i < line.size(); i++) {
			Token token = line.get(i);
			if (token.getCaracter() != null && token.getCaracter().contains(DOT)) {
				il = i; break;
			}
		}
		
		for (int i = 0; i < nextLine.size(); i++) {
			Token token = nextLine.get(i);
			if (token.getCaracter() != null && token.getCaracter().contains(DOT)) {
				in = i; break;
			}
		}
		
		if (il < 0 || in < 0 || (!line.subList(0, il).equals(nextLine.subList(0, in)))) return null;
		
		String[] tokenLine = line.get(il).getCaracter().split(DOT);
		String[] tokenNext = nextLine.get(in).getCaracter().split(DOT);
		
		if (!tokenLine[0].equals(tokenNext[0])) return null;
		
		String[] attribute = this.fromInstanceToVarieble(tokenLine[1]);
		String[] attributeNext = this.fromInstanceToVarieble(tokenNext[1]);
		
		if (!attribute[0].equals(attributeNext[0])) return null;
		
		final List<Token> result = new ArrayList<Token>();
		result.add(FOR_TOKEN);
		result.add(line.get(il));
		result.addAll(line);
		
		return result;
	}
	
	private JExpression createIfExpression(JDefinedClass definedClass, List<Token> tokens) {
		
		int index = tokens.indexOf(new Token(Token.AND));
		
		if (index < 0) {
			return this.createMinAttribution(tokens);
		} else {
			JExpression expr1 = this.createMinAttribution(tokens.subList(0, index));
			JExpression expr2 = this.createMinAttribution(tokens.subList(index+1, tokens.size()));
			return expr1.cand(expr2);
		}
	}

	private JType whichClass(String attribute) {
		try {
			return this.codeModel.ref(Integer.valueOf(attribute).getClass());
		} catch (Exception e) {			
			try {
				return this.codeModel.ref(Double.valueOf(attribute).getClass());
			} catch (Exception e1) {
				try {
					return this.codeModel.ref(new Date(Long.valueOf(attribute)).getClass());
				} catch (Exception e2) {
					if (attribute.equals("YES") || attribute.equals("NO")) {
						return this.codeModel.ref(Boolean.valueOf(attribute).getClass());
					} else if (attribute.startsWith("\"")) {
						return this.codeModel.ref(String.valueOf(attribute).getClass());
					} else {
						return null;
					}
				}
			}
		}
	}
	
	public JType getClassType(List<Token> tokens) {
		if (tokens.size() > 0) {
			JType type = this.whichClass(tokens.get(0).getCaracter());
			if (type != null) return type;
		}
		
		if (tokens.contains(new Token(Token.TIMES)) || tokens.contains(new Token(Token.MORE))) {
			return this.codeModel.ref(Double.class);
		} else {
			for (Token token : tokens) {
				if (token.getCaracter() == null) continue;
				
				if (token.getCaracter().contains(DOT)) {
					String[] attribute = token.getCaracter().split(DOT);
					String[] instance = this.fromInstanceToVarieble(attribute[0]);
					JDefinedClass definedClass = this.codeModel._getClass(this.getPackagePath(instance[0]));
					JFieldVar field = definedClass.fields().get(attribute[1]);
					if (field == null) {
						String[] var = this.fromInstanceToVarieble(attribute[1]);
						field = definedClass.fields().get(var[1]);
						if (field == null) {
							for (JFieldVar fieldVar : definedClass.fields().values()) {
								if (fieldVar.type().name().contains(var[0])) {
									field = fieldVar;
									break;
								}
							}
						}
					}
					return field.type();
				} else if (tokens.get(0).getCaracter().contains("valor")) {
					return this.codeModel.ref(Double.class);
				}
			}
		}
		return this.codeModel.ref(String.class);
	}
	
	public JType getClassType(String name) {
		if (name.startsWith("List-")) {
			return this.codeModel.ref(List.class).narrow(this.getClassType(name.split("-")[1]));
		} else if (AttributeTypeEnum.contains(name)) {
			return this.codeModel.ref(name);
		} else {
			JDefinedClass type = this.codeModel._getClass(this.getPackagePath(name));
			if (type == null) {
				try {
					return this.codeModel.ref(this.getPackagePath(name)).elementType();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return type;
		}
	}
	
	public JType getClassType(Attribute attribute) {
		JType type = null;

		switch (attribute.getType()) {
		case INTEGER:
			type = this.codeModel.ref(Integer.class);
			break;
		case LONG:
			type = this.codeModel.ref(Long.class);
			break;
		case DOUBLE:
			type = this.codeModel.ref(Double.class);
			break;
		case STRING:
			type = this.codeModel.ref(String.class);
			break;
		case DATE:
			type = this.codeModel.ref(Date.class);
			break;
		case BOOLEAN:
			type = this.codeModel.ref(Boolean.class);
			break;
		}

		if (attribute.isMany()) {
			return this.codeModel.ref(List.class).narrow(type);
		} else {
			return type;
		}
	}
	
	private JExpression getLitValue(JType type, String value) {
		switch (type.name()) {
		case "Integer":
			return this.getLitValue(AttributeTypeEnum.INTEGER, value);
		case "Double":
			return this.getLitValue(AttributeTypeEnum.DOUBLE, value);
		case "String":
			return this.getLitValue(AttributeTypeEnum.STRING, value);
		case "Date":
			return this.getLitValue(AttributeTypeEnum.DATE, value);
		case "Boolean":
			value = (value.equals("YES")) ? "true" : "false";
			return this.getLitValue(AttributeTypeEnum.BOOLEAN, value);
		default:
			return JExpr._null();
		}
	}
	
	public JExpression getLitValue(AttributeTypeEnum type, String value) {
		switch (type) {
		case INTEGER:
			return JExpr.lit(Integer.valueOf(value));
		case DOUBLE:
			return JExpr.lit(Double.valueOf(value));
		case STRING:
			return JExpr.lit(value);
		case DATE:
			return JExpr.lit(value);
		case BOOLEAN:
			return JExpr.lit(Boolean.valueOf(value));
		default:
			return JExpr._null();
		}
	}
	
	public JFieldVar createAttribute(JDefinedClass definedClass, JType type, String name) {
		JFieldVar dataType = definedClass.field(JMod.PRIVATE, type, name);
		
		this.generateGetMethod(definedClass, dataType);
		this.generateSetMethod(definedClass, dataType);
		
		return dataType;
	}
	
	/**
	 * Generates a public [classType] get() method to the given class
	 * @param definedClass : The class to generate the method
	 * @param attribute : The class attribute to be returned
	 */
	public void generateGetMethod(final JDefinedClass definedClass, final JFieldVar attribute) {
		String name = GET + attribute.name().substring(0, 1).toUpperCase() + attribute.name().substring(1, attribute.name().length());
		JMethod getMethod = definedClass.method(JMod.PUBLIC, attribute.type(), name);
		getMethod.body()._return(attribute);
	}
	
	/**
	 * Generates a public void set([classType]) method to the given class
	 * @param definedClass : The class to generate the method
	 * @param attribute : The class attribute to be modified
	 */
	public void generateSetMethod(final JDefinedClass definedClass, final JFieldVar attribute) {
		String name = SET + attribute.name().substring(0, 1).toUpperCase() + attribute.name().substring(1, attribute.name().length());
		JMethod setMethod = definedClass.method(JMod.PUBLIC, this.codeModel.VOID, name);
		
		setMethod.param(attribute.type(), attribute.name());
		setMethod.body().assign(JExpr._this().ref(definedClass.fields().get(attribute.name())), attribute);
	}
	
	private static List<List<Token>> breakInLines(List<Token> tokens) {
		
		final List<List<Token>> lines = new ArrayList<List<Token>>();
		
		for (int fromIndex = 0; fromIndex < tokens.size(); fromIndex++) {
			int eolIndex = tokens.subList(fromIndex, tokens.size()).indexOf(new Token(Token.EOL)) + fromIndex;

			if (fromIndex <= eolIndex) {
				lines.add(tokens.subList(fromIndex, eolIndex));
				fromIndex = eolIndex;
			}
		}
		return lines;
	}
	
	private static List<List<List<Token>>> breakInBlocks(List<List<Token>> lines) {
		
		final List<List<List<Token>>> blocks = new ArrayList<List<List<Token>>>();
		List<List<Token>> block = null;
		
		for (List<Token> line : lines) {
			if (line.size() <= 1) continue;
			
			if (line.get(0).getName().equals(Token.TAB)) {
				
				if (line.get(1).getName().equals(Token.TAB)) {
					block.add(line.subList(2, line.size()));
				} else {
					if (block != null) blocks.add(block);
					block = new ArrayList<List<Token>>();
					block.add(line.subList(1, line.size()));
				}

			} else break;
		}
		
		blocks.add(block);
		return blocks;
	}

}