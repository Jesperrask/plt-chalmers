import CPP.Absyn.*;
import java.util.*;

public class TypeChecker
{
	// Global signature of functions
	private Map<String,FunType> sig;

	// Stack of contexts
	private List<Map<String,Type>> cxt;

	// Return type of function we are checking
	private Type returnType;

	// Share type constants
	public final Type BOOL   = new Type_bool();
	public final Type INT    = new Type_int();
	public final Type DOUBLE = new Type_double();
	public final Type VOID   = new Type_void();

	// Entry point

	public Program typecheck(Program p) {
		return p.accept(new ProgramVisitor(), null);
	}

	////////////////////////////// Program //////////////////////////////

	public class ProgramVisitor implements Program.Visitor<Program,Void>
	{
		public Program visit(CPP.Absyn.PDefs p, Void arg)
		{
			// Put primitive functions into signature
			sig = new TreeMap<String,FunType>();
			sig.put("printInt"   , new FunType(VOID, singleArg(INT)));
			sig.put("readInt"    , new FunType(INT, new ListArg()));
			sig.put("printDouble", new FunType(VOID, singleArg(DOUBLE)));
			sig.put("readDouble" , new FunType(DOUBLE, new ListArg()));

			// Extend signature by all the definitions
			for (Def x: p.listdef_) {
				DFun d = (DFun)x;
				// make sure x is not already in the signature.
				if (sig.get(d.id_) != null)
					throw new TypeException("function " + d.id_ + " has already been declared");
				sig.put(d.id_, new FunType(d.type_, d.listarg_));
			}

			ListDef typedDefs = new ListDef();
			
			// Check definitions
			for (Def x: p.listdef_) {
				typedDefs.add(x.accept(new DefVisitor(), arg));
			}

			FunType ft = sig.get("main");
			if (ft == null)
				throw new TypeException("main function missing");
			if(!ft.returnType.equals(INT))
				throw new TypeException("main function is not Int!");
			if(!ft.args.isEmpty())
				throw new TypeException("main function cannot have any arguments!");
			
			return new PDefs(typedDefs);
		}
	}

	public ListArg singleArg (Type t) {
		ListArg l = new ListArg();
		l.add(new ADecl(t, "x"));
		return l;
	}

	////////////////////////////// Function //////////////////////////////

	public class DefVisitor implements Def.Visitor<Def,Void>
	{
		public Def visit(CPP.Absyn.DFun p, Void arg)
		{
			// set return type and initial context
			returnType = p.type_;
			cxt = new LinkedList<Map<String,Type>>();
			cxt.add(new TreeMap<String,Type>());

			ListArg args = new ListArg();
			// add all function parameters to context
			for (Arg a: p.listarg_) {
				args.add(a.accept(new ArgVisitor(), arg));
			}

			ListStm typedStms = new ListStm();
			// check function statements
			for (Stm s: p.liststm_) {
				typedStms.add(s.accept(new StmVisitor(), arg));
			}

			return new DFun(p.type_, p.id_, args, typedStms);
		}
	}

	///////////////////////// Function argument /////////////////////////

	// Add a type declaration to the context
	public class ArgVisitor implements Arg.Visitor<Arg,Void>
	{
		public Arg visit(CPP.Absyn.ADecl p, Void arg)
		{
			newVar(p.id_, p.type_);
			return p;
		}
	}

	////////////////////////////// Statement //////////////////////////////

	public class StmVisitor implements Stm.Visitor<Stm,Void>
	{
		public Stm visit(CPP.Absyn.SExp p, Void arg)
		{
			Exp typedExp = p.exp_.accept(new ExpVisitor(), arg);
			return new SExp(typedExp);
		}

		public Stm visit(CPP.Absyn.SDecls p, Void arg)
		{
			for(String id : p.listid_){
				newVar(id, p.type_);
			}
			return p;
		}

		// E.g. "int x = 1";
		public Stm visit(CPP.Absyn.SInit p, Void arg)
		{
			Exp typedExp = check (p.type_, p.exp_.accept(new ExpVisitor(), arg));
			newVar (p.id_, p.type_);
			return new SInit(p.type_, p.id_, typedExp);
		}

		public Stm visit(CPP.Absyn.SReturn p, Void arg)
		{
			Exp typedExp = check (returnType, p.exp_.accept(new ExpVisitor(), arg));
			return new SReturn(typedExp);
		}

		public Stm visit(CPP.Absyn.SWhile p, Void arg)
		{
			newBlock();
			Exp typedExp = check(BOOL, p.exp_.accept(new ExpVisitor(), arg));
			Stm typedStm = p.stm_.accept(new StmVisitor(), arg);
			popBlock();
			return new SWhile(typedExp, typedStm);
		}

