import CPP.Absyn.ADecl;
import CPP.Absyn.ListArg;
import CPP.Absyn.Type;

/**
Contributors: Nachi
*/
public class Util {

	public static ListArg singleArg (Type t) {
		ListArg l = new ListArg();
		l.add(new ADecl(t, "x"));
		return l;
	}

	public static ListArg noArg(){
		return new ListArg();
	}

}
