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

    public String visitPrintStmt(Stmt.Print stmt) {
        return parenthesize("Print", stmt.expression);
    }

    public String visitVarStmt(Stmt.Var stmt) {
        return parenthesize("Var " + stmt.name.lexeme, stmt.initializer);
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