		// E.g. int x; { int x = 1; x++; }
		public Stm visit(CPP.Absyn.SBlock p, Void arg)
		{
			newBlock();
			ListStm typedStms = new ListStm();
			for (Stm s: p.liststm_) 
				typedStms.add(s.accept(new StmVisitor(), arg));
			popBlock();
			return new SBlock(typedStms);
		}
		
		public Stm visit(CPP.Absyn.SIfElse p, Void arg)
		{
			Exp typedExp = check(BOOL, p.exp_.accept(new ExpVisitor(), arg));
			newBlock();
			Stm typedStm_1 = p.stm_1.accept(new StmVisitor(), arg);
			popBlock();
			newBlock();
			Stm typedStm_2 = p.stm_2.accept(new StmVisitor(), arg);
			popBlock();
			return new SIfElse(typedExp, typedStm_1, typedStm_2);
		}
	}

	////////////////////////////// Expression //////////////////////////////

	public class ExpVisitor implements Exp.Visitor<Exp,Void>
	{

		// Literals
		public Exp visit(CPP.Absyn.ETrue p, Void arg)
		{
			return new ETyped(p, BOOL);
		}
		public Exp visit(CPP.Absyn.EFalse p, Void arg)
		{
			return new ETyped(p, BOOL);
		}
		public Exp visit(CPP.Absyn.EInt p, Void arg)
		{
			return new ETyped(p, INT);
		}
		public Exp visit(CPP.Absyn.EDouble p, Void arg)
		{
			return new ETyped(p, DOUBLE);
		}

		// Variable
		public Exp visit(CPP.Absyn.EId p, Void arg)
		{
			return new ETyped(p, lookupVar (p.id_));
		}

		// Function call  plus(4,3)  where  int plus(int x, int y);
		public Exp visit(CPP.Absyn.EApp p, Void arg)
		{
			FunType ft = sig.get(p.id_);
			if (ft == null)
				throw new TypeException("Undefined function " + p.id_);
			if (ft.args.size() != p.listexp_.size())
				throw new TypeException("Function " + p.id_ + " not applied to correct number of arguments");
			ListExp typedExpList = new ListExp();
			// Check function arguments
			int i = 0;
			for (Exp e: p.listexp_) {
				ADecl a = (ADecl)(ft.args.get(i));
				typedExpList.add(check(a.type_, e.accept(new ExpVisitor(), arg)));
				i++;
			}
			return new ETyped( new EApp(p.id_, typedExpList), ft.returnType);
		}

		// Increment, decrement

		// x++
		public Exp visit(CPP.Absyn.EPostIncr p, Void arg)
		{
			return new ETyped(p, numericType(lookupVar(isVar(p.exp_))));
		}
		public Exp visit(CPP.Absyn.EPostDecr p, Void arg)
		{
			return new ETyped(p, numericType(lookupVar(isVar(p.exp_))));
		}
		public Exp visit(CPP.Absyn.EPreIncr p, Void arg)
		{
			return new ETyped(p, numericType(lookupVar(isVar(p.exp_))));
		}
		public Exp visit(CPP.Absyn.EPreDecr p, Void arg)
		{
			return new ETyped(p, numericType(lookupVar(isVar(p.exp_))));
		}

		// Arithmetical operators

