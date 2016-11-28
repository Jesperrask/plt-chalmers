import java.util.*;
import CPP.Absyn.*;

public class Interpreter {

	private Scanner in;

	// signature
	private Map<String,DFun> symTab;

	//env
	List<Map<String, Value>> env = new ArrayList<>();

	// Share type constants
	public final Type BOOL   = new Type_bool();
	public final Type INT    = new Type_int();
	public final Type DOUBLE = new Type_double();
	public final Type VOID   = new Type_void();

	//predefined functions
	DFun printInt = new DFun(VOID, "printInt", singleArg(INT), new ListStm());
	DFun printDouble = new DFun(VOID, "printDouble", singleArg(DOUBLE), new ListStm());
	DFun readInt = new DFun(INT, "readInt", new ListArg(), new ListStm());
	DFun readDouble = new DFun(DOUBLE, "readDouble", new ListArg(), new ListStm());


	// entrypoint
	public void interpret(Program p) {
		in = new Scanner(System.in);
		// environment
		p.accept(new ProgramVisitor(), null);
		in.close();
	}

	public class ProgramVisitor implements Program.Visitor<Void,Void>
	{

		public Void visit(PDefs p, Void arg) {
			/* Code For PDefs Goes Here */

			//initialize environment
			newBlock();

			//predefined functions
			symTab = new TreeMap<String,DFun>();
			symTab.put(printInt.id_   , printInt);
			symTab.put(printDouble.id_, printDouble);
			symTab.put(readInt.id_, readInt);
			symTab.put(readDouble.id_, readDouble);

			// Extend symbol table by all the fn definitions
			for (Def x: p.listdef_) {
				x.accept(new DefVisitor(), arg);
			}

			DFun main = symTab.get("main");
			for(Stm s : main.liststm_){
				s.accept(new StmVisitor(), arg);
			}

			return null;

		}
	}

	public class DefVisitor implements Def.Visitor<Void,Void>
	{
		public Void visit(CPP.Absyn.DFun p, Void arg)
		{ /* Code For DFun Goes Here */
			DFun fun = new DFun(p.type_, p.id_, p.listarg_, p.liststm_);
			symTab.put(fun.id_, fun);
			return null;
		}
	}

	////////////////////////////// Statements //////////////////////////////

	public class StmVisitor implements Stm.Visitor<Value,Void>
	{
		public Value visit(CPP.Absyn.SExp p, Void arg)
		{ /* Code For SExp Goes Here */
			p.exp_.accept(new ExpVisitor(), arg);
			return null;
		}
		public Value visit(CPP.Absyn.SDecls p, Void arg)
		{ /* Code For SDecls Goes Here */
			for (String x: p.listid_)
			{ 
				newVar(x, new VNull());
			}
			return null;
		}
		public Value visit(CPP.Absyn.SInit p, Void arg)
		{ /* Code For SInit Goes Here */
			String id = p.id_;
			Value v = p.exp_.accept(new ExpVisitor(), arg);
			newVar(id, v);
			return null;
		}
		public Value visit(CPP.Absyn.SReturn p, Void arg)
		{ /* Code For SReturn Goes Here */
			return p.exp_.accept(new ExpVisitor(), arg);
		}

		public Value visit(CPP.Absyn.SWhile p, Void arg)
		{ /* Code For SWhile Goes Here */
			Value condition = p.exp_.accept(new ExpVisitor(), arg);
			if(((VBool)condition).value == true){
				Value v = p.stm_.accept(new StmVisitor(), arg);
				if(p.stm_ instanceof SReturn)
					return v;
				else
					p.accept(new StmVisitor(), arg);
			}
			return null;
		}

		public Value visit(CPP.Absyn.SBlock p, Void arg)
		{ /* Code For SBlock Goes Here */
			newBlock();
			for (Stm s: p.liststm_) {
				Value v = s.accept(new StmVisitor(), arg);
				if(s instanceof SReturn){
					popBlock();
					return v;
				}
			}
			popBlock();
			return null;
		}

		public Value visit(CPP.Absyn.SIfElse p, Void arg)
		{ /* Code For SIfElse Goes Here */
			Value condition = p.exp_.accept(new ExpVisitor(), arg);
			if(((VBool)condition).value == true){
				Value thenStmt = p.stm_1.accept(new StmVisitor(), arg);
				return thenStmt;
			} else{
				Value elseStmt = p.stm_2.accept(new StmVisitor(), arg);
				return elseStmt;
			}
		}
	}

	////////////////////////////// Expressions //////////////////////////////

