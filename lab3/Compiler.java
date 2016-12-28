import java.util.*;
import CPP.Absyn.*;

public class Compiler
{
	// The output of the compiler is a list of strings.
	LinkedList<String> output;

	// Signature mapping function names to their JVM name and type
	Map<String,Fun> sig;

	// Context mapping variable identifiers to their type.
	// TODO: also to their address!
	List<Map<String,VariableEntry>> cxt;

	// Next free address for local variable;
	int nextLocal;

	// Number of locals needed for current function
	int limitLocals;

	// Maximum stack size needed for current function
	int limitStack;

	// Current stack size
	int currentStack;

	// Global counter to get next label;
	int nextLabel = 0;

	public void compile(String name, CPP.Absyn.Program p) {
		// Initialize output
		output = new LinkedList();

		//class declaration
		output.add(".class public " + name);
		output.add(".super java/lang/Object");
		
		//auto-generated constructor body
		output.add(".method public <init>()V");
		output.add("	.limit locals 1");
		output.add("	.limit stack 1");
		output.add("	aload_0");
		output.add("	invokespecial java/lang/Object/<init>()V");
		output.add("	return");
		output.add(".end method");
		
		// Create signature
		sig = new TreeMap();
		
		for (Def d: ((PDefs)p).listdef_) {
			DFun def = (DFun)d;
			sig.put(def.id_,
					new Fun(name + "/" + def.id_, new FunType(def.type_, def.listarg_)));
		}
		
		// Run compiler
		p.accept(new ProgramVisitor(), null);

		// Output result
		for (String s: output) {
			System.out.println(s);
		}
	}

	public class ProgramVisitor implements Program.Visitor<Void,Void>
	{
		public Void visit(CPP.Absyn.PDefs p, Void arg)
		{
			for (Def def: p.listdef_)
			{
				def.accept(new DefVisitor(), null);
			}
			return null;
		}
	}
	public class DefVisitor implements Def.Visitor<Void,Void>
	{
		public Void visit(CPP.Absyn.DFun p, Void arg)
		{
			// reset state for new function
			cxt = new LinkedList();
			cxt.add(new TreeMap());
			nextLocal = 0;
			limitLocals = 0;
			limitStack  = 0;
			currentStack = 0;

			// save output so far and reset output;
			LinkedList<String> savedOutput = output;
			output = new LinkedList();

			//p.id_;

			// Compile function

			// Add function parameters to context
			for (Arg x: p.listarg_)
				x.accept (new ArgVisitor(), null);
			for (Stm s: p.liststm_)
				s.accept (new StmVisitor(), null);

			// add new Output to old output
			LinkedList<String> newOutput = output;
			output = savedOutput;

			output.add(".method public static " + sig.get(p.id_).toJVM());
			output.add("  .limit locals " + limitLocals);
			output.add("  .limit stack " + limitStack);
			for (String s: newOutput) {
				output.add("  " + s);
			}
			output.add(".end method");
			return null;
		}
	}
	public class ArgVisitor implements Arg.Visitor<Void,Void>
	{
		public Void visit(CPP.Absyn.ADecl p, Void arg)
		{
			newVar (p.id_, p.type_);
			return null;
		}
	}
	public class StmVisitor implements Stm.Visitor<Void,Void>
	{
		// e;
		public Void visit(CPP.Absyn.SExp p, Void arg)
		{
			p.exp_.accept(new ExpVisitor(), arg);
			Type t = new Type_int(); // TODO: Annotating type checker
			emit(new Pop(t));
			return null;
		}

		// int x,y,z;
		public Void visit(CPP.Absyn.SDecls p, Void arg)
		{
			for (String x: p.listid_) newVar (x, p.type_);
			return null;
		}

