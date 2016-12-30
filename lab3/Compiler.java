import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import CPP.Absyn.Arg;
import CPP.Absyn.DFun;
import CPP.Absyn.Def;
import CPP.Absyn.EId;
import CPP.Absyn.ETyped;
import CPP.Absyn.Exp;
import CPP.Absyn.PDefs;
import CPP.Absyn.Program;
import CPP.Absyn.SReturn;
import CPP.Absyn.Stm;
import CPP.Absyn.Type;
import CPP.Absyn.Type_bool;
import CPP.Absyn.Type_double;
import CPP.Absyn.Type_int;
import CPP.Absyn.Type_void;

public class Compiler
{
	// The output of the compiler is a list of strings.
	LinkedList<String> output;

	// Signature mapping function names to their JVM name and type
	Map<String,Fun> sig;

	// Context mapping variable identifiers to their type.
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

	// type constants
	public final Type BOOL   = new Type_bool();
	public final Type INT    = new Type_int();
	public final Type DOUBLE = new Type_double();
	public final Type VOID   = new Type_void();

	//last seen var
	public String lastSeenVar = null;

	public LinkedList<String> compile(String name, CPP.Absyn.Program p) {
		// Initialize output
		output = new LinkedList<String>();

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

		output.add(".method public static main([Ljava/lang/String;)V");
		output.add("	.limit locals 1");
		output.add("	.limit stack 1");
		output.add("	invokestatic "+name+"/main()I");
		output.add("	pop");
		output.add("	return");
		output.add(".end method");

		// Create signature
		sig = new TreeMap<String, Fun>();

		//add runtime methods
		sig.put("printInt", new Fun("Runtime/printInt", new FunType(VOID, Util.singleArg(INT))));
		sig.put("printDouble", new Fun("Runtime/printDouble", new FunType(VOID, Util.singleArg(DOUBLE))));
		sig.put("readInt", new Fun("Runtime/readInt", new FunType(INT, Util.noArg())));
		sig.put("readDouble", new Fun("Runtime/readDouble", new FunType(DOUBLE, Util.noArg())));

		for (Def d: ((PDefs)p).listdef_) {
			DFun def = (DFun)d;
			sig.put(def.id_,
					new Fun(name + "/" + def.id_, new FunType(def.type_, def.listarg_)));
		}

		// Run compiler
		p.accept(new ProgramVisitor(), null);

		return output;
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
			cxt = new LinkedList<Map<String, VariableEntry>>();
			cxt.add(new TreeMap<String, VariableEntry>());
			nextLocal = 0;
			limitLocals = 0;
			limitStack  = 0;
			currentStack = 0;

			// save output so far and reset output;
			LinkedList<String> savedOutput = output;
			output = new LinkedList<String>();

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

			output.add(".method public static " + p.id_ + sig.get(p.id_).funType.toJVM());
			output.add("	.limit locals " + limitLocals);
			output.add("	.limit stack " + limitStack);
			for (String s: newOutput) {
				output.add("	" + s);
			}
			if(isWithoutReturn(p)){
				if(p.id_.equals("main")){
					output.add("	iconst_0");
					output.add("	ireturn");
				} else {
					output.add("	return");
				}
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

			Type t = p.exp_.accept(new ExpVisitor(), null);
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
			p.exp_.accept(new ExpVisitor(), null);
			int addr = lookupVar(p.id_);
			emit (new Store(p.type_, addr));
			return null;
		}
		public Void visit(CPP.Absyn.SReturn p, Void arg)
		{ /* Code For SReturn Goes Here */
			Type t = p.exp_.accept(new ExpVisitor(), null);
			emit(new Return(t));
			return null;
		}
		public Void visit(CPP.Absyn.SWhile p, Void arg)
		{ /* Code For SWhile Goes Here */
			Label loopLabel = new Label(newLabel("WHILE"));
			Label endLabel = new Label(newLabel("ENDWHILE"));
			emit(loopLabel);
			p.exp_.accept(new ExpVisitor(), null);
			emit(new IfZ(endLabel));
			p.stm_.accept(new StmVisitor(), arg);
			emit(new Goto(loopLabel));
			emit(endLabel);
			return null;
		}
		public Void visit(CPP.Absyn.SBlock p, Void arg)
		{ /* Code For SBlock Goes Here */
			newBlock();
			for (Stm x: p.liststm_)
			{ 
				x.accept(new StmVisitor(), arg);
			}
			popBlock();
			return null;
		}
		public Void visit(CPP.Absyn.SIfElse p, Void arg)
		{ /* Code For SIfElse Goes Here */
			Label trueLabel = new Label(newLabel("IFTRUE"));
			Label falseLabel = new Label(newLabel("IFFALSE"));
			p.exp_.accept(new ExpVisitor(), null);
			emit(new IfZ(falseLabel));
			p.stm_1.accept(new StmVisitor(), arg);
			emit(new Goto(trueLabel));
			emit(falseLabel);
			p.stm_2.accept(new StmVisitor(), arg);
			emit(trueLabel);
			return null;
		}
	}
	public class ExpVisitor implements Exp.Visitor<Type,Type>
	{
		public Type visit(CPP.Absyn.ETrue p, Type arg)
		{ /* Code For ETrue Goes Here */
			emit(new IConst(1));
			return BOOL;
		}
		public Type visit(CPP.Absyn.EFalse p, Type arg)
		{ /* Code For EFalse Goes Here */
			emit(new IConst(0));
			return BOOL;
		}
		public Type visit(CPP.Absyn.EInt p, Type arg)
		{
			emit (new IConst (p.integer_));
			return INT;
		}
		public Type visit(CPP.Absyn.EDouble p, Type arg)
		{ /* Code For EDouble Goes Here */
			emit(new DConst(p.double_));
			return DOUBLE;
		}
		//x
		public Type visit(CPP.Absyn.EId p, Type type)
		{ /* Code For EId Goes Here */
			emit(new Load(type, lookupVar(p.id_)));
			lastSeenVar = p.id_;
			return type;
		}
		public Type visit(CPP.Absyn.EApp p, Type arg)
		{ /* Code For EApp Goes Here */
			Fun fn = sig.get(p.id_);

			if(fn==null)
				throw new RuntimeException(fn +  " is not defined!");

			for (Exp x: p.listexp_){ 
				x.accept(new ExpVisitor(), arg);
			}

			emit(new Call(fn));

			return null;
		}
		//exp ++
		public Type visit(CPP.Absyn.EPostIncr p, Type arg)
		{ /* Code For EPostIncr Goes Here */
			Type t = p.exp_.accept(new ExpVisitor(), arg);
			if(arg instanceof Type_int){
				emit(new Inc(t, lookupVar(lastSeenVar), 1));
			}
			else{
				emit(new Dup(arg));
				emit(new DConst(1.0));
				emit(new Add(arg));
				emit(new Store(arg, lookupVar(lastSeenVar)));
			}
			return null;
		}
		public Type visit(CPP.Absyn.EPostDecr p, Type arg)
		{ /* Code For EPostDecr Goes Here */
			Type t = p.exp_.accept(new ExpVisitor(), arg);
			emit(new Dup(t));
			if(arg instanceof Type_int)
				emit(new IConst(1));
			else
				emit(new DConst(1.0));
			emit(new Sub(arg));
			emit(new Store(arg, lookupVar(lastSeenVar)));
			return null;
		}
		public Type visit(CPP.Absyn.EPreIncr p, Type arg)
		{ /* Code For EPreIncr Goes Here */
			Type t = p.exp_.accept(new ExpVisitor(), arg);
			if(arg instanceof Type_int){
				System.out.println(t);
				emit(new Pop(t));
				emit(new Inc(t, lookupVar(lastSeenVar), 1));
				emit(new Load(t, lookupVar(lastSeenVar)));
			}
			else{
				emit(new DConst(1.0));
				emit(new Add(t));
				emit(new Dup(t));
				emit(new Store(t, lookupVar(lastSeenVar)));
			}
			return null;
		}
		public Type visit(CPP.Absyn.EPreDecr p, Type arg)
		{ /* Code For EPreDecr Goes Here */
			Type t = p.exp_.accept(new ExpVisitor(), arg);
			if(arg instanceof Type_int)
				emit(new IConst(1));
			else
				emit(new DConst(1.0));
			emit(new Sub(t));
			emit(new Dup(t));
			emit(new Store(t, lookupVar(lastSeenVar)));
			return null;
		}
		public Type visit(CPP.Absyn.ETimes p, Type arg)
		{ /* Code For ETimes Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			emit(new Mul(arg));
			return null;
		}
		public Type visit(CPP.Absyn.EDiv p, Type arg)
		{ /* Code For EDiv Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			emit(new Div(arg));
			return null;
		}
		public Type visit(CPP.Absyn.EPlus p, Type arg)
		{ /* Code For EPlus Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			emit(new Add(arg));
			return null;
		}
		public Type visit(CPP.Absyn.EMinus p, Type arg)
		{ /* Code For EMinus Goes Here */
			p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			emit(new Sub(arg));
			return null;
		}
		public Type visit(CPP.Absyn.ELt p, Type arg)
		{ /* Code For ELt Goes Here */
			Label trueLabel = new Label(newLabel("TRUE"));
			emit(new IConst(1));
			Type t = p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			if(t instanceof Type_int){
				emit(new IfLt(t, trueLabel));
				emit(new Pop(new Type_int()));
				emit(new IConst(0));
				emit(trueLabel);
			}
			//TODO for double
			return arg;
		}
		public Type visit(CPP.Absyn.EGt p, Type arg)
		{ /* Code For EGt Goes Here */
			Label trueLabel = new Label(newLabel("TRUE"));
			emit(new IConst(1));
			Type t = p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			if(t instanceof Type_int){
				emit(new IfGt(t, trueLabel));
				emit(new Pop(new Type_int()));
				emit(new IConst(0));
				emit(trueLabel);
			}
			//TODO for double
			return arg;
		}
		public Type visit(CPP.Absyn.ELtEq p, Type arg)
		{ /* Code For ELtEq Goes Here */
			Label trueLabel = new Label(newLabel("TRUE"));
			emit(new IConst(1));
			Type t = p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			if(t instanceof Type_int){
				emit(new IfLe(t, trueLabel));
				emit(new Pop(new Type_int()));
				emit(new IConst(0));
				emit(trueLabel);
			}
			//TODO for double
			return arg;
		}
		public Type visit(CPP.Absyn.EGtEq p, Type arg)
		{ /* Code For EGtEq Goes Here */
			Label trueLabel = new Label(newLabel("TRUE"));
			emit(new IConst(1));
			Type t = p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			if(t instanceof Type_int){
				emit(new IfGe(t, trueLabel));
				emit(new Pop(new Type_int()));
				emit(new IConst(0));
				emit(trueLabel);
			}
			//TODO for double
			return arg;
		}
		public Type visit(CPP.Absyn.EEq p, Type arg)
		{ /* Code For EEq Goes Here */
			Label trueLabel = new Label(newLabel("TRUE"));
			emit(new IConst(1));
			Type t = p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			if(t instanceof Type_int){
				emit(new IfEq(t, trueLabel));
				emit(new Pop(new Type_int()));
				emit(new IConst(0));
				emit(trueLabel);
			}
			//TODO for double
			return arg;
		}
		public Type visit(CPP.Absyn.ENEq p, Type arg)
		{ /* Code For ENEq Goes Here */
			Label trueLabel = new Label(newLabel("TRUE"));
			emit(new IConst(1));
			Type t = p.exp_1.accept(new ExpVisitor(), arg);
			p.exp_2.accept(new ExpVisitor(), arg);
			if(t instanceof Type_int){
				emit(new IfNe(t, trueLabel));
				emit(new Pop(new Type_int()));
				emit(new IConst(0));
				emit(trueLabel);
			}
			//TODO for double
			return arg;
		}
		public Type visit(CPP.Absyn.EAnd p, Type arg)
		{ /* Code For EAnd Goes Here */
			Label falseLabel = new Label(newLabel("ANDFALSE"));
			//assuming false
			emit(new IConst(0));
			p.exp_1.accept(new ExpVisitor(), arg);
			//check if 0
			//if yes, jump to true
			emit(new IConst(0));
			emit(new IfEq(INT, falseLabel));
			p.exp_2.accept(new ExpVisitor(), arg);
			//check if 0
			//if yes, jump to true
			emit(new IConst(0));
			emit(new IfEq(INT, falseLabel));
			//if no, pop 0 
			emit(new Pop(INT));
			//push 1 - tell them their life was indeed true
			emit(new IConst(1));
			//false label
			emit(falseLabel);
			return arg;
		}
		public Type visit(CPP.Absyn.EOr p, Type arg)
		{ /* Code For EOr Goes Here */
			Label trueLabel = new Label(newLabel("ORTRUE"));
			//assuming true
			emit(new IConst(1));
			p.exp_1.accept(new ExpVisitor(), arg);
			//check if 1
			//if yes, jump to true
			emit(new IConst(1));
			emit(new IfEq(INT, trueLabel));
			p.exp_2.accept(new ExpVisitor(), arg);
			//check if 1
			//if yes, jump to true
			emit(new IConst(1));
			emit(new IfEq(INT, trueLabel));
			//if no, pop1
			emit(new Pop(INT));
			//push 0 - tell them their life was a lie
			emit(new IConst(0));
			//true label
			emit(trueLabel);
			return arg;
		}
		public Type visit(CPP.Absyn.EAss p, Type arg)
		{ /* Code For EAss Goes Here */
			Integer addr = lookupVar(((EId) p.exp_1).id_);
			p.exp_2.accept(new ExpVisitor(), arg);
			emit(new Dup(arg));
			emit(new Store(arg, addr));
			return arg;
		}
		@Override
		public Type visit(ETyped p, Type arg) {
			p.exp_.accept(new ExpVisitor(), p.type_);
			return p.type_;
		}
	}