	public class ExpVisitor implements Exp.Visitor<Value,Void>
	{
		public Value visit(CPP.Absyn.ETrue p, Void arg)
		{ /* Code For ETrue Goes Here */
			return new VBool(true);
		}
		public Value visit(CPP.Absyn.EFalse p, Void arg)
		{ /* Code For EFalse Goes Here */
			return new VBool(false);
		}
		public Value visit(CPP.Absyn.EInt p, Void arg)
		{ /* Code For EInt Goes Here */
			return new VInt(p.integer_);
		}
		public Value visit(CPP.Absyn.EDouble p, Void arg)
		{ /* Code For EDouble Goes Here */
			return new VDouble(p.double_);
		}
		public Value visit(CPP.Absyn.EId p, Void arg)
		{ /* Code For EId Goes Here */
			return lookupVar(p.id_);
		}
		public Value visit(CPP.Absyn.EApp p, Void arg)
		{ /* Code For EApp Goes Here */
			//evaluate all argument expressions in current environment
			List<Value> evaluatedActualParam = new ArrayList<Value>();
			for (Exp a: p.listexp_)
			{ 
				Value v = a.accept(new ExpVisitor(), arg);
				evaluatedActualParam.add(v);
			}


			//get function record from symbol table
			String fId = p.id_;
			DFun fun = symTab.get(fId);
			if(fun == null)
				throw new RuntimeException("No such function " + fId);

			//build new environment for function execution
			//add the evaluated parameters to the function's execution environment 
			//using the function declaration names from symbol table
			newBlock();
			int i = 0;
			for(Arg a : fun.listarg_){
				ADecl x = (ADecl) a;
				newVar(x.id_, evaluatedActualParam.get(i));
				i++;
			}
			Value retVal = null;
			if(fun.id_.equals("printInt")){
				System.out.println(
						((VInt)lookupVar(((ADecl) fun.listarg_.get(0)).id_)).value);
			} else if(fun.id_.equals("printDouble")){
				System.out.println(
						((VDouble)lookupVar(((ADecl) fun.listarg_.get(0)).id_)).value);
			} else if(fun.id_.equals("readInt")){
				int num = in.nextInt();
				retVal = new VInt(num);
			}else if(fun.id_.equals("readDouble")){
				double num = in.nextDouble();
				retVal = new VDouble(num);
			}
			else{
				//execute statements in function
				for(Stm s : fun.liststm_){
					retVal = s.accept(new StmVisitor(), arg);
					if(s instanceof SReturn || retVal!=null)
						break;
				}
			}
			popBlock();
			return retVal;
		}
		public Value visit(CPP.Absyn.EPostIncr p, Void arg)
		{ /* Code For EPostIncr Goes Here */
			EId id = (EId) p.exp_;
			Value v = lookupVar(id.id_);
			Value updatedV;
			if(v instanceof VDouble)
				updatedV = new VDouble(((VDouble)v).value + 1.0);
			else if(v instanceof VInt)
				updatedV = new VInt(((VInt)v).value + 1);
			else
				throw new RuntimeException("value must be a numeric type");
			updateEnv(id.id_, updatedV);
			return v;
		}
		public Value visit(CPP.Absyn.EPostDecr p, Void arg)
		{ /* Code For EPostDecr Goes Here */
			EId id = (EId) p.exp_;
			Value v = lookupVar(id.id_);
			Value updatedV;
			if(v instanceof VDouble)
				updatedV = new VDouble(((VDouble)v).value - 1.0);
			else if(v instanceof VInt)
				updatedV = new VInt(((VInt)v).value - 1);
			else
				throw new RuntimeException("value must be a numeric type");
			updateEnv(id.id_, updatedV);
			return v;
		}
		public Value visit(CPP.Absyn.EPreIncr p, Void arg)
		{ /* Code For EPreIncr Goes Here */
			EId id = (EId) p.exp_;
			Value v =lookupVar(id.id_);
			Value updatedV;
			if(v instanceof VDouble)
				updatedV = new VDouble(((VDouble)v).value + 1.0);
			else if(v instanceof VInt)
				updatedV = new VInt(((VInt)v).value + 1);
			else
				throw new RuntimeException("value must be a numeric type");
			updateEnv(id.id_, updatedV);
			return updatedV;
		}
		public Value visit(CPP.Absyn.EPreDecr p, Void arg)
		{ /* Code For EPreDecr Goes Here */
			/* Code For EPreIncr Goes Here */
			EId id = (EId) p.exp_;
			Value v =lookupVar(id.id_);
			Value updatedV;
			if(v instanceof VDouble)
				updatedV = new VDouble(((VDouble)v).value - 1.0);
			else if(v instanceof VInt)
				updatedV = new VInt(((VInt)v).value - 1);
			else
				throw new RuntimeException("value must be a numeric type");
			updateEnv(id.id_, updatedV);
			return updatedV;
		}
		public Value visit(CPP.Absyn.ETimes p, Void arg)
		{ /* Code For ETimes Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
			if(v1 instanceof VInt)
				return new VInt(((VInt)v1).value * ((VInt)v2).value); 
			else if(v1 instanceof VDouble)
				return new VDouble(((VDouble)v1).value * ((VDouble)v2).value); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.EDiv p, Void arg)
		{ /* Code For EDiv Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
			if(v1 instanceof VInt)
				return new VInt(((VInt)v1).value / ((VInt)v2).value); 
			else if(v1 instanceof VDouble)
				return new VDouble(((VDouble)v1).value / ((VDouble)v2).value); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.EPlus p, Void arg)
		{ /* Code For EPlus Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
			if(v1 instanceof VInt)
				return new VInt(((VInt)v1).value + ((VInt)v2).value); 
			else if(v1 instanceof VDouble)
				return new VDouble(((VDouble)v1).value + ((VDouble)v2).value); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.EMinus p, Void arg)
		{ /* Code For EMinus Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
			if(v1 instanceof VInt)
				return new VInt(((VInt)v1).value - ((VInt)v2).value); 
			else if(v1 instanceof VDouble)
				return new VDouble(((VDouble)v1).value - ((VDouble)v2).value); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.ELt p, Void arg)
		{ /* Code For ELt Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
			if(v1 instanceof VInt)
				return new VBool( ((VInt)v1).value < ((VInt)v2).value); 
			else if(v1 instanceof VDouble)
				return new VBool(((VDouble)v1).value < ((VDouble)v2).value); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.EGt p, Void arg)
		{ /* Code For EGt Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
			if(v1 instanceof VInt)
				return new VBool( ((VInt)v1).value > ((VInt)v2).value); 
			else if(v1 instanceof VDouble)
				return new VBool(((VDouble)v1).value > ((VDouble)v2).value); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.ELtEq p, Void arg)
		{ /* Code For ELtEq Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
			if(v1 instanceof VInt)
				return new VBool( ((VInt)v1).value <= ((VInt)v2).value); 
			else if(v1 instanceof VDouble)
				return new VBool(((VDouble)v1).value <= ((VDouble)v2).value); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.EGtEq p, Void arg)
		{ /* Code For EGtEq Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
			if(v1 instanceof VInt)
				return new VBool( ((VInt)v1).value >= ((VInt)v2).value); 
			else if(v1 instanceof VDouble)
				return new VBool(((VDouble)v1).value >= ((VDouble)v2).value); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.EEq p, Void arg)
		{ /* Code For EEq Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
			if(v1 instanceof VInt)
				return new VBool( ((VInt)v1).value.equals(((VInt)v2).value)); 
			else if(v1 instanceof VDouble)
				return new VBool(((VDouble)v1).value.equals(((VDouble)v2).value)); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.ENEq p, Void arg)
		{ /* Code For ENEq Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			Value v2 = p.exp_2.accept(new ExpVisitor(), arg);

			if(v1 instanceof VInt)
				return new VBool(!((VInt)v1).value.equals(((VInt)v2).value)); 
			else if(v1 instanceof VDouble)
				return new VBool(!((VDouble)v1).value.equals(((VDouble)v2).value)); 
			throw new RuntimeException("value must be a numeric type");
		}
		public Value visit(CPP.Absyn.EAnd p, Void arg)
		{ /* Code For EAnd Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			if(((VBool)v1).value == true){
				Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
				return (VBool) v2;
			} else{
				return new VBool(false);
			}
		}
		public Value visit(CPP.Absyn.EOr p, Void arg)
		{ /* Code For EOr Goes Here */
			Value v1 = p.exp_1.accept(new ExpVisitor(), arg);
			if(((VBool)v1).value == false){
				Value v2 = p.exp_2.accept(new ExpVisitor(), arg);
				return (VBool) v2;
			} else{
				return new VBool(true);
			}
		}
		public Value visit(CPP.Absyn.EAss p, Void arg)
		{ /* Code For EAss Goes Here */
			Value v = p.exp_2.accept(new ExpVisitor(), arg);
			EId id = (EId) p.exp_1;
			updateEnv(id.id_, v);
			return v;
		}
	}

	//ENVIRONMENT Functions
	public void newBlock() {
		env.add(0, new TreeMap());
	}

	public void popBlock() {
		env.remove(0);
	}

	public void newVar(String x, Value value) {
		env.get(0).put(x, value);
	}

	public Value lookupVar(String x) {
		for (Map<String,Value> m : env) {
			Value v = m.get(x);
			if (v != null) return v;
		}
		throw new TypeException("uninitialized variable " + x);
	}

	public Value updateEnv(String x, Value v){
		for (Map<String,Value> m : env) {
			Value oldV = m.get(x);
			if (oldV != null) {
				m.put(x, v);
				return v;
			}
		}
		throw new TypeException("update failed - unbound variable " + x);
	}

	public ListArg singleArg (Type t) {
		ListArg l = new ListArg();
		l.add(new ADecl(t, "x"));
		return l;
	}

}
