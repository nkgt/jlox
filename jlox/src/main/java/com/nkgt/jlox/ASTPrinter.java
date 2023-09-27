package com.nkgt.jlox;

class ASTPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
    String print(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return "Var " + expr.name.lexeme;
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return parenthesize("Assign " + expr.name.lexeme, expr.value);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        StringBuilder builder = new StringBuilder();
        builder.append("(Call ").append(expr.callee.accept(this)).append(" ");

        if(!expr.arguments.isEmpty()) {
            builder.append("(Args ");

            for(int i = 0; i < expr.arguments.size(); i++) {
                builder.append(expr.arguments.get(i).accept(this));

                if(i != expr.arguments.size() - 1) builder.append(" ");
            }

            builder.append(")");
        }

        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(If ")
               .append(parenthesize("Condition", stmt.condition))
               .append("\n\tthen ").append(stmt.thenBranch.accept(this));

        if(stmt.elseBranch != null) {
            builder.append("\n\telse ").append(stmt.elseBranch.accept(this)).append(")");
        }

        return builder.toString();
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        return "(While " + parenthesize("Condition", stmt.condition) +
               "\n\t" + stmt.body.accept(this) + ")";
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);

        for(Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }

        builder.append(")");

        return builder.toString();
    }

    public String visitExpressionStmt(Stmt.Expression stmt) {
       return parenthesize("Stmt", stmt.expression);
    }

    public String visitFunctionStmt(Stmt.Function stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(Fun ").append(stmt.name.lexeme).append(" ");

        if(!stmt.params.isEmpty()) {
            builder.append("\n\t(Params ");

            for(int i = 0; i < stmt.params.size(); i++) {
                builder.append(stmt.params.get(i).lexeme);

                if(i != stmt.params.size() - 1) builder.append(" ");
            }

            builder.append("\n\t)");
        }

        if(!stmt.body.isEmpty()) {
            builder.append("\n\t(Body");

            for(Stmt statement : stmt.body) {
                builder.append("\n\t\t(").append(statement.accept(this)).append(")");
            }

            builder.append("\n\t)");
        }

        builder.append(")");

        return builder.toString();
    }

    public String visitPrintStmt(Stmt.Print stmt) {
        return parenthesize("Print", stmt.expression);
    }

    public String visitReturnStmt(Stmt.Return stmt) {
        return parenthesize("Return", stmt.value);
    }

    public String visitVarStmt(Stmt.Var stmt) {
        if(stmt.initializer != null) {
            return parenthesize("Var " + stmt.name.lexeme, stmt.initializer);
        }

        return "(Var " + stmt.name.lexeme + ")";
    }

    public String visitBlockStmt(Stmt.Block stmt) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append("Block\n");

        for(Stmt statement : stmt.statements) {
            builder.append(" ")
                   .append(statement.accept(this))
                   .append("\n");
        }

        builder.append("\n)");
        return builder.toString();
    }
}