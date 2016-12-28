import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import CPP.Yylex;
import CPP.parser;

public class lab3 {
    public static void main(String args[]) {
        if (args.length != 1) {
            System.err.println("Usage: lab3 <SourceFile>");
            System.exit(1);
        }

        Yylex l = null;
        try {
            String path = args[0];
            l = new Yylex(new FileReader(path));
            parser p = new parser(l);
            CPP.Absyn.Program parse_tree = p.pProgram();
            new TypeChecker().typecheck(parse_tree);
            new Compiler().compile(FilenameUtils.getBaseName(path), parse_tree);
        } catch (TypeException e) {
            System.out.println("TYPE ERROR");
            System.err.println(e.toString());
            System.exit(1);
        } catch (RuntimeException e) {
            System.out.println("RUNTIME ERROR");
            e.printStackTrace();
            System.err.println(e.toString());
            System.exit(-1);
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(1);
        } catch (Throwable e) {
            System.out.println("SYNTAX ERROR");
            System.out.println("At line " + String.valueOf(l.line_num())
                       + ", near \"" + l.buff() + "\" :");
            System.out.println("     " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