		// int x = e;
		public Void visit(CPP.Absyn.SInit p, Void arg)
		{
			newVar (p.id_, p.type_);
			p.exp_.accept(new ExpVisitor(), arg);
			int addr = lookupVar(p.id_);
			emit (new Store(p.type_, addr));
			return null;
		}
		public Void visit(CPP.Absyn.SReturn p, Void arg)
		{ /* Code For SReturn Goes Here */
			p.exp_.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.SWhile p, Void arg)
		{ /* Code For SWhile Goes Here */
			p.exp_.accept(new ExpVisitor(), arg);
			p.stm_.accept(new StmVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.SBlock p, Void arg)
		{ /* Code For SBlock Goes Here */
			for (Stm x: p.liststm_)
			{ /* ... */ }
			return null;
		}
		public Void visit(CPP.Absyn.SIfElse p, Void arg)
		{ /* Code For SIfElse Goes Here */
			p.exp_.accept(new ExpVisitor(), arg);
			p.stm_1.accept(new StmVisitor(), arg);
			p.stm_2.accept(new StmVisitor(), arg);
			return null;
		}
	}
	public class ExpVisitor implements Exp.Visitor<Void,Void>
	{
		public Void visit(CPP.Absyn.ETrue p, Void arg)
		{ /* Code For ETrue Goes Here */
			return null;
		}
		public Void visit(CPP.Absyn.EFalse p, Void arg)
		{ /* Code For EFalse Goes Here */
			return null;
		}
		public Void visit(CPP.Absyn.EInt p, Void arg)
		{
			emit (new IConst (p.integer_));
			return null;
		}
		public Void visit(CPP.Absyn.EDouble p, Void arg)
		{ /* Code For EDouble Goes Here */
			//p.double_;
			return null;
		}
		public Void visit(CPP.Absyn.EId p, Void arg)
		{ /* Code For EId Goes Here */
			//p.id_;
			return null;
		}
		public Void visit(CPP.Absyn.EApp p, Void arg)
		{ /* Code For EApp Goes Here */
			//p.id_;
			for (Exp x: p.listexp_)
			{ /* ... */ }
			return null;
		}
		public Void visit(CPP.Absyn.EPostIncr p, Void arg)
		{ /* Code For EPostIncr Goes Here */
			p.exp_.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EPostDecr p, Void arg)
		{ /* Code For EPostDecr Goes Here */
			p.exp_.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EPreIncr p, Void arg)
		{ /* Code For EPreIncr Goes Here */
			p.exp_.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EPreDecr p, Void arg)
		{ /* Code For EPreDecr Goes Here */
			p.exp_.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.ETimes p, Void arg)
		{ /* Code For ETimes Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EDiv p, Void arg)
		{ /* Code For EDiv Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EPlus p, Void arg)
		{ /* Code For EPlus Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EMinus p, Void arg)
		{ /* Code For EMinus Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.ELt p, Void arg)
		{ /* Code For ELt Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EGt p, Void arg)
		{ /* Code For EGt Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.ELtEq p, Void arg)
		{ /* Code For ELtEq Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EGtEq p, Void arg)
		{ /* Code For EGtEq Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EEq p, Void arg)
		{ /* Code For EEq Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.ENEq p, Void arg)
		{ /* Code For ENEq Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EAnd p, Void arg)
		{ /* Code For EAnd Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EOr p, Void arg)
		{ /* Code For EOr Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
		public Void visit(CPP.Absyn.EAss p, Void arg)
		{ /* Code For EAss Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			return null;
		}
	}

	void emit (Code c) {
		output.add(c.accept(new CodeToJVM()));
		adjustStack(c);
	}

	void newVar(String x, Type t) {
		VariableEntry varEntry = new VariableEntry(t, nextLocal);
		cxt.get(0).put(x,varEntry);
		nextLocal = nextLocal + t.accept(new Size(), null);
	}

	Integer lookupVar (String x) {
		for (Map<String,VariableEntry> b: cxt) {
			VariableEntry ve = b.get(x);
			if(ve != null && ve.getAddress() != null)
				return ve.getAddress();
		}
		return null;
	}

	// update limitStack, currentStack according to instruction
	void adjustStack(Code c) {
		c.accept(new AdjustStack());
	}

	void incStack(Type t) {
		currentStack = currentStack + t.accept(new Size(), null);
		if (currentStack > limitStack) limitStack = currentStack;
	}

	void decStack(Type t) {
		currentStack = currentStack - t.accept(new Size(), null);
	}

	class Size implements Type.Visitor<Integer,Void> {
		// public Size() {}
		public Integer visit (Type_int t, Void arg) {
			return 1;
		}
		public Integer visit (Type_double t, Void arg) {
			return 2;
		}
		public Integer visit (Type_bool t, Void arg) {
			return 1;
		}
		public Integer visit (Type_void t, Void arg) {
			return 0;
		}

	}

	class AdjustStack implements CodeVisitor<Void> {
		public Void visit (Store c) {
			decStack(c.type);
			return null;
		}

		public Void visit (Load c) {
			incStack(c.type);
			return null;
		}

		public Void visit (IConst c) {
			incStack(new Type_int());
			return null;
		}

		public Void visit (DConst c) {
			incStack(new Type_double());
			return null;
		}

		public Void visit (Dup c) {
			incStack(c.type);
			return null;
		}

		public Void visit (Pop c) {
			return null;
		}

		public Void visit (Return c) {
			;
			return null;
		}

		public Void visit (Call c) {
			;
			return null;
		}

		public Void visit (Label c) {
			;
			return null;
		}

		public Void visit (Goto c) {
			;
			return null;
		}

		public Void visit (IfZ c) {
			;
			return null;
		}

		public Void visit (IfNZ c) {
			;
			return null;
		}

		public Void visit (IfEq c) {
			;
			return null;
		}

		public Void visit (IfNe c) {
			;
			return null;
		}

		public Void visit (IfLt c) {
			;
			return null;
		}

		public Void visit (IfGt c) {
			;
			return null;
		}

		public Void visit (IfLe c) {
			;
			return null;
		}

		public Void visit (IfGe c) {
			;
			return null;
		}

		public Void visit (DGt c) {
			;
			return null;
		}

		public Void visit (DLt c) {
			;
			return null;
		}

		public Void visit (Inc c) {
			;
			return null;
		}

		public Void visit (Add c) {
			;
			return null;
		}

		public Void visit (Sub c) {
			;
			return null;
		}

		public Void visit (Mul c) {
			;
			return null;
		}

		public Void visit (Div c) {
			;
			return null;
		}


	}
}
