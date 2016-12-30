// Code.java
// Created by github.com/andreasabel/java-adt

import CPP.Absyn.*;

class Fun {
	public String id;
	public FunType funType;
	public Fun (String id, FunType funType) {
		this.id = id;
		this.funType = funType;
	}
	public String toJVM() {
		return id + funType.toJVM();
	}
}

abstract class Code {
	public abstract <R> R accept (CodeVisitor<R> v);
}

class Store extends Code {
	public Type type;
	public Integer addr;
	public Store (Type type, Integer addr) {
		this.type = type;
		this.addr = addr;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Load extends Code {
	public Type type;
	public Integer addr;
	public Load (Type type, Integer addr) {
		this.type = type;
		this.addr = addr;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IConst extends Code {
	public Integer immed;
	public IConst (Integer immed) {
		this.immed = immed;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class DConst extends Code {
	public Double immed;
	public DConst (Double immed) {
		this.immed = immed;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Dup extends Code {
	public Type type;
	public Dup (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Pop extends Code {
	public Type type;
	public Pop (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Return extends Code {
	public Type type;
	public Return (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Call extends Code {
	public Fun fun;
	public Call (Fun fun) {
		this.fun = fun;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Label extends Code {
	public String label;
	public Label (String label) {
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Goto extends Code {
	public Label label;
	public Goto (Label label) {
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfZ extends Code {
	public Label label;
	public IfZ (Label label) {
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfNZ extends Code {
	public Label label;
	public IfNZ (Label label) {
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfEq extends Code {
	public Type type;
	public Label label;
	public IfEq (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfNe extends Code {
	public Type type;
	public Label label;
	public IfNe (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfLt extends Code {
	public Type type;
	public Label label;
	public IfLt (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfGt extends Code {
	public Type type;
	public Label label;
	public IfGt (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfLe extends Code {
	public Type type;
	public Label label;
	public IfLe (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class IfGe extends Code {
	public Type type;
	public Label label;
	public IfGe (Type type, Label label) {
		this.type = type;
		this.label = label;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class DGt extends Code {
	public DGt () {
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class DLt extends Code {
	public DLt () {
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Inc extends Code {
	public Type type;
	public Integer addr;
	public Integer delta;
	public Inc (Type type, Integer addr, Integer delta) {
		this.type = type;
		this.addr = addr;
		this.delta = delta;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Add extends Code {
	public Type type;
	public Add (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Sub extends Code {
	public Type type;
	public Sub (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Mul extends Code {
	public Type type;
	public Mul (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

class Div extends Code {
	public Type type;
	public Div (Type type) {
		this.type = type;
	}
	public <R> R accept (CodeVisitor<R> v) {
		return v.visit (this);
	}
}

interface CodeVisitor<R> {
	public R visit (Store c);
	public R visit (Load c);
	public R visit (IConst c);
	public R visit (DConst c);
	public R visit (Dup c);
	public R visit (Pop c);
	public R visit (Return c);
	public R visit (Call c);
	public R visit (Label c);
	public R visit (Goto c);
	public R visit (IfZ c);
	public R visit (IfNZ c);
	public R visit (IfEq c);
	public R visit (IfNe c);
	public R visit (IfLt c);
	public R visit (IfGt c);
	public R visit (IfLe c);
	public R visit (IfGe c);
	public R visit (DGt c);
	public R visit (DLt c);
	public R visit (Inc c);
	public R visit (Add c);
	public R visit (Sub c);
	public R visit (Mul c);
	public R visit (Div c);
}

class CodeToJVM implements CodeVisitor<String> {

	public String visit (Store c) {
		//TODO for i=0..3
		if(c.type instanceof Type_int)
			return "istore "+c.addr;
		else if (c.type instanceof Type_double)
			return "dstore "+c.addr;
		else 
			return "astore "+c.addr;
	}

	public String visit (Load c) {
		//TODO for i=0..3
		if(c.type instanceof Type_int)
			return "iload "+c.addr;
		else if (c.type instanceof Type_double)
			return "dload "+c.addr;
		else 
			return "aload "+c.addr;
	}

	public String visit (IConst c) {
		int i = c.immed.intValue();
		if (i == -1) return "iconst_m1";
		if (i >= 0 && i <= 5) return "iconst_" + i;
		if (i >= -128 && i < 128) return "bipush " + i;
		return "ldc " + c.immed.toString();
	}

	public String visit (DConst c) {
		double d = c.immed;
		if(d==0)
			return "dconst_"+0;
		else if(d==1)
			return "dconst_"+1;
		else
			return "ldc2_w " + d;
	}

	public String visit (Dup c) {
		if(c.type instanceof Type_double)
			return "dup2";
		else
			return "dup";
	}

	public String visit (Pop c) {
		if (c.type instanceof Type_double) 
			return "pop2";
		else if(c.type instanceof Type_int ||
				c.type instanceof Type_bool)
			return "pop";
		else 
			return "";
	}

	public String visit (Return c) {
		if(c.type instanceof Type_void)
			return "return";
		else if (c.type instanceof Type_double)
			return "dreturn";
		else 
			return "ireturn";
	}

	public String visit (Call c) {
		return "invokestatic " + c.fun.toJVM();
	}

	public String visit (Label c) {
		return c.label + ":";
	}

	public String visit (Goto c) {
		return "goto " + c.label.label;
	}

	public String visit (IfZ c) {
		return "";
	}

	public String visit (IfNZ c) {
		return "";
	}

	public String visit (IfEq c) {
		return "";
	}

	public String visit (IfNe c) {
		return "";
	}

	public String visit (IfLt c) {
		return "";
	}

	public String visit (IfGt c) {
		return "";
	}

	public String visit (IfLe c) {
		return "";
	}

	public String visit (IfGe c) {
		return "";
	}

	public String visit (DGt c) {
		return "";
	}

	public String visit (DLt c) {
		return "";
	}

	public String visit (Inc c) {
		if(c.type instanceof Type_double)
			throw new RuntimeException("Internal error: cannot inc for a double!");
		else
			return "iinc " + c.addr + " " +c.delta;
	}

	public String visit (Add c) {
		if(c.type instanceof Type_int)
			return "iadd";
		else if (c.type instanceof Type_double)
			return "dadd";
		else 
			return "invokestatic Runtime/plusString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
	}

	public String visit (Sub c) {
		if(c.type instanceof Type_int)
			return "isub";
		else if(c.type instanceof Type_double)
			return "dsub";
		throw new RuntimeException("Internal error: cannot sub for non-numeric type");
	}

	public String visit (Mul c) {
		if(c.type instanceof Type_int)
			return "imul";
		else if(c.type instanceof Type_double)
			return "dmul";
		throw new RuntimeException("Internal error: cannot mul for non-numeric type");
	}

	public String visit (Div c) {
		if(c.type instanceof Type_int)
			return "idiv";
		else if(c.type instanceof Type_double)
			return "ddiv";
		throw new RuntimeException("Internal error: cannot div for non-numeric type");
	}

}