	void emit (Code c) {
		output.add(c.accept(new CodeToJVM()));
		adjustStack(c);
	}

	void newVar(String x, Type t) {
		VariableEntry varEntry = new VariableEntry(t, nextLocal);
		cxt.get(0).put(x,varEntry);
		Integer size = t.accept(new Size(), null);
		nextLocal = nextLocal + size;
		limitLocals = limitLocals + size;
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

	boolean isWithoutReturn(DFun fun){
		for(Stm s : fun.liststm_){
			//ret stm found
			if(s instanceof SReturn)
				return false;
		}
		//no ret stm found
		return true;
	}
	
	String newLabel(String prefix){
		return prefix + (nextLabel++);
	}

	public void newBlock() {
		cxt.add(0, new TreeMap<String, VariableEntry>());
	}
	public void popBlock() {
		cxt.remove(0);
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
			decStack(c.type);
			return null;
		}

		public Void visit (Return c) {
			return null;
		}

		public Void visit (Call c) {
			incStack(c.fun.funType.returnType);
			return null;
		}

		public Void visit (Label c) {
			return null;
		}

		public Void visit (Goto c) {
			return null;
		}

		public Void visit (IfZ c) {
			decStack(INT);
			return null;
		}

		public Void visit (IfNZ c) {
			decStack(INT);
			return null;
		}

		public Void visit (IfEq c) {
			decStack(INT);
			decStack(INT);
			return null;
		}

		public Void visit (IfNe c) {
			decStack(INT);
			decStack(INT);
			return null;
		}

		public Void visit (IfLt c) {
			decStack(INT);
			decStack(INT);
			return null;
		}

		public Void visit (IfGt c) {
			decStack(INT);
			decStack(INT);
			return null;
		}

		public Void visit (IfLe c) {
			decStack(INT);
			decStack(INT);
			return null;
		}

		public Void visit (IfGe c) {
			decStack(INT);
			decStack(INT);
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
			return null;
		}

		public Void visit (Add c) {
			decStack(c.type);
			return null;
		}

		public Void visit (Sub c) {
			decStack(c.type);;
			return null;
		}

		public Void visit (Mul c) {
			decStack(c.type);
			return null;
		}

		public Void visit (Div c) {
			decStack(c.type);
			return null;
		}


	}
}
