package com.nkgt.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.err.println("Usage: generate_ast [output directory]");
            System.exit(64);
        }

        String outputDir = args[0];
        defineAST(
            outputDir,
            "Expr",
            Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary   : Token operator, Expr right"
            )
        );
    }

    private static void defineAST(
            String outputDir,
            String baseName,
            List<String> types
    ) throws IOException {
        String path  = outputDir + "/" + baseName + ".java";
        try(PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package com.nkgt.jlox;\n");
            //writer.println("import java.util.List;\n");
            writer.println("abstract class " + baseName + " {");

            for(int i = 0; i < types.size(); i++) {
                String[] tokens = types.get(i).split(":");
                String className = tokens[0].trim();
                String fields = tokens[1].trim();
                defineType(writer, baseName, className, fields);

                if(i != types.size() - 1) writer.println();
            }

            writer.println("}");
        }
    }

    private static void defineType(
            PrintWriter writer,
            String baseName,
            String className,
            String fieldList
    ) {
        String[] fields = fieldList.split(", ");

        writer.println("\tstatic class " + className + " extends " + baseName + " {");
        writer.println("\t\t" + className + "(" + fieldList + ") {");

        for(String field : fields) {
            String name = field.split(" ")[1];
            writer.println("\t\t\tthis." + name + " = " + name + ";");
        }

        writer.println("\t\t}\n");

        for(String field : fields) {
            writer.println("\t\tfinal " + field + ";");
        }

        writer.println("\t}");
    }
}