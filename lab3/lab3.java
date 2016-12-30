import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.LinkedList;

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
			File ipFile = new File(path);
			String baseName = FilenameUtils.getBaseName(ipFile.getAbsolutePath());
			String dir = ipFile.getParent();

			l = new Yylex(new FileReader(path));
			parser p = new parser(l);
			CPP.Absyn.Program parse_tree = p.pProgram();
			CPP.Absyn.Program annotated_parse_tree = new TypeChecker().typecheck(parse_tree);
			LinkedList<String> jasminCode = new Compiler().compile(baseName, annotated_parse_tree);

			//write jasmin file in the CWD
			String jasminFile = baseName + ".j";
			try(  PrintWriter out = new PrintWriter(jasminFile)  ){
				for (String s: jasminCode) {
					if(!s.trim().isEmpty()){
						out.write(s);
						out.write("\n");
					}
				}
			}

			//generate class file
			//assumes jasmin.jar is in CWD
			ProcessBuilder pb = new ProcessBuilder("java","-jar","jasmin.jar",jasminFile);
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.INHERIT);
			pb.start().waitFor();
			
			//move .class file from CWD to directory of i/p file
			String classFileName = baseName + ".class";
			pb = new ProcessBuilder("mv", classFileName, dir);
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.INHERIT);
			pb.start().waitFor();
			
			//TODO clean up jasmin file
			
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
