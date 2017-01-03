import java.util.*;
import Fun.Absyn.*;

public class InterpreterMutable {

	// Signature
	final Map<String,Exp> sig = new TreeMap();

	// Strategy
	final boolean strategy;

	public InterpreterMutable (boolean strategy) {
		this.strategy = strategy;
	}

	public void interpret(Program p) {
		System.out.println(p.accept(new ProgramVisitor(), null).intValue());
	}

	public class ProgramVisitor implements Program.Visitor<Value,Void>
	{
		public Value visit(Fun.Absyn.Prog p, Void arg)
		{
			// build signature
			for (Def d: p.listdef_) d.accept(new DefVisitor(), null);
			// execute main expression
			return p.main_.accept(new MainVisitor(), null);
		}
	}

	public class MainVisitor implements Main.Visitor<Value,Void>
	{
		public Value visit(Fun.Absyn.DMain p, Void arg)
		{
			return p.exp_.accept(new EvalVisitor(), new TreeMap());
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
			for (String x: p.listident_) e = new EAbs(x, e);

			// Add to signature
			sig.put(p.ident_, e);
			return null;
		}
	}

	public class EvalVisitor implements Exp.Visitor<Value,TreeMap<String,Value>>
	{
		// variable
		public Value visit(Fun.Absyn.EVar p, TreeMap<String,Value> env)
		{
			// first try to find it in the environment

			// then try to find it in the signature

			// not found?
			throw new RuntimeException("unbound variable " + p.ident_);
		}

		// literal
		public Value visit(Fun.Absyn.EInt p, TreeMap<String,Value> env)
		{
			return new VInt(p.integer_);
		}

		// lambda
		public Value visit(Fun.Absyn.EAbs p, TreeMap<String,Value> env)
		{
			return new VClos(p, env);
		}

		// application
		public Value visit(Fun.Absyn.EApp p, TreeMap<String,Value> env)
		{
			Value f = p.exp_1.accept(new EvalVisitor(), env);
			Value v;
			if (strategy) {
				// call-by-name

				// TODO

			} else {
				// call-by-value
				v = p.exp_2.accept(new EvalVisitor(), env);
			}
			return null;//f.apply(v);
		}

		// plus
		public Value visit(Fun.Absyn.EAdd p, TreeMap<String,Value> env)
		{
			// p.exp_1.accept(new EvalVisitor(), env);
			// p.exp_2.accept(new EvalVisitor(), env);
			return null;
		}

		// minus
		public Value visit(Fun.Absyn.ESub p, TreeMap<String,Value> env)
		{
			// p.exp_1.accept(new EvalVisitor(), env);
			// p.exp_2.accept(new EvalVisitor(), env);
			return null;
		}

		// less-than
		public Value visit(Fun.Absyn.ELt p, TreeMap<String,Value> env)
		{
			// p.exp_1.accept(new EvalVisitor(), env);
			// p.exp_2.accept(new EvalVisitor(), env);
			return null;
		}

		// if
		public Value visit(Fun.Absyn.EIf p, TreeMap<String,Value> env)
		{
			// p.exp_1.accept(new EvalVisitor(), env);
			// p.exp_2.accept(new EvalVisitor(), env);
			// p.exp_3.accept(new EvalVisitor(), env);
			return null;
		}
	}

	abstract class Value {
		abstract public int intValue();
		abstract public Value apply(Value v);

	}

	class VInt extends Value {
		final int value;
		public VInt (int v) { value = v; }
		public int intValue() { return value; }
		public Value apply(Value v) { throw new RuntimeException ("cannot apply a number"); }
	}

	class VClos extends Value {
		final Exp exp;
		final TreeMap<String,Value> env;
		public VClos(Exp exp, TreeMap<String,Value> env) {
			this.exp = exp;
			this.env = env;
		}
		public int intValue() { throw new RuntimeException ("not an integer"); }

		public Value apply (Value v) {
			if (!(exp instanceof EAbs))
				throw new RuntimeException ("not a function, cannot apply");
			EAbs f = (EAbs)exp;
			TreeMap<String,Value> rho = (TreeMap)(env.clone());
			rho.put(f.ident_, v);
			return f.exp_.accept (new EvalVisitor(), rho);
		}

	}

}