		public Exp visit(CPP.Absyn.ETimes p, Void arg)
		{
			Exp typedE1 = p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = numericType(typedE1);
			Exp typedE2 = check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped( new ETimes(typedE1, typedE2), t1);
		}
		public Exp visit(CPP.Absyn.EDiv p, Void arg)
		{
			Exp typedE1 = p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = numericType(typedE1);
			Exp typedE2 = check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new EDiv(typedE1, typedE2), t1);
		}
		public Exp visit(CPP.Absyn.EPlus p, Void arg)
		{
			Exp typedE1 = p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = numericType(typedE1);
			Exp typedE2 = check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new EPlus(typedE1, typedE2), t1);
		}
		public Exp visit(CPP.Absyn.EMinus p, Void arg)
		{
			Exp typedE1 = p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = numericType(typedE1);
			Exp typedE2 = check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new EMinus(typedE1, typedE2), t1);
		}

		// Comparison operators

		public Exp visit(CPP.Absyn.ELt p, Void arg)
		{
			Exp typedE1 = p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = numericType(typedE1);
			Exp typedE2 = check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new ELt(typedE1, typedE2), BOOL);
		}
		public Exp visit(CPP.Absyn.EGt p, Void arg)
		{
			Exp typedE1 = p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = numericType(typedE1);
			Exp typedE2 = check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new EGt(typedE1, typedE2), BOOL);
		}
		public Exp visit(CPP.Absyn.ELtEq p, Void arg)
		{
			Exp typedE1 = p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = numericType(typedE1);
			Exp typedE2 = check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new ELtEq(typedE1, typedE2), BOOL);
		}
		public Exp visit(CPP.Absyn.EGtEq p, Void arg)
		{
			Exp typedE1 = p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = numericType(typedE1);
			Exp typedE2 = check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new EGtEq(typedE1, typedE2), BOOL);
		}

		// Equality operators

		public Exp visit(CPP.Absyn.EEq p, Void arg)
		{
			Exp typedE1 = p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = eQType(((ETyped)typedE1).type_);
			Exp typedE2 = check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new EEq(typedE1, typedE2), BOOL);
		}
		public Exp visit(CPP.Absyn.ENEq p, Void arg)
		{
			ETyped typedE1 = (ETyped) p.exp_1.accept(new ExpVisitor(), arg);
			Type t1 = eQType(typedE1.type_);
			ETyped typedE2 = (ETyped) check(t1, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new ENEq(typedE1, typedE2), BOOL);
		}

		// Logic operators

		public Exp visit(CPP.Absyn.EAnd p, Void arg)
		{
			Exp typedE1 = check(BOOL, p.exp_1.accept(new ExpVisitor(), arg));
			Exp typedE2 = check(BOOL, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped( new EAnd(typedE1, typedE2), BOOL);
		}
		public Exp visit(CPP.Absyn.EOr p, Void arg)
		{
			Exp typedE1 = check(BOOL, p.exp_1.accept(new ExpVisitor(), arg));
			Exp typedE2 = check(BOOL, p.exp_2.accept(new ExpVisitor(), arg));
			return  new ETyped(new EOr(typedE1, typedE2), BOOL);
		}

		// Assignment
		public Exp visit(CPP.Absyn.EAss p, Void arg)
		{
			Type varType = lookupVar(isVar(p.exp_1));
			Exp typedExp = check(varType, p.exp_2.accept(new ExpVisitor(), arg));
			return new ETyped(new EAss(p.exp_1, typedExp), varType);
		}
		
		@Override
		public Exp visit(CPP.Absyn.ETyped p, Void arg) {
			throw new RuntimeException("Internal error: why are you even visiting Etyped?!");
		}
	}

	///////////////////////// Context handling /////////////////////////

	public void newVar (String x, Type t) {
		// Get the top context block
		Map<String,Type> m = cxt.get(0);
		// Check that the variable is not declared already
		if (m.containsKey(x))
			throw new TypeException("duplicate variable binding " + x);
		// Add the binding
		m.put(x, t);
	}

	public void newBlock() {
		cxt.add(0, new TreeMap<String,Type>());
	}
	public void popBlock() {
		cxt.remove(0);
	}

	public Type lookupVar(String x) {
		for (Map<String,Type> m : cxt) {
			Type t = m.get(x);
			if (t != null) return t;
		}
		throw new TypeException("unbound variable " + x);
	}

	////////////////////////////// Exp / Type shape //////////////////////////////

	public String isVar (Exp e) {
		if (e instanceof EId) {
			return ((EId)e).id_;
		} else throw new TypeException("expected variable, found " + e);
	}

	// Expected type: t
	// Inferred type: u
	public Exp check (Type t, Exp e) {
		if(!(e instanceof ETyped))
			throw new RuntimeException("Internal error: expression not typed!");
		Type u = ((ETyped)e).type_;
		if (!t.equals(u))
			throw new TypeException("Expected type " + t + ", but found type " + u);
		return e;
	}

	public Type numericType (Exp e) {
		if(!(e instanceof ETyped))
			throw new RuntimeException("Internal error: expression not typed!");
		Type t = ((ETyped)e).type_;
		return numericType(t);
	}
	
	public Type numericType (Type t) {
		if (!t.equals(INT) && !t.equals(DOUBLE))
			throw new TypeException("expected expression of numeric type");
		return t;
	}
	
	public Type eQType (Exp e) {
		if(!(e instanceof ETyped))
			throw new RuntimeException("Internal error: expression not typed!");
		Type t1 = ((ETyped)e).type_;
		return eQType(t1);
	}
	
	public Type eQType (Type t1) {
		if (!t1.equals(INT) && !t1.equals(DOUBLE) && !t1.equals(BOOL))
			throw new TypeException("expected expression of equality type");
		return t1;
	}
	
	public Type isBool (Exp e) {
		if(!(e instanceof ETyped))
			throw new RuntimeException("Internal error: expression not typed!");
		Type t1 = ((ETyped)e).type_;
		if (!t1.equals(BOOL))
			throw new TypeException("expected expression of equality type");
		return t1;
	}
	
}
