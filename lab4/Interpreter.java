import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import Fun.Absyn.Def;
import Fun.Absyn.EAbs;
import Fun.Absyn.Exp;
import Fun.Absyn.Program;

public class Interpreter {

	// Signature
	final Map<String,Exp> sig = new TreeMap<String,Exp>();

	// Strategy
	final boolean callByNameStrategy;

	public Interpreter (boolean strategy) {
		this.callByNameStrategy = strategy;
	}

	public void interpret(Program p) {
		Value v = p.accept(new ProgramVisitor(), null);
		if(v instanceof VInt)
			System.out.println(v.intValue());
		else
			throw new RuntimeException("main must evaluate to an integer");
	}

	public class ProgramVisitor implements Program.Visitor<Value,Void>
	{
		public Value visit(Fun.Absyn.Prog p, Void arg)
		{
			// build signature
			for (Def d: p.listdef_) 
				d.accept(new DefVisitor(), null);
			return p.main_.accept(new MainVisitor(), null);
		}
	}

	public class MainVisitor implements Fun.Absyn.Main.Visitor<Value,Void>
	{
		public Value visit(Fun.Absyn.DMain p, Void arg)
		{
			return p.exp_.accept(new EvalVisitor(), emptyEnvironment());
		}
	}

	// visit defs only to build the signature.
	public class DefVisitor implements Def.Visitor<Void,Void>
	{
		public Void visit(Fun.Absyn.DDef p, Void arg)
		{
			// abstract over arguments from right to left
			Exp e = p.exp_;

			Collections.reverse(p.listident_);

			for (String x: p.listident_) 
				e = new EAbs(x, e);

			// Add to signature
			sig.put(p.ident_, e);

			return null;
		}
	}

	public class EvalVisitor implements Exp.Visitor<Value,Environment>
	{
		// variable
		public Value visit(Fun.Absyn.EVar p, Environment env)
		{
			// first try to find it in the environment
			Value v = env.lookup(p.ident_);
			if(v!=null){
				//this value will be VInt
				//unless in the case of CBN where it will be a VClos
				return v.getValue();
			}
			// then try to find it in the signature
			else{
				//this e is mostly EAbs except in case of 0 arguments
				//in which case, it is a VInt
				Exp e = sig.get(p.ident_);
				if(e==null)
					throw new RuntimeException("unbound variable " + p.ident_);
				return e.accept(new EvalVisitor(), env);
			} 
		}

		// literal
		public Value visit(Fun.Absyn.EInt p, Environment env)
		{
			return new VInt(p.integer_);
		}

		// lambda
		public Value visit(Fun.Absyn.EAbs p, Environment env)
		{
			return new VClos(p, env);
		}

		// application
		public Value visit(Fun.Absyn.EApp p, Environment env)
		{
			Value fclosure = p.exp_1.accept(new EvalVisitor(), env);
			Value v;
			if (callByNameStrategy) {
				//return a closure of exp2 with current env 
				v = new VClos (p.exp_2, env);
			} else {
				//evaluate exp 2 in current env
				v = p.exp_2.accept(new EvalVisitor(), env);
			}
			//evaluate the function body
			//in the environment of fun closure augmeneted with
			//function argument as evaluated value (CBV) or closure (CBN)
			return fclosure.apply(v);
		}

		// plus
		public Value visit(Fun.Absyn.EAdd p, Environment env)
		{
			try{
				VInt v1 = (VInt) p.exp_1.accept(new EvalVisitor(), env);
				VInt v2 =  (VInt) p.exp_2.accept(new EvalVisitor(), env);
				return new VInt(v1.value+v2.value);
			} catch(ClassCastException e){
				throw new RuntimeException("+ is defined only for integers");
			}
		}

		// minus
		public Value visit(Fun.Absyn.ESub p, Environment env)
		{
			try{
				VInt v1 = (VInt) p.exp_1.accept(new EvalVisitor(), env);
				VInt v2 =  (VInt) p.exp_2.accept(new EvalVisitor(), env);
				return new VInt(v1.value - v2.value);
			} catch(ClassCastException e){
				throw new RuntimeException("- is defined only for integers");
			}
		}

		// less-than
		public Value visit(Fun.Absyn.ELt p, Environment env)
		{
			try{
				VInt v1 = (VInt) p.exp_1.accept(new EvalVisitor(), env);
				VInt v2 =  (VInt) p.exp_2.accept(new EvalVisitor(), env);
				return new VInt(v1.value<v2.value?1:0);
			}catch(ClassCastException e){
				throw new RuntimeException("< is defined only for integer");
			}
		}

		// if
		public Value visit(Fun.Absyn.EIf p, Environment env)
		{
			try{
				VInt condition = (VInt) p.exp_1.accept(new EvalVisitor(), env);
				if(condition.value == 0)
					return p.exp_3.accept(new EvalVisitor(), env);
				else
					return p.exp_2.accept(new EvalVisitor(), env);
			}catch(ClassCastException e){
				throw new RuntimeException("If condition must evaluate to an integer");
			}
		}
	}

	abstract class Value {
		abstract public int intValue();
		abstract public Value apply(Value v);
		abstract public Value getValue();
	}

	class VInt extends Value {
		final int value;
		public VInt (int v) { value = v; }
		public int intValue() { return value; }

		public Value apply(Value v) { throw new RuntimeException ("cannot apply a number"); }
		public String toString(){
			return String.valueOf(value);
		}

		@Override
		public Value getValue() {
			return this;
		}
	}

	class VClos extends Value {
		final Exp exp;
		final Environment env;
		public VClos(Exp exp, Environment env) {
			this.exp = exp;
			this.env = env;
		}
		public int intValue() { throw new RuntimeException ("not an integer"); }

		public Value apply (Value v) {
			if (!(exp instanceof EAbs))
				throw new RuntimeException ("not a function, cannot apply");
			EAbs f = (EAbs)exp;
			return f.exp_.accept (new EvalVisitor(), env.extend(f.ident_, v));
		}

		public String toString(){
			return exp.toString() + "{"+env.toString()+"}";
		}

		@Override
		public Value getValue() {
			return exp.accept (new EvalVisitor(), env);
		}

	}
	
	interface Environment{
		public Value lookup (String x);
		public Environment extend(String x, Value v);
	}
	
	public Environment emptyEnvironment(){
		return new MutableTreeMapEnvImpl();
	}
	
	class MutableTreeMapEnvImpl extends TreeMap<String, Value> implements Environment{

		private static final long serialVersionUID = 1L;

		public Value lookup (String x){
			return this.get(x);
		}
		
		public Environment extend(String x, Value v){
			MutableTreeMapEnvImpl envClone = (MutableTreeMapEnvImpl) this.clone();
			envClone.put(x, v);
			return envClone;
		}
	
	}

}